package com.example.demo.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.entities.Movies;
import com.example.demo.repositories.MovieRepository;

@Service
public class MovieServiceImpl implements MovieService {

    @Autowired
    private MovieRepository movieRepository;

    @Override
    public List<Movies> findByMovieStatusTrue(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit phải lớn hơn 0.");
        }
        Pageable pageable = PageRequest.of(0, limit);
        return movieRepository.findByStatus(true, pageable);
    }

    @Override
    public List<Movies> findByMovieStatusFalse(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit phải lớn hơn 0.");
        }
        Pageable pageable = PageRequest.of(0, limit);
        return movieRepository.findByStatus(false, pageable);
    }

    @Override
    public Movies findById(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID phim phải lớn hơn 0.");
        }
        return movieRepository.findById(id).orElse(null);
    }

    @Override
    public List<Movies> getMoviesByStatus(boolean status, int page, int size) {
        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("Page phải >= 0 và size phải > 0.");
        }
        Pageable pageable = PageRequest.of(page, size);
        return movieRepository.findByStatus(status, pageable);
    }

	@Override
	public List<Movies> findByMoviesKeyword(String keyword, int limit) {
		if (limit <= 0) {
            throw new IllegalArgumentException("Limit phải lớn hơn 0.");
        }
        Pageable pageable = PageRequest.of(0, limit);
        return movieRepository.findByTitle(keyword, pageable);
	}

	@Override
	public List<Movies> findMoviesByGenreName(String genreName) {
		return movieRepository.findMoviesByGenreName(genreName);
	}

	@Override
	public List<Movies> findMoviesByGenreNameAndTitle(String genreName, String title) {
		return movieRepository.findMoviesByGenreNameAndTitle(genreName, title);
	}
}
