package com.example.demo.repostitoriesAdmin;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entities.Brands;

public interface BrandsRepository extends JpaRepository<Brands, Integer> {

	@Query("SELECT b FROM Brands b WHERE b.users.userId = :userId")
	List<Brands> findByUsersUserId(@Param("userId") Integer userId);
	
	@Query("SELECT b FROM Brands b WHERE b.brandId = :id")
	public List<Brands> findBrandsById(@Param("id") Integer id);
	
	@Query("SELECT b FROM Brands b")
	List<Brands> findAllBrands();

}
