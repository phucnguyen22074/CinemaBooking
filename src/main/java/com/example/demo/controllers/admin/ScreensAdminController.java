package com.example.demo.controllers.admin;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entities.BookingDetails;
import com.example.demo.entities.Bookings;
import com.example.demo.entities.Screens;
import com.example.demo.entities.Seats;
import com.example.demo.entities.Theaters;
import com.example.demo.serviceAdmin.ScreensService;
import com.example.demo.serviceAdmin.SeatsService;
import com.example.demo.serviceAdmin.TheatersService;

@Controller
@RequestMapping({ "admin", "admin/screens" })
public class ScreensAdminController {

	@Autowired
	private ScreensService screensService;

	@Autowired
	private TheatersService theatersService;
	
	@Autowired
	private SeatsService seatsService;

	private static final int DEFAULT_PAGE_SIZE = 10;
	private static final int MAX_SEATS_LIMIT = 100;

	@GetMapping("index")
	public String showScreens(ModelMap model, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		size = validatePageSize(size);
		Page<Screens> screensPage = screensService.findAll(page, size);
		populateIndexModel(model, screensPage, size, null, null, null);
		return "admin/screens/index";
	}

	@GetMapping("delete/{id}")
	public String deleteScreen(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
		String message = screensService.delete(id) ? "Delete Success" : "Delete Failed";
		redirectAttributes.addFlashAttribute("msg", message);
		return "redirect:/admin/screens/index";
	}

	@GetMapping("filter")
	public String filterScreens(@RequestParam(required = false) String nameScreen,
			@RequestParam(required = false) String nameTheater, @RequestParam(required = false) Integer seats,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, ModelMap model) {

		Page<Screens> screenPage = screensService.filterScreens(nameScreen, nameTheater, seats, page, size);
		populateIndexModel(model, screenPage, size, nameScreen, nameTheater, seats);
		return "admin/screens/index";
	}

	@GetMapping("{screenId}/seats")
	public String viewSeats(@PathVariable("screenId") int screenId, ModelMap model) {
	    Screens screen = screensService.findById(screenId);
	    if (screen == null) {
	        model.addAttribute("msg", "Screen not found!");
	        return "redirect:/admin/screens/index";
	    }
	    List<Seats> seatsList = seatsService.findByScreensId(screenId);
	    String screenName = screen.getName();
	   
	    model.addAttribute("seatsList", seatsList);
	    model.addAttribute("screen", screen); 
	    model.addAttribute("nameScreen", screenName);
	    return "admin/screens/seats";
	}
	
	
	@GetMapping("/seats/{seatId}/booking")
	@ResponseBody
	public Map<String, Object> getBookingDetails(@PathVariable("seatId") int seatId) {
	    Map<String, Object> response = new HashMap<>();
	    try {
	        // Tìm thông tin đặt chỗ liên quan đến ghế
	        Seats seat = seatsService.findById(seatId);
	        if (seat == null || seat.getBookingDetailses() == null || seat.getBookingDetailses().isEmpty()) {
	            response.put("message", "No booking found for this seat.");
	            return response;
	        }

	        // Lấy thông tin đặt chỗ đầu tiên (giả sử mỗi ghế chỉ có một đặt chỗ)
	        BookingDetails bookingDetails = seat.getBookingDetailses().iterator().next();
	        Bookings booking = bookingDetails.getBookings();

	        // Trả về thông tin đặt chỗ
	        response.put("bookingId", booking.getBookingId());
	        response.put("user", booking.getUsers().getFullName());
	        response.put("bookingDate", booking.getBookingDate());
	        response.put("totalPrice", booking.getTotalPrice());
	        response.put("barcode", booking.getBarcode());
	        response.put("seatNumber", seat.getSeatNumber());
	        response.put("status", seat.getStatus());
	    } catch (Exception e) {
	        response.put("message", "Error fetching booking details.");
	        e.printStackTrace();
	    }
	    return response;
	}
	
	@PostMapping("{screenId}/seats/edit")
	public String editSeat(@PathVariable("screenId") int screenId,
	                       @RequestParam("seatId") int seatId,
	                       @RequestParam("seatNumber") String seatNumber,
	                       @RequestParam("price") BigDecimal price,
	                       @RequestParam("status") byte status,
	                       RedirectAttributes redirectAttributes) {
	    try {
	        // Tìm ghế ngồi theo ID
	        Seats existingSeat = seatsService.findById(seatId);
	        if (existingSeat == null) {
	            redirectAttributes.addFlashAttribute("msg", "Seat not found!");
	            return "redirect:/admin/screens/" + screenId + "/seats";
	        }

	        // Cập nhật thông tin ghế ngồi
	        existingSeat.setSeatNumber(seatNumber);
	        existingSeat.setPrice(price);
	        existingSeat.setStatus(status);

	        // Lưu thay đổi vào cơ sở dữ liệu
	        seatsService.save(existingSeat);

	        redirectAttributes.addFlashAttribute("msg", "Seat updated successfully!");
	    } catch (Exception e) {
	        handleException(redirectAttributes, e, "Failed to update seat!");
	    }
	    return "redirect:/admin/screens/" + screenId + "/seats";
	}
	@PostMapping("save")
	public String saveScreen(@ModelAttribute("screen") Screens screen, RedirectAttributes redirectAttributes) {
		try {
			if (!validateScreenSeats(screen, redirectAttributes)) {
				return "redirect:/admin/screens/index";
			}
			updateScreenTheater(screen);
			String message = screensService.save(screen) ? "Save screen success!" : "Save screen failed!";
			redirectAttributes.addFlashAttribute("msg", message);
		} catch (Exception e) {
			handleException(redirectAttributes, e, "Save process failed!");
		}
		return "redirect:/admin/screens/index";
	}

	@PostMapping("edit")
	public String updateScreen(@ModelAttribute("Screens") Screens screen, RedirectAttributes redirectAttributes) {
		try {
			Screens existingScreen = screensService.findById(screen.getScreenId());
			if (existingScreen == null) {
				redirectAttributes.addFlashAttribute("msg", "Screen not found!");
				return "redirect:/admin/screens/index";
			}
			if (!validateScreenSeats(screen, redirectAttributes)) {
				return "redirect:/admin/screens/index";
			}
			updateExistingScreen(existingScreen, screen);
			String message = screensService.save(existingScreen) ? "Save screen success!" : "Save screen failed!";
			redirectAttributes.addFlashAttribute("msg", message);
		} catch (Exception e) {
			handleException(redirectAttributes, e, "Update process failed!");
		}
		return "redirect:/admin/screens/index";
	}

	// Helper methods
	private int validatePageSize(int size) {
		return size <= 0 ? DEFAULT_PAGE_SIZE : size;
	}

	private boolean validateScreenSeats(Screens screen, RedirectAttributes redirectAttributes) {
		int totalSeats = screen.getTotalSeats();
		if (totalSeats <= 0 || totalSeats > MAX_SEATS_LIMIT) {
			redirectAttributes.addFlashAttribute("msg", "Total seats must be between 1 and " + MAX_SEATS_LIMIT + "!");
			return false;
		}
		return true;
	}

	private void populateIndexModel(ModelMap model, Page<Screens> screensPage, int size, String nameScreen,
			String nameTheater, Integer seats) {
		model.addAttribute("screens", screensPage.getContent());
		model.addAttribute("currentPage", screensPage.getNumber());
		model.addAttribute("totalPages", screensPage.getTotalPages());
		model.addAttribute("totalItems", screensPage.getTotalElements());
		model.addAttribute("size", size);
		model.addAttribute("screen", new Screens());
		model.addAttribute("theaters", theatersService.findAll());
		model.addAttribute("nameScreen", nameScreen);
		model.addAttribute("nameTheater", nameTheater);
		model.addAttribute("seats", seats);
	}

	private void updateScreenTheater(Screens screen) {
		if (screen.getTheaters() != null && screen.getTheaters().getTheaterId() != null) {
			Theaters theater = theatersService.findById(screen.getTheaters().getTheaterId());
			screen.setTheaters(theater);
		}
	}

	private void updateExistingScreen(Screens existing, Screens updated) {
		existing.setName(updated.getName());
		existing.setTotalSeats(updated.getTotalSeats());
		if (updated.getTheaters() != null && updated.getTheaters().getTheaterId() != null) {
			Theaters theater = theatersService.findById(updated.getTheaters().getTheaterId());
			existing.setTheaters(theater != null ? theater : null);
		}
	}
	
	
	private void handleException(RedirectAttributes redirectAttributes, Exception e, String defaultMessage) {
		e.printStackTrace();
		redirectAttributes.addFlashAttribute("msg", defaultMessage);
	}
}