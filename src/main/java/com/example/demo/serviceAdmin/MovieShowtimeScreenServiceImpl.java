package com.example.demo.serviceAdmin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entities.MovieShowtimeScreen;
import com.example.demo.entities.Movies;
import com.example.demo.repostitoriesAdmin.MovieShowtimeRepository;
@Service
public class MovieShowtimeScreenServiceImpl implements MovieShowtimeScreenService {
	
	@Autowired
	private MovieShowtimeRepository movieShowtimeRepository;
	
	@Override
	public Movies findMovieIdByMvs(Integer movieId) {
		// TODO Auto-generated method stub
		return movieShowtimeRepository.findMoviesIdByMvs(movieId);
	}

	@Override
	public boolean save(MovieShowtimeScreen movieShowtimeScreen) {
		// TODO Auto-generated method stub
		try {
			movieShowtimeRepository.save(movieShowtimeScreen);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	
}
