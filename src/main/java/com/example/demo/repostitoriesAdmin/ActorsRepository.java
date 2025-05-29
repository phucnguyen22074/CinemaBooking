package com.example.demo.repostitoriesAdmin;



import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entities.Actor;

public interface ActorsRepository extends JpaRepository<Actor, Integer> {
	
}
