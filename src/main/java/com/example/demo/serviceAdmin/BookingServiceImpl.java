package com.example.demo.serviceAdmin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.entities.Bookings;
import com.example.demo.repostitoriesAdmin.BookingsRepositoryAdmin;
@Service("bookingService")
public class BookingServiceImpl implements BookingService{
	
	@Autowired
	private BookingsRepositoryAdmin bookingsRepository;

	@Override
	public Page<Bookings> findAll(int page, int size) {
		// TODO Auto-generated method stub
		Pageable pageable = PageRequest.of(page, size);
		return bookingsRepository.findAll(pageable);
	}

	@Override
	public Bookings findById(int id) {
		// TODO Auto-generated method stub
		return bookingsRepository.findById(id).get();
	}

	@Override
	public boolean save(Bookings bookings) {
		try {
			bookingsRepository.save(bookings);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean delete(int bookingId) {
		try {
			bookingsRepository.deleteById(bookingId);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public Iterable<Bookings> findAllSize() {
		// TODO Auto-generated method stub
		return bookingsRepository.findAll();
	}
	
}
