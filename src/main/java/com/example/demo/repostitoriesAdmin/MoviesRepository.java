package com.example.demo.repostitoriesAdmin;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entities.Movies;

public interface MoviesRepository extends JpaRepository<Movies, Integer> {
	@Query("SELECT m FROM Movies m JOIN m.theaterses t WHERE t.theaterId = :theaterId")
	public List<Movies> findByTheaterId(@Param("theaterId") int theaterId);
	
	@Query("SELECT m FROM Movies m WHERE "
	         + "(:genreId IS NULL OR EXISTS (SELECT g FROM m.genreses g WHERE g.genreId = :genreId)) "
	         + "AND (:keyword IS NULL OR m.title LIKE %:keyword%) "
	         + "AND (:status IS NULL OR m.status = :status) "
	         + "AND (:minDuration IS NULL OR m.duration >= :minDuration) "
	         + "AND (:maxDuration IS NULL OR m.duration <= :maxDuration) "
	         + "AND (:releaseYear IS NULL OR YEAR(m.releaseDate) = :releaseYear) "
	         + "AND (:rating IS NULL OR m.rating = :rating)")
	    Page<Movies> findByFilters(@Param("genreId") Integer genreId,
	                               @Param("keyword") String keyword,
	                               @Param("status") Boolean status,
	                               @Param("minDuration") Integer minDuration,
	                               @Param("maxDuration") Integer maxDuration,
	                               @Param("releaseYear") Integer releaseYear,
	                               @Param("rating") String rating,
	                               Pageable pageable);
}
