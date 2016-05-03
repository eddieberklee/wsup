package com.compscieddy.timetracker.models;

import com.orm.SugarRecord;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by elee on 4/28/16.
 */
public class Event extends SugarRecord {

  String title;
  int color;
  Day day;
  Date date;

  public Event() {}

  public Event(Day day, Date date) {
    this.day = day;
    this.date = date;
    save();
  }

  public String getTitle() {
    return title;
  }
  public Date getDate() { return date; }

  public void setTitle(String title) {
    this.title = title;
    save();
  }

  public void setColor(int color) {
    this.color = color;
    save();
  }

  public long getMinutesDifference(Event nextEvent) {
    Calendar nextCalendar = Calendar.getInstance();
    nextCalendar.setTime(nextEvent.getDate());
    return getMinutesDifference(nextCalendar.getTimeInMillis());
  }

  public long getMinutesDifference(long nextEventTimeInMillis) {
    Calendar currentCalendar = Calendar.getInstance();
    currentCalendar.setTime(this.getDate());
    long millisDifference = currentCalendar.getTimeInMillis() - nextEventTimeInMillis;
    long minutesDifference = millisDifference / 1000 / 60;
    return minutesDifference;
  }

}
