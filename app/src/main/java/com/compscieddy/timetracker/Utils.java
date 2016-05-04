package com.compscieddy.timetracker;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by elee on 5/2/16.
 */
public class Utils {

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
  public static void Right(View view, int margin) {
    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
    layoutParams.rightMargin = margin;
  }
  public static void setMarginBottom(View view, int margin) {
    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
    layoutParams.bottomMargin = margin;
  }

}
