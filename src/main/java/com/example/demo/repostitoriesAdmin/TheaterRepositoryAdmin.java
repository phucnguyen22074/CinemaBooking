package com.example.demo.repostitoriesAdmin;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entities.Theaters;

public interface TheaterRepositoryAdmin extends JpaRepository<Theaters, Integer> {

	@Query("SELECT t FROM Theaters t ORDER by t.id DESC")
	public List<Theaters> findAllByOrderByIdDesc();

	@Query("SELECT t FROM Theaters t WHERE t.brands.brandId = :brandId ORDER BY t.id DESC")
	public Page<Theaters> findByBrandId(@Param("brandId") Integer brandId,Pageable pageable);

	@Query("SELECT t FROM Theaters t WHERE t.locations.locationId = :locationId ORDER BY t.id DESC")
	public Page<Theaters> findByLocationId(@Param("locationId") Integer locationId,Pageable pageable);

	@Query("SELECT t FROM Theaters t WHERE t.brands.brandId = :brandId AND t.locations.locationId = :locationId ORDER BY t.id DESC")
	public Page<Theaters> findByBrandIdAndLocationId(@Param("brandId") Integer brandId,
			@Param("locationId") Integer locationId,Pageable pageable);

	public Page<Theaters> findAll(Pageable pageable);

}
