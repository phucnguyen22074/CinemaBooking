package com.example.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.demo.entities.Genres;
import com.example.demo.repositories.GenresRepository;

@Service("genresService")
public class GenresServiceImpl implements GenresService{

	@Autowired
	private GenresRepository genresRepository;
	
	@Override
	@Cacheable(value = "genres")
	public Iterable<Genres> findAll() {
		return genresRepository.findAll();
	}

}
