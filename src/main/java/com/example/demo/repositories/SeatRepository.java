package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.Genres;
import com.example.demo.entities.Movies;
import com.example.demo.entities.Seats;

import jakarta.transaction.Transactional;

@Repository
public interface SeatRepository extends CrudRepository<Seats, Integer> {
	
	@Query("SELECT s FROM Seats s WHERE s.screens.screenId = :screenId")
	public List<Seats> findByScreenId(@Param("screenId") Integer screenId);
	
	@Query("FROM Seats WHERE status = :status")
    public List<Seats> findByStatus(@Param("status") Byte status);
	
	@Modifying
    @Transactional
    @Query("UPDATE Seats s SET s.status = 0 WHERE s.seatId IN :seatIds")
    void updateSeatsStatusToFalse(List<Integer> seatIds);
}
