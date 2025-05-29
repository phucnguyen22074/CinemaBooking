package com.example.demo.serviceAdmin;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entities.Genres;
import com.example.demo.entities.Movies;
import com.example.demo.entities.Theaters;
import com.example.demo.repostitoriesAdmin.MoviesRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service("moviesService")
public class MoviesServiceImpl implements MoviesService {

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private MoviesRepository moviesRepository;

	@Override
	public Iterable<Movies> findAll() {
		// TODO Auto-generated method stub
		return moviesRepository.findAll();
	}

	@Override
	public List<Movies> findByTheaterId(int theaterId) {
		// TODO Auto-generated method stub
		return moviesRepository.findByTheaterId(theaterId);
	}

	@Override
	public boolean save(Movies movies) {
		// TODO Auto-generated method stub
		try {
			moviesRepository.save(movies);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public Movies findById(Integer id) {
		// TODO Auto-generated method stub
		return moviesRepository.findById(id).get();
	}

	@Override
	public boolean detele(int id) {
		try {
			moviesRepository.deleteById(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public List<Movies> filterMovies(Integer genreId, String keyword, Boolean status, Integer minDuration,
			Integer maxDuration, Integer year, String rating) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Movies> query = cb.createQuery(Movies.class);
		Root<Movies> movie = query.from(Movies.class);

		List<Predicate> predicates = new ArrayList<>();

		if (keyword != null && !keyword.trim().isEmpty()) {
			predicates.add(cb.or(cb.like(cb.lower(movie.get("title")), "%" + keyword.toLowerCase() + "%")));
		}

		if (genreId != null && genreId > 0) {
			Join<Movies, Genres> genresJoin = movie.join("genreses");
			predicates.add(cb.equal(genresJoin.get("genreId"), genreId));
		}
		if (status != null) {
			predicates.add(cb.equal(movie.get("status"), status));
		}

		if (minDuration != null) {
			predicates.add(cb.greaterThanOrEqualTo(movie.get("duration"), minDuration));
		}

		if (maxDuration != null) {
			predicates.add(cb.lessThanOrEqualTo(movie.get("duration"), maxDuration));
		}
		if (year != null) {
			Expression<Integer> yearExpression = cb.function("YEAR", Integer.class, movie.get("releaseDate"));
			predicates.add(cb.equal(yearExpression, year));
		}
		if (rating != null && !rating.isEmpty()) {
			if ("high".equals(rating)) {
				predicates.add(cb.greaterThanOrEqualTo(movie.get("rating"), 4.0));
			} else if ("medium".equals(rating)) {
				predicates.add(cb.and(cb.greaterThanOrEqualTo(movie.get("rating"), 3.0),
						cb.lessThan(movie.get("rating"), 4.0)));
			} else if ("low".equals(rating)) {
				predicates.add(cb.lessThan(movie.get("rating"), 3.0));
			}
		}

		// sử dụng câu lệnh này để áp dụng tất cả các điều kiện
		if (predicates.isEmpty()) {
			query.where(cb.isTrue(cb.literal(true))); // Điều kiện luôn đúng
		} else {
			query.where(predicates.toArray(new Predicate[0]));
		}
		return entityManager.createQuery(query).getResultList();
	}

	@Override
	public List<Movies> filterMoviesByTheater(Integer genreId, String keyword, Boolean status, Integer minDuration,
			Integer maxDuration, Integer year, String rating, int theaterId) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Movies> query = cb.createQuery(Movies.class);
		Root<Movies> movie = query.from(Movies.class);

		List<Predicate> predicates = new ArrayList<>();
		
		Join<Movies, Theaters> theaterJoin = movie.join("theaterses", JoinType.INNER);
        predicates.add(cb.equal(theaterJoin.get("theaterId"), theaterId));
		
		if (keyword != null && !keyword.trim().isEmpty()) {
			predicates.add(cb.or(cb.like(cb.lower(movie.get("title")), "%" + keyword.toLowerCase() + "%")));
		}

		if (genreId != null && genreId > 0) {
			Join<Movies, Genres> genresJoin = movie.join("genreses");
			predicates.add(cb.equal(genresJoin.get("genreId"), genreId));
		}
		if (status != null) {
			predicates.add(cb.equal(movie.get("status"), status));
		}

		if (minDuration != null) {
			predicates.add(cb.greaterThanOrEqualTo(movie.get("duration"), minDuration));
		}

		if (maxDuration != null) {
			predicates.add(cb.lessThanOrEqualTo(movie.get("duration"), maxDuration));
		}
		if (year != null) {
			Expression<Integer> yearExpression = cb.function("YEAR", Integer.class, movie.get("releaseDate"));
			predicates.add(cb.equal(yearExpression, year));
		}
		if (rating != null && !rating.isEmpty()) {
			if ("high".equals(rating)) {
				predicates.add(cb.greaterThanOrEqualTo(movie.get("rating"), 4.0));
			} else if ("medium".equals(rating)) {
				predicates.add(cb.and(cb.greaterThanOrEqualTo(movie.get("rating"), 3.0),
						cb.lessThan(movie.get("rating"), 4.0)));
			} else if ("low".equals(rating)) {
				predicates.add(cb.lessThan(movie.get("rating"), 3.0));
			}
		}

		// sử dụng câu lệnh này để áp dụng tất cả các điều kiện
		if (predicates.isEmpty()) {
			query.where(cb.isTrue(cb.literal(true))); // Điều kiện luôn đúng
		} else {
			query.where(predicates.toArray(new Predicate[0]));
		}
		return entityManager.createQuery(query).getResultList();
	}



}
