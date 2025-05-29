package com.example.demo.repositories;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.Screens;
import com.example.demo.entities.Showtimes;

@Repository
public interface ScreenRepository extends CrudRepository<Screens, Integer> {
	
}
