package com.example.demo.dto;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

public class MoviesDTO {
	private Integer movieId;
	private String title;
	private String description;
	private int duration;

	@JsonProperty(access = Access.READ_WRITE) // Cho phép cả đọc và ghi
	@JsonFormat(pattern = "dd-MM-yyyy") // Định dạng ngày tháng
	private Date releaseDate;
	private String imageUrl;
	private String trailerUrl;
	private Boolean status;
	private Double rating;
	private String photoUrl;
	private String trailer;
	private List<ActorDTO> actors;
	private List<DirectorDTO> directors;
	private List<GenresDTO> genres;
	private List<String> theaters;

	// Getters and Setters
	public Integer getMovieId() {
		return movieId;
	}

	public void setMovieId(Integer movieId) {
		this.movieId = movieId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public Date getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getTrailerUrl() {
		return trailerUrl;
	}

	public void setTrailerUrl(String trailerUrl) {
		this.trailerUrl = trailerUrl;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public Double getRating() {
		return rating;
	}

	public void setRating(Double rating) {
		this.rating = rating;
	}

	public List<ActorDTO> getActors() {
		return actors;
	}

	public void setActors(List<ActorDTO> actors) {
		this.actors = actors;
	}

	public List<DirectorDTO> getDirectors() {
		return directors;
	}

	public void setDirectors(List<DirectorDTO> directors) {
		this.directors = directors;
	}

	public List<GenresDTO> getGenres() {
		return genres;
	}

	public void setGenres(List<GenresDTO> genres) {
		this.genres = genres;
	}

	public List<String> getTheaters() {
		return theaters;
	}

	public void setTheaters(List<String> theaters) {
		this.theaters = theaters;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public String getTrailer() {
		return trailer;
	}

	public void setTrailer(String trailer) {
		this.trailer = trailer;
	}

}
