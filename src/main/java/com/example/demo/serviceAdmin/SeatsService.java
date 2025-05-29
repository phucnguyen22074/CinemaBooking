package com.example.demo.serviceAdmin;

import java.util.List;

import com.example.demo.entities.Seats;

public interface SeatsService {

	public List<Seats> findByScreensId(int screenId);
	
	public Iterable<Seats> findAll();
	
	public Seats findById(int id);
	
	public boolean save(Seats seats);
}
