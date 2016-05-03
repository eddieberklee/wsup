package com.compscieddy.timetracker;

/**
 * Created by elee on 5/2/16.
 */
public class Utils {

  public static float mapValue(float value, float min1, float max1, float min2, float max2) {
    float firstSpan = max1 - min1;
    float secondSpan = max2 - min2;
    float scaledValue = (value - min1) / firstSpan;
    return min2 + (scaledValue * secondSpan);
  }

}
