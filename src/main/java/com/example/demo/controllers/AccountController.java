package com.example.demo.controllers;

import java.util.Date;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entities.Roles;
import com.example.demo.entities.Users;
import com.example.demo.helper.SecurityCodeHelper;
import com.example.demo.services.AccountService;

import jakarta.validation.Valid;

@Controller
@RequestMapping({ "account" })
public class AccountController {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AccountService accountService;

	/* Khu Vuc GET */

	@GetMapping({ "login" })
	public String Login(ModelMap modelMap) {
		return "account/login";
	}

	@GetMapping({ "register" })
	public String Register(ModelMap modelMap) {
		Users users = new Users();
		modelMap.put("user", users);
		return "account/register";
	}

	@GetMapping({ "access-denied" })
	public String accessDenied() {
		return "account/accessdenied";
	}

	@GetMapping("verify")
	public String verify(@RequestParam("email") String email, @RequestParam("securitycode") String securitycode,
	                     RedirectAttributes redirectAttributes) {
	    Users account = accountService.findByEmail(email);
	    if (account != null && Objects.equals(account.getSecurityCode(), securitycode)) {
	        account.setStatus(true);
	        accountService.save(account);
	        redirectAttributes.addFlashAttribute("msg", "Verification successful!");
	    } else {
	        redirectAttributes.addFlashAttribute("msg", "Invalid email or security code!");
	    }
	    return "redirect:/account/login";
	}


	@GetMapping({ "profile" })
	public String Profile(Authentication authentication, ModelMap modelMap) {
	    if (authentication == null) {
	        return "redirect:/account/login";
	    }
	    String email = authentication.getName();
	    modelMap.put("user", accountService.findByEmail(email));
	    return "account/profile";
	}


	/* Khu Vuc Post */
	@PostMapping({ "profile" })
	public String Profile(Authentication authentication, @Valid @ModelAttribute("user") Users account,
	                      BindingResult result, RedirectAttributes redirectAttributes) {
	    if (authentication == null) {
	        return "redirect:/account/login";
	    }
	    
	    if (result.hasErrors()) {
	        return "account/profile";
	    }
	    
	    String email = authentication.getName();
	    Users currentAccount = accountService.findByEmail(email);
	    
	    if (account.getPassword() != null && !account.getPassword().trim().isEmpty()) {
	        currentAccount.setPassword(passwordEncoder.encode(account.getPassword()));
	    }
	    
	    currentAccount.setDob(account.getDob());
	    currentAccount.setFullName(account.getFullName());
	    
	    if (accountService.save(currentAccount)) {
	        redirectAttributes.addFlashAttribute("msg", "Success");
	    } else {
	        redirectAttributes.addFlashAttribute("msg", "Failed");
	    }
	    
	    return "redirect:/account/profile";
	}


	@PostMapping("save")
	public String Save(@Valid @ModelAttribute("user") Users users, BindingResult result,
	                   RedirectAttributes redirectAttributes) {
	    if (result.hasErrors()) {
	        return "account/register";
	    }

	    // Kiểm tra email đã tồn tại chưa
	    if (accountService.findByEmail(users.getEmail()) != null) {
	        redirectAttributes.addFlashAttribute("msg", "Email đã được sử dụng.");
	        redirectAttributes.addFlashAttribute("msgType", "error");
	        return "redirect:/account/register";
	    }

	    String securityCode = SecurityCodeHelper.gennerate();
	    users.setStatus(false);
	    users.setPassword(passwordEncoder.encode(users.getPassword()));
	    users.setSecurityCode(securityCode);
	    Roles role = new Roles();
	    role.setRoleId(1);
	    users.setRoles(role);
	    users.setCreatedAt(new Date());

	    if (accountService.save(users)) {
	        if (accountService.sendActivationEmail(users)) {
	            redirectAttributes.addFlashAttribute("msg", "Vào email để kích hoạt tài khoản.");
	            redirectAttributes.addFlashAttribute("msgType", "success");
	        } else {
	            redirectAttributes.addFlashAttribute("msg", "Gửi email kích hoạt thất bại.");
	            redirectAttributes.addFlashAttribute("msgType", "error");
	        }
	        return "redirect:/home/index";
	    } else {
	        redirectAttributes.addFlashAttribute("msg", "Đăng ký thất bại.");
	        redirectAttributes.addFlashAttribute("msgType", "error");
	        return "redirect:/account/register";
	    }
	}

}
