package com.example.demo.serviceAdmin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.entities.Showtimes;
import com.example.demo.repostitoriesAdmin.ShowTimesRepository;

import jakarta.transaction.Transactional;

@Service("showtimeService")
public class ShowTimeServiceImpl implements ShowTimeService {
	
	@Autowired
	private ShowTimesRepository showTimesRepository;
	
	@Override
	public Page<Showtimes> findAll(int page, int size) {
		// TODO Auto-generated method stub
		Pageable pageable = PageRequest.of(page, size);
		return showTimesRepository.findAll(pageable);
	}

	@Override
	public Showtimes findById(int id) {
		// TODO Auto-generated method stub
		return showTimesRepository.findById(id).get();
	}

	@Override
	@Transactional
	public boolean save(Showtimes showtimes) {
		try {
			showTimesRepository.saveAndFlush(showtimes);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean delete(int showtimeId) {
		try {
			showTimesRepository.deleteById(showtimeId);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public Iterable<Showtimes> findAllSize() {
		// TODO Auto-generated method stub
		return showTimesRepository.findAll();
	}

	@Override
	public Showtimes getShowtimeDetails(Integer showtimeId) {
		// TODO Auto-generated method stub
		if (showtimeId != null) {
            return showTimesRepository.findById(showtimeId)
                    .orElse(null); 
        } else {
            return showTimesRepository.findAll()
                    .stream()
                    .findFirst()
                    .orElse(null);
        }
	}
	
}
