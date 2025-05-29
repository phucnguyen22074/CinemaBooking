package com.example.demo.servicesDTO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.BookingHistoryDTO;
import com.example.demo.dto.BookingRequestDTO;
import com.example.demo.dto.BookingsDTO;
import com.example.demo.entities.BookingDetails;
import com.example.demo.entities.Bookings;
import com.example.demo.entities.Seats;
import com.example.demo.entities.Showtimes;
import com.example.demo.entities.Users;
import com.example.demo.repositories.BookingDetailsRepository;
import com.example.demo.repositories.BookingsRepository;
import com.example.demo.repositories.SeatRepository;
import com.example.demo.repositories.ShowTimeRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.services.MailService;

import jakarta.transaction.Transactional;

@Service
public class BookingDTOServiceImpl implements BookingDTOService {

    private static final Logger logger = LoggerFactory.getLogger(BookingDTOServiceImpl.class);

    @Autowired
    private BookingsRepository bookingRepository;

    @Autowired
    private BookingDetailsRepository bookingDetailsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ShowTimeRepository showtimeRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private MailService mailService; // Giả sử bạn đã có MailService tương tự SeatServiceImpl

    @Transactional
    @Override
    public BookingsDTO createBooking(BookingRequestDTO request) {
        try {
            // Kiểm tra người dùng
            Users user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng với ID: " + request.getUserId()));
            logger.info("User found: {}", user.getUserId());

            // Kiểm tra suất chiếu
            Showtimes showtime = showtimeRepository.findById(request.getShowtimeId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy suất chiếu."));

            // Lấy danh sách ghế và kiểm tra trạng thái
            List<Seats> seats = (List<Seats>) seatRepository.findAllById(request.getSeatIds());
            if (seats.size() != request.getSeatIds().size()) {
                throw new IllegalArgumentException("Một số ghế không tồn tại.");
            }

            for (Seats seat : seats) {
                if (seat.getStatus() != null && seat.getStatus() == 1) {
                    throw new IllegalStateException("Ghế " + seat.getSeatNumber() + " đã được đặt.");
                }
            }

            // Tạo Booking
            Bookings booking = new Bookings();
            booking.setUsers(user);
            booking.setBookingDate(new Date());
            BigDecimal totalPrice = seats.stream()
                    .map(Seats::getPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            booking.setTotalPrice(totalPrice);
            booking.setStatus("CONFIRMED");
            String barcode = generateBarcode();
            booking.setBarcode(barcode);
            booking = bookingRepository.save(booking);

            // Tạo BookingDetails và cập nhật trạng thái ghế
            List<BookingDetails> bookingDetailsList = new ArrayList<>();
            for (Seats seat : seats) {
                BookingDetails detail = new BookingDetails();
                detail.setBookings(booking);
                detail.setSeats(seat);
                detail.setShowtimes(showtime);
                detail.setPrice(seat.getPrice());
                bookingDetailsList.add(detail);

                seat.setStatus((byte) 1); // 1 = đã đặt
            }

            bookingDetailsRepository.saveAll(bookingDetailsList);
            seatRepository.saveAll(seats);

            // Gửi email xác nhận với barcode (tích hợp từ SeatServiceImpl)
            String emailContent = buildEmailContent(user, seats, showtime, barcode);
            boolean isEmailSent = mailService.sendEmailWithBarcode(
                    "phucnguyen220704@gmail.com", // Email người gửi
                    user.getEmail(),              // Email người nhận
                    "Xác nhận đặt vé thành công",
                    emailContent,
                    barcode
            );
            if (!isEmailSent) {
                logger.warn("Không thể gửi email xác nhận tới: {}", user.getEmail());
                // Có thể thêm logic rollback nếu cần
            } else {
                logger.info("Đã gửi email xác nhận tới: {}", user.getEmail());
            }

            // Chuyển sang DTO trước khi trả về
            return modelMapper.map(booking, BookingsDTO.class);

        } catch (Exception e) {
            logger.error("Lỗi khi tạo đặt vé: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi khi tạo đặt vé: " + e.getMessage(), e);
        }
    }

    private String buildEmailContent(Users user, List<Seats> seats, Showtimes showtime, String barcode) {
        String seatNumbers = seats.stream()
                .map(Seats::getSeatNumber)
                .collect(Collectors.joining(", "));
        
        String theaterName = showtime.getMovieShowtimeScreens().stream()
                .findFirst()
                .map(mss -> mss.getScreens().getTheaters().getName())
                .orElse("N/A");
        String screenName = showtime.getMovieShowtimeScreens().stream()
                .findFirst()
                .map(mss -> mss.getScreens().getName())
                .orElse("N/A");
        String movieTitle = showtime.getMovieShowtimeScreens().stream()
                .findFirst()
                .map(mss -> mss.getMovies().getTitle())
                .orElse("N/A");

        return "<html><body>" +
                "<h2>Xin chào " + user.getFullName() + ",</h2>" +
                "<p>Bạn đã đặt vé thành công!</p>" +
                "<ul>" +
                "<li><strong>Tên phim:</strong> " + movieTitle + "</li>" +
                "<li><strong>Rạp:</strong> " + theaterName + "</li>" +
                "<li><strong>Phòng chiếu:</strong> " + screenName + "</li>" +
                "<li><strong>Ghế:</strong> " + seatNumbers + "</li>" +
                "<li><strong>Ngày chiếu:</strong> " + showtime.getShowDate() + "</li>" +
                "<li><strong>Giờ bắt đầu:</strong> " + showtime.getStartTime() + "</li>" +
                "</ul>" +
                "<p>Vui lòng xuất trình mã vạch dưới đây khi đến rạp:</p>" +
                "<img src='cid:barcodeImage' alt='Mã vạch đặt vé' style='width: 200px; height: auto;'/>" +
                "<p>Mã đặt vé (dự phòng): " + barcode + "</p>" +
                "<p>Nếu không thấy hình ảnh, vui lòng bật hiển thị hình ảnh trong email.</p>" +
                "<p>Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi!</p>" +
                "</body></html>";
    }

    private String generateBarcode() {
        return "BK" + System.currentTimeMillis() + (int) (Math.random() * 1000);
    }

    @Override
    public List<BookingHistoryDTO> getBookingHistoryByEmail(String email) {
        List<Bookings> bookings = bookingRepository.findBookingHistoryByEmail(email);
        return bookings.stream()
                .map(booking -> modelMapper.map(booking, BookingHistoryDTO.class))
                .collect(Collectors.toList());
    }
}