package com.example.demo.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entities.Screens;
import com.example.demo.repositories.ScreenRepository;

@Service("showTimeService")
public class ScreenServiceImpl implements ScreenService {

    @Autowired
    private ScreenRepository screenRepository;

    public Screens findById(int screenId) {
        if (screenId <= 0) {
            throw new IllegalArgumentException("ID phòng chiếu phải lớn hơn 0.");
        }

        return screenRepository.findById(screenId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng chiếu với ID: " + screenId));
    }
}
