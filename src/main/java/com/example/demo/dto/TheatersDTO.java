package com.example.demo.dto;

import java.util.List;

public class TheatersDTO {
	private Integer theaterId;
	private Integer brandId;
	private String brandImage;
	private String brand;
	private Integer locationId;
	private String name;
	private String address;
	private List<ScreensDTO> screens;
	
	public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

	public Integer getTheaterId() {
		return theaterId;
	}

	public void setTheaterId(Integer theaterId) {
		this.theaterId = theaterId;
	}

	public Integer getBrandId() {
		return brandId;
	}

	public void setBrandId(Integer brandId) {
		this.brandId = brandId;
	}

	public Integer getLocationId() {
		return locationId;
	}

	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public List<ScreensDTO> getScreens() {
		return screens;
	}

	public void setScreens(List<ScreensDTO> screens) {
		this.screens = screens;
	}

	public String getBrandImage() {
		return brandImage;
	}

	public void setBrandImage(String brandImage) {
		this.brandImage = brandImage;
	}

	
}
