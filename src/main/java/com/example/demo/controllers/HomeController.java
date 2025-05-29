package com.example.demo.controllers;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.services.MovieService;


@Controller
@RequestMapping({"", "/", "home"})
public class HomeController {

	@Autowired
	private MovieService movieService;
	
	@GetMapping({"","/","index"})
	public String Index(ModelMap modelMap) {
		modelMap.put("moviesTrue", movieService.findByMovieStatusTrue(5));
		modelMap.put("movies", movieService.findByMovieStatusTrue(10));
		modelMap.put("moviesFalse", movieService.findByMovieStatusFalse(8));
		return "home/index";
	}
	
}
