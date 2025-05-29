package com.example.demo.dto;

import java.util.List;

public class CreatePaymentRequest {
    private double amount;
    private String currency;
    private String returnUrl;
    private String cancelUrl;
    private int showtimeId;
    private List<Integer> seatIds;
    private int userId;

    // Default constructor
    public CreatePaymentRequest() {
    }

    // Full constructor
    public CreatePaymentRequest(double amount, String currency, String returnUrl, String cancelUrl,
                                int showtimeId, List<Integer> seatIds, int userId) {
        this.amount = amount;
        this.currency = currency;
        this.returnUrl = returnUrl;
        this.cancelUrl = cancelUrl;
        this.showtimeId = showtimeId;
        this.seatIds = seatIds;
        this.userId = userId;
    }

    // Getters và Setters
    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getCancelUrl() {
        return cancelUrl;
    }

    public void setCancelUrl(String cancelUrl) {
        this.cancelUrl = cancelUrl;
    }

    public int getShowtimeId() {
        return showtimeId;
    }

    public void setShowtimeId(int showtimeId) {
        this.showtimeId = showtimeId;
    }

    public List<Integer> getSeatIds() {
        return seatIds;
    }

    public void setSeatIds(List<Integer> seatIds) {
        this.seatIds = seatIds;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}