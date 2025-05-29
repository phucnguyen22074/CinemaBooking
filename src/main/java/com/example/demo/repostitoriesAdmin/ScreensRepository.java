package com.example.demo.repostitoriesAdmin;




import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entities.Screens;

public interface ScreensRepository extends JpaRepository<Screens, Integer> {
	Page<Screens> findAll(Pageable pageable);
	
	@Query("SELECT s FROM Screens s WHERE "
	        + "(:nameScreen IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :nameScreen, '%'))) "
	        + "AND (:nameTheater IS NULL OR LOWER(s.theaters.name) LIKE LOWER(CONCAT('%', :nameTheater, '%'))) "
	        + "AND (s.totalSeats = :seats OR :seats IS NULL) ")
	Page<Screens> filterScreens(
	        @Param("nameScreen") String nameScreen,
	        @Param("nameTheater") String nameTheater,
	        @Param("seats") Integer seats,
	        Pageable pageable);
}
