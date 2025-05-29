package com.example.demo.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public class BookingsDTO {
	private Integer bookingId;
	private Integer userId;
	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	private Date bookingDate;
	private BigDecimal totalPrice;
	private String barcode;
	private List<BookingDetailsDTO> bookingDetails;

	// Getters and Setters
	public Integer getBookingId() {
		return bookingId;
	}

	public void setBookingId(Integer bookingId) {
		this.bookingId = bookingId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Date getBookingDate() {
		return bookingDate;
	}

	public void setBookingDate(Date bookingDate) {
		this.bookingDate = bookingDate;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public List<BookingDetailsDTO> getBookingDetails() {
		return bookingDetails;
	}

	public void setBookingDetails(List<BookingDetailsDTO> bookingDetails) {
		this.bookingDetails = bookingDetails;
	}
}