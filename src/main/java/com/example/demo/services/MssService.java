package com.example.demo.services;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.example.demo.entities.MovieShowtimeScreen;
import com.example.demo.entities.Movies;
import com.example.demo.entities.Theaters;
import com.example.demo.entities.Users;

public interface MssService{

	public List<MovieShowtimeScreen> findMssByMovieId(int movieId);
	public List<MovieShowtimeScreen> getShowtimesByMovieAndDate(int movieId, LocalDate date);
	public Map<Theaters, List<MovieShowtimeScreen>> getGroupedShowtimesByMovieAndDate(int movieId, LocalDate date);
	public Map<Theaters, List<MovieShowtimeScreen>> getGroupedShowtimesByMovieAndDateAndBrandName(int movieId, LocalDate date, String brandName);
	public List<MovieShowtimeScreen> getShowtimesByMovieBrandAndDate(int movieId, LocalDate showDate, String brandName);
	public List<Movies> getMoviesByTheaterId(Integer theaterId);
}
