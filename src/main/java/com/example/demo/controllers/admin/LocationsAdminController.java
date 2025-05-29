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

import com.example.demo.entities.Locations;
import com.example.demo.serviceAdmin.LocationsService;

@Controller
@RequestMapping({"admin", "admin/locations"})
public class LocationsAdminController {

    @Autowired
    private LocationsService locationsService;

    private static final int DEFAULT_PAGE_SIZE = 10;

    @GetMapping("index")
    public String showIndex(ModelMap model,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size) {
        size = validatePageSize(size);
        Page<Locations> locationPage = locationsService.getLocations(page, size);
        List<Locations> allLocations = (List<Locations>) locationsService.findAll();
        populateIndexModel(model, locationPage, allLocations, null, size);
        return "admin/locations/index";
    }

    @GetMapping("filter")
    public String filterLocations(@RequestParam(required = false) Integer locationId,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 ModelMap model) {
        size = validatePageSize(size);
        List<Locations> allLocations = (List<Locations>) locationsService.findAll();
        Page<Locations> filteredLocationsPage = getFilteredLocations(locationId, page, size);
        populateIndexModel(model, filteredLocationsPage, allLocations, locationId, size);
        return "admin/locations/index";
    }

    @GetMapping("delete/{id}")
    public String deleteLocation(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        String message = locationsService.detele(id) ? "Delete Success" : "Delete Failed";
        redirectAttributes.addFlashAttribute("msg", message);
        return "redirect:/admin/locations/index";
    }

    @GetMapping("edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, ModelMap model) {
        Locations location = locationsService.findById(id);
        if (location == null) {
            return "redirect:/admin/locations/index";
        }
        model.addAttribute("location", location);
        return "admin/locations/edit";
    }

    @PostMapping("save")
    public String saveLocation(@ModelAttribute("location") Locations location,
                              RedirectAttributes redirectAttributes) {
        try {
            String message = locationsService.save(location) 
                ? "Save location success!" 
                : "Save location failed!";
            redirectAttributes.addFlashAttribute("msg", message);
        } catch (Exception e) {
            handleException(redirectAttributes, e, "Save process failed!");
        }
        return "redirect:/admin/locations/index";
    }

    @PostMapping("edit")
    public String updateLocation(@RequestParam("id") Integer id,
                                @RequestParam("city") String city,
                                RedirectAttributes redirectAttributes) {
        try {
            Locations location = locationsService.findById(id);
            if (location == null) {
                redirectAttributes.addFlashAttribute("msg", "Location not found!");
                return "redirect:/admin/locations/index";
            }
            if (city == null || city.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("msg", "City cannot be empty!");
                return "redirect:/admin/locations/index";
            }
            location.setCity(city);
            String message = locationsService.save(location) 
                ? "Location updated successfully!" 
                : "Failed to update location!";
            redirectAttributes.addFlashAttribute("msg", message);
        } catch (Exception e) {
            handleException(redirectAttributes, e, "Update process failed!");
        }
        return "redirect:/admin/locations/index";
    }

    // Helper methods
    private int validatePageSize(int size) {
        return size <= 0 ? DEFAULT_PAGE_SIZE : size;
    }

    private void populateIndexModel(ModelMap model, Page<Locations> locationPage, 
                                   List<Locations> allLocations, Integer selectedLocationId, int size) {
        model.addAttribute("locations", locationPage.getContent());
        model.addAttribute("allLocations", allLocations);
        model.addAttribute("selectedLocationId", selectedLocationId);
        model.addAttribute("currentPage", locationPage.getNumber());
        model.addAttribute("totalPages", locationPage.getTotalPages());
        model.addAttribute("totalItems", locationPage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("location", new Locations());
    }

    private Page<Locations> getFilteredLocations(Integer locationId, int page, int size) {
        if (locationId != null && locationId > 0) {
            return locationsService.filterLocations(locationId, page, size);
        }
        return locationsService.getLocations(page, size);
    }

    private void handleException(RedirectAttributes redirectAttributes, Exception e, String defaultMessage) {
        e.printStackTrace();
        redirectAttributes.addFlashAttribute("msg", defaultMessage);
    }
}