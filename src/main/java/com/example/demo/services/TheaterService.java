package com.example.demo.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.query.Param;

import com.example.demo.entities.TheaterWithDistance;
import com.example.demo.entities.Theaters;

import jakarta.servlet.http.HttpServletRequest;

public interface TheaterService {
	public Theaters findById(int theaterId);
	
	public List<String> findAllBrands();
	
	public List<Theaters> searchTheatersByName(String name);
	
	public List<Theaters> findByBrandName(String brandName);
	
	public Iterable<Theaters> findAll();
}
