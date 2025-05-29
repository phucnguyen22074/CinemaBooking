package com.example.demo.serviceAdmin;

import java.util.List;
import java.util.Set;

import com.example.demo.entities.Genres;

public interface GenresService {
	public Iterable<Genres> findAll();
	public Genres findById(int id);
	public Set<Genres> findByIds(List<Integer> ids);
}
