package com.compscieddy.timetracker.models;

import com.compscieddy.eddie_utils.Lawg;
import com.orm.SugarRecord;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by elee on 4/28/16.
 */
public class Event extends SugarRecord {

  private static final Lawg lawg = Lawg.newInstance(Event.class.getSimpleName());

  String title;
  int color;
  Day day;
  Date date;
  long dateMillis;

  public Event() {}

  public Event(Day day, Date date) {
    this.day = day;
    this.date = date;
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    this.dateMillis = calendar.getTimeInMillis();
    save();
  }

  public String getTitle() { return title; }
  public Date getDate() { return date; }
  public int getColor() { return color; }
  public long getDateMillis() { return dateMillis; }

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
    Date eventDate = getDate();
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(eventDate);
    int amPm = calendar.get(Calendar.AM_PM);
    String amPmString = (amPm == 0) ? "am" : "pm";
    return amPmString;
  }

  public String getDuration() {
    // Find the next event by looking for an event belonging to the same day but has a larger dateMillis
    List<Event> events = Event.find(Event.class, "day = ? & date_millis > ?", this.day.getId().toString(), String.valueOf(this.dateMillis));
    String durationString = "";
    if (events.size() > 0) {
      Event nextEvent = events.get(0);
      long nextEventMillis = nextEvent.getDateMillis();
      long duration = nextEventMillis - getDateMillis();
      durationString = Event.getFormattedDuration(duration);
    }
    return durationString;
  }







  /*********************** Static ***********************/

  public static String getFormattedDuration(long duration) {
    long seconds = duration / (long) 1000;
    long minutes = seconds / (long) 60;
    long hours = minutes / (long) 60;
    if (hours > 1) {
      return String.format("%.1f", hours);
    } else if (minutes > 1) {
      return String.valueOf(Math.round(minutes));
    } else if (seconds > 1) {
      return String.valueOf(seconds);
    } else {
      lawg.e("UH HOW ARE THERE LESS THAN 1 SECONDS");
      return "";
    }
  }

}
