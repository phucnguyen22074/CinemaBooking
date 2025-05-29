package com.example.demo.servicesDTO;

import java.util.List;

import com.example.demo.dto.BookingHistoryDTO;
import com.example.demo.dto.BookingRequestDTO;
import com.example.demo.dto.BookingsDTO;
import com.example.demo.entities.Bookings;

public interface BookingDTOService {
	public BookingsDTO createBooking(BookingRequestDTO request);
	
	public List<BookingHistoryDTO> getBookingHistoryByEmail(String email);
}
