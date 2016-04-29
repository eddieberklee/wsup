package com.compscieddy.timetracker.models;

import com.orm.SugarRecord;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by elee on 4/28/16.
 */
public class Day extends SugarRecord {

  int year;
  int dayOfYear;

  public Day() {}

  public Day(int year, int dayOfYear) {
    this.year = year;
    this.dayOfYear = dayOfYear;
    save();
  }

  public boolean isSameDay(Date d) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(d);
    boolean sameDay = year == cal.get(Calendar.YEAR)
        && dayOfYear == cal.get(Calendar.DAY_OF_YEAR);
    return sameDay;
  }

  public List<Event> getEvents() {
    return Event.find(Event.class, "day = ?", getId().toString());
  }

}
