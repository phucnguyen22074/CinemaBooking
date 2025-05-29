package com.example.demo.services;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entities.*;
import com.example.demo.repositories.*;

import jakarta.transaction.Transactional;

@Service
public class SeatServiceImpl implements SeatService {

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private BookingDetailsRepository bookingDetailsRepository;

    @Autowired
    private BookingsRepository bookingsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShowTimeRepository showTimeRepository;

    @Autowired
    private BarcodeService barcodeService;

    @Autowired
    private MailService mailService;

    @Transactional
    public void bookSeats(Set<Integer> seatIds) {
        if (seatIds == null || seatIds.isEmpty()) {
            throw new IllegalArgumentException("Danh sách ghế không được để trống.");
        }

        for (Integer seatId : seatIds) {
            Seats seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy ghế với ID: " + seatId));

            if (seat.getStatus() == (byte) 1) {
                throw new RuntimeException("Ghế " + seat.getSeatNumber() + " đã được đặt.");
            }

            seat.setStatus((byte) 1); // 1 = BOOKED
            seatRepository.save(seat);
        }
    }

    @Override
    @Transactional
    public String createPendingBooking(List<Integer> seatIds, Integer userId, Integer showtimeId) {
        if (seatIds == null || seatIds.isEmpty()) {
            throw new IllegalArgumentException("Danh sách ghế không được để trống.");
        }

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));
        Showtimes showtime = showTimeRepository.findById(showtimeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy suất chiếu với ID: " + showtimeId));

        // Tạo mã booking tạm thời
        String bookingCode = UUID.randomUUID().toString().replace("-", "").substring(0, 10);

        // Tạo bản ghi PENDING
        Bookings booking = new Bookings();
        booking.setUsers(user);
        booking.setBookingDate(new Date());
        booking.setTotalPrice(calculateTotalPrice(seatIds));
        booking.setBarcode(bookingCode);
        booking.setStatus("PENDING");
        Bookings savedBooking = bookingsRepository.save(booking);

        // Kiểm tra xem savedBooking có được lưu thành công không
        if (savedBooking.getBookingId() == null) {
            throw new RuntimeException("Không thể lưu bản ghi booking với barcode: " + bookingCode);
        }

        // Lưu chi tiết đặt vé và đặt trạng thái PENDING cho ghế
        for (Integer seatId : seatIds) {
            Seats seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy ghế với ID: " + seatId));
            if (seat.getStatus() != (byte) 0) {
                throw new RuntimeException("Ghế " + seat.getSeatNumber() + " không khả dụng (status: " + seat.getStatus() + ")");
            }
            seat.setStatus((byte) 3); // PENDING
            seatRepository.save(seat);

            BookingDetails bookingDetail = new BookingDetails();
            bookingDetail.setBookings(savedBooking);
            bookingDetail.setSeats(seat);
            bookingDetail.setShowtimes(showtime);
            bookingDetail.setPrice(seat.getPrice());
            bookingDetailsRepository.save(bookingDetail);
        }

        return bookingCode;
    }

    @Transactional
    public void confirmBooking(String bookingCode) {
        Bookings booking = bookingsRepository.findByBarcode(bookingCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy booking với mã: " + bookingCode));
        
        if (!"PENDING".equals(booking.getStatus())) {
            throw new RuntimeException("Booking đã được xử lý hoặc không tồn tại.");
        }

        // Lấy thông tin từ BookingDetails để xây dựng email
        List<BookingDetails> details = booking.getBookingDetailses().stream().collect(Collectors.toList());
        if (details.isEmpty()) {
            throw new RuntimeException("Không tìm thấy chi tiết đặt vé cho booking: " + bookingCode);
        }

        Users user = booking.getUsers();
        List<Integer> seatIds = details.stream().map(d -> d.getSeats().getSeatId()).collect(Collectors.toList());
        Movies movie = details.get(0).getShowtimes().getMovieShowtimeScreens().iterator().next().getMovies();
        Theaters theater = details.get(0).getShowtimes().getMovieShowtimeScreens().iterator().next().getScreens().getTheaters();
        Screens screen = details.get(0).getShowtimes().getMovieShowtimeScreens().iterator().next().getScreens();

        // Xác nhận booking
        booking.setStatus("CONFIRMED");
        bookingsRepository.save(booking);

        // Gửi email xác nhận với barcode
        String content = buildEmailContent(user, seatIds, movie, theater, screen, bookingCode); // Truyền bookingCode
        boolean isEmailSent = mailService.sendEmailWithBarcode(
                "phucnguyen220704@gmail.com",
                user.getEmail(),
                "Xác nhận đặt vé thành công",
                content,
                bookingCode
        );
        if (!isEmailSent) {
            // Rollback trạng thái nếu gửi email thất bại
            booking.setStatus("PENDING");
            bookingsRepository.save(booking);
            throw new RuntimeException("Không thể gửi email xác nhận.");
        }
        for (BookingDetails detail : details) {
            Seats seat = detail.getSeats();
            if (seat.getStatus() != (byte) 3) {
                throw new RuntimeException("Ghế " + seat.getSeatNumber() + " không ở trạng thái PENDING.");
            }
            seat.setStatus((byte) 1); // 1 = BOOKED
            seatRepository.save(seat);
        }
    }

    public BigDecimal calculateTotalPrice(List<Integer> seatIds) {
        return seatIds.stream()
                .map(seatId -> seatRepository.findById(seatId)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy ghế với ID: " + seatId))
                        .getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String buildEmailContent(Users user, List<Integer> seatIds, Movies movie, Theaters theater, Screens screen, String barcode) {
        String seats = seatIds.stream()
                .map(seatId -> seatRepository.findById(seatId).map(Seats::getSeatNumber).orElse("N/A"))
                .collect(Collectors.joining(", "));
        
        return "<html><body>" +
                "<h2>Xin chào " + user.getFullName() + ",</h2>" +
                "<p>Bạn đã đặt vé thành công!</p>" +
                "<ul>" +
                "<li><strong>Tên phim:</strong> " + movie.getTitle() + "</li>" +
                "<li><strong>Rạp:</strong> " + theater.getName() + "</li>" +
                "<li><strong>Phòng chiếu:</strong> " + screen.getName() + "</li>" +
                "<li><strong>Ghế:</strong> " + seats + "</li>" +
                "</ul>" +
                "<p>Vui lòng xuất trình mã vạch dưới đây khi đến rạp:</p>" +
                "<img src='cid:barcodeImage' alt='Mã vạch đặt vé' style='width: 200px; height: auto;'/>" +
                "<p>Mã đặt vé (dự phòng): " + barcode + "</p>" +
                "<p>Nếu không thấy hình ảnh, vui lòng bật hiển thị hình ảnh trong email.</p>" +
                "<p>Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi!</p>" +
                "</body></html>";
    }
}