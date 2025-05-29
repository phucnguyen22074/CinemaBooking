package com.example.demo.controllers.admin;

import java.util.List;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entities.Brands;
import com.example.demo.entities.Locations;
import com.example.demo.entities.Movies;
import com.example.demo.entities.Theaters;
import com.example.demo.serviceAdmin.BrandsService;
import com.example.demo.serviceAdmin.GenresService;
import com.example.demo.serviceAdmin.LocationsService;
import com.example.demo.serviceAdmin.MoviesService;
import com.example.demo.serviceAdmin.TheatersService;

@Controller
@RequestMapping({"admin", "admin/theaters"})
public class TheatersAdminController {

    @Autowired
    private TheatersService theatersService;

    @Autowired
    private BrandsService brandsService;

    @Autowired
    private LocationsService locationsService;

    @Autowired
    private MoviesService moviesService;

    @Autowired
    private GenresService genresService;

    private static final int DEFAULT_PAGE_SIZE = 10;

    @GetMapping("index")
    public String showIndex(ModelMap model, 
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size) {
        size = validatePageSize(size);
        Page<Theaters> theatersPage = theatersService.getTheaters(page, size);
        populateIndexModel(model, theatersPage, null, null, size);
        return "admin/theaters/index";
    }

    @GetMapping("filter")
    public String filterTheaters(@RequestParam(required = false) Integer brandId,
                                 @RequestParam(required = false) Integer locationId,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 ModelMap model) {
        size = validatePageSize(size);
        Page<Theaters> theatersPage = getFilteredTheaters(brandId, locationId, page, size);
        populateIndexModel(model, theatersPage, brandId, locationId, size);
        return "admin/theaters/index";
    }

    @GetMapping("delete/{id}")
    public String deleteTheater(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        String message = theatersService.detele(id) ? "Delete Success" : "Delete Failed";
        redirectAttributes.addFlashAttribute("msg", message);
        return "redirect:/admin/theaters/index";
    }

    @GetMapping("edit/{id}")
    public String showEditForm(@PathVariable("id") int id, ModelMap model) {
        Theaters theater = theatersService.findById(id);
        if (theater == null) {
            return "redirect:/admin/theaters/index";
        }
        model.put("theater", theater);
        model.put("brands", brandsService.findAll());
        model.put("locations", locationsService.findAll());
        return "admin/theaters/edit";
    }

    @PostMapping("save")
    public String saveTheater(@ModelAttribute("Theaters") Theaters theaters, 
                             RedirectAttributes redirectAttributes) {
        try {
            updateTheaterRelations(theaters);
            String message = theatersService.save(theaters) ? "Save theaters success!" : "Save theaters failed!";
            redirectAttributes.addFlashAttribute("msg", message);
        } catch (Exception e) {
            handleException(redirectAttributes, e, "Save process failed!");
        }
        return "redirect:/admin/theaters/index";
    }

    @PostMapping("edit")
    public String updateTheater(@ModelAttribute("Theaters") Theaters theaters, 
                               RedirectAttributes redirectAttributes) {
        try {
            Theaters existingTheater = theatersService.findById(theaters.getTheaterId());
            if (existingTheater == null) {
                redirectAttributes.addFlashAttribute("msg", "Theater not found!");
                return "redirect:/admin/theaters/index";
            }
            updateExistingTheater(existingTheater, theaters);
            String message = theatersService.save(existingTheater) ? "Save theater successfully!" : "Failed to save theater!";
            redirectAttributes.addFlashAttribute("msg", message);
        } catch (Exception e) {
            handleException(redirectAttributes, e, "Update process failed!");
        }
        return "redirect:/admin/theaters/index";
    }

    @GetMapping("{theaterId}/movies/filter")
    public String filterMoviesByTheater(@PathVariable("theaterId") int theaterId,
                                        @RequestParam(value = "keyword", defaultValue = "") String keyword,
                                        @RequestParam(required = false) Integer genreId,
                                        @RequestParam(required = false) Boolean status,
                                        @RequestParam(required = false) String durationRange,
                                        @RequestParam(required = false) Integer year,
                                        @RequestParam(required = false) String rating,
                                        ModelMap model) {
        Theaters theater = theatersService.findById(theaterId);
        if (theater == null) {
            return "redirect:/admin/theaters/index?error=InvalidTheaterId";
        }
        populateMoviesFilterModel(model, theaterId, keyword, genreId, status, durationRange, year, rating);
        return "admin/theaters/movie";
    }

    @GetMapping("{id}/movies")
    public String showMoviesByTheater(@PathVariable("id") int id, ModelMap model) {
        Theaters theater = theatersService.findById(id);
        if (theater == null) {
            return "redirect:/admin/theaters/index";
        }
        List<Movies> movies = moviesService.filterMoviesByTheater(null, null, null, null, null, null, null, id);
        populateMoviesModel(model, theater, movies);
        return "admin/theaters/movie";
    }

    // Helper methods
    private int validatePageSize(int size) {
        return size <= 0 ? DEFAULT_PAGE_SIZE : size;
    }

    private void populateIndexModel(ModelMap model, Page<Theaters> theatersPage, 
                                   Integer brandId, Integer locationId, int size) {
        model.addAttribute("theaters", theatersPage.getContent());
        model.addAttribute("brands", theatersService.getAllBrands());
        model.addAttribute("locations", theatersService.getAllLocations());
        model.addAttribute("selectedBrandId", brandId);
        model.addAttribute("selectedLocationId", locationId);
        model.addAttribute("currentPage", theatersPage.getNumber());
        model.addAttribute("totalPages", theatersPage.getTotalPages());
        model.addAttribute("totalItems", theatersPage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("theater", new Theaters());
    }

    private Page<Theaters> getFilteredTheaters(Integer brandId, Integer locationId, int page, int size) {
        if (brandId != null && locationId != null) {
            return theatersService.getTheatersByBrandAndLocation(brandId, locationId, page, size);
        } else if (brandId != null) {
            return theatersService.getTheatersByBrand(brandId, page, size);
        } else if (locationId != null) {
            return theatersService.getTheatersByLocation(locationId, page, size);
        }
        return theatersService.getTheaters(page, size);
    }

    private void updateTheaterRelations(Theaters theaters) {
        if (theaters.getBrands() != null && theaters.getBrands().getBrandId() != null) {
            theaters.setBrands(brandsService.findById(theaters.getBrands().getBrandId()));
        }
        if (theaters.getLocations() != null && theaters.getLocations().getLocationId() != null) {
            theaters.setLocations(locationsService.findById(theaters.getLocations().getLocationId()));
        }
    }

    private void updateExistingTheater(Theaters existing, Theaters updated) {
        if (updated.getName() != null && !updated.getName().isEmpty()) {
            existing.setName(updated.getName());
        }
        if (updated.getAddress() != null && !updated.getAddress().isEmpty()) {
            existing.setAddress(updated.getAddress());
        }
        if (updated.getBrands() != null && updated.getBrands().getBrandId() != null) {
            Brands brand = brandsService.findById(updated.getBrands().getBrandId());
            existing.setBrands(brand != null ? brand : null);
        }
        if (updated.getLocations() != null && updated.getLocations().getLocationId() != null) {
            Locations location = locationsService.findById(updated.getLocations().getLocationId());
            existing.setLocations(location != null ? location : null);
        }
    }

    private void handleException(RedirectAttributes redirectAttributes, Exception e, String defaultMessage) {
        e.printStackTrace();
        redirectAttributes.addFlashAttribute("msg", defaultMessage);
    }

    private void populateMoviesFilterModel(ModelMap model, int theaterId, String keyword, Integer genreId,
                                           Boolean status, String durationRange, Integer year, String rating) {
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
        List<Movies> movies = moviesService.filterMoviesByTheater(genreId, keyword, status, year, genreId, year, rating, theaterId);
        model.addAttribute("movies", movies);
        model.addAttribute("selectedGenreId", genreId);
        model.addAttribute("theaterId", theaterId);
        model.addAttribute("minDuration", minDuration);
        model.addAttribute("maxDuration", maxDuration);
        model.addAttribute("releaseYear", year);
        model.addAttribute("rating", rating);
        model.addAttribute("isTheatersController", true);
        model.addAttribute("movie", new Movies());
    }

    private void populateMoviesModel(ModelMap model, Theaters theater, List<Movies> movies) {
        model.addAttribute("theater", theater);
        model.addAttribute("theaterId", theater.getTheaterId());
        model.addAttribute("movies", movies);
        model.addAttribute("isTheatersController", true);
        model.addAttribute("isMoviesController", false);
        model.addAttribute("allGenres", genresService.findAll());
        model.addAttribute("movie", new Movies());
    }
}