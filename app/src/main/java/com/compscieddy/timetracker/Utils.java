package com.compscieddy.timetracker;

import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.compscieddy.eddie_utils.Lawg;

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
    for (String s : strings) {
      if (src.toLowerCase().contains(s)) return true;
    }
    return false;
  }

  public static String getFormattedDuration(long duration) {
    long seconds = duration / (long) 1000;
    long minutes = seconds / (long) 60;
    long hours = minutes / (long) 60;
    if (true) lawg.e(" seconds: " + seconds + " minutes: " + minutes + " hours: " + hours);
    StringBuilder builder = new StringBuilder();
    if (hours >= 1) {
      builder.append(String.valueOf((int) hours));
      builder.append("hr\n");
    }
    if (minutes >= 1) {
      builder.append(String.valueOf(Math.round(minutes) % 60));
      builder.append("m\n");
    }
    if (seconds >= 1) {
      builder.append(String.valueOf(seconds % 60));
      builder.append("s");
    } else {
      lawg.e("[Warning] Time duration is less than 1 second.");
      return "";
    }
    return builder.toString();
  }

}
