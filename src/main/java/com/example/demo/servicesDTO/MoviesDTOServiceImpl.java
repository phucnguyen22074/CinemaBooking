package com.example.demo.servicesDTO;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.dto.MoviesDTO;
import com.example.demo.dto.ScreensDTO;
import com.example.demo.dto.ShowtimesDTO;
import com.example.demo.dto.TheatersDTO;
import com.example.demo.entities.*;

import com.example.demo.repositories.MovieRepository;
import com.example.demo.repositories.MssRepository;
import com.example.demo.repositories.TheaterRepository;

@Service
public class MoviesDTOServiceImpl implements MoviesDTOService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private MssRepository mssRepository;

    @Autowired
    private Environment environment;
    
    @Autowired
    private TheaterRepository theaterRepository;

    @Override
    public List<MoviesDTO> findByMovieStatusTrue(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Movies> movies = movieRepository.findByStatus(true, pageable);
        return (movies == null || movies.isEmpty()) ? Collections.emptyList()
                : modelMapper.map(movies, new TypeToken<List<MoviesDTO>>() {}.getType());
    }

    @Override
    public List<MoviesDTO> findByMovieStatusFalse(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Movies> movies = movieRepository.findByStatus(false, pageable);
        return (movies == null || movies.isEmpty()) ? Collections.emptyList()
                : modelMapper.map(movies, new TypeToken<List<MoviesDTO>>() {}.getType());
    }

    @Override
    public MoviesDTO findByMovieId(int id) {
        return movieRepository.findById(id)
                .map(movie -> modelMapper.map(movie, MoviesDTO.class))
                .orElse(null);
    }

    @Override
    public List<TheatersDTO> getTheatersWithScreensAndShowtimes(int movieId, LocalDate showDate) {
        List<MovieShowtimeScreen> mssList = mssRepository.findShowtimesByMovieAndDate(movieId, showDate);
        
        if (mssList == null || mssList.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Integer, TheatersDTO> theaterMap = new HashMap<>();

        for (MovieShowtimeScreen mss : mssList) {
            Screens screen = mss.getScreens();
            if (screen == null || screen.getTheaters() == null) continue;

            Theaters theater = screen.getTheaters();
            Showtimes showtime = mss.getShowtimes();
            Brands brand = theater.getBrands();

            TheatersDTO theaterDTO = theaterMap.computeIfAbsent(theater.getTheaterId(), id -> {
                TheatersDTO dto = new TheatersDTO();
                dto.setTheaterId(theater.getTheaterId());
                dto.setName(theater.getName());
                dto.setAddress(theater.getAddress());
                dto.setLocationId(theater.getLocations() != null ? theater.getLocations().getLocationId() : null);
                dto.setScreens(new ArrayList<>());
                
                if (brand != null && brand.getImageUrl() != null) {
                    String imageUrl = environment.getProperty("brands_url") + brand.getImageUrl();
                    dto.setBrandId(brand.getBrandId());
                    dto.setBrandImage(imageUrl);
                }
                return dto;
            });

            ScreensDTO screenDTO = theaterDTO.getScreens().stream()
                    .filter(s -> s.getScreenId().equals(screen.getScreenId()))
                    .findFirst()
                    .orElseGet(() -> {
                        ScreensDTO dto = new ScreensDTO();
                        dto.setScreenId(screen.getScreenId());
                        dto.setName(screen.getName());
                        dto.setTotalSeats(screen.getTotalSeats());
                        dto.setTheaterId(theater.getTheaterId());
                        dto.setShowtimes(new ArrayList<>());
                        theaterDTO.getScreens().add(dto);
                        return dto;
                    });

            ShowtimesDTO showtimeDTO = new ShowtimesDTO();
            showtimeDTO.setShowtimeId(showtime.getShowtimeId());
            showtimeDTO.setShowDate(showtime.getShowDate());
            showtimeDTO.setStartTime(showtime.getStartTime());
            showtimeDTO.setEndTime(showtime.getEndTime());
            screenDTO.getShowtimes().add(showtimeDTO);
        }

        return new ArrayList<>(theaterMap.values());
    }

    @Override
    public List<MoviesDTO> findByTitle(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Movies> movies = movieRepository.findByTitle(keyword, pageable);
        return movies.stream()
                .map(movie -> modelMapper.map(movie, MoviesDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<MoviesDTO> findMoviesByGenreName(String genreName) {
        List<Movies> movies = movieRepository.findMoviesByGenreName(genreName);
        return movies.stream()
                .map(movie -> modelMapper.map(movie, MoviesDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<MoviesDTO> findMoviesByGenreNameAndTitle(String genreName, String title) {
        List<Movies> movies = movieRepository.findMoviesByGenreNameAndTitle(genreName, title);
        return movies.stream()
                .map(movie -> modelMapper.map(movie, MoviesDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<TheatersDTO> getTheatersByBrandMovieAndDate(String brand, int movieId, LocalDate showDate) {
        List<Theaters> theaters = theaterRepository.findTheatersByBrandMovieAndDate(brand, movieId, showDate);
        if (theaters == null || theaters.isEmpty()) {
            return Collections.emptyList();
        }

        return theaters.stream().map(theater -> {
            // Ánh xạ cơ bản bằng ModelMapper
            TheatersDTO theaterDTO = modelMapper.map(theater, TheatersDTO.class);

            // Lọc và cập nhật showtimes theo movieId và showDate
            theaterDTO.getScreens().forEach(screenDTO -> {
                List<ShowtimesDTO> filteredShowtimes = screenDTO.getShowtimes().stream()
                    .filter(showtimeDTO -> {
                        for (MovieShowtimeScreen mss : theater.getScreenses().stream()
                                .filter(s -> s.getScreenId().equals(screenDTO.getScreenId()))
                                .findFirst().get().getMovieShowtimeScreens()) {
                            if (mss.getMovies().getMovieId() == movieId && 
                                mss.getShowtimes().getShowDate().equals(showDate) && 
                                mss.getShowtimes().getShowtimeId().equals(showtimeDTO.getShowtimeId())) {
                                return true;
                            }
                        }
                        return false;
                    })
                    .collect(Collectors.toList());
                screenDTO.setShowtimes(filteredShowtimes);
            });

            return theaterDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<String> getAllBrands() {
        List<String> brands = theaterRepository.findAllBrands();
        return brands != null ? brands : Collections.emptyList();
    }
}
