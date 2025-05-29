package com.example.demo.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.Movies;
import com.example.demo.entities.Theaters;

@Repository
public interface TheaterRepository extends CrudRepository<Theaters, Integer> {
	@Query("SELECT DISTINCT t FROM Theaters t " + "JOIN t.screenses s JOIN s.movieShowtimeScreens mss "
			+ "WHERE mss.movies.movieId = :movieId AND mss.showtimes.showDate = :date")
	List<Theaters> findTheatersByMovieAndDate(@Param("movieId") int movieId, @Param("date") LocalDate date);

	@Query("SELECT DISTINCT t.brands.name FROM Theaters t WHERE t.brands.name IS NOT NULL AND TRIM(t.brands.name) != ''")
	List<String> findAllBrands();

	@Query("SELECT t FROM Theaters t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%'))")
	List<Theaters> findByNameContaining(@Param("name") String name);

	@Query("SELECT t FROM Theaters t WHERE t.brands.name = :brandName")
	List<Theaters> findByBrandName(@Param("brandName") String brandName);

	@Query("SELECT DISTINCT t FROM Theaters t " + "JOIN t.screenses s JOIN s.movieShowtimeScreens mss "
			+ "WHERE t.brands.name = :brand AND mss.movies.movieId = :movieId AND mss.showtimes.showDate = :date")
	List<Theaters> findTheatersByBrandMovieAndDate(@Param("brand") String brand, @Param("movieId") int movieId,
			@Param("date") LocalDate date);
}
