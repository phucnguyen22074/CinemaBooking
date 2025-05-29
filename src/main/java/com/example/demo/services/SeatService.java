package com.example.demo.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public interface SeatService {
	public void bookSeats(Set<Integer> seatIds);

	public BigDecimal calculateTotalPrice(List<Integer> seatIds);
	
	public String createPendingBooking(List<Integer> seatIds, Integer userId, Integer showtimeId);
	
	public void confirmBooking(String bookingCode);
}
