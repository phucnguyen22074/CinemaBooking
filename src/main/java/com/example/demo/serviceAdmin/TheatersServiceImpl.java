package com.example.demo.serviceAdmin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.demo.entities.Brands;
import com.example.demo.entities.Locations;
import com.example.demo.entities.Theaters;
import com.example.demo.repostitoriesAdmin.BrandsRepository;
import com.example.demo.repostitoriesAdmin.LocationsRepository;
import com.example.demo.repostitoriesAdmin.TheaterRepositoryAdmin;

@Service("theatersService")
public class TheatersServiceImpl implements TheatersService {

	@Autowired
	private TheaterRepositoryAdmin theaterRepository;

	@Autowired
	private BrandsRepository brandsRepository;

	@Autowired
	private LocationsRepository locationsRepository;

	@Override
	public Iterable<Theaters> findAll() {
		return theaterRepository.findAll();
	}

	@Override
	public Theaters findById(int id) {
		return theaterRepository.findById(id).get();
	}

	@Override
	public boolean save(Theaters theaters) {
		try {
			theaterRepository.save(theaters);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean detele(int id) {
		try {
			theaterRepository.deleteById(id);
			;
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public Iterable<Theaters> findAllByOrderByIdDesc() {
		return theaterRepository.findAllByOrderByIdDesc();
	}

	@Override
	public Page<Theaters> getTheatersByBrand(Integer brandId, int page , int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
		return theaterRepository.findByBrandId(brandId, pageable);
	}

	@Override
	public List<Brands> getAllBrands() {
		return (List<Brands>) brandsRepository.findAll();
	}

	@Override
	public Page<Theaters> getTheatersByBrandAndLocation(Integer brandId, Integer locationId,int page , int size) {
	    Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return theaterRepository.findByBrandIdAndLocationId(brandId, locationId, pageable);
	}

	@Override
	public Page<Theaters> getTheatersByLocation(Integer locationId, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return theaterRepository.findByLocationId(locationId, pageable);
	}

	@Override
	public List<Locations> getAllLocations() {
		return (List<Locations>) locationsRepository.findAll();
	}

	@Override
	public boolean edit(Theaters theaters) {
		try {
			Theaters existingTheater = theaterRepository.findById(theaters.getTheaterId()).orElse(null);
			if (existingTheater == null) {
				return false; // Không tồn tại thì không update
			}

			// Copy giá trị từ theaters mới vào existingTheater để Hibernate nhận diện
			existingTheater.setName(theaters.getName());
			existingTheater.setAddress(theaters.getAddress());
			existingTheater.setLatitude(theaters.getLatitude());
			existingTheater.setLongitude(theaters.getLongitude());
			existingTheater.setBrands(theaters.getBrands());
			existingTheater.setLocations(theaters.getLocations());
			// Nếu có các trường khác, hãy cập nhật chúng tại đây

			theaterRepository.save(existingTheater);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public Page<Theaters> getTheaters(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return theaterRepository.findAll(pageable);
	}

	@Override
	public Set<Theaters> findByIds(List<Integer> ids) {
		// TODO Auto-generated method stub
		return new HashSet<>(theaterRepository.findAllById(ids));
	}

}
