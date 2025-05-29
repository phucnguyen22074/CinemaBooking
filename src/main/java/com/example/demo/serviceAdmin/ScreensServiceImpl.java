package com.example.demo.serviceAdmin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.entities.Screens;
import com.example.demo.repostitoriesAdmin.ScreensRepository;

@Service("screensService")
public class ScreensServiceImpl implements ScreensService {

	@Autowired
	private ScreensRepository screensRepository;

	@Override
	public Page<Screens> findAll(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return screensRepository.findAll(pageable);
	}

	@Override
	public boolean save(Screens screens) {
		try {
			screensRepository.save(screens);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public Screens findById(int id) {
		// TODO Auto-generated method stub
		return screensRepository.findById(id).get();
	}

	@Override
	public Iterable<Screens> findAllSize() {
		// TODO Auto-generated method stub
		return screensRepository.findAll();
	}

	@Override
	public boolean delete(int id) {
		try {
			screensRepository.deleteById(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public Page<Screens> filterScreens(String nameScreen, String nameTheater, Integer seats,
			int page, int size) {

		return screensRepository.filterScreens(nameScreen, nameTheater, seats, PageRequest.of(page, size));
	}

}
