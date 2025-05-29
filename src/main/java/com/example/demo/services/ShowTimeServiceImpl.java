package com.example.demo.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entities.Showtimes;
import com.example.demo.repositories.ShowTimeRepository;

@Service
public class ShowTimeServiceImpl implements ShowTimeService {

    @Autowired
    private ShowTimeRepository showTimeRepository;

    @Override
    public Showtimes findById(int showtimeId) {
        return showTimeRepository.findById(showtimeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy suất chiếu với ID: " + showtimeId));
    }

    @Override
    public List<Showtimes> getShowtimesByDateAndMovie(String date, Integer movieId) {
        if (date == null || movieId == null) {
            throw new IllegalArgumentException("Ngày chiếu và ID phim không được để trống.");
        }

        LocalDate parsedDate;
        try {
            parsedDate = LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Định dạng ngày không hợp lệ, vui lòng nhập theo định dạng yyyy-MM-dd.");
        }

        return showTimeRepository.findByShowDateAndMovieId(parsedDate, movieId);
    }

    @Override
    public List<Showtimes> getShowtimesByDateBrandAndMovie(String date, String brandName, Integer movieId) {
        if (date == null || brandName == null || brandName.isEmpty() || movieId == null) {
            throw new IllegalArgumentException("Ngày chiếu, thương hiệu và ID phim không được để trống.");
        }

        LocalDate parsedDate;
        try {
            parsedDate = LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Định dạng ngày không hợp lệ, vui lòng nhập theo định dạng yyyy-MM-dd.");
        }

        return showTimeRepository.findByShowDateBrandAndMovieId(parsedDate, brandName, movieId);
    }
    
    @Override
    public List<Showtimes> getShowtimesByDateBrandAndTime(String date, String brandName, String startTime) {
        LocalDate parsedDate = LocalDate.parse(date);
        LocalTime parsedTime = LocalTime.parse(startTime);
        return showTimeRepository.findByShowDateBrandAndStartTime(parsedDate, brandName, parsedTime);
    }
}
