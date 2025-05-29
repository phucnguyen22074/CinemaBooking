package com.example.demo.dto;

import java.util.List;

public class ScreensDTO {
	private Integer screenId;
	private String name;
	private int totalSeats;
	private Integer theaterId;
	private List<ShowtimesDTO> showtimes;

	public Integer getScreenId() {
		return screenId;
	}

	public void setScreenId(Integer screenId) {
		this.screenId = screenId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getTotalSeats() {
		return totalSeats;
	}

	public void setTotalSeats(int totalSeats) {
		this.totalSeats = totalSeats;
	}

	public Integer getTheaterId() {
		return theaterId;
	}

	public void setTheaterId(Integer theaterId) {
		this.theaterId = theaterId;
	}

	public List<ShowtimesDTO> getShowtimes() {
		return showtimes;
	}

	public void setShowtimes(List<ShowtimesDTO> showtimes) {
		this.showtimes = showtimes;
	}

	
	
}
