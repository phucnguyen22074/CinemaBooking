package com.example.demo.services;

import java.util.List;
import java.util.Map;

import com.example.demo.entities.Bookings;
import com.example.demo.entities.Users;

public interface BookingService {
	public List<Bookings> getBookingsByUser(Users user);
	
	public List<Map<String, Object>> getBookingHistoryByEmail(String email);
}
