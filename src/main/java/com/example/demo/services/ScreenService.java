package com.example.demo.services;

import java.util.List;

import org.springframework.data.repository.query.Param;

import com.example.demo.entities.Screens;
import com.example.demo.entities.Showtimes;
import com.example.demo.entities.Theaters;

public interface ScreenService {
	public Screens findById(int screenId);
}
