package com.compscieddy.timetracker.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import com.compscieddy.timetracker.FontCache;
import com.compscieddy.timetracker.R;

/**
 * Created by elee on 1/6/16.
 */
public class ForadayTextView extends TextView {

  public ForadayTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }

  private void init(Context context, AttributeSet attrs) {
    if (isInEditMode()) return;

    TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ForadayTextView);
    int typefaceId = ta.getInt(R.styleable.ForadayTextView_fontface, FontCache.MONTSERRAT_REGULAR);
    setTypeface(FontCache.get(context, typefaceId));
    ta.recycle();

  }


}
