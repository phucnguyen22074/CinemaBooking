package com.example.demo.services;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entities.MovieShowtimeScreen;
import com.example.demo.entities.Movies;
import com.example.demo.entities.Theaters;
import com.example.demo.repositories.MssRepository;

@Service
public class MssServiceImpl implements MssService {

    @Autowired
    private MssRepository mssRepository;

    @Override
    public List<MovieShowtimeScreen> findMssByMovieId(int movieId) {
        if (movieId <= 0) {
            throw new IllegalArgumentException("ID phim phải lớn hơn 0.");
        }
        return Optional.ofNullable(mssRepository.findMssByMovieId(movieId))
                       .orElse(Collections.emptyList());
    }

    public Map<Theaters, List<MovieShowtimeScreen>> getGroupedShowtimesByMovieAndDateAndBrandName(int movieId, LocalDate date, String brandName) {
        if (movieId <= 0) {
            throw new IllegalArgumentException("ID phim phải lớn hơn 0.");
        }
        if (date == null) {
            throw new IllegalArgumentException("Ngày không được để trống.");
        }
        if (brandName == null || brandName.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên thương hiệu không được để trống.");
        }

        return mssRepository.findByMovieIdBrandAndShowDate(movieId, date, brandName)
                            .stream()
                            .collect(Collectors.groupingBy(
                                mss -> mss.getScreens().getTheaters(),
                                LinkedHashMap::new, // Đảm bảo thứ tự
                                Collectors.toList()
                            ));
    }

    public List<MovieShowtimeScreen> getShowtimesByMovieAndDate(int movieId, LocalDate date) {
        if (movieId <= 0) {
            throw new IllegalArgumentException("ID phim phải lớn hơn 0.");
        }
        if (date == null) {
            throw new IllegalArgumentException("Ngày không được để trống.");
        }

        return Optional.ofNullable(mssRepository.findShowtimesByMovieAndDate(movieId, date))
                       .orElse(Collections.emptyList());
    }

    @Override
    public List<MovieShowtimeScreen> getShowtimesByMovieBrandAndDate(int movieId, LocalDate showDate, String brandName) {
        if (movieId <= 0) {
            throw new IllegalArgumentException("ID phim phải lớn hơn 0.");
        }
        if (showDate == null) {
            throw new IllegalArgumentException("Ngày chiếu không được để trống.");
        }
        if (brandName == null || brandName.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên thương hiệu không được để trống.");
        }

        return Optional.ofNullable(mssRepository.findByMovieIdBrandAndShowDate(movieId, showDate, brandName))
                       .orElse(Collections.emptyList());
    }

    @Override
    public Map<Theaters, List<MovieShowtimeScreen>> getGroupedShowtimesByMovieAndDate(int movieId, LocalDate date) {
        if (movieId <= 0) {
            throw new IllegalArgumentException("ID phim phải lớn hơn 0.");
        }
        if (date == null) {
            throw new IllegalArgumentException("Ngày không được để trống.");
        }

        return mssRepository.findShowtimesByMovieAndDate(movieId, date)
                            .stream()
                            .collect(Collectors.groupingBy(
                                mss -> mss.getScreens().getTheaters(),
                                LinkedHashMap::new, // Đảm bảo thứ tự
                                Collectors.toList()
                            ));
    }

	@Override
	public List<Movies> getMoviesByTheaterId(Integer theaterId) {
		if (theaterId == null || theaterId <= 0) {
	        throw new IllegalArgumentException("ID rạp phải lớn hơn 0.");
	    }
	    return mssRepository.findByTheaterId(theaterId).stream()
	            .map(MovieShowtimeScreen::getMovies)
	            .distinct()
	            .collect(Collectors.toList());
	}
}
