package com.example.demo.controllers.admin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entities.Actor;
import com.example.demo.entities.Director;
import com.example.demo.entities.Genres;
import com.example.demo.entities.Movies;
import com.example.demo.entities.Theaters;
import com.example.demo.helper.FileHelper;
import com.example.demo.serviceAdmin.ActorService;
import com.example.demo.serviceAdmin.DirectorService;
import com.example.demo.serviceAdmin.GenresService;
import com.example.demo.serviceAdmin.MoviesService;
import com.example.demo.serviceAdmin.TheatersService;

@Controller
@RequestMapping({ "admin", "admin/movies" })
public class MoviesAdminController {

	@Autowired
	private MoviesService moviesService;

	@Autowired
	private ActorService actorService;

	@Autowired
	private DirectorService directorService;

	@Autowired
	private GenresService genresService;

	@Autowired
	private TheatersService theatersService;

	@GetMapping({ "", "/", "index" })
	public String index(ModelMap modelMap) {
		modelMap.addAttribute("isMoviesController", true);
		modelMap.put("movies", moviesService.findAll());

		// Add Model Form
		modelMap.addAttribute("movie", new Movies());
		modelMap.addAttribute("allActors", actorService.findAll());
		modelMap.addAttribute("allDirectors", directorService.findAll());
		modelMap.addAttribute("allGenres", genresService.findAll());
		modelMap.addAttribute("allTheaters", theatersService.findAll());
		return "admin/theaters/movie";
	}

	@GetMapping("edit/{id}")
	public String editBrand(@PathVariable("id") Integer id, ModelMap modelMap) {
		// Tìm brand theo ID
		Movies movie = moviesService.findById(id);
		if (movie == null) {
			return "redirect:/admin/movies/index";
		}

		// Truyền brand và danh sách users vào model
		modelMap.addAttribute("movie", movie);
		modelMap.addAttribute("allActors", actorService.findAll());
		modelMap.addAttribute("allDirectors", directorService.findAll());
		modelMap.addAttribute("allGenres", genresService.findAll());
		modelMap.addAttribute("allTheaters", theatersService.findAll());

		return "admin/movies/index"; // Trả về view edit.html
	}

	@GetMapping("delete/{id}")
	public String delete(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
		String message = moviesService.detele(id) ? "Delete Success" : "Delete Failed";
		redirectAttributes.addFlashAttribute("msg", message);
		return "redirect:/admin/movies/index";
	}

	@GetMapping("filter")
	public String searchUsers(@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
			@RequestParam(value = "genreId", required = false) Integer genreId,
			@RequestParam(value = "status", required = false) Boolean status,
			@RequestParam(value = "durationRange", required = false) String durationRange,
			@RequestParam(value = "releaseYear", required = false) Integer year,
			@RequestParam(value = "rating", required = false) String rating,
			@RequestParam(value = "isLinkedToTheater", required = false) Boolean isLinkedToTheater,
			ModelMap modelMap) {
		Integer minDuration = null;
		Integer maxDuration = null;
		if ("short".equals(durationRange)) {
			maxDuration = 90;
		} else if ("medium".equals(durationRange)) {
			minDuration = 90;
			maxDuration = 120;
		} else if ("long".equals(durationRange)) {
			minDuration = 120;
		}
		// Filter movies by criteria
		 List<Movies> movies = moviesService.filterMovies(genreId, keyword, status, minDuration, maxDuration, year, rating);
	    
		// Add data to the model
		modelMap.addAttribute("movies", movies);
		modelMap.put("movie", new Movies());
		modelMap.addAttribute("allActors", actorService.findAll());
		modelMap.addAttribute("allDirectors", directorService.findAll());
		modelMap.addAttribute("allGenres", genresService.findAll());
		modelMap.addAttribute("allTheaters", theatersService.findAll());
		// Keep selected state
		modelMap.addAttribute("selectedGenreId", genreId);
		modelMap.addAttribute("minDuration", minDuration);
		modelMap.addAttribute("maxDuration", maxDuration);
		modelMap.addAttribute("releaseYear", year);
	    modelMap.addAttribute("rating", rating);
		// Add flags to distinguish controllers
	    modelMap.addAttribute("isMoviesController", true);
	    modelMap.addAttribute("isTheaterController", false);
		return "admin/theaters/movie";
	}

	// POST
	@PostMapping("save")
	public String saveMovie(@ModelAttribute("movie") Movies movie, @RequestParam List<Integer> actorIds,
	        @RequestParam List<Integer> directorIds, @RequestParam List<Integer> genreIds,
	        @RequestParam List<Integer> theaterIds, @RequestParam("file") MultipartFile file,
	        RedirectAttributes redirectAttributes) {
	    try {
	        if (!file.isEmpty()) {
	            String fileName = FileHelper.generateFileName(file.getOriginalFilename());
	            File imageFolder = new ClassPathResource("static/assets/photo/").getFile();
	            Path path = Paths.get(imageFolder.getAbsolutePath() + File.separator + fileName);
	            System.out.println("path: " + path.toString());
	            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
	            movie.setImageUrl(fileName);
	        } else {
	            movie.setImageUrl(movie.getImageUrl());
	        }

	        // Set basic movie information
	        movie.setTitle(movie.getTitle());
	        movie.setDescription(movie.getDescription());
	        movie.setDuration(movie.getDuration());
	        movie.setReleaseDate(movie.getReleaseDate());
	        String trailerUrl = movie.getTrailerUrl();
	        String videoId = extractVideoIdFromUrl(trailerUrl);
	        if (videoId != null) {
	            movie.setTrailerUrl(videoId); // Save the video ID to the database
	        } else {
	            redirectAttributes.addFlashAttribute("msg", "Invalid YouTube URL!");
	            return "redirect:/admin/movies/index";
	        }
	        movie.setStatus(movie.getStatus());
	        movie.setRating(movie.getRating());

	        // Convert Sets to Lists for relationships
	        movie.setActors(new ArrayList<>(actorService.findByIds(actorIds)));
	        movie.setDirectors(new ArrayList<>(directorService.findByIds(directorIds)));
	        movie.setGenreses(new ArrayList<>(genresService.findByIds(genreIds)));
	        movie.setTheaterses(new ArrayList<>(theatersService.findByIds(theaterIds)));

	        // Save the movie
	        if (moviesService.save(movie)) {
	            redirectAttributes.addFlashAttribute("msg", "Save movie success!");
	            return "redirect:/admin/movies/index";
	        } else {
	            redirectAttributes.addFlashAttribute("msg", "Save movie failed!");
	            return "redirect:/admin/movies/index";
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        redirectAttributes.addFlashAttribute("msg", "An error occurred during the save process!");
	        return "redirect:/admin/movies/index";
	    }
	}

	@PostMapping("/edit")
	public String editMovie(@RequestParam("movieId") Integer movieId, @RequestParam("title") String title,
	        @RequestParam("duration") Integer duration, @RequestParam("description") String description,
	        @RequestParam("releaseDate") String releaseDate, @RequestParam("status") Boolean status,
	        @RequestParam("trailerUrl") String trailerUrl, @RequestParam("rating") Double rating,
	        @RequestParam("genreIds") List<Integer> genreIds, @RequestParam("actorIds") List<Integer> actorIds,
	        @RequestParam("directorIds") List<Integer> directorIds,
	        @RequestParam("theaterIds") List<Integer> theaterIds,
	        @RequestParam(value = "file", required = false) MultipartFile file, RedirectAttributes redirectAttributes)
	        throws IOException, ParseException {

	    Movies existingMovie = moviesService.findById(movieId);
	    if (existingMovie == null) {
	        redirectAttributes.addFlashAttribute("error", "Movie not found!");
	        return "redirect:/admin/movies";
	    }

	    existingMovie.setTitle(title);
	    existingMovie.setDuration(duration);
	    existingMovie.setDescription(description);
	    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
	    existingMovie.setReleaseDate(simpleDateFormat.parse(releaseDate));
	    existingMovie.setStatus(status);
	    existingMovie.setTrailerUrl(trailerUrl);
	    existingMovie.setRating(rating);

	    if (!file.isEmpty()) {
	        String fileName = FileHelper.generateFileName(file.getOriginalFilename());
	        File imageFolder = new ClassPathResource("static/assets/photo/").getFile();
	        Path path = Paths.get(imageFolder.getAbsolutePath() + File.separator + fileName);
	        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
	        existingMovie.setImageUrl(fileName);
	    }

	    // Convert Sets to Lists
	    existingMovie.setGenreses(new ArrayList<>(genresService.findByIds(genreIds)));
	    existingMovie.setActors(new ArrayList<>(actorService.findByIds(actorIds)));
	    existingMovie.setDirectors(new ArrayList<>(directorService.findByIds(directorIds)));
	    existingMovie.setTheaterses(new ArrayList<>(theatersService.findByIds(theaterIds)));

	    if (moviesService.save(existingMovie)) {
	        redirectAttributes.addFlashAttribute("msg", "Movie updated successfully!");
	        return "redirect:/admin/movies/index";
	    } else {
	        redirectAttributes.addFlashAttribute("msg", "Movie update failed!");
	        return "redirect:/admin/movies/index";
	    }
	}

	private boolean isValidVideoId(String input) {
		// Kiểm tra nếu input chỉ chứa chữ cái, số, dấu gạch ngang và dấu gạch dưới
		return input.matches("^[a-zA-Z0-9_-]{11}$");
	}

	private String extractVideoIdFromUrl(String url) {
		if (isValidVideoId(url)) {
			return url; // Đây là ID video hợp lệ
		}

		// Xử lý URL như bình thường
		if (url.contains("v=")) {
			String[] parts = url.split("v=");
			if (parts.length > 1) {
				String videoId = parts[1];
				int ampersandIndex = videoId.indexOf('&');
				if (ampersandIndex != -1) {
					videoId = videoId.substring(0, ampersandIndex);
				}
				return videoId;
			}
		}

		if (url.contains("youtu.be/")) {
			String[] parts = url.split("youtu.be/");
			if (parts.length > 1) {
				String videoId = parts[1];
				int questionMarkIndex = videoId.indexOf('?');
				if (questionMarkIndex != -1) {
					videoId = videoId.substring(0, questionMarkIndex);
				}
				return videoId;
			}
		}

		return null;
	}
}
