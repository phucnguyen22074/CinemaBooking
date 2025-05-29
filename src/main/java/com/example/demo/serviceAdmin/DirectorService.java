package com.example.demo.serviceAdmin;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;

import com.example.demo.entities.Director;

public interface DirectorService {
	public Iterable<Director> findAll();
	public Director findById(int id);
	public Set<Director> findByIds(List<Integer> ids);
	public boolean save(Director director);
	public boolean delete(int directorId);
	public Page<Director> findAll(int page, int size);
}
