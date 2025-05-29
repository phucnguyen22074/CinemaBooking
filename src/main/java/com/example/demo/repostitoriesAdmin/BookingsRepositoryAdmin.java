package com.example.demo.repostitoriesAdmin;




import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entities.Bookings;

public interface BookingsRepositoryAdmin extends JpaRepository<Bookings, Integer> {
	
}
