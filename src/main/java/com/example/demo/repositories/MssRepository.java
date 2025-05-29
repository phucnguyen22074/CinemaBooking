package com.example.demo.repositories;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entities.Genres;
import com.example.demo.entities.MovieShowtimeScreen;
import com.example.demo.entities.Movies;
import com.example.demo.entities.Screens;

@Repository
public interface MssRepository extends CrudRepository<MovieShowtimeScreen, Integer> {
	@Query("SELECT mss FROM MovieShowtimeScreen mss WHERE mss.movies.movieId = :movieId")
	public List<MovieShowtimeScreen> findMssByMovieId(@Param("movieId") int movieId);

	@Query("SELECT mss FROM MovieShowtimeScreen mss WHERE mss.movies.movieId = :movieId AND mss.showtimes.showDate = :showDate")
	public List<MovieShowtimeScreen> findShowtimesByMovieAndDate(@Param("movieId") int movieId,
			@Param("showDate") LocalDate showDate);

	@Query("SELECT mss FROM MovieShowtimeScreen mss " + "JOIN mss.screens sc " + "JOIN sc.theaters t "
			+ "WHERE mss.movies.movieId = :movieId " + "AND mss.showtimes.showDate = :showDate "
			+ "AND t.brands.name = :brandName")
	List<MovieShowtimeScreen> findByMovieIdBrandAndShowDate(@Param("movieId") int movieId,
			@Param("showDate") LocalDate showDate, @Param("brandName") String brandName);
	
	@Query("SELECT mss.screens FROM MovieShowtimeScreen mss WHERE mss.showtimes.showtimeId = :showtimeId")
    Optional<Screens> findScreenByShowtimeId(@Param("showtimeId") Integer showtimeId);
	
	@Query("SELECT mss FROM MovieShowtimeScreen mss " +
		       "JOIN mss.screens sc " +
		       "JOIN sc.theaters t " +
		       "WHERE t.theaterId = :theaterId")
		List<MovieShowtimeScreen> findByTheaterId(@Param("theaterId") Integer theaterId);

	@Transactional
	@Modifying
	@Query("DELETE FROM MovieShowtimeScreen mss " + "WHERE mss.showtimes.endTime <= :currentDateTime "
			+ "AND mss.showtimes.showDate = :currentDate")
	public void deleteExpiredShowtimes(Date currentDateTime, Date currentDate);
}
