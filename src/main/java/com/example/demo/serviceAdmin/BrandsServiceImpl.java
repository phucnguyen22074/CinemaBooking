package com.example.demo.serviceAdmin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entities.Brands;
import com.example.demo.repostitoriesAdmin.BrandsRepository;

@Service("brandsService")
public class BrandsServiceImpl implements BrandsService {

	@Autowired
	private BrandsRepository brandsRepository;

	@Override
	public Brands findById(int id) {
		return brandsRepository.findById(id).get();
	}

	@Override
	public Iterable<Brands> findAll() {
		return brandsRepository.findAll();
	}

	@Override
	public boolean save(Brands brands) {
		try {
			brandsRepository.save(brands);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public List<Brands> findByOwnerId(Integer ownerId) {
		return brandsRepository.findByUsersUserId(ownerId);
	}

	@Override
	public boolean detele(int id) {
		try {
			brandsRepository.deleteById(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public List<Brands> filterBrands(Integer id) {
		List<Brands> filteredBrands = brandsRepository.findBrandsById(id);
	    if (id != null && id > 0) {
	    	filteredBrands = brandsRepository.findBrandsById(id);
	    }
	    return filteredBrands;
	}


}
