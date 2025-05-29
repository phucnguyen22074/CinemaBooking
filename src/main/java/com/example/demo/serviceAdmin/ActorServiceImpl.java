package com.example.demo.serviceAdmin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.entities.Actor;
import com.example.demo.repostitoriesAdmin.ActorsRepository;

@Service("actorService")
public class ActorServiceImpl implements ActorService{
	
	@Autowired
	private ActorsRepository actorsRepository;

	@Override
	public Iterable<Actor> findAll() {
		// TODO Auto-generated method stub
		return actorsRepository.findAll();
	}

	@Override
	public Actor findById(int id) {
		// TODO Auto-generated method stub
		return actorsRepository.findById(id).get();
	}

	@Override
	public Set<Actor> findByIds(List<Integer> ids) {
		// TODO Auto-generated method stub
		return new HashSet<>(actorsRepository.findAllById(ids));
		 
	}

	@Override
	public boolean save(Actor actor) {
		try {
			actorsRepository.save(actor);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public Page<Actor> findAll(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return actorsRepository.findAll(pageable);
	}

	@Override
	public boolean delete(int actorId) {
		try {
			actorsRepository.deleteById(actorId);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
