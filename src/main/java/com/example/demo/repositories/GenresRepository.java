package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.Genres;
import com.example.demo.entities.Movies;

@Repository
public interface GenresRepository extends CrudRepository<Genres, Integer> {
}
