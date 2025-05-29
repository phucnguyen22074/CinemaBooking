package com.example.demo.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.demo.dto.BookingHistoryDTO;
import com.example.demo.dto.BookingRequestDTO;
import com.example.demo.dto.BookingsDTO;
import com.example.demo.dto.GenresDTO;
import com.example.demo.dto.MoviesDTO;
import com.example.demo.dto.SeatsDTO;
import com.example.demo.dto.TheatersDTO;
import com.example.demo.servicesDTO.BookingDTOService;
import com.example.demo.servicesDTO.GenresDTOService;
import com.example.demo.servicesDTO.MoviesDTOService;
import com.example.demo.servicesDTO.SeatDTOService;

@RestController
@RequestMapping({ "api/movieDTO" })
public class MoviesDTOController {

	@Autowired
	private MoviesDTOService moviesDTOService;

	@Autowired
	private SeatDTOService seatDTOService;

	@Autowired
	private BookingDTOService bookingDTOService; 
	
	@Autowired
	private GenresDTOService genresDTOService;

	private static final Logger logger = LoggerFactory.getLogger(MoviesDTOController.class);

	/* GET */
	@GetMapping(value = "find-movie-status-true", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<MoviesDTO>> findMovieStatusTrue() {
		try {
			List<MoviesDTO> movies = moviesDTOService.findByMovieStatusTrue(10);
			if (movies == null || movies.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			return new ResponseEntity<>(movies, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Error in findMovieStatusTrue: {}", e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping(value = "showtimes/{movieId}/brand", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<TheatersDTO>> getShowtimesByBrandMovieAndDate(
	        @PathVariable("movieId") int movieId,
	        @RequestParam("brand") String brand,
	        @RequestParam("showDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate showDate) {
	    try {
	        List<TheatersDTO> showtimes = moviesDTOService.getTheatersByBrandMovieAndDate(brand, movieId, showDate);
	        if (showtimes == null || showtimes.isEmpty()) {
	            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	        }
	        return new ResponseEntity<>(showtimes, HttpStatus.OK);
	    } catch (Exception e) {
	        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}

	@GetMapping(value = "brands", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<String>> getAllBrands() {
	    try {
	        List<String> brands = moviesDTOService.getAllBrands();
	        if (brands == null || brands.isEmpty()) {
	            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	        }
	        return new ResponseEntity<>(brands, HttpStatus.OK);
	    } catch (Exception e) {
	        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	@GetMapping(value = "find-all-genres", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<GenresDTO>> findAllGenres() {
		try {
			List<GenresDTO> genresDTOs = genresDTOService.findAll();
			if (genresDTOs == null || genresDTOs.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			return new ResponseEntity<>(genresDTOs, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Error in findMovieStatusTrue: {}", e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(value = "find-movie-status-false", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<MoviesDTO>> findMovieStatusFalse() {
		try {
			List<MoviesDTO> movies = moviesDTOService.findByMovieStatusFalse(10);
			if (movies == null || movies.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			return new ResponseEntity<>(movies, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Error in findMovieStatusFalse: {}", e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(value = "find-movieid/{id}", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
	public ResponseEntity<MoviesDTO> findMovieId(@PathVariable("id") int id) {
		try {
			MoviesDTO movies = moviesDTOService.findByMovieId(id);
			if (movies == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity<>(movies, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Error in findMovieId: {}", e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(value = "showtimes/{movieId}", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<TheatersDTO>> getShowtimesByMovieAndDate(@PathVariable("movieId") int movieId,
			@RequestParam("showDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate showDate) {
		try {
			List<TheatersDTO> showtimes = moviesDTOService.getTheatersWithScreensAndShowtimes(movieId, showDate);
			if (showtimes == null || showtimes.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			return new ResponseEntity<>(showtimes, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Error in getShowtimesByMovieAndDate: {}", e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(value = "seats/{showtimeId}", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getSeatsByShowtime(@PathVariable Integer showtimeId) {
		try {
			List<SeatsDTO> seats = seatDTOService.getSeatsByShowtime(showtimeId);
			if (seats.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT)
						.body("No seats available for showtime ID: " + showtimeId);
			}
			return new ResponseEntity<>(seats, HttpStatus.OK);
		} catch (ConfigDataResourceNotFoundException ex) {
			logger.warn("Showtime not found: {}", ex.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
		} catch (Exception ex) {
			logger.error("Error fetching seats for showtime {}: {}", showtimeId, ex.getMessage(), ex);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving seats");
		}
	}

	@GetMapping(value = "/history", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<BookingHistoryDTO>> getBookingHistory(@RequestParam("email") String email) {
		try {
			List<BookingHistoryDTO> history = bookingDTOService.getBookingHistoryByEmail(email);
			if (history == null || history.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			return new ResponseEntity<>(history, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Error fetching booking history for email {}: {}", email, e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/title")
    public ResponseEntity<List<MoviesDTO>> findByTitle(
            @RequestParam("keyword") String keyword,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        List<MoviesDTO> movies = moviesDTOService.findByTitle(keyword, page, size);
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/genre")
    public ResponseEntity<List<MoviesDTO>> findMoviesByGenreName(
            @RequestParam("genreName") String genreName) {
        List<MoviesDTO> movies = moviesDTOService.findMoviesByGenreName(genreName);
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/genre-title")
    public ResponseEntity<List<MoviesDTO>> findMoviesByGenreNameAndTitle(
            @RequestParam("genreName") String genreName,
            @RequestParam("title") String title) {
        List<MoviesDTO> movies = moviesDTOService.findMoviesByGenreNameAndTitle(genreName, title);
        return ResponseEntity.ok(movies);
    }

	/* POST */
	@PostMapping(value = "bookings/create", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> createBooking(@RequestBody BookingRequestDTO request) {
		try {
			BookingsDTO booking = bookingDTOService.createBooking(request);
			if (booking == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Không thể tạo đặt vé.");
			}
			return new ResponseEntity<>(booking, HttpStatus.CREATED);
		} catch (IllegalArgumentException e) {
			logger.warn("Invalid input for booking: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		} catch (IllegalStateException e) {
			logger.warn("Seat conflict: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		} catch (Exception e) {
			logger.error("Error creating booking: {}", e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống: " + e.getMessage());
		}
	}

	/* PUT */

	/* DELETE */
}