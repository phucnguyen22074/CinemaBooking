package com.example.demo.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.Bookings;
import com.example.demo.entities.Genres;
import com.example.demo.entities.Movies;
import com.example.demo.entities.Seats;
import com.example.demo.entities.Users;

import jakarta.transaction.Transactional;

@Repository
public interface BookingsRepository extends CrudRepository<Bookings, Integer> {

	@Query("SELECT b FROM Bookings b WHERE b.users = :user ORDER BY b.bookingDate DESC")
	List<Bookings> findByUsersOrderByBookingDateDesc(@Param("user") Users user);

	@Query("SELECT DISTINCT b FROM Bookings b " + "LEFT JOIN FETCH b.bookingDetailses bd "
			+ "LEFT JOIN FETCH bd.seats s " + "LEFT JOIN FETCH bd.showtimes st "
			+ "LEFT JOIN FETCH st.movieShowtimeScreens mss " + "LEFT JOIN FETCH mss.screens sc "
			+ "LEFT JOIN FETCH sc.theaters t " + "WHERE b.users.email = :email " + "AND b.status = 'CONFIRMED' "
			+ "ORDER BY b.bookingDate DESC")
	List<Bookings> findBookingHistoryByEmail(@Param("email") String email);

	@Query("SELECT b FROM Bookings b WHERE b.barcode = :barcode")
	Optional<Bookings> findByBarcode(@Param("barcode") String barcode);

	@Modifying
	@Transactional
	@Query("DELETE FROM Bookings b WHERE b.status = 'PENDING'")
	void deleteByStatusPending();
}
