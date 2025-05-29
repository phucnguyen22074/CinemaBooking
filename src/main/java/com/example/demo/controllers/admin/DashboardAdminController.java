package com.example.demo.controllers.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping({"admin","admin/dashboard"})
public class DashboardAdminController {

	@GetMapping({"index"})
	public String index() {
		return "admin/dashboard/index";
	}
}
