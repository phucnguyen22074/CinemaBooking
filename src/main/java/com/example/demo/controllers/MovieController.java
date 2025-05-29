package com.example.demo.controllers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.demo.entities.Bookings;
import com.example.demo.entities.ConfirmSeatsRequest;
import com.example.demo.entities.MovieShowtimeScreen;
import com.example.demo.entities.Movies;
import com.example.demo.entities.Screens;
import com.example.demo.entities.Seats;
import com.example.demo.entities.Showtimes;
import com.example.demo.entities.Theaters;
import com.example.demo.entities.Users;
import com.example.demo.repositories.BookingsRepository;
import com.example.demo.services.AccountService;
import com.example.demo.services.BookingService;
import com.example.demo.services.MovieService;
import com.example.demo.services.MssService;
import com.example.demo.services.SeatService;
import com.example.demo.services.ShowTimeService;
import com.example.demo.services.TheaterService;

@Controller
@RequestMapping({ "movie" })
public class MovieController {

	@Autowired
	private MovieService movieService;

	@Autowired
	private TheaterService theaterService;

	@Autowired
	private ShowTimeService showTimeService;

	@Autowired
	private MssService mssService;
	
	@Autowired
	private SeatService seatService;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private BookingService bookingService;
	
	@Autowired
	private Environment environment;
	
	@Autowired
	private BookingsRepository bookingsRepository;
	
	/*Get*/
	@GetMapping("details/{id}")
	public String Index(ModelMap modelMap, @PathVariable("id") int id) {
	    try {
	        Movies movie = movieService.findById(id);
	        if (movie == null) {
	            throw new RuntimeException("Không tìm thấy phim với ID: " + id);
	        }

	        LocalDate today = LocalDate.now();
	        modelMap.put("movie", movie);
	        modelMap.put("groupedShowtimes", mssService.getGroupedShowtimesByMovieAndDate(id, today));
	        modelMap.put("mss", mssService.getShowtimesByMovieAndDate(id, today));
	        return "movie/details";
	    } catch (Exception ex) {
	        ex.printStackTrace();
	        modelMap.put("error", "Không thể tải thông tin phim!");
	        return "error";
	    }
	}
	
	// Trong com.example.demo.controllers.MovieController
	@GetMapping("/search-movies")
	public String searchMovies(
	        @RequestParam(value = "genre", required = false) String genre,
	        @RequestParam(value = "keyword", required = false) String keyword,
	        @RequestParam(value = "theaterId", required = false) Integer theaterId, // Thêm theaterId
	        @RequestParam(value = "brandName", required = false) String brandName,
	        @RequestParam(value = "date", required = false) String date,
	        @RequestParam(value = "startTime", required = false) String startTime,
	        ModelMap modelMap) {
	    try {
	        LocalDate showDate = date != null && !date.isEmpty() ? LocalDate.parse(date) : LocalDate.now();
	        List<Movies> movies = new ArrayList<>();

	        // Tìm kiếm theo theaterId nếu có
	        if (theaterId != null && theaterId > 0) {
	            movies = mssService.getMoviesByTheaterId(theaterId);
	        } else {
	            // Logic tìm kiếm ban đầu nếu không có theaterId
	            if (genre != null && !genre.isEmpty() && keyword != null && !keyword.isEmpty()) {
	                movies = movieService.findMoviesByGenreNameAndTitle(genre, keyword);
	            } else if (genre != null && !genre.isEmpty()) {
	                movies = movieService.findMoviesByGenreName(genre);
	            } else if (keyword != null && !keyword.isEmpty()) {
	                movies = movieService.findByMoviesKeyword(keyword, 10);
	            } else {
	                movies = movieService.findByMovieStatusTrue(10);
	            }
	        }

	        // Lọc thêm theo các tiêu chí khác nếu có
	        if (brandName != null && !brandName.isEmpty()) {
	            List<Theaters> brandTheaters = theaterService.findByBrandName(brandName);
	            movies = movies.stream()
	                    .filter(m -> mssService.findMssByMovieId(m.getTheaterses().stream()
	                            .map(Theaters::getTheaterId)
	                            .filter(id -> brandTheaters.stream().anyMatch(t -> t.getTheaterId().equals(id)))
	                            .findFirst().orElse(null)) != null)
	                    .collect(Collectors.toList());
	        }

	        if (startTime != null && !startTime.isEmpty()) {
	            LocalTime parsedTime = LocalTime.parse(startTime);
	            List<Showtimes> showtimes = showTimeService.getShowtimesByDateBrandAndTime(
	                    showDate.toString(), brandName, startTime);
	            movies = movies.stream()
	                    .filter(m -> showtimes.stream()
	                            .anyMatch(s -> s.getMovieShowtimeScreens().stream()
	                                    .anyMatch(mss -> mss.getMovies().getMovieId().equals(m.getMovieId()))))
	                    .collect(Collectors.toList());
	        }

	        modelMap.put("movies", movies);
	        modelMap.put("movieTrailers", movieService.findByMovieStatusTrue(10));
	        modelMap.put("brands", theaterService.findAllBrands());
	        modelMap.put("theaters", theaterService.findAll()); // Thêm danh sách rạp để chọn
	        modelMap.put("searchCriteria", Map.of(
	                "genre", genre != null ? genre : "",
	                "keyword", keyword != null ? keyword : "",
	                "theaterId", theaterId != null ? theaterId.toString() : "",
	                "brandName", brandName != null ? brandName : "",
	                "date", showDate.toString(),
	                "startTime", startTime != null ? startTime : ""
	        ));
	        return "movie/search-movies";
	    } catch (Exception ex) {
	        modelMap.put("error", "Không thể thực hiện tìm kiếm: " + ex.getMessage());
	        return "error";
	    }
	}


	@GetMapping("/showtimes")
	public String getShowtimesByMovieAndDate(
	        @RequestParam("movieId") int movieId,
	        @RequestParam(value = "date", required = false) String date,
	        @RequestParam(value = "brandName", required = false) String brandName,
	        ModelMap modelMap) {
	    try {
	        // Format ngày với giá trị mặc định là ngày hiện tại nếu không có
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d");
	        LocalDate showDate = (date == null || date.trim().isEmpty()) 
	            ? LocalDate.now() 
	            : LocalDate.parse(date.trim(), formatter);

	        // Lấy thông tin phim
	        Movies movie = movieService.findById(movieId);
	        if (movie == null) {
	            throw new RuntimeException("Không tìm thấy phim với ID: " + movieId);
	        }

	        // Lọc theo brandName, nếu null hoặc "Tất cả" thì lấy tất cả
	        Map<Theaters, List<MovieShowtimeScreen>> groupedShowtimes;
	        if (brandName == null || brandName.trim().isEmpty() || "Tất cả".equalsIgnoreCase(brandName)) {
	            groupedShowtimes = mssService.getGroupedShowtimesByMovieAndDate(movieId, showDate);
	        } else {
	            groupedShowtimes = mssService.getGroupedShowtimesByMovieAndDateAndBrandName(movieId, showDate, brandName.trim());
	        }

	        // Đưa dữ liệu vào model
	        modelMap.put("movie", movie);
	        modelMap.put("groupedShowtimes", groupedShowtimes);
	        modelMap.put("mss", mssService.getShowtimesByMovieAndDate(movieId, showDate));
	        return "movie/details";
	    } catch (Exception ex) {
	        ex.printStackTrace();
	        modelMap.put("error", "Không thể tải suất chiếu: " + ex.getMessage());
	        return "error";
	    }
	}

	@GetMapping("/select-seats")
	public String selectSeats(@RequestParam int showtimeId, ModelMap modelMap) {
	    try {
	        Showtimes showtime = showTimeService.findById(showtimeId);
	        if (showtime == null) {
	            throw new RuntimeException("Không tìm thấy suất chiếu.");
	        }

	        Screens screen = showtime.getMovieShowtimeScreens().stream()
	                .findFirst()
	                .map(MovieShowtimeScreen::getScreens)
	                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng chiếu."));

	        Set<Seats> seats = screen.getSeatses();
	        if (seats == null) {
	            throw new RuntimeException("Không tìm thấy ghế ngồi.");
	        }

	        // Chỉ sử dụng danh sách ghế thực tế từ cơ sở dữ liệu
	        List<Seats> allSeats = new ArrayList<>(seats);

	        // Tổ chức ghế thành hàng
	        List<List<Seats>> seatRows = organizeSeatsIntoRows(allSeats, allSeats.size()); // Sử dụng kích thước thực tế

	        Movies movie = showtime.getMovieShowtimeScreens().stream()
	                .findFirst()
	                .map(MovieShowtimeScreen::getMovies)
	                .orElseThrow(() -> new RuntimeException("Không tìm thấy phim."));

	        modelMap.put("showtime", showtime);
	        modelMap.put("screen", screen);
	        modelMap.put("movie", movie);
	        modelMap.put("seatRows", seatRows);
	        modelMap.put("posturl", environment.getProperty("paypal.posturl"));
	        modelMap.put("business", environment.getProperty("paypal.business"));
	        modelMap.put("returnurl", environment.getProperty("paypal.returnurl"));
	        return "movie/select-seats";
	    } catch (Exception ex) {
	        ex.printStackTrace();
	        modelMap.put("error", "Không thể tải trang chọn ghế!");
	        return "error";
	    }
	}
	
	@GetMapping("/booking-success")
	public String bookingSuccess(ModelMap modelMap, Authentication authentication) {
	    String email = authentication.getName();
	    Users currentUser = accountService.findByEmail(email);

	    if (currentUser != null) {
	        List<Bookings> bookings = bookingService.getBookingsByUser(currentUser);
	        if (!bookings.isEmpty()) {
	        	modelMap.put("booking", bookings.get(0));
	        }
	    }

	    return "movie/booking-success";
	}
	
	@GetMapping("/booking-history")
	public String bookingHistory(ModelMap modelMap, Authentication authentication) {
	    try {
	        // Lấy email của người dùng hiện tại
	        String email = authentication.getName();
	        if (email == null || email.isEmpty()) {
	            throw new RuntimeException("Không thể xác định người dùng hiện tại.");
	        }

	        // Gọi service để lấy lịch sử đặt vé theo email
	        List<Map<String, Object>> bookingHistory = bookingService.getBookingHistoryByEmail(email);
	        System.out.println(email);

	        // Đưa danh sách lịch sử đặt vé vào model để hiển thị trên giao diện
	        modelMap.put("bookingHistory", bookingHistory);

	        return "movie/booking-history"; // Trang lịch sử booking
	    } catch (Exception ex) {
	        ex.printStackTrace();
	        modelMap.put("error", "Có lỗi xảy ra khi tải lịch sử đặt vé: " + ex.getMessage());
	        return "error"; // Trang lỗi
	    }
	}
	
	/*Post*/
	
	@PostMapping("/confirm-seats")
	public ResponseEntity<?> confirmSeats(@RequestBody ConfirmSeatsRequest request, Authentication authentication) {
	    try {
	        if (authentication == null || !authentication.isAuthenticated()) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                    .body(Map.of("error", "Vui lòng đăng nhập để tiếp tục!"));
	        }

	        String email = authentication.getName();
	        Users currentUser = accountService.findByEmail(email);
	        if (currentUser == null) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                    .body(Map.of("error", "Người dùng không tồn tại!"));
	        }

	        if (request.getSeatIds().isEmpty()) {
	            throw new IllegalArgumentException("Danh sách ghế không được để trống!");
	        }

	        // Tạo bản ghi tạm thời
	        String bookingCode = seatService.createPendingBooking(request.getSeatIds(), currentUser.getUserId(), request.getShowtimeId());

	        // Tạo URL thanh toán PayPal với return URL đúng là /paypal-success
	        String paypalUrl = environment.getProperty("paypal.posturl") +
	                "?cmd=_xclick" +
	                "&business=" + environment.getProperty("paypal.business") +
	                "&item_name=Thanh toán vé xem phim" +
	                "&amount=" + calculateTotalPrice(request.getSeatIds()) +
	                "&currency_code=USD" +
	                "&return=" + environment.getProperty("paypal.returnurl") + "?bookingCode=" + bookingCode + // Đúng endpoint
	                "&cancel_return=" + environment.getProperty("paypal.cancelurl") + "?showtimeId=" + request.getShowtimeId() +
	                "&custom=" + bookingCode;

	        return ResponseEntity.ok().body(Map.of(
	                "message", "Chuyển hướng đến thanh toán PayPal",
	                "redirectUrl", paypalUrl
	        ));
	    } catch (Exception ex) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(Map.of("error", "Có lỗi xảy ra khi đặt ghế: " + ex.getMessage()));
	    }
	}

    private BigDecimal calculateTotalPrice(List<Integer> seatIds) {
        return seatService.calculateTotalPrice(seatIds);
    }

    @GetMapping("/paypal-success")
    public String paypalSuccess(@RequestParam("bookingCode") String bookingCode, ModelMap modelMap) {
        try {
            if (bookingCode == null || bookingCode.isEmpty()) {
                modelMap.put("error", "Mã đặt vé không hợp lệ.");
                return "error"; // Trang lỗi tùy chỉnh
            }

            // Xác nhận booking
            seatService.confirmBooking(bookingCode);
            Bookings booking = bookingsRepository.findByBarcode(bookingCode).orElse(null);
            if (booking == null) {
                modelMap.put("error", "Không tìm thấy thông tin đặt vé với mã: " + bookingCode);
                return "error";
            }

            modelMap.put("booking", booking);
            return "movie/booking-success";
        } catch (Exception ex) {
            modelMap.put("error", "Xác nhận thanh toán thất bại: " + ex.getMessage());
            return "error";
        }
    }
	
	/*Viet Them*/
    private List<List<Seats>> organizeSeatsIntoRows(List<Seats> seats, int totalSeats) {
        // Phân loại ghế thực tế (bỏ qua ghế ảo vì không còn tạo ghế ảo)
        List<Seats> realSeats = seats.stream()
                .sorted(Comparator.comparing(Seats::getSeatNumber))
                .toList();

        List<List<Seats>> seatRows = new ArrayList<>();
        int seatsPerRow = 10; // Số ghế mỗi hàng

        for (int i = 0; i < realSeats.size(); i += seatsPerRow) {
            seatRows.add(realSeats.subList(i, Math.min(i + seatsPerRow, realSeats.size())));
        }

        return seatRows;
    }
	
}
