package com.example.demo.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.BookingDetails;
import com.example.demo.entities.Genres;
import com.example.demo.entities.Movies;
import com.example.demo.entities.Seats;

import jakarta.transaction.Transactional;

@Repository
public interface BookingDetailsRepository extends CrudRepository<BookingDetails, Integer> {
	// Lấy danh sách seat_id từ BookingDetails có status = "PENDING"
    @Query("SELECT bd.seats.seatId FROM BookingDetails bd WHERE bd.bookings.status = 'PENDING'")
    List<Integer> findSeatIdsByBookingsStatusPending();

    // Xóa tất cả BookingDetails có status = "PENDING"
    @Modifying
    @Transactional
    @Query("DELETE FROM BookingDetails bd WHERE bd.bookings.status = 'PENDING'")
    void deleteByBookingsStatusPending();
}
