package com.example.demo.serviceAdmin;


import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;

import com.example.demo.entities.Brands;
import com.example.demo.entities.Locations;
import com.example.demo.entities.Theaters;


public interface TheatersService {
	
	public Iterable<Theaters> findAll();
	
	public Iterable<Theaters> findAllByOrderByIdDesc();
	
	public Theaters findById(int id);
	
	public List<Brands> getAllBrands();
	
	public List<Locations> getAllLocations();
	
	public Page<Theaters> getTheatersByBrand(Integer brandId , int page, int size);
	
	public Page<Theaters> getTheatersByLocation(Integer locationId,int page, int size);
	
	public Page<Theaters> getTheatersByBrandAndLocation(Integer brandId , Integer locationId,int page, int size);
	
	public boolean save(Theaters theaters);
	
	public boolean detele(int id);
	
	public boolean edit(Theaters theaters);
	
	public Set<Theaters> findByIds(List<Integer> ids);
	
	//Paginated
	
	public Page<Theaters> getTheaters(int page, int size);
	
}

