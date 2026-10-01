package com.webservice.week05.dto;

public record MovieRequest(
        String title,
        String director,
        String genre,
        int releaseYear,
        double rating,
        int runningTime
) {}
