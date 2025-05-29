package com.example.demo.controllers.admin;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entities.BookingDetails;
import com.example.demo.entities.Bookings;
import com.example.demo.entities.MovieShowtimeScreen;
import com.example.demo.entities.Movies;
import com.example.demo.entities.Seats;
import com.example.demo.entities.Showtimes;
import com.example.demo.entities.Users;
import com.example.demo.serviceAdmin.BookingService;
import com.example.demo.serviceAdmin.SeatsService;
import com.example.demo.serviceAdmin.ShowTimeService;
import com.example.demo.serviceAdmin.UsersService;

@Controller
@RequestMapping({ "admin", "admin/booking" })
public class BookingAdminController {

	@Autowired
	private BookingService bookingService;

	@Autowired
	private UsersService usersService;

	@Autowired
	private SeatsService seatsService;
	
	@Autowired
	private ShowTimeService showTimeService;

	@GetMapping({"index"})
	public String viewBookings(ModelMap modelMap, 
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size, 
	        Integer selectedBookingId) {
	    
	    Page<Bookings> bookingPage = bookingService.findAll(page, size);
	    modelMap.put("bookings", bookingPage);
	    modelMap.addAttribute("currentPage", bookingPage.getNumber());
	    modelMap.addAttribute("totalPages", bookingPage.getTotalPages());
	    modelMap.addAttribute("totalItems", bookingPage.getTotalElements());
	    modelMap.addAttribute("users", usersService.findAll());
	    modelMap.addAttribute("booking", new Bookings());
	    modelMap.addAttribute("selectedBookingId", selectedBookingId);
	    modelMap.addAttribute("seats", seatsService.findAll());
	    
	    MovieShowtimeScreen movieShowtimeScreen = showTimeService.getShowtimeDetails(selectedBookingId)
	            .getMovieShowtimeScreens()
	            .stream()
	            .findFirst()
	            .orElse(null);
	    
	    String movieTitle = "N/A"; // Default value if no movie is found
	    if (movieShowtimeScreen != null) {
	        Movies movie = movieShowtimeScreen.getMovies();
	        if (movie != null) {
	            movieTitle = movie.getTitle();
	        }
	    }
	    modelMap.addAttribute("movieTitle", movieTitle);
	    
	    return "admin/booking/index";
	}

	@GetMapping("delete/{id}")
	public String deleteBooking(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
		String message = bookingService.delete(id) ? "Delete Success" : "Delete Failed";
		redirectAttributes.addFlashAttribute("msg", message);
		return "redirect:/admin/booking/index";
	}

	@PostMapping("edit")
	public String editBooking(@ModelAttribute("booking") Bookings booking, ModelMap modelMap,
			RedirectAttributes redirectAttributes, @RequestParam("seats.seatId") Integer seatId,
			@RequestParam("showDate") String showDate, @RequestParam("startTime") String startTime,
			@RequestParam("endTime") String endTime) {
		try {
			Bookings existingBooking = bookingService.findById(booking.getBookingId());
			Seats seat = seatsService.findById(seatId);
			if (booking.getBookingId() == null) {
				redirectAttributes.addFlashAttribute("msg", "not Found BookingId");
			}
			existingBooking.setBookingDate(booking.getBookingDate());
			existingBooking.setTotalPrice(booking.getTotalPrice());
			if (booking.getUsers() != null) {
				existingBooking.setUsers(booking.getUsers());
			}
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

			BookingDetails bookingDetails = existingBooking.getBookingDetailses().stream().findFirst()
					.orElse(new BookingDetails());
			modelMap.addAttribute("bookingdetail", bookingDetails);
			bookingDetails.setSeats(seat);
			
			Showtimes showtime = bookingDetails.getShowtimes();
			if (showtime == null) {
				showtime = new Showtimes();
			}
			
			
			showtime.setShowDate(dateFormat.parse(showDate));
			showtime.setStartTime(timeFormat.parse(startTime));
			showtime.setEndTime(timeFormat.parse(endTime));

			bookingDetails.setShowtimes(showtime);
			existingBooking.getBookingDetailses().clear();
			existingBooking.getBookingDetailses().add(bookingDetails);

			if (bookingService.save(existingBooking)) {
				redirectAttributes.addFlashAttribute("msg", "update Booking Success");
			} else {
				redirectAttributes.addFlashAttribute("msg", "update Booking Failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/admin/booking/index";
	}
}
