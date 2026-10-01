package com.webservice.week05.repository;

import com.webservice.week05.domain.Movie;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class MemoryMovieRepository implements MovieRepository {
    private final List<Movie> store = new ArrayList<>();
    private long sequence = 0L;

    @Override
    public synchronized Movie save(Movie movie) {
        movie.setId(++sequence);   // ID 자동 생성 위치
        store.add(movie);
        return movie;
    }

    @Override
    public synchronized List<Movie> findAll() {
        return new ArrayList<>(store);
    }

    @Override
    public synchronized Optional<Movie> findById(Long id) {
        return store.stream()
                .filter(movie -> movie.getId().equals(id))
                .findFirst();
    }

    @Override
    public synchronized Movie update(Movie movie) {
        for (int i = 0; i < store.size(); i++) {
            if (store.get(i).getId().equals(movie.getId())) {
                store.set(i, movie);
                return movie;
            }
        }
        return movie;
    }

    @Override
    public synchronized void deleteById(Long id) {
        store.removeIf(movie -> movie.getId().equals(id));
    }
}
