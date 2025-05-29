package com.example.demo.serviceAdmin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entities.Genres;
import com.example.demo.repostitoriesAdmin.GenresRepositoryAdmin;

@Service("genresServiceAdmin")
public class GenresServiceImpl implements GenresService{
	
	@Autowired
	private GenresRepositoryAdmin genresRepository;

	@Override
	public Iterable<Genres> findAll() {
		// TODO Auto-generated method stub
		return genresRepository.findAll();
	}

	@Override
	public Genres findById(int id) {
		// TODO Auto-generated method stub
		return genresRepository.findById(id).get();
	}

	@Override
	public Set<Genres> findByIds(List<Integer> ids) {
		// TODO Auto-generated method stub
		return new HashSet<>(genresRepository.findAllById(ids));
	}

}
