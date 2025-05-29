package com.example.demo.repostitoriesAdmin;




import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entities.MovieShowtimeScreen;
import com.example.demo.entities.Movies;

public interface MovieShowtimeRepository extends JpaRepository<MovieShowtimeScreen, Integer> {
	@Query("SELECT m FROM MovieShowtimeScreen m where m.movies.movieId = :movieId")
	public Movies findMoviesIdByMvs(@Param("movieId") Integer movieId);
	
	 @Query("SELECT m FROM MovieShowtimeScreen m WHERE m.showtimes.showtimeId = :showtimeId")
	 public List<MovieShowtimeScreen> findByShowtimeId(@Param("showtimeId") Integer showtimeId);
}
