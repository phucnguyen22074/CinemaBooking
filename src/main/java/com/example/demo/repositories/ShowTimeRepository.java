package com.example.demo.repositories;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entities.Genres;
import com.example.demo.entities.Movies;
import com.example.demo.entities.Showtimes;

@Repository
public interface ShowTimeRepository extends CrudRepository<Showtimes, Integer> {
	@Query("SELECT s FROM Showtimes s " + "JOIN s.movieShowtimeScreens mss " + "WHERE s.showDate = :showDate "
			+ "AND mss.movies.movieId = :movieId")
	List<Showtimes> findByShowDateAndMovieId(@Param("showDate") LocalDate showDate, @Param("movieId") Integer movieId);

	/**
	 * Tìm lịch chiếu theo ngày, thương hiệu rạp và phim.
	 */
	@Query("SELECT s FROM Showtimes s " + "JOIN s.movieShowtimeScreens mss " + "JOIN mss.screens sc "
			+ "JOIN sc.theaters t " + "WHERE s.showDate = :showDate " + "AND t.brands.name = :brandName "
			+ "AND mss.movies.movieId = :movieId")
	List<Showtimes> findByShowDateBrandAndMovieId(@Param("showDate") LocalDate showDate,
			@Param("brandName") String brandName, @Param("movieId") Integer movieId);
	
	@Transactional
	@Modifying
	@Query("UPDATE Showtimes s SET s.isDeleted = true WHERE s.endTime <= :currentDateTime")
	void markExpiredShowtimesAsDeleted(@Param("currentDateTime") Date currentDateTime);
	
	@Query("SELECT s FROM Showtimes s " +
		       "JOIN s.movieShowtimeScreens mss " +
		       "JOIN mss.screens sc " +
		       "JOIN sc.theaters t " +
		       "WHERE s.showDate = :showDate " +
		       "AND t.brands.name = :brandName " +
		       "AND s.startTime = :startTime")
		List<Showtimes> findByShowDateBrandAndStartTime(
		    @Param("showDate") LocalDate showDate,
		    @Param("brandName") String brandName,
		    @Param("startTime") LocalTime startTime);
}
