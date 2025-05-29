package com.example.demo.serviceAdmin;

import com.example.demo.entities.MovieShowtimeScreen;
import com.example.demo.entities.Movies;

public interface MovieShowtimeScreenService {

	public Movies findMovieIdByMvs(Integer movieId);
	
	public boolean save(MovieShowtimeScreen movieShowtimeScreen);
}
