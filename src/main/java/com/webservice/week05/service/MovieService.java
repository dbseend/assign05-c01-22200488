package com.webservice.week05.service;

import com.webservice.week05.domain.Movie;
import com.webservice.week05.dto.MovieRequest;
import com.webservice.week05.dto.MovieResponse;
import com.webservice.week05.repository.MovieRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class MovieService {
    private final MovieRepository repository;

    public MovieService(MovieRepository repository) {
        this.repository = repository;
    }

    public MovieResponse create(MovieRequest request) {
        Movie movie = new Movie(
                request.title(),
                request.director(),
                request.genre(),
                request.releaseYear(),
                request.rating(),
                request.runningTime()
        );
        return MovieResponse.from(repository.save(movie));
    }

    /** genre가 비어 있으면 전체, 아니면 장르가 일치하는 영화만 반환 (대소문자 무시). */
    public List<MovieResponse> findAll(String genre) {
        return repository.findAll().stream()
                .filter(movie -> genre == null || genre.isBlank()
                        || movie.getGenre().equalsIgnoreCase(genre.trim()))
                .map(MovieResponse::from)
                .toList();
    }

    public MovieResponse findById(Long id) {
        return MovieResponse.from(getOrThrow(id));
    }

    public MovieResponse update(Long id, MovieRequest request) {
        Movie movie = getOrThrow(id);
        movie.setTitle(request.title());
        movie.setDirector(request.director());
        movie.setGenre(request.genre());
        movie.setReleaseYear(request.releaseYear());
        movie.setRating(request.rating());
        movie.setRunningTime(request.runningTime());
        return MovieResponse.from(repository.update(movie));
    }

    public void delete(Long id) {
        getOrThrow(id);
        repository.deleteById(id);
    }

    private Movie getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found with id: " + id));
    }
}
