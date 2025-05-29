package com.example.demo.dto;

import java.math.BigDecimal;

import com.example.demo.entities.Screens;

public class SeatsDTO {
	private Integer seatId;
	private Integer screensId;
	private String seatNumber;
	private BigDecimal price;
	private Byte status;

	public Integer getSeatId() {
		return seatId;
	}

	public void setSeatId(Integer seatId) {
		this.seatId = seatId;
	}

	public Integer getScreensId() {
		return screensId;
	}

	public void setScreensId(Integer screensId) {
		this.screensId = screensId;
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

	public Byte getStatus() {
		return status;
	}

	public void setStatus(Byte status) {
		this.status = status;
	}

}
