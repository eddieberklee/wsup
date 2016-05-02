package com.compscieddy.timetracker.models;

import com.orm.SugarRecord;

/**
 * Created by elee on 4/28/16.
 */
public class Event extends SugarRecord {

  String title;
  int color;
  Day day;

  public Event() {}

  public Event(Day day) {
    this.day = day;
    save();
  }

  public void setTitle(String title) {
    this.title = title;
    save();
  }

  public void setColor(int color) {
    this.color = color;
    save();
  }

}
