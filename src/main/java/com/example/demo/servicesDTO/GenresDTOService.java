package com.example.demo.servicesDTO;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import com.example.demo.dto.GenresDTO;
import com.example.demo.dto.MoviesDTO;
import com.example.demo.dto.ShowtimesDTO;
import com.example.demo.dto.TheatersDTO;

public interface GenresDTOService {
	public List<GenresDTO> findAll();
}
