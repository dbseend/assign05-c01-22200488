package com.webservice.week05.domain;

public class Movie {
    private Long id;
    private String title;
    private String director;
    private String genre;
    private int releaseYear;
    private double rating;
    private int runningTime;

    public Movie() {}

    public Movie(String title, String director, String genre, int releaseYear, double rating, int runningTime) {
        this.title = title;
        this.director = director;
        this.genre = genre;
        this.releaseYear = releaseYear;
        this.rating = rating;
        this.runningTime = runningTime;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDirector() { return director; }
    public void setDirector(String director) { this.director = director; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public int getReleaseYear() { return releaseYear; }
    public void setReleaseYear(int releaseYear) { this.releaseYear = releaseYear; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public int getRunningTime() { return runningTime; }
    public void setRunningTime(int runningTime) { this.runningTime = runningTime; }
}
