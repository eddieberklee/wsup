package com.compscieddy.timetracker.models;

import com.compscieddy.timetracker.Lawg;
import com.orm.SugarRecord;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by elee on 4/28/16.
 */
public class Event extends SugarRecord {

  private static final Lawg lawg = Lawg.newInstance(Event.class.getSimpleName());

  String title;
  int color;
  Day day;
  Date date;
  long timeMillis;

  public Event() {}

  public Event(Day day, Date date) {
    this.day = day;
    this.date = date;
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    this.timeMillis = calendar.getTimeInMillis();
    save();
  }

  public String getTitle() { return title; }
  public Date getEventDate() { return date; }
  public int getColor() { return color; }
  public long getTimeMillis() { return timeMillis; }

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
    nextCalendar.setTime(nextEvent.getEventDate());
    return getMinutesDifference(nextCalendar.getTimeInMillis());
  }

  public long getMinutesDifference(long nextEventTimeInMillis) {
    Calendar currentCalendar = Calendar.getInstance();
    currentCalendar.setTime(this.getEventDate());
    long millisDifference = currentCalendar.getTimeInMillis() - nextEventTimeInMillis;
    long minutesDifference = millisDifference / 1000 / 60;
    return minutesDifference;
  }

  public String getTimeText() {
    Date eventDate = getEventDate();
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(eventDate);

    int hour = calendar.get(Calendar.HOUR);
    if (hour == 0) hour = 12;
    int amPm = calendar.get(Calendar.AM_PM);
//    String amPmString = (amPm == 0) ? "AM" : "PM";
    int minutes = calendar.get(Calendar.MINUTE);
    String minutesString = (minutes < 10) ? "0".concat(String.valueOf(minutes)) : String.valueOf(minutes);

    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(hour);
    stringBuilder.append(":");
    stringBuilder.append(minutesString);
//    stringBuilder.append(" ");
//    stringBuilder.append(amPmString);
    return stringBuilder.toString();
  }

  public String getTimeAmPmText() {
    Date eventDate = getEventDate();
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(eventDate);
    int amPm = calendar.get(Calendar.AM_PM);
    String amPmString = (amPm == 0) ? "am" : "pm";
    return amPmString;
  }

}
