package com.example.demo.serviceAdmin;

import java.util.List;

import com.example.demo.entities.Brands;

public interface BrandsService {
	public Brands findById(int id);
	public Iterable<Brands> findAll();
	public boolean save(Brands brands);
	public boolean detele(int id);
	public List<Brands> filterBrands(Integer id);
	public List<Brands> findByOwnerId(Integer ownerId);
}
