package com.example.demo.entities;

import com.example.demo.entities.Theaters;

public class TheaterWithDistance {
    private Theaters theater;
    private double distance;

    public TheaterWithDistance(Theaters theater, double distance) {
        this.theater = theater;
        this.distance = distance;
    }

    public Theaters getTheater() {
        return theater;
    }

    public void setTheater(Theaters theater) {
        this.theater = theater;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}