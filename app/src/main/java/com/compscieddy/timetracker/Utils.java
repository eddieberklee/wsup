package com.compscieddy.timetracker;

import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.compscieddy.eddie_utils.Lawg;
import com.compscieddy.timetracker.models.Event;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by elee on 5/2/16.
 */
public class Utils {
  private static final Lawg lawg = Lawg.newInstance(Utils.class.getSimpleName());

  public static float mapValue(float value, float min1, float max1, float min2, float max2) {
    float firstSpan = max1 - min1;
    float secondSpan = max2 - min2;
    float scaledValue = (value - min1) / firstSpan;
    scaledValue = Math.min(scaledValue, 1.0f);
    return min2 + (scaledValue * secondSpan);
  }

  public static void setMarginTop(View view, int margin) {
    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
    layoutParams.topMargin = margin;
  }

  public static void setMarginLeft(View view, int margin) {
    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
    layoutParams.leftMargin = margin;
  }

  public static void setMarginRight(View view, int margin) {
    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
    layoutParams.rightMargin = margin;
  }

  public static void setMarginBottom(View view, int margin) {
    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
    layoutParams.bottomMargin = margin;
  }

  /**********************************************************************/

  public static boolean isTextViewEllipsized(TextView textView) {
    Layout layout = textView.getLayout();
    if (layout != null) {
      int lines = layout.getLineCount();
      if (lines > 0 && layout.getEllipsisCount(lines - 1) > 0) {
        return true;
      }
    }
    return false;
  }

  public static boolean containsAtLeastOne(String src, String[] strings) {
    // TODO: regex isn't good enough - doesn't work with the start or end of line
    src = src.toLowerCase();
    for (String s : strings) {
      Pattern p = Pattern.compile(".*[^a-z]" + s + "[^a-z].*");
      Matcher m = p.matcher(src);
      if (m.matches()) {
        return true;
      }
    }
    return false;
  }

  public static String getFormattedDuration(long duration, boolean isMostRecentItem) {
    long seconds = duration / (long) 1000;
    long minutes = seconds / (long) 60;
    long hours = minutes / (long) 60;
    if (false) lawg.e(" seconds: " + seconds + " minutes: " + minutes + " hours: " + hours);
    StringBuilder builder = new StringBuilder();

    if (hours >= 1) {
      builder.append(String.valueOf((int) hours));
      builder.append("hr\n");
      builder.append(" ");
      builder.append(" ");
    }

    if (minutes >= 1) {
      builder.append(String.valueOf(Math.round(minutes) % 60));
      builder.append("m\n");
      builder.append(" ");
      builder.append(" ");
      builder.append(" ");
      builder.append(" ");
    }

    if (seconds <= 0) {
      seconds = 0;
    }

    if (isMostRecentItem || builder.length() == 0) {
      // Seconds shouldn't be shown if the event is not the most recent item - unless it's less than 1 minute long
      builder.append(String.valueOf(seconds % 60));
      builder.append("s");
    }

    return (builder.toString()).trim();
  }

  public static String getDurationString(int i, List<Event> events, boolean isMostRecentItem) {
    long endTime;
    if (i == events.size() - 1) {
      endTime = System.currentTimeMillis();
    } else {
      Event event = events.get(i + 1);
      endTime = event.getTimeMillis();
    }
    long timeDuration = endTime - events.get(i).getTimeMillis();
    if (false) lawg.e("eventTIME: " + events.get(i).getTimeMillis() + " i: " + i + " timeDuration: " + timeDuration + " final: " + Utils.getFormattedDuration(timeDuration, isMostRecentItem));
    return Utils.getFormattedDuration(timeDuration, isMostRecentItem);
  }

  public static Date getPreviousDate(int numDaysPrevious) {
    Date date = new Date();
    long dayMillis = 1 * 1000 * 60 * 60 * 24;
    long dateTimeMillis = date.getTime();
    long previousTimeMillis = dateTimeMillis - dayMillis * numDaysPrevious;
    date.setTime(previousTimeMillis);
    return date;
  }

}
