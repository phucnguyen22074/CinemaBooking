package com.example.demo.repostitoriesAdmin;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import com.example.demo.entities.Locations;


public interface LocationsRepository extends JpaRepository<Locations, Integer> {
	@Query("SELECT l FROM Locations l WHERE l.locationId = :id")
	public List<Locations> findLocationsById(@Param("id") Integer id);
	
	@Query("SELECT l FROM Locations l")
	List<Locations> findAllLocations();
	
	Page<Locations> findAll(Pageable pageable);
	
	Page<Locations> findByLocationId(Integer locationId, Pageable pageable);
}
