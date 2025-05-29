package com.example.demo.controllers.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping({"admin", "admin/login"})
public class LoginAdminController {

    @GetMapping({"", "/", "index"})
    public String index() {
        return "admin/login/index";
    }
}
