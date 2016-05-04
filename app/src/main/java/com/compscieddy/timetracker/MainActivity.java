package com.compscieddy.timetracker;

import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DimenRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.compscieddy.eddie_utils.Etils;
import com.compscieddy.eddie_utils.Lawg;
import com.compscieddy.timetracker.models.Day;
import com.compscieddy.timetracker.models.Event;
import com.compscieddy.timetracker.ui.ForadayEditText;
import com.compscieddy.timetracker.ui.LockableScrollView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

  private static final Lawg lawg = Lawg.newInstance(MainActivity.class.getSimpleName());

  @Bind(R.id.new_event_input) ForadayEditText mNewEventInput;
  @Bind(R.id.fake_new_event_input) ForadayEditText mFakeNewEventInput;
  @Bind(R.id.new_event_add_button) View mNewEventAddButton;
  @Bind(R.id.events_container) LinearLayout mEventsContainer;
  @Bind(R.id.root_view) View mRootView;
  @Bind(R.id.events_scroll_view) LockableScrollView mEventsScrollView;
  @Bind(R.id.new_event_dot) View mNewEventDot;

  Day mCurrentDay;
  List<Event> mEvents;
  private LayoutInflater mLayoutInflater;

  int[] colors = new int[] {
      R.color.flatui_red_1,
      R.color.flatui_red_2,
      R.color.flatui_orange_1,
      R.color.flatui_orange_2,
      R.color.flatui_yellow_1,
      R.color.flatui_yellow_2,
      R.color.flatui_green_1,
      R.color.flatui_green_2,
      R.color.flatui_blue_1,
      R.color.flatui_blue_2,
      R.color.flatui_purple_1,
      R.color.flatui_purple_2,
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mLayoutInflater = getLayoutInflater();
    ViewGroup rootView = (ViewGroup) mLayoutInflater.inflate(R.layout.activity_main, null);
    setContentView(rootView);
    ButterKnife.bind(this);

    mCurrentDay = getCurrentDay();
    if (mCurrentDay == null) {
      lawg.d("Current Day not found, creating a new one");
      mCurrentDay = createCurrentDay();
    }
    initEvents();

    mNewEventDot.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        setNewEventRandomColor();
      }
    });

    setNewEventRandomColor();

    final boolean DEBUG_KEYB_DETECT = false;

    // keyboard detection trick - http://stackoverflow.com/a/4737265/4326052
    mRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
      @Override
      public void onGlobalLayout() {
        int heightDiff = mRootView.getRootView().getHeight() - mRootView.getHeight();
        if (DEBUG_KEYB_DETECT) lawg.d("heightDiff: " + heightDiff + " dp150: " + Etils.dpToPx(150));
        if (heightDiff > Etils.dpToPx(150)) { // if more than 100 pixels, its probably a keyboard...
          mEventsScrollView.fullScroll(View.FOCUS_DOWN);
          if (DEBUG_KEYB_DETECT) lawg.d("Setting scrollable false");
          mEventsScrollView.setScrollable(false);
        } else {
          if (DEBUG_KEYB_DETECT) lawg.d("Setting scrollable true");
          mEventsScrollView.setScrollable(true);
        }
      }
    });

    mNewEventInput.addTextChangedListener(mEventInputTextWatcher);
    mNewEventAddButton.setOnClickListener(mAddButtonOnClickListener);

  }

  TextWatcher mEventInputTextWatcher = new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
      if (s.length() == 0) {
        mNewEventAddButton.setVisibility(View.INVISIBLE);
      } else {
        mNewEventAddButton.setVisibility(View.VISIBLE);
      }
    }
  };

  View.OnClickListener mAddButtonOnClickListener = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      Date currentDate = new Date();
      Event newEvent = new Event(mCurrentDay, currentDate);
      newEvent.setTitle(mNewEventInput.getText().toString());
      newEvent.setColor(mNewEventInput.getCurrentTextColor());
      mNewEventInput.setText("");

      setNewEventRandomColor();

      initEvents();
    }
  };

  public void setNewEventRandomColor() {
    int randomColor = getResources().getColor(colors[(int) Math.round(Math.random() * (colors.length - 1))]);
    mNewEventInput.setColor(randomColor);
    int alphaRandomColor = Etils.setAlpha(randomColor, 0.3f);
    mNewEventInput.setHintTextColor(alphaRandomColor);
    mFakeNewEventInput.setColor(randomColor);
    LayerDrawable layerDrawable = (LayerDrawable) mNewEventDot.getBackground();
    GradientDrawable innerDot = (GradientDrawable) layerDrawable.findDrawableByLayerId(R.id.inner_dot);
    innerDot.setColor(randomColor);
    GradientDrawable outerDot = (GradientDrawable) layerDrawable.findDrawableByLayerId(R.id.outer_dot);
    outerDot.setColor(getResources().getColor(R.color.white));
    outerDot.setStroke(Etils.dpToPx(1), randomColor);
    Etils.applyColorFilter(mNewEventAddButton.getBackground(), randomColor);

    // TODO: don't allow this color to be the color of the previous event (if prev exists)
  }

  @Nullable
  public Day getCurrentDay() {
    Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);

    Day day = null;
    List<Day> days = Day.find(Day.class, "year = ? and day_of_year = ?", String.valueOf(year), String.valueOf(dayOfYear));

    if (days.size() > 1) { // sanity check
      String message = "DAFUQ more than 1 Day found...";
      lawg.e(message);
      Etils.showToast(MainActivity.this, message);
    }
    if (days.size() == 1) {
      day = days.get(0);
    }
    return day;
  }

  public Day createCurrentDay() {
    Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
    Day day = new Day(year, dayOfYear);
    return day;
  }

  /**
   * mCurrentDay needs to be populated before this method.
   */
  private void initEvents() {
    mEvents = Event.find(Event.class, "day = ?", mCurrentDay.getId().toString());
    lawg.d("initEvents() " + mEvents.size() + " events found");
    mEventsContainer.removeAllViews();
    for (int i = 0; i < mEvents.size(); i++) {
      Event event = mEvents.get(i);

      int height;
      long minutesDifference = -1;
      if (i < mEvents.size() - 1) {
        Event nextEvent = mEvents.get(i + 1);
        minutesDifference = event.getMinutesDifference(nextEvent);
      } else { // last item - we still need to set a relevant height
        minutesDifference = event.getMinutesDifference(System.currentTimeMillis());
      }
      minutesDifference = Math.abs(minutesDifference);

      int minHeight = Etils.dpToPx(35);
      int maxHeight = Etils.dpToPx(150);
      lawg.d(" minutesDifference: " + minutesDifference);
      if (minutesDifference <= 30) { // 30 minutes
        height = minHeight;
      } else if (minutesDifference <= 60 * 4) { // 4 hours
        height = (int) Utils.mapValue(minutesDifference, 30, 60*4, minHeight, maxHeight);
      } else {
        height = maxHeight;
      }

      View eventLayout = mLayoutInflater.inflate(R.layout.item_event_layout, null);
      ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, height);
      mEventsContainer.addView(eventLayout, layoutParams);

      int color = event.getColor();

      TextView titleView = ButterKnife.findById(eventLayout, R.id.event_title);
      View dotView = ButterKnife.findById(eventLayout, R.id.event_dot);
      View lineView = ButterKnife.findById(eventLayout, R.id.event_vertical_line);
      TextView timeView = ButterKnife.findById(eventLayout, R.id.event_time);
      TextView timeAmPmView = ButterKnife.findById(eventLayout, R.id.event_am_pm);

      titleView.setText(event.getTitle());
      timeView.setText(event.getTimeText());
      timeAmPmView.setText(event.getTimeAmPmText());

      int numStyles = 3;
      int styleBucket = Math.round(Utils.mapValue(minutesDifference, 0, 60*4, 0, numStyles - 1));
      @DimenRes int titleTextSizeId, titleTopMarginId;
      @DimenRes int timeTextSizeId, timeTopMarginId;
      @DimenRes int timeAmPmTextSizeId, timeAmPmBottomMarginId;

      int[] textSizeIds = new int[] {
          R.dimen.event_title_text_size_smaller_1, R.dimen.event_time_text_size_smaller_1, R.dimen.event_time_am_pm_text_size_smaller_1,
          R.dimen.event_title_text_size_normal, R.dimen.event_time_text_size_normal, R.dimen.event_time_am_pm_text_size_normal,
          R.dimen.event_title_text_size_larger_1, R.dimen.event_time_text_size_larger_1, R.dimen.event_time_am_pm_text_size_larger_1,
      };
      int[] marginIds = new int[] {
          R.dimen.event_title_top_margin_smaller_1, R.dimen.event_time_top_margin_smaller_1, R.dimen.event_time_am_pm_bottom_margin_smaller_1,
          R.dimen.event_title_top_margin_normal, R.dimen.event_time_top_margin_normal, R.dimen.event_time_am_pm_bottom_margin_normal,
          R.dimen.event_title_top_margin_larger_1, R.dimen.event_time_top_margin_larger_1, R.dimen.event_time_am_pm_bottom_margin_larger_1,
      };

      Resources res = getResources();
      final int TITLE = 0, TIME = 1, TIME_AM_PM = 2;

      titleTextSizeId = textSizeIds[styleBucket * 3 + TITLE];
      titleTopMarginId = marginIds[styleBucket * 3 + TITLE];
      timeTextSizeId = textSizeIds[styleBucket * 3 + TIME];
      timeTopMarginId = marginIds[styleBucket * 3 + TIME];
      timeAmPmTextSizeId = textSizeIds[styleBucket * 3 + TIME_AM_PM];
      timeAmPmBottomMarginId = marginIds[styleBucket * 3 + TIME_AM_PM];

      titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimensionPixelSize(titleTextSizeId));
      timeView.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimensionPixelSize(timeTextSizeId));
      timeAmPmView.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimensionPixelSize(timeAmPmTextSizeId));

      Utils.setMarginTop(titleView, res.getDimensionPixelSize(titleTopMarginId));
      Utils.setMarginTop(timeView, res.getDimensionPixelSize(timeTopMarginId));
      Utils.setMarginBottom(timeAmPmView, res.getDimensionPixelSize(timeAmPmBottomMarginId));

      titleView.requestLayout();
      titleView.getParent().requestLayout();

      titleView.setTextColor(color);
      timeView.setTextColor(color);
      timeAmPmView.setTextColor(color);
      Etils.applyColorFilter(dotView.getBackground(), color);
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        ColorDrawable lineViewBackground = (ColorDrawable) lineView.getBackground();
      lineViewBackground.setColor(color);
      } else {
        Etils.applyColorFilter(lineView.getBackground(), color);
      }

    }
  }

}

