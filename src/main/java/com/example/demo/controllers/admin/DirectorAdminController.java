package com.example.demo.controllers.admin;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entities.Director;
import com.example.demo.entities.Roles;
import com.example.demo.entities.Showtimes;
import com.example.demo.entities.Users;
import com.example.demo.helper.FileHelper;
import com.example.demo.serviceAdmin.DirectorService;
import com.example.demo.serviceAdmin.UsersService;

@Controller
@RequestMapping({ "admin", "admin/director" })
public class DirectorAdminController {

	@Autowired
	private DirectorService directorService;

	@GetMapping({ "index" })
	public String viewShowtimes(ModelMap modelMap, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, Integer selecteddirectorId) {
		Page<Director> directorPage = directorService.findAll(page, size);
		modelMap.put("directors", directorPage.getContent());
		modelMap.addAttribute("currentPage", directorPage.getNumber());
		modelMap.addAttribute("totalPages", directorPage.getTotalPages());
		modelMap.addAttribute("totalItems", directorPage.getTotalElements());

		modelMap.addAttribute("selecteddirectorId", selecteddirectorId);
		modelMap.addAttribute("director", new Director());
		return "admin/director/index";
	}
	
	@GetMapping("delete/{id}")
    public String deleteBrand(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        String message = directorService.delete(id) ? "Delete Success" : "Delete Failed";
        redirectAttributes.addFlashAttribute("msg", message);
        return "redirect:/admin/director/index";
    }
	
	@PostMapping({ "save" })
	public String savedirector(@ModelAttribute("director") Director director, @RequestParam("file") MultipartFile file,
			RedirectAttributes redirectAttributes) {
		try {
			if (!file.isEmpty()) {
				String fileName = FileHelper.generateFileName(file.getOriginalFilename());
				File imageFolder = new ClassPathResource("static/assets/hinhanh/director").getFile();
				Path path = Paths.get(imageFolder.getAbsolutePath() + File.separator + fileName);
				System.out.println("path: " + path.toString());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				director.setImageUrl(fileName);
			} else {
				director.setImageUrl("No_image2.png");
			}
			director.setName(director.getName());
			if (directorService.save(director)) {
				redirectAttributes.addFlashAttribute("msg", "save success");
			} else {
				redirectAttributes.addFlashAttribute("msg", "save failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/admin/director/index";
	}

	@PostMapping({ "edit" })
	public String editdirector(@ModelAttribute("director") Director directorForm, @RequestParam("file") MultipartFile file,
			RedirectAttributes redirectAttributes) {
		try {
			Director director = directorService.findById(directorForm.getId());
			if (!file.isEmpty()) {
				String fileName = FileHelper.generateFileName(file.getOriginalFilename());
				File imageFolder = new ClassPathResource("static/assets/hinhanh/director").getFile();
				Path path = Paths.get(imageFolder.getAbsolutePath() + File.separator + fileName);
				System.out.println("path: " + path.toString());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				director.setImageUrl(fileName);
			} else {
				director.setImageUrl(director.getImageUrl());
			}
			director.setName(directorForm.getName());
			if (directorService.save(director)) {
				redirectAttributes.addFlashAttribute("msg", "save success");
			} else {
				redirectAttributes.addFlashAttribute("msg", "save failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/admin/director/index";
	}
}
