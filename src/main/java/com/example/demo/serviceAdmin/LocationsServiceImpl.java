package com.example.demo.serviceAdmin;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.demo.entities.Locations;
import com.example.demo.repostitoriesAdmin.LocationsRepository;

@Service("locationsService")
public class LocationsServiceImpl implements LocationsService {

	@Autowired
	private LocationsRepository locationsRepository;

	@Override
	public Locations findById(int id) {
		return locationsRepository.findById(id).get();
	}

	@Override
	public Iterable<Locations> findAll() {
		return locationsRepository.findAll();
	}

	@Override
	public boolean save(Locations locations) {
		try {
			locationsRepository.save(locations);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean detele(int id) {
		try {
			locationsRepository.deleteById(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public Page<Locations> getLocations(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return locationsRepository.findAll(pageable);
	}

	@Override
	public Page<Locations> filterLocations(Integer locationId, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("locationId").ascending());
		return locationsRepository.findByLocationId(locationId, pageable);
	}

}
