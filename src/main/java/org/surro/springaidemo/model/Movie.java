package org.surro.springaidemo.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"name", "year", "genre", "imdb"})
public record Movie(String name, int year, String genre, String imdbRating) {
}
