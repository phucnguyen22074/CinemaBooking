package com.example.demo.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entities.GeoLocation;
import com.example.demo.entities.TheaterWithDistance;
import com.example.demo.entities.Theaters;
import com.example.demo.repositories.TheaterRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service("theaterService")
public class TheaterServiceImpl implements TheaterService {

	@Autowired
	private TheaterRepository theaterRepository;
	
	@Autowired
    private GeoLocationService geoLocationService;
	

	@Override
	public Theaters findById(int theaterId) {
		return theaterRepository.findById(theaterId).get();
	}


	@Override
	public List<String> findAllBrands() {
		return theaterRepository.findAllBrands();
	}
	
	@Override
	public List<Theaters> searchTheatersByName(String name) {
	    if (name == null || name.trim().isEmpty()) {
	        return ((Collection<Theaters>) theaterRepository.findAll()).stream().collect(Collectors.toList());
	    }
	    return theaterRepository.findByNameContaining(name);
	}

	@Override
	public List<Theaters> findByBrandName(String brandName) {
	    if (brandName == null || brandName.trim().isEmpty()) {
	        return ((Collection<Theaters>) theaterRepository.findAll()).stream().collect(Collectors.toList());
	    }
	    return theaterRepository.findByBrandName(brandName);
	}


	@Override
	public Iterable<Theaters> findAll() {
		return theaterRepository.findAll();
	}
}
