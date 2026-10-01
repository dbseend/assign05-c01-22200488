package com.webservice.week05.dto;

import com.webservice.week05.domain.Movie;

public record MovieResponse(
        Long id,
        String title,
        String director,
        String genre,
        int releaseYear,
        double rating,
        int runningTime
) {
    public static MovieResponse from(Movie movie) {
        return new MovieResponse(
                movie.getId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getGenre(),
                movie.getReleaseYear(),
                movie.getRating(),
                movie.getRunningTime()
        );
    }
}
