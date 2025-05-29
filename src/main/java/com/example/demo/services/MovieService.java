package com.example.demo.services;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import com.example.demo.entities.Movies;
import com.example.demo.entities.Screens;

public interface MovieService {

	public List<Movies> findByMovieStatusTrue(int limit);

	public List<Movies> findByMovieStatusFalse(int limit);

	public Movies findById(int id);

	public List<Movies> getMoviesByStatus(boolean status, int page, int size);

	public List<Movies> findByMoviesKeyword(String keyword, int limit);

	public List<Movies> findMoviesByGenreName(String genreName);

	public List<Movies> findMoviesByGenreNameAndTitle(String genreName, String title);
}
