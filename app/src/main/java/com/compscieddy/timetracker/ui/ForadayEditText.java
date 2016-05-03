package com.compscieddy.timetracker.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.widget.EditText;

import com.compscieddy.eddie_utils.Etils;
import com.compscieddy.timetracker.FontCache;
import com.compscieddy.timetracker.R;

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
    GradientDrawable backgroundDrawable = (GradientDrawable) this.getBackground();
    backgroundDrawable.setStroke(getResources().getDimensionPixelSize(R.dimen.new_event_edit_stroke_width),
        color);
    this.setTextColor(color);
    this.setHighlightColor(Etils.setAlpha(color, 0.4f));
  }

}
