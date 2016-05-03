package com.compscieddy.timetracker.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by elee on 5/3/16.
 * http://stackoverflow.com/a/5763815/4326052
 *
 */
public class LockableScrollView extends ScrollView {

  public LockableScrollView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  private boolean isScrollable = true;

  public void setScrollable(boolean isScrollable) {
    this.isScrollable = isScrollable;
  }

  public boolean isScrollable() {
    return isScrollable;
  }

  @Override
  public boolean onTouchEvent(MotionEvent ev) {
    switch (ev.getAction()) {
      case MotionEvent.ACTION_DOWN:
        if (!isScrollable) return false;
    }
    return super.onTouchEvent(ev);
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    if (!isScrollable) return false;
    return super.onInterceptTouchEvent(ev);
  }
}
