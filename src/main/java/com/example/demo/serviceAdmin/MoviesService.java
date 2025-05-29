
package com.example.demo.serviceAdmin;

import java.util.List;

import com.example.demo.entities.Movies;

public interface MoviesService {

	public Iterable<Movies> findAll();
	
	List<Movies> findByTheaterId(int theaterId);
	
	public boolean save(Movies movies);
	
	public Movies findById(Integer id);
	
	public boolean detele(int id);
	
	//Filter
	
	public List<Movies> filterMovies(Integer genreId, String keyword, Boolean status, 
			Integer minDuration, Integer maxDuration, Integer year, String rating);
	
	public List<Movies> filterMoviesByTheater(Integer genreId, String keyword, Boolean status, 
			Integer minDuration, Integer maxDuration, Integer year, String rating, int theaterId);
	
}
