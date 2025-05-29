package com.example.demo.repostitoriesAdmin;



import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entities.Director;

public interface DirectorsRepository extends JpaRepository<Director, Integer> {
	
}
