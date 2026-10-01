package com.webservice.week05.repository;

import com.webservice.week05.domain.Movie;

import java.util.List;
import java.util.Optional;

public interface MovieRepository {
    Movie save(Movie movie);
    List<Movie> findAll();
    Optional<Movie> findById(Long id);
    Movie update(Movie movie);
    void deleteById(Long id);
}
