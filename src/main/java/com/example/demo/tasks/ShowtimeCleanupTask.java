package com.example.demo.tasks;

import com.example.demo.repositories.BookingsRepository;
import com.example.demo.repositories.BookingDetailsRepository;
import com.example.demo.repositories.MssRepository;
import com.example.demo.repositories.SeatRepository;
import com.example.demo.repositories.ShowTimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class ShowtimeCleanupTask {

    @Autowired
    private MssRepository movieShowtimeScreenRepository;

    @Autowired
    private ShowTimeRepository showTimeRepository;

    @Autowired
    private BookingsRepository bookingsRepository;

    @Autowired
    private BookingDetailsRepository bookingDetailsRepository;

    @Autowired
    private SeatRepository seatRepository;

    // Chạy định kỳ mỗi giây
    @Scheduled(fixedRate = 1000)
    @Transactional
    public void cleanupExpiredShowtimesAndBookings() {
        // Lấy thời gian hiện tại
        Date currentDateTime = new Date();

        // Lấy ngày hiện tại (chỉ lấy phần ngày)
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDateTime);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date currentDate = calendar.getTime();

        // Xóa các suất chiếu hết hạn
        movieShowtimeScreenRepository.deleteExpiredShowtimes(currentDateTime, currentDate);
        showTimeRepository.markExpiredShowtimesAsDeleted(currentDateTime);
    }
}