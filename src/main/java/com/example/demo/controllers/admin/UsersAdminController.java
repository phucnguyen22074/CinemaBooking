package com.example.demo.controllers.admin;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entities.Roles;
import com.example.demo.entities.Users;
import com.example.demo.serviceAdmin.RoleService;
import com.example.demo.serviceAdmin.UsersService;

@Controller
@RequestMapping({ "admin", "admin/users" })
public class UsersAdminController {

	@Autowired
	private UsersService usersService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private PasswordEncoder encoder;

	private static final String DATE_FORMAT = "dd/MM/yyyy";

	@GetMapping({ "", "/", "index" })
	public String showIndex(ModelMap model, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		size = validatePageSize(size);
		Page<Users> usersPage = usersService.findAllUsersExceptAdmin(page, size);
		populateIndexModel(model, usersPage, null, null, null, size, null);
		model.addAttribute("roles", roleService.findAllRolesExceptAdmin());

		model.addAttribute("role", new Roles());
		return "admin/users/index";
	}

	@GetMapping("filter")
	public String filterUsers(@RequestParam(required = false) Integer roleId,
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false) Boolean status, @RequestParam(required = false) String createdAt,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, ModelMap model) {

		Date createdDate = parseDate(createdAt, model);
		Page<Users> usersPage = usersService.filterUsers(roleId, status, createdDate, page, size, keyword);
		populateIndexModel(model, usersPage, roleId, status, createdAt, size, keyword);
		return "admin/users/index";
	}

	@GetMapping("delete/{id}")
	public String deleteUser(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
		String message = usersService.detele(id) ? "Delete Success" : "Delete Failed";
		redirectAttributes.addFlashAttribute("msg", message);
		return "redirect:/admin/users/index";
	}

	@GetMapping("edit/{id}")
	public String showEditForm(@PathVariable("id") int id, ModelMap model) {
		Users user = usersService.findById(id);
		if (user == null) {
			return "redirect:/admin/users/index";
		}
		model.put("user", user);
		model.put("roles", roleService.findAllRolesExceptAdmin());
		model.put("role", new Roles());
		return "admin/users/index";
	}

	@PostMapping("save")
	public String saveUser(@ModelAttribute("Users") Users user, RedirectAttributes redirectAttributes) {
		try {
			initializeNewUser(user);
			String message = usersService.save(user) ? "Save Users Success" : "Save Users Failed";
			redirectAttributes.addFlashAttribute("msg", message);
		} catch (Exception e) {
			handleException(redirectAttributes, e, "Save process failed!");
		}
		return "redirect:/admin/users/index";
	}

	@PostMapping("edit")
	public String updateUser(@ModelAttribute("Users") Users user, RedirectAttributes redirectAttributes) {
		try {
			Users existingUser = usersService.findById(user.getUserId());
			if (existingUser == null) {
				redirectAttributes.addFlashAttribute("msg", "User not found!");
				return "redirect:/admin/users/index";
			}
			updateExistingUser(existingUser, user);
			String message = usersService.save(existingUser) ? "Save Users Success" : "Save Users Failed";
			redirectAttributes.addFlashAttribute("msg", message);
		} catch (Exception e) {
			handleException(redirectAttributes, e, "Update process failed!");
		}
		return "redirect:/admin/users/index";
	}

	private int validatePageSize(int size) {
	    if (size < 1) {
	        throw new IllegalArgumentException("Page size must not be less than one. Invalid value: " + size);
	    }
	    return size;
	}
	private void populateIndexModel(ModelMap model, Page<Users> usersPage, Integer roleId, Boolean status,
			String createdAt, int size, String keyword) {
		model.addAttribute("users", usersPage.getContent());
		model.addAttribute("roles", roleService.findAllRolesExceptAdmin());
		model.addAttribute("selectedRoleId", roleId);
		model.addAttribute("selectedStatus", status);
		model.addAttribute("selectedCreatedAt", createdAt);
		model.addAttribute("keyword", keyword);
		model.addAttribute("currentPage", usersPage.getNumber());
		model.addAttribute("totalPages", usersPage.getTotalPages());
		model.addAttribute("totalItems", usersPage.getTotalElements());
		model.addAttribute("size", size);
		model.addAttribute("user", new Users());
		model.addAttribute("role", new Roles());
	}

	private void initializeNewUser(Users user) {
		user.setPassword(encoder.encode(user.getPassword()));
		user.setStatus(true);
		user.setCreatedAt(new Date());
		if (user.getRoles() != null && user.getRoles().getRoleId() != null) {
			Roles role = roleService.findById(user.getRoles().getRoleId());
			user.setRoles(role);
		}
	}

	private void updateExistingUser(Users existing, Users updated) {
		if (updated.getPassword() != null && !updated.getPassword().isEmpty()) {
			existing.setPassword(encoder.encode(updated.getPassword()));
		}
		if (updated.getFullName() != null && !updated.getFullName().isEmpty()) {
			existing.setFullName(updated.getFullName());
		}
		if (updated.getDob() != null) {
			existing.setDob(updated.getDob());
		}
		Boolean status = updated.isStatus();
		if (status != null) {
			existing.setStatus(status);
		}
		if (updated.getRoles() != null && updated.getRoles().getRoleId() != null) {
			Roles role = roleService.findById(updated.getRoles().getRoleId());
			existing.setRoles(role != null ? role : existing.getRoles());
		}
		existing.setCreatedAt(existing.getCreatedAt());
	}

	private Date parseDate(String createdAt, ModelMap model) {
		if (createdAt == null || createdAt.isEmpty()) {
			return null;
		}
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
			simpleDateFormat.setLenient(false);
			return simpleDateFormat.parse(createdAt);
		} catch (Exception e) {
			model.addAttribute("msg", "Invalid date format! Use " + DATE_FORMAT + ".");
			return null;
		}
	}

	private void handleException(RedirectAttributes redirectAttributes, Exception e, String defaultMessage) {
		e.printStackTrace();
		redirectAttributes.addFlashAttribute("msg", defaultMessage);
	}
}