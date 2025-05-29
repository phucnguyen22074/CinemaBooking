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

import com.example.demo.entities.Actor;
import com.example.demo.entities.Roles;
import com.example.demo.entities.Showtimes;
import com.example.demo.entities.Users;
import com.example.demo.helper.FileHelper;
import com.example.demo.serviceAdmin.ActorService;
import com.example.demo.serviceAdmin.UsersService;

@Controller
@RequestMapping({ "admin", "admin/actor" })
public class ActorAdminController {

	@Autowired
	private ActorService actorService;

	@GetMapping({ "index" })
	public String viewShowtimes(ModelMap modelMap, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, Integer selectedActorId) {
		Page<Actor> actorPage = actorService.findAll(page, size);
		modelMap.put("actors", actorPage.getContent());
		modelMap.addAttribute("currentPage", actorPage.getNumber());
		modelMap.addAttribute("totalPages", actorPage.getTotalPages());
		modelMap.addAttribute("totalItems", actorPage.getTotalElements());

		modelMap.addAttribute("selectedActorId", selectedActorId);
		modelMap.addAttribute("actor", new Actor());
		return "admin/actor/index";
	}
	
	@GetMapping("delete/{id}")
    public String deleteBrand(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        String message = actorService.delete(id) ? "Delete Success" : "Delete Failed";
        redirectAttributes.addFlashAttribute("msg", message);
        return "redirect:/admin/actor/index";
    }
	
	@PostMapping({ "save" })
	public String saveActor(@ModelAttribute("actor") Actor actor, @RequestParam("file") MultipartFile file,
			RedirectAttributes redirectAttributes) {
		try {
			if (!file.isEmpty()) {
				String fileName = FileHelper.generateFileName(file.getOriginalFilename());
				File imageFolder = new ClassPathResource("static/assets/hinhanh/").getFile();
				Path path = Paths.get(imageFolder.getAbsolutePath() + File.separator + fileName);
				System.out.println("path: " + path.toString());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				actor.setImageUrl(fileName);
			} else {
				actor.setImageUrl("No_image2.png");
			}
			actor.setName(actor.getName());
			if (actorService.save(actor)) {
				redirectAttributes.addFlashAttribute("msg", "save success");
			} else {
				redirectAttributes.addFlashAttribute("msg", "save failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/admin/actor/index";
	}

	@PostMapping({ "edit" })
	public String editActor(@ModelAttribute("actor") Actor actorForm, @RequestParam("file") MultipartFile file,
			RedirectAttributes redirectAttributes) {
		try {
			Actor actor = actorService.findById(actorForm.getId());
			if (!file.isEmpty()) {
				String fileName = FileHelper.generateFileName(file.getOriginalFilename());
				File imageFolder = new ClassPathResource("static/assets/hinhanh/").getFile();
				Path path = Paths.get(imageFolder.getAbsolutePath() + File.separator + fileName);
				System.out.println("path: " + path.toString());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				actor.setImageUrl(fileName);
			} else {
				actor.setImageUrl(actor.getImageUrl());
			}
			actor.setName(actorForm.getName());
			if (actorService.save(actor)) {
				redirectAttributes.addFlashAttribute("msg", "save success");
			} else {
				redirectAttributes.addFlashAttribute("msg", "save failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/admin/actor/index";
	}
}
