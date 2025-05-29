package com.example.demo.servicesDTO;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.dto.GenresDTO;
import com.example.demo.dto.MoviesDTO;
import com.example.demo.dto.ScreensDTO;
import com.example.demo.dto.ShowtimesDTO;
import com.example.demo.dto.TheatersDTO;
import com.example.demo.entities.*;
import com.example.demo.repositories.GenresRepository;
import com.example.demo.repositories.MovieRepository;
import com.example.demo.repositories.MssRepository;

@Service
public class GenresDTOServiceImpl implements GenresDTOService {

	@Autowired
	private GenresRepository genresRepository;
    
    @Autowired
    private ModelMapper modelMapper;

    @Override
	public List<GenresDTO> findAll() {
		return modelMapper.map(genresRepository.findAll(), 
				new TypeToken<List<GenresDTO>>() {}.getType());
	}
}
