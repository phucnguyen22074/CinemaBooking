package com.example.demo.serviceAdmin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entities.Seats;
import com.example.demo.repostitoriesAdmin.SeatsRepository;

@Service
public class SeatsServiceImpl implements SeatsService{

	@Autowired
	private SeatsRepository seatsRepository;
	
	@Override
	public List<Seats> findByScreensId(int screenId) {
		// TODO Auto-generated method stub
		return seatsRepository.findByScreenId(screenId);
	}

	@Override
	public Iterable<Seats> findAll() {
		// TODO Auto-generated method stub
		return seatsRepository.findAll();
	}

	@Override
	public Seats findById(int id) {
		// TODO Auto-generated method stub
		return seatsRepository.findById(id).get();
	}

	@Override
	public boolean save(Seats seats) {
		// TODO Auto-generated method stub
		try {
			seatsRepository.save(seats);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
