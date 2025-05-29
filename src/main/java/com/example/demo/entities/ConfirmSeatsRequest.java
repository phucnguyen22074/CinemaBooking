package com.example.demo.entities;

import java.util.List;

public class ConfirmSeatsRequest {
    private List<Integer> seatIds;
    private Integer showtimeId;

    // Getters and Setters
    public List<Integer> getSeatIds() {
        return seatIds;
    }

    public void setSeatIds(List<Integer> seatIds) {
        this.seatIds = seatIds;
    }

    public Integer getShowtimeId() {
        return showtimeId;
    }

    public void setShowtimeId(Integer showtimeId) {
        this.showtimeId = showtimeId;
    }
}