package com.example.demo.serviceAdmin;

import org.springframework.data.domain.Page;

import com.example.demo.entities.BookingDetails;
import com.example.demo.entities.Bookings;

public interface BookingService {
	
	public Page<Bookings> findAll(int page, int size); 
	
	public Bookings findById(int id);
	
	
	public boolean save(Bookings bookings);
	
	public boolean delete(int bookingId);
	
	public Iterable<Bookings> findAllSize();
}
