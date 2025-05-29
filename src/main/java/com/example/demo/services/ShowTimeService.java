package com.example.demo.services;

import java.util.List;

import org.springframework.data.repository.query.Param;

import com.example.demo.entities.Showtimes;
import com.example.demo.entities.Theaters;

public interface ShowTimeService {
	public Showtimes findById(int showtimeId);
	public List<Showtimes> getShowtimesByDateAndMovie(String date, Integer movieId);
	public List<Showtimes> getShowtimesByDateBrandAndMovie(String date, String brandName, Integer movieId);
	public List<Showtimes> getShowtimesByDateBrandAndTime(String date, String brandName, String startTime);
}
