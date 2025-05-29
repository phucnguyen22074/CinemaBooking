package com.example.demo.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.Movies;

@Repository
public interface MovieRepository extends CrudRepository<Movies, Integer> {

	@Query("FROM Movies WHERE status = :status ORDER BY releaseDate DESC")
	public List<Movies> findByStatus(@Param("status") boolean status, Pageable pageable);

	@Query("FROM Movies WHERE title LIKE %:keyword% ORDER BY releaseDate DESC")
	public List<Movies> findByTitle(@Param("keyword") String keyword, Pageable pageable);

	@Query("SELECT DISTINCT m FROM Movies m JOIN m.genreses g WHERE LOWER(g.name) LIKE LOWER(CONCAT('%', :genreName, '%'))")
	public List<Movies> findMoviesByGenreName(@Param("genreName") String genreName);

	@Query("SELECT DISTINCT m FROM Movies m JOIN m.genreses g WHERE "
			+ "LOWER(g.name) LIKE LOWER(CONCAT('%', :genreName, '%')) AND "
			+ "LOWER(m.title) LIKE LOWER(CONCAT('%', :title, '%'))")
	public List<Movies> findMoviesByGenreNameAndTitle(@Param("genreName") String genreName,
			@Param("title") String title);

}
