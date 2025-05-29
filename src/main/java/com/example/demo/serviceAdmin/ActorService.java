package com.example.demo.serviceAdmin;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;

import com.example.demo.entities.Actor;

public interface ActorService {
	public Iterable<Actor> findAll();
	public Actor findById(int id);
	public Set<Actor> findByIds(List<Integer> ids);
	public boolean save(Actor actor);
	public boolean delete(int actorId);
	public Page<Actor> findAll(int page, int size);
}
