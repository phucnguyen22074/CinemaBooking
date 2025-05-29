package com.example.demo.dto;

import java.math.BigDecimal;

public class BookingDetailsDTO {
	private Integer bookingDetailId;
	private Integer seatId;
	private Integer showtimeId;
	private BigDecimal price;

	// Getters and Setters
	public Integer getBookingDetailId() {
		return bookingDetailId;
	}

	public void setBookingDetailId(Integer bookingDetailId) {
		this.bookingDetailId = bookingDetailId;
	}

	public Integer getSeatId() {
		return seatId;
	}

	public void setSeatId(Integer seatId) {
		this.seatId = seatId;
	}

	public Integer getShowtimeId() {
		return showtimeId;
	}

	public void setShowtimeId(Integer showtimeId) {
		this.showtimeId = showtimeId;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}
}