package com.soma.doubanen.domains.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MediaGenre {
  Ambient,
  Blues,
  ClassicalMusic("Classical Music"),
  Country,
  Dance,
  Electronic,
  Experimental,
  Folk,
  HipHop("Hip Hop"),
  IndustrialAndNoise("Industrial & Noise"),
  Jazz,
  Metal,
  MusicalTheatreAndEntertainment("Musical Theatre and Entertainment"),
  NewAge("New Age"),
  Pop,
  Psychedelia,
  Punk,
  RnB("R&B"),
  RegionalMusic,
  Rock,
  SingerSongwriter("Singer-Songwriter"),
  SpokenWord,
  Action,
  Adventure,
  Animation,
  Biography,
  Comedy,
  Crime,
  Documentary,
  Drama,
  Fantasy,
  History,
  Horror,
  Mystery,
  Musical,
  Romance,
  SciFi("Sci-Fi"),
  War,
  HistoricalFiction("Historical Fiction"),
  ContemporaryFiction("Contemporary Fiction"),
  LiteraryFiction("Literary Fiction"),
  Poetry,
  Plays,
  Philosophy,
  Politics,
  Essays,

  NonFiction("Non-Fiction");

  private final String genre;

  MediaGenre() {
    this.genre = name();
  }

  MediaGenre(String genre) {
    this.genre = genre;
  }

  @JsonValue
  public String getGenre() {
    return genre;
  }

  @Override
  public String toString() {
    return genre;
  }
}
