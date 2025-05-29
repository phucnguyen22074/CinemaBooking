package com.example.demo.controllers.admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entities.Users;
import com.example.demo.serviceAdmin.UsersService;

@Controller
@RequestMapping({"admin","admin/profile"})
public class ProfileAdminController {
	
	@Autowired
	private UsersService usersService;
	
	@GetMapping({"index"})
	public String viewProfile(ModelMap modelMap, Authentication authentication) {
		String email = authentication.getName();
		modelMap.put("user", usersService.findByEmail(email));
		return "admin/profile/index";
	}
	@PostMapping("/save")
	public String saveProfile(
	    @ModelAttribute("user") Users user,
	    BindingResult result,
	    RedirectAttributes redirectAttributes
	) {
	    try {
	        Users existingUser = usersService.findById(user.getUserId());
	        if (existingUser == null) {
	            redirectAttributes.addFlashAttribute("msg", "User not found");
	            return "redirect:/admin/profile/index";
	        }
	        user.setPassword(existingUser.getPassword()); 
	        user.setEmail(existingUser.getEmail());       
	        user.setCreatedAt(existingUser.getCreatedAt()); 
	        user.setRoles(existingUser.getRoles());      
	        user.setSecurityCode(existingUser.getSecurityCode()); 
	        usersService.save(user);
	        redirectAttributes.addFlashAttribute("msg", "Profile updated successfully");
	    } catch (Exception e) {
	        e.printStackTrace();
	        redirectAttributes.addFlashAttribute("msg", "Error updating profile: " + e.getMessage());
	    }
	    return "redirect:/admin/profile/index";
	}
}
