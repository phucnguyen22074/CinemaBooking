package com.example.demo.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public class BookingHistoryDTO {
	private Integer bookingId;
	
	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	private Date bookingDate;
	private BigDecimal totalPrice;
	private String barcode;
	private List<BookingDetailDTO> bookingDetails;

	public static class BookingDetailDTO {
		private Integer bookingDetailId;
		private String seatNumber;
		private BigDecimal price;
		private String theaterName;
		private String showDate;
		private String startTime;
		private String movieTitle;

		// Constructor
		public BookingDetailDTO() {
		}

		public BookingDetailDTO(Integer bookingDetailId, String seatNumber, BigDecimal price, String theaterName,
				String showDate, String startTime) {
			this.bookingDetailId = bookingDetailId;
			this.seatNumber = seatNumber;
			this.price = price;
			this.theaterName = theaterName;
			this.showDate = showDate;
			this.startTime = startTime;
		}
		

		public String getMovieTitle() {
			return movieTitle;
		}

		public void setMovieTitle(String movieTitle) {
			this.movieTitle = movieTitle;
		}

		// Getters and Setters
		public Integer getBookingDetailId() {
			return bookingDetailId;
		}

		public void setBookingDetailId(Integer bookingDetailId) {
			this.bookingDetailId = bookingDetailId;
		}

		public String getSeatNumber() {
			return seatNumber;
		}

		public void setSeatNumber(String seatNumber) {
			this.seatNumber = seatNumber;
		}

		public BigDecimal getPrice() {
			return price;
		}

		public void setPrice(BigDecimal price) {
			this.price = price;
		}

		public String getTheaterName() {
			return theaterName;
		}

		public void setTheaterName(String theaterName) {
			this.theaterName = theaterName;
		}

		public String getShowDate() {
			return showDate;
		}

		public void setShowDate(String showDate) {
			this.showDate = showDate;
		}

		public String getStartTime() {
			return startTime;
		}

		public void setStartTime(String startTime) {
			this.startTime = startTime;
		}
	}

	// Getters and Setters
	public Integer getBookingId() {
		return bookingId;
	}

	public void setBookingId(Integer bookingId) {
		this.bookingId = bookingId;
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

	public List<BookingDetailDTO> getBookingDetails() {
		return bookingDetails;
	}

	public void setBookingDetails(List<BookingDetailDTO> bookingDetails) {
		this.bookingDetails = bookingDetails;
	}
	
	
}
