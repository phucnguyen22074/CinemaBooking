package com.example.demo.servicesDTO;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.SeatsDTO;
import com.example.demo.entities.Screens;
import com.example.demo.entities.Seats;
import com.example.demo.repositories.MssRepository;
import com.example.demo.repositories.SeatRepository;

@Service
public class SeatDTOServiceImpl implements SeatDTOService{
	@Autowired
	private MssRepository mssRepository;
	
	@Autowired SeatRepository seatRepository;
	
	@Autowired
	private ModelMapper modelMapper;

	@Override
	public List<SeatsDTO> getSeatsByShowtime(Integer showtimeId) {
        Screens screen = mssRepository.findScreenByShowtimeId(showtimeId)
                .orElseThrow();
        return seatRepository.findByScreenId(screen.getScreenId())
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
	}
	
	private SeatsDTO convertToDto(Seats seat) {
        return modelMapper.map(seat, SeatsDTO.class);
    }
}
