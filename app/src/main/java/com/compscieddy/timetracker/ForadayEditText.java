package com.compscieddy.timetracker;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.EditText;

import com.compscieddy.eddie_utils.Etils;

/**
 * Created by elee on 1/7/16.
 */
public class ForadayEditText extends EditText {

  public ForadayEditText(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }

  private void init(Context context, AttributeSet attrs) {
    if (isInEditMode()) return;

    TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ForadayEditText);
    int typefaceId = ta.getInt(R.styleable.ForadayEditText_fontface, FontCache.MONTSERRAT_REGULAR);
    int color = ta.getInt(R.styleable.ForadayEditText_color, -1);
    ta.recycle();

    if (color != -1) {
      setColor(color);
    }
    setTypeface(FontCache.get(context, typefaceId));
  }

  public void setColor(int color) {
    Etils.applyColorFilter(this.getBackground(), color, true);
    this.setTextColor(color);
    this.setHighlightColor(Etils.setAlpha(color, 0.4f));
  }

}
