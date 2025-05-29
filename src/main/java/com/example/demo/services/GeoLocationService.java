package com.example.demo.services;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.entities.GeoLocation;

@Service
public class GeoLocationService {

	private static final String API_URL = "http://ip-api.com/json/";

	public double[] getCoordinatesByIp(String ip) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.getForObject(API_URL + ip, Map.class);
        
        if (response != null && "success".equals(response.get("status"))) {
            double latitude = (double) response.get("lat");
            double longitude = (double) response.get("lon");
            return new double[]{latitude, longitude};
        }
        return null;
    }
}