package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.Movies;
import com.example.demo.entities.Users;

@Repository
public interface UserRepository extends CrudRepository<Users, Integer> {

	@Query("FROM Users WHERE email = :email")
	public Users findByEmail(@Param("email") String email);
}
