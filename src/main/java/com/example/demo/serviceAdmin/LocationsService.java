package com.example.demo.serviceAdmin;


import org.springframework.data.domain.Page;

import com.example.demo.entities.Locations;

public interface LocationsService {
	public Locations findById(int id);
	public Iterable<Locations> findAll();
	public Page<Locations> filterLocations(Integer locationId, int page, int size);
	public boolean save(Locations locations);
	public boolean detele(int id);
	public Page<Locations> getLocations(int page, int size);
	
}
