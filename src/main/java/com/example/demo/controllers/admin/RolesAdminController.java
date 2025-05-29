package com.example.demo.controllers.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.example.demo.serviceAdmin.RoleService;

@Controller
@RequestMapping({ "admin", "admin/roles" })
public class RolesAdminController {

	@Autowired
	private RoleService roleService;
	@GetMapping({ "index" })
    public String index(ModelMap modelMap) {
      
        List<Roles> allroles = (List<Roles>) roleService.findAll();
        modelMap.addAttribute("roles", allroles); // Danh sách roles cho dropdown
        modelMap.addAttribute("filteredRoles", allroles); // Ban đầu hiển thị toàn bộ danh sách
        modelMap.addAttribute("selectedRoleId", null); // Không có rolenào được chọn ban đầu
        modelMap.addAttribute("role", new Roles()); // Đối tượng rỗng cho form thêm mới
        return "admin/roles/index";
    }

	/** Delete a roles */
	@GetMapping("delete/{id}")
	public String delete(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
		String message = roleService.detele(id) ? "Delete Success" : "Delete Failed";
		redirectAttributes.addFlashAttribute("msg", message);
		return "redirect:/admin/roles/index";
	}

	@GetMapping("filter")
	public String filterRoles(@RequestParam(required = false) Integer roleId, ModelMap modelMap) {
	    List<Roles> filteredRoles;
	    if (roleId != null && roleId > 0) {
	        filteredRoles = roleService.filterRoles(roleId);
	    } else {
	        filteredRoles = roleService.findAllRoles();
	    }

	    List<Roles> allRoles = roleService.findAllRoles();
	    modelMap.addAttribute("roles", allRoles);
	    modelMap.addAttribute("filteredRoles", filteredRoles);
	    modelMap.addAttribute("selectedRoleId", roleId);
	    modelMap.addAttribute("role", new Roles());

	    return "admin/roles/index";
	}
	
	/** Load role edit form */
	@GetMapping("edit/{id}")
	public String editRoles(@PathVariable("id") int id, ModelMap modelMap) {
		modelMap.put("role", roleService.findById(id));
		modelMap.put("roles", roleService.findAll());
		return "admin/roles/edit";
	}

	@PostMapping({ "save" })
	public String saveRoles(@ModelAttribute("Roles") Roles roles, RedirectAttributes redirectAttributes) {
		try {
			roles.setName(roles.getName());
			if (roleService.save(roles)) {
				redirectAttributes.addFlashAttribute("msg", "Save Roles success!");
				return "redirect:/admin/roles/index";
			} else {
				redirectAttributes.addFlashAttribute("msg", "Save Roles failed!");
				return "redirect:/admin/roles/index";
			}
		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("msg", "An error occurred during the save process!");
			return "redirect:/admin/theaters/index";
		}

	}

	/** Edit existing role */
	@PostMapping("edit")
	public String editroles(@ModelAttribute("Roles") Roles roles, RedirectAttributes redirectAttributes) {
		try {
			Roles existingRole = roleService.findById(roles.getRoleId());

			if (existingRole == null) {
				redirectAttributes.addFlashAttribute("msg", "Role not found!");
				return "redirect:/admin/role/index";
			}

			roles.setName(
					roles.getName() != null && !roles.getName().isEmpty() ? roles.getName() : existingRole.getName());

			boolean success = roleService.save(roles);
			redirectAttributes.addFlashAttribute("msg", success ? "Save roles Success" : "Save roles Failed");
		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("msg", "An error occurred during the save process!");
		}
		return "redirect:/admin/roles/index";
	}
}
