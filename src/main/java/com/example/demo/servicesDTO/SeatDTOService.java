package com.example.demo.servicesDTO;

import java.util.List;

import com.example.demo.dto.SeatsDTO;

public interface SeatDTOService {
	public List<SeatsDTO> getSeatsByShowtime(Integer showtimeId);
}
