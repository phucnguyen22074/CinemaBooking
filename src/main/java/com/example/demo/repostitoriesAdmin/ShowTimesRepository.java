package com.example.demo.repostitoriesAdmin;




import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entities.Showtimes;

public interface ShowTimesRepository extends JpaRepository<Showtimes, Integer> {
	
}
