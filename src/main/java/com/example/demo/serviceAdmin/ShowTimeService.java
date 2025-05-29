package com.example.demo.serviceAdmin;

import org.springframework.data.domain.Page;

import com.example.demo.entities.Showtimes;

public interface ShowTimeService {
	
	public Page<Showtimes> findAll(int page, int size); 
	
	public Showtimes findById(int id);
	
	public boolean save(Showtimes showtimes);
	
	public boolean delete(int showtimeId);
	
	public Iterable<Showtimes> findAllSize();
	
	public Showtimes getShowtimeDetails(Integer showtimeId);
}
