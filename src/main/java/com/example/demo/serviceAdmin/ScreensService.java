package com.example.demo.serviceAdmin;

import org.springframework.data.domain.Page;

import com.example.demo.entities.Screens;

public interface ScreensService {
	
	public boolean save(Screens screens);
	
	public Page<Screens> findAll(int page, int size);
	
	public Iterable<Screens> findAllSize();
	
	public Screens findById(int id);
	
	public boolean delete(int id);
	
	public Page<Screens> filterScreens(String nameScreen, String nameTheater,Integer seats
			, int page, int size);
}
