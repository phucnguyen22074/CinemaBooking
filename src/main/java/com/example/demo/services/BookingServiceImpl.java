package com.example.demo.services;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entities.*;
import com.example.demo.repositories.BookingsRepository;

@Service
public class BookingServiceImpl implements BookingService {

	private static final Logger logger = LoggerFactory.getLogger(BookingServiceImpl.class);
	
    @Autowired
    private BookingsRepository bookingsRepository;

    @Override
    public List<Bookings> getBookingsByUser(Users user) {
        if (user == null) {
            throw new IllegalArgumentException("Người dùng không được để trống.");
        }
        return bookingsRepository.findByUsersOrderByBookingDateDesc(user);
    }

    @Override
    public List<Map<String, Object>> getBookingHistoryByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email không được để trống.");
        }

        try {
            List<Bookings> bookings = Optional.ofNullable(bookingsRepository.findBookingHistoryByEmail(email))
                                            .orElse(Collections.emptyList());
            if (bookings.isEmpty()) {
                logger.info("Không tìm thấy lịch sử đặt vé cho email: {}", email);
                return Collections.emptyList();
            }

            Map<String, Map<String, Object>> groupedHistory = groupBookingDetails(bookings);
            return new ArrayList<>(groupedHistory.values());
        } catch (Exception e) {
            logger.error("Lỗi khi lấy lịch sử đặt vé cho email {}: {}", email, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    private Map<String, Map<String, Object>> groupBookingDetails(List<Bookings> bookings) {
        Map<String, Map<String, Object>> groupedHistory = new LinkedHashMap<>();

        for (Bookings booking : bookings) {
            Set<BookingDetails> details = Optional.ofNullable(booking.getBookingDetailses())
                                                 .orElse(Collections.emptySet());
            if (details.isEmpty()) {
                logger.warn("Booking ID {} không có chi tiết đặt chỗ.", booking.getBookingId());
                continue;
            }

            for (BookingDetails bookingDetail : details) {
                Seats seat = bookingDetail.getSeats();
                Showtimes showtime = bookingDetail.getShowtimes();

                if (seat == null || showtime == null) {
                    logger.warn("BookingDetail thiếu thông tin ghế hoặc suất chiếu cho Booking ID {}", booking.getBookingId());
                    continue;
                }

                MovieShowtimeScreen mss = showtime.getMovieShowtimeScreens().stream().findFirst()
                                                 .orElse(null);
                if (mss == null) {
                    logger.warn("Showtime ID {} không có thông tin phòng chiếu.", showtime.getShowtimeId());
                    continue;
                }

                Theaters theater = mss.getScreens().getTheaters();
                Movies movie = mss.getMovies();
                String screenName = mss.getScreens().getName();
                String theaterName = theater != null ? theater.getName() : "N/A";
                String showDate = showtime.getShowDate() != null ? showtime.getShowDate().toString() : "N/A";
                String startTime = showtime.getStartTime() != null ? showtime.getStartTime().toString() : "N/A";
                String movieTitle = movie != null ? movie.getTitle() : "N/A";

                String groupKey = theaterName + "|" + screenName + "|" + showDate + "|" + startTime;

                groupedHistory.computeIfAbsent(groupKey, k -> {
                    Map<String, Object> historyItem = new HashMap<>();
                    historyItem.put("theater", theaterName);
                    historyItem.put("screen", screenName);
                    historyItem.put("showDate", showDate);
                    historyItem.put("startTime", startTime);
                    historyItem.put("qrCode", booking.getBarcode());
                    historyItem.put("seats", new ArrayList<String>());
                    historyItem.put("movieTitle", movieTitle);
                    return historyItem;
                });

                @SuppressWarnings("unchecked")
                List<String> seatsList = (List<String>) groupedHistory.get(groupKey).get("seats");
                seatsList.add(seat.getSeatNumber());
            }
        }
        return groupedHistory;
    }
}
