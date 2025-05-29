package com.example.demo.repostitoriesAdmin;




import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entities.Seats;

public interface SeatsRepository extends JpaRepository<Seats, Integer> {
	
	@Query("SELECT s FROM Seats s WHERE s.screens.screenId = :screenId")
    public List<Seats> findByScreenId(@Param("screenId") int screenId);
}
