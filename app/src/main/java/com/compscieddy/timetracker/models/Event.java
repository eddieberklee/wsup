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

  public String getTitle() { return title; }
  public Date getDate() { return date; }
  public int getColor() { return color; }

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

  public String getTimeText() {
    Date eventDate = getDate();
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(eventDate);

    int hour = calendar.get(Calendar.HOUR);
    if (hour == 0) hour = 12;
    int amPm = calendar.get(Calendar.AM_PM);
    String amPmString = (amPm == 0) ? "am" : "pm";
    int minutes = calendar.get(Calendar.MINUTE);
    String minutesString = (minutes < 10) ? "0".concat(String.valueOf(minutes)) : String.valueOf(minutes);

    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(hour);
    stringBuilder.append(":");
    stringBuilder.append(minutesString);
    stringBuilder.append(amPmString);
    return stringBuilder.toString();
  }

}
