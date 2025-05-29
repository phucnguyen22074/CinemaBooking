package com.example.demo.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ShowtimesDTO {
	private Integer showtimeId;

	@JsonFormat(pattern = "dd/MM/yyyy")
	private Date showDate;
	
	@JsonFormat(pattern = "HH:mm")
	private Date startTime;
	
	@JsonFormat(pattern = "HH:mm")
	private Date endTime;
	
	

	public Integer getShowtimeId() {
		return showtimeId;
	}

	public void setShowtimeId(Integer showtimeId) {
		this.showtimeId = showtimeId;
	}

	public Date getShowDate() {
		return showDate;
	}

	public void setShowDate(Date showDate) {
		this.showDate = showDate;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

}
