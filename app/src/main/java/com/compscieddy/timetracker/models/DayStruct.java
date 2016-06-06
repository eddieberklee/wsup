package com.compscieddy.timetracker.models;

/**
 * Created by elee on 6/5/16.
 */
public class DayStruct {
  public int dayOfYear;
  public int year;
  public DayStruct(int dayOfYear, int year) {
    this.dayOfYear = dayOfYear;
    this.year = year;
  }
}
