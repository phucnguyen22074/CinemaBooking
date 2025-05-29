package com.example.demo.tasks;

import com.example.demo.repositories.BookingDetailsRepository;
import com.example.demo.repositories.BookingsRepository;
import com.example.demo.repositories.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class PendingBookingCleanupTask {

    @Autowired
    private BookingsRepository bookingsRepository;

    @Autowired
    private BookingDetailsRepository bookingDetailsRepository;

    @Autowired
    private SeatRepository seatRepository;

    // Chạy định kỳ mỗi 3 phút (180,000 milliseconds)
    @Scheduled(fixedRate = 180000)
    @Transactional
    public void cleanupPendingBookings() {
        // Lấy danh sách seat_id từ BookingDetails có status = "PENDING"
//        List<Integer> seatIds = bookingDetailsRepository.findSeatIdsByBookingsStatusPending();
//
//        if (!seatIds.isEmpty()) {
//            seatRepository.updateSeatsStatusToFalse(seatIds);
//        }
//
//        // Xóa BookingDetails và Bookings có status = "PENDING"
//        bookingDetailsRepository.deleteByBookingsStatusPending();
//        bookingsRepository.deleteByStatusPending();

        System.out.println("Đã xóa các bookings PENDING và cập nhật trạng thái ghế vào lúc: " + new java.util.Date());
    }
}