package com.example.demo.servicesDTO;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import com.example.demo.dto.MoviesDTO;
import com.example.demo.dto.ShowtimesDTO;
import com.example.demo.dto.TheatersDTO;

public interface MoviesDTOService {
	public List<MoviesDTO> findByMovieStatusTrue(int limit);

	public List<MoviesDTO> findByMovieStatusFalse(int limit);

	public MoviesDTO findByMovieId(int id);

	public List<TheatersDTO> getTheatersWithScreensAndShowtimes(int movieId, LocalDate showDate);

	public List<MoviesDTO> findByTitle(String keyword, int page, int size);

	public List<MoviesDTO> findMoviesByGenreName(String genreName);

	public List<MoviesDTO> findMoviesByGenreNameAndTitle(String genreName, String title);

	List<TheatersDTO> getTheatersByBrandMovieAndDate(String brand, int movieId, LocalDate showDate);

	List<String> getAllBrands();
}
