package com.example.demo.repostitoriesAdmin;




import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entities.Genres;

public interface GenresRepositoryAdmin extends JpaRepository<Genres, Integer> {
	
}
