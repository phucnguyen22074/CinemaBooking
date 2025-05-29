package com.example.demo.controllers.admin;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

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

import com.example.demo.entities.Brands;
import com.example.demo.entities.Users;
import com.example.demo.helper.FileHelper;
import com.example.demo.serviceAdmin.BrandsService;
import com.example.demo.serviceAdmin.UsersService;

@Controller
@RequestMapping({"admin", "admin/brands"})
public class BrandsAdminController {

    @Autowired
    private BrandsService brandsService;

    @Autowired
    private UsersService usersService;

    private static final String DEFAULT_IMAGE = "No_image2.png";
    private static final String IMAGE_FOLDER = "static/assets/images/brands/";

    @GetMapping("index")
    public String showIndex(ModelMap model) {
        List<Brands> allBrands = (List<Brands>) brandsService.findAll();
        populateIndexModel(model, allBrands, allBrands, null);
        return "admin/brands/index";
    }

    @GetMapping("filter")
    public String filterBrands(@RequestParam(required = false) Integer brandId, ModelMap model) {
        List<Brands> allBrands = (List<Brands>) brandsService.findAll();
        List<Brands> filteredBrands = (brandId != null && brandId > 0) 
            ? brandsService.filterBrands(brandId) 
            : allBrands;
        populateIndexModel(model, allBrands, filteredBrands, brandId);
        return "admin/brands/index";
    }

    @GetMapping("delete/{id}")
    public String deleteBrand(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        String message = brandsService.detele(id) ? "Delete Success" : "Delete Failed";
        redirectAttributes.addFlashAttribute("msg", message);
        return "redirect:/admin/brands/index";
    }

    @GetMapping("edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, ModelMap model) {
        Brands brand = brandsService.findById(id);
        if (brand == null) {
            return "redirect:/admin/brands/index";
        }
        if (brand.getUsers() == null) {
            brand.setUsers(new Users());
        }
        model.addAttribute("brand", brand);
        model.addAttribute("users", usersService.findAll());
        return "admin/brands/edit";
    }

    @PostMapping("save")
    public String saveBrand(@ModelAttribute("Brands") Brands brand,
                           @RequestParam("file") MultipartFile file,
                           RedirectAttributes redirectAttributes) {
        try {
            updateBrandImage(brand, file, redirectAttributes);
            updateBrandUser(brand, redirectAttributes);
            String message = brandsService.save(brand) ? "Save brand success!" : "Save brand failed!";
            redirectAttributes.addFlashAttribute("msg", message);
        } catch (Exception e) {
            handleException(redirectAttributes, e, "Save process failed!");
        }
        return "redirect:/admin/brands/index";
    }

    @PostMapping("edit")
    public String updateBrand(@RequestParam("brandId") Integer brandId,
                             @RequestParam("name") String name,
                             @RequestParam("users.userId") Integer userId,
                             @RequestParam(value = "file", required = false) MultipartFile file,
                             RedirectAttributes redirectAttributes) {
        try {
            Brands brand = brandsService.findById(brandId);
            if (brand == null) {
                redirectAttributes.addFlashAttribute("msg", "Brand not found!");
                return "redirect:/admin/brands/index";
            }
            Users user = usersService.findById(userId);
            if (user == null) {
                redirectAttributes.addFlashAttribute("msg", "User not found!");
                return "redirect:/admin/brands/index";
            }
            brand.setName(name);
            brand.setUsers(user);
            if (file != null && !file.isEmpty()) {
                updateBrandImage(brand, file, redirectAttributes);
            }
            String message = brandsService.save(brand) ? "Brand updated successfully!" : "Failed to update brand!";
            redirectAttributes.addFlashAttribute("msg", message);
        } catch (Exception e) {
            handleException(redirectAttributes, e, "Update process failed!");
        }
        return "redirect:/admin/brands/index";
    }

    // Helper methods
    private void populateIndexModel(ModelMap model, List<Brands> allBrands, 
                                  List<Brands> filteredBrands, Integer selectedBrandId) {
        model.addAttribute("brands", allBrands);
        model.addAttribute("filteredBrands", filteredBrands);
        model.addAttribute("selectedBrandId", selectedBrandId);
        model.addAttribute("brand", new Brands());
        model.addAttribute("user", new Users());
        model.addAttribute("users", usersService.findOwner(3));
    }

    private void updateBrandImage(Brands brand, MultipartFile file, RedirectAttributes redirectAttributes) throws Exception {
        if (file != null && !file.isEmpty()) {
            String fileName = FileHelper.generateFileName(file.getOriginalFilename());
            File imageFolder = new ClassPathResource(IMAGE_FOLDER).getFile();
            Path path = Paths.get(imageFolder.getAbsolutePath(), fileName);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            brand.setImageUrl(fileName);
        } else if (brand.getImageUrl() == null) {
            brand.setImageUrl(DEFAULT_IMAGE);
        }
    }

    private void updateBrandUser(Brands brand, RedirectAttributes redirectAttributes) {
        if (brand.getUsers() != null && brand.getUsers().getUserId() != null) {
            Users user = usersService.findById(brand.getUsers().getUserId());
            if (user == null) {
                redirectAttributes.addFlashAttribute("msg", "User not found!");
            }
            brand.setUsers(user);
        }
    }

    private void handleException(RedirectAttributes redirectAttributes, Exception e, String defaultMessage) {
        e.printStackTrace();
        redirectAttributes.addFlashAttribute("msg", defaultMessage);
    }
}