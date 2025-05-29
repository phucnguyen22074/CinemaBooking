package com.example.demo.controllers.admin;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

import com.example.demo.entities.MovieShowtimeScreen;
import com.example.demo.entities.Movies;
import com.example.demo.entities.Screens;
import com.example.demo.entities.Showtimes;
import com.example.demo.serviceAdmin.MovieShowtimeScreenService;
import com.example.demo.serviceAdmin.MoviesService;
import com.example.demo.serviceAdmin.ScreensService;
import com.example.demo.serviceAdmin.ShowTimeService;

@Controller
@RequestMapping({ "admin", "admin/showtime" })
public class ShowTimesAdminController {

	@Autowired
	private ShowTimeService showTimeService;

	@Autowired
	private MoviesService moviesService;
	
	@Autowired
	private ScreensService screensService;

	@Autowired
	private MovieShowtimeScreenService movieShowtimeScreenService;

	@GetMapping({ "index" })
	public String viewShowtimes(ModelMap modelMap, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,Integer selectedShowtimeId) {
		Page<Showtimes> showtimePage = showTimeService.findAll(page, size);
		modelMap.put("showtimes", showtimePage);
		modelMap.addAttribute("currentPage", showtimePage.getNumber());
		modelMap.addAttribute("totalPages", showtimePage.getTotalPages());
		modelMap.addAttribute("totalItems", showtimePage.getTotalElements());
		modelMap.addAttribute("movies", moviesService.findAll());
		modelMap.addAttribute("showtime", new Showtimes());
		modelMap.addAttribute("selectedShowtimeId", selectedShowtimeId);
		modelMap.addAttribute("screens", screensService.findAllSize());
		return "admin/showtime/index";
	}
	
	 @GetMapping("delete/{id}")
	    public String deleteBrand(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
	        String message = showTimeService.delete(id) ? "Delete Success" : "Delete Failed";
	        redirectAttributes.addFlashAttribute("msg", message);
	        return "redirect:/admin/showtime/index";
	    }

	
	@PostMapping("save")
	public String saveShowtimes(@ModelAttribute("showtime") Showtimes showtime,
	                            @RequestParam("movieId") Integer movieId,
	                            @RequestParam("screenId") Integer screenId,
	                            RedirectAttributes redirectAttributes) {
	    try {
	        Movies movie = moviesService.findById(movieId);
	        Screens screen = screensService.findById(screenId);

	        if (movie == null || screen == null) {
	            redirectAttributes.addFlashAttribute("msg", "Movie or screen does not exist.");
	            return "redirect:/admin/showtime/index";
	        }
	        if(showtime.getIsDeleted() == null) {
	        	showtime.setIsDeleted(false);
	        }
	        showTimeService.save(showtime);

	        // Tạo & lưu MovieShowtimeScreen
	        MovieShowtimeScreen mss = new MovieShowtimeScreen();
	        mss.setMovies(movie);
	        mss.setScreens(screen);
	        mss.setShowtimes(showtime);

	        movieShowtimeScreenService.save(mss);
	        
	        redirectAttributes.addFlashAttribute("msg", "Showtime added successfully.");
	    } catch (Exception e) {
	        redirectAttributes.addFlashAttribute("msg", "Error: " + e.getMessage());
	    }
	    return "redirect:/admin/showtime/index";
	}


	@PostMapping({ "edit" })
	public String editShowtimes(ModelMap modelMap, 
	                            @ModelAttribute("Showtimes") Showtimes showtime,
	                            @ModelAttribute("MovieShowtimeScreen") MovieShowtimeScreen movieShowtimeScreen,
	                            RedirectAttributes redirectAttributes, 
	                            @RequestParam("movies.movieId") int movieId,
	                            @RequestParam("screens.screenId") int screenId) {
	    try {
	        Showtimes existingShowtime = showTimeService.findById(showtime.getShowtimeId());
	        if (existingShowtime == null) {
	            redirectAttributes.addFlashAttribute("msg", "Not found ShowtimeId");
	            return "redirect:/admin/showtime/index";
	        }

	        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
	        existingShowtime.setShowDate(dateFormat.parse(dateFormat.format(showtime.getShowDate())));
	        existingShowtime.setStartTime(timeFormat.parse(timeFormat.format(showtime.getStartTime())));
	        existingShowtime.setEndTime(timeFormat.parse(timeFormat.format(showtime.getEndTime())));

	        Movies movie = moviesService.findById(movieId);
	        Screens screen = screensService.findById(screenId);

	        if (movie != null && screen != null) {
	            // Tìm hoặc tạo mới MovieShowtimeScreen
	            MovieShowtimeScreen mvs = existingShowtime.getMovieShowtimeScreens().stream().findFirst()
	                    .orElse(new MovieShowtimeScreen());

	            mvs.setMovies(movie);
	            mvs.setScreens(screen);
	            mvs.setShowtimes(existingShowtime);
	            
	            existingShowtime.getMovieShowtimeScreens().clear();
	            existingShowtime.getMovieShowtimeScreens().add(mvs);
	        } else {
	            redirectAttributes.addFlashAttribute("msg", "Movie or Screen not found");
	            return "redirect:/admin/showtime/index";
	        }

	        if (showTimeService.save(existingShowtime)) {
	            redirectAttributes.addFlashAttribute("msg", "Update Success");
	            return "redirect:/admin/showtime/index";
	        } else {
	            redirectAttributes.addFlashAttribute("msg", "Update Failed");
	            return "redirect:/admin/showtime/index";
	        }
	    } catch (Exception e) {
	        handleException(redirectAttributes, e, "Update Failed");
	    }
	    return "redirect:/admin/showtime/index";
	}

	private void handleException(RedirectAttributes redirectAttributes, Exception e, String defaultMessage) {
		e.printStackTrace();
		redirectAttributes.addFlashAttribute("msg", defaultMessage);
	}
}
