package com.example.demo.serviceAdmin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.entities.Director;
import com.example.demo.repostitoriesAdmin.DirectorsRepository;

@Service("directorService")
public class DirectorServiceImpl implements DirectorService{
	
	@Autowired
	private DirectorsRepository directorsRepository;

	@Override
	public Iterable<Director> findAll() {
		// TODO Auto-generated method stub
		return directorsRepository.findAll();
	}

	@Override
	public Director findById(int id) {
		// TODO Auto-generated method stub
		return directorsRepository.findById(id).get();
	}

	@Override
	public Set<Director> findByIds(List<Integer> ids) {
		// TODO Auto-generated method stub
		return new HashSet<>(directorsRepository.findAllById(ids));
	}

	@Override
	public boolean save(Director director) {
		try {
			directorsRepository.save(director);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean delete(int directorId) {
		try {
			directorsRepository.deleteById(directorId);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public Page<Director> findAll(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return directorsRepository.findAll(pageable);
	}

}
