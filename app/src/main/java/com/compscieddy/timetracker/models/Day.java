package com.compscieddy.timetracker.models;

import com.orm.SugarRecord;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by elee on 4/28/16.
 */
public class Day extends SugarRecord {

  Date date;
  Calendar calendar = Calendar.getInstance();

  public Day() {}

  public Day(Date date) {
    this.date = date;
    calendar.setTime(this.date);
    save();
  }

  public Date getDate() {
    return this.date;
  }

  public boolean isSameDay(Date d) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(d);
    boolean sameDay = this.calendar.get(Calendar.YEAR) == cal.get(Calendar.YEAR)
        && this.calendar.get(Calendar.DAY_OF_YEAR) == cal.get(Calendar.DAY_OF_YEAR);
    return sameDay;
  }

  public List<Event> getEvents() {
    return Event.find(Event.class, "day = ?", getId().toString());
  }

}
