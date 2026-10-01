package com.webservice.week05.controller;

import com.webservice.week05.dto.MovieRequest;
import com.webservice.week05.dto.MovieResponse;
import com.webservice.week05.service.MovieService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {
    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MovieResponse create(@Valid @RequestBody MovieRequest request) {
        return movieService.create(request);
    }

    @GetMapping
    public List<MovieResponse> findAll(@RequestParam(required = false) String genre) {
        return movieService.findAll(genre);
    }

    @GetMapping("/{id}")
    public MovieResponse findById(@PathVariable Long id) {
        return movieService.findById(id);
    }

    @PutMapping("/{id}")
    public MovieResponse update(@PathVariable Long id, @Valid @RequestBody MovieRequest request) {
        return movieService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        movieService.delete(id);
    }
}
