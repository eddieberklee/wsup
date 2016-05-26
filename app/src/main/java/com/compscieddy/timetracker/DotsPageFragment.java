package com.compscieddy.timetracker;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.compscieddy.eddie_utils.Etils;
import com.compscieddy.eddie_utils.Lawg;
import com.compscieddy.timetracker.models.Day;
import com.compscieddy.timetracker.models.Event;
import com.compscieddy.timetracker.ui.ForadayEditText;
import com.compscieddy.timetracker.ui.LockableScrollView;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by elee on 5/25/16.
 */
public class DotsPageFragment extends Fragment {

  private static final Lawg lawg = Lawg.newInstance(DotsPageFragment.class.getSimpleName());

  @Bind(R.id.root_view) View mRootView;
  @Bind(R.id.new_event_input) ForadayEditText mNewEventInput;
  @Bind(R.id.new_event_dot) View mAddNewEventDot;
  @Bind(R.id.new_event_create_button) View mNewEventCreateButton;
  @Bind(R.id.events_container) LinearLayout mEventsContainer;
  @Bind(R.id.events_scroll_view) LockableScrollView mEventsScrollView;

  private int pagerIndex;
  private int pagerCount;
  private static final String INDEX_KEY = "index_key";
  private static final String COUNT_KEY = "count_key";
  private Context mContext;
  private SpringSystem mSpringSystem;

  public static DotsPageFragment newInstance(int index, int count) {
    DotsPageFragment f = new DotsPageFragment();
    Bundle args = new Bundle();
    args.putInt(INDEX_KEY, index);
    args.putInt(COUNT_KEY, count);
    f.setArguments(args);
    return f;
  }

  public DotsPageFragment() {
    super();
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_dots_page, container, false);
    ButterKnife.bind(this, rootView);

    Bundle args = getArguments();
    pagerIndex = args.getInt(INDEX_KEY);
    pagerCount = args.getInt(COUNT_KEY);
    mContext = getContext();

    setListeners();
    init();
    initEvents();

    return rootView;
  }

  @Override
  public void onPause() {
    super.onPause();
    mHandler.removeCallbacks(mUpdateDurationRunnable);
  }

  @Override
  public void onResume() {
    super.onResume();
    mHandler.postDelayed(mUpdateDurationRunnable, 1000);
  }

  Day mDay;
  private boolean isAddOpened = false;
  List<Event> mEvents;
  private LayoutInflater mLayoutInflater;

  TextWatcher mEventInputTextWatcher = new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
      if (s.length() == 0) {
        mNewEventCreateButton.setVisibility(View.INVISIBLE);
      } else {
        mNewEventCreateButton.setVisibility(View.VISIBLE);
      }
    }
  };

  private View.OnClickListener mAddNewEventDotClickListener = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      final float screenWidth = Etils.getScreenWidth(mContext);
      final float finalRotation = -45f;

      isAddOpened = !isAddOpened;
      Spring spring = mSpringSystem.createSpring();
      spring.setSpringConfig(new SpringConfig(180, 8));

      spring.addListener(new SimpleSpringListener() {
        @Override
        public void onSpringUpdate(Spring spring) {
          float value = (float) spring.getCurrentValue();
          lawg.e("new transX: " + (-screenWidth * (1 - value)) + " value: " + value);
          if (isAddOpened) { // open -> closing
            mNewEventInput.requestFocus();
            mNewEventInput.setTranslationX(-screenWidth * (1 - value));
            mNewEventInput.setAlpha(value);
            mAddNewEventDot.setRotation(finalRotation * (value));
          } else { // close -> opening
            mNewEventInput.setTranslationX(-screenWidth * (value));
            mNewEventInput.setAlpha(1 - value);
            mAddNewEventDot.setRotation(finalRotation * (1 - value));
          }

          // TODO: WHILE ONSPRINGUPDATE() IS GOING ON, DISABLE TAPPING ON THE PLUS BUTTON AGAIN - RESULTS IN BAD STATE CHANGE
          if (value == 1.0f) {
            if (isAddOpened) {
              // noop
            } else {
              mNewEventInput.setVisibility(View.GONE);
              mNewEventInput.clearFocus();
            }
             // flip value after the animation for opening has completed
          }

        }
      });

      if (isAddOpened) {
        mNewEventInput.setTranslationX(-screenWidth);
        mNewEventInput.setAlpha(0);
        mNewEventInput.setVisibility(View.VISIBLE);
        Etils.showKeyboard(mContext);
      } else {
        mNewEventInput.setTranslationX(0);
        mNewEventInput.setAlpha(1);
        Etils.hideKeyboard(mContext, mNewEventInput);
      }
      spring.setEndValue(1);
    }
  };

  private View.OnClickListener mCreateButtonOnClickListener = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      Date currentDate = new Date();
      Event newEvent = new Event(mDay, currentDate);
      newEvent.setTitle(mNewEventInput.getText().toString());
      newEvent.setColor(mNewEventInput.getCurrentTextColor());
      mNewEventInput.setText("");

      initEvents();
    }
  };

  private Handler mHandler;
  private Runnable mUpdateDurationRunnable = new Runnable() {
    @Override
    public void run() {
      int childCount = mEventsContainer.getChildCount();
      int lastItemIndex = childCount - 1;
      if (lastItemIndex >= 0) {
        ViewGroup lastEventView = (ViewGroup) mEventsContainer.getChildAt(lastItemIndex);
        TextView eventDuration = ButterKnife.findById(lastEventView, R.id.event_duration);
        String durationString = Utils.getDurationString(lastItemIndex, mEvents);
        eventDuration.setText(durationString);
        mHandler.postDelayed(mUpdateDurationRunnable, 1000);
      }
    }
  };

  private ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
    @Override
    public void onGlobalLayout() {
      int heightDiff = mRootView.getRootView().getHeight() - mRootView.getHeight();
      if (false) lawg.d("heightDiff: " + heightDiff + " dp150: " + Etils.dpToPx(150));
      if (heightDiff > Etils.dpToPx(150)) { // if more than 100 pixels, its probably a keyboard...
        mEventsScrollView.fullScroll(View.FOCUS_DOWN);
        if (false) lawg.d("Setting scrollable false");
//          mEventsScrollView.setScrollable(false);
      } else {
        if (false) lawg.d("Setting scrollable true");
//          mEventsScrollView.setScrollable(true);
      }
    }
  };

  private View.OnTouchListener mRootTouchListener = new View.OnTouchListener() {
    @Override
    public boolean onTouch(View v, MotionEvent event) {
      float y = event.getRawY();
      float startY = -1, currentY = -1;
      switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
          startY = event.getY();
          break;
        case MotionEvent.ACTION_UP:
          currentY = event.getY();
          float difference = startY - currentY;
          break;
        case MotionEvent.ACTION_MOVE:
          break;
      }
      return false;
    }
  };

  private void setListeners() {

    mAddNewEventDot.setOnClickListener(mAddNewEventDotClickListener);
    mNewEventInput.addTextChangedListener(mEventInputTextWatcher);
    mNewEventCreateButton.setOnClickListener(mCreateButtonOnClickListener);
    // keyboard detection trick - http://stackoverflow.com/a/4737265/4326052
    mRootView.getViewTreeObserver().addOnGlobalLayoutListener(mGlobalLayoutListener);
    mRootView.setOnTouchListener(mRootTouchListener);

  }

  private void init() {

    mLayoutInflater = LayoutInflater.from(mContext);
    mHandler = new Handler(Looper.getMainLooper());
    mHandler.postDelayed(mUpdateDurationRunnable, 1000);
    mSpringSystem = SpringSystem.create();

    mDay = getDay();
    if (mDay == null) {
      lawg.d("Current Day not found, creating a new one");
      mDay = createCurrentDay();
    }

  }

  /**
   * mDay needs to be populated before this method.
   */
  private void initEvents() {
    mEvents = Event.find(Event.class, "day = ?", mDay.getId().toString());
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

      int minHeight = Etils.dpToPx(60);
      int maxHeight = Etils.dpToPx(150);
      lawg.d(" minutesDifference: " + minutesDifference);
      if (minutesDifference <= 30) { // 30 minutes
        height = minHeight;
      } else if (minutesDifference <= 60 * 4) { // 4 hours
        height = (int) Utils.mapValue(minutesDifference, 30, 60*4, minHeight, maxHeight);
      } else {
        height = maxHeight;
      }
      // Last event needs extra height because part of it is overlapping with the new event section
      if (i == mEvents.size() - 1) {
        height += getResources().getDimensionPixelSize(R.dimen.overlap_gap_for_new_add_section) * 2/5f;
      }

      View eventLayout = mLayoutInflater.inflate(R.layout.item_event_dots_layout, null);
      ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, height);
//      if (i != 0) eventLayout.setTranslationY(Etils.dpToPx(-8));
      mEventsContainer.addView(eventLayout, layoutParams);

      int color = event.getColor();

      TextView titleView = ButterKnife.findById(eventLayout, R.id.event_title);
      ImageView dotView = ButterKnife.findById(eventLayout, R.id.event_dot);
      View lineViewBottom = ButterKnife.findById(eventLayout, R.id.event_vertical_line_bottom);
      TextView timeView = ButterKnife.findById(eventLayout, R.id.event_time);
      TextView timeAmPmView = ButterKnife.findById(eventLayout, R.id.event_am_pm);
      TextView durationView = ButterKnife.findById(eventLayout, R.id.event_duration);

      titleView.setText(event.getTitle());
      timeView.setText(event.getTimeText());
      timeAmPmView.setText(event.getTimeAmPmText());
      durationView.setText(Utils.getDurationString(i, mEvents));

      /* Shadows
      int darkerColor = Etils.getIntermediateColor(color, getResources().getColor(R.color.black), 0.1f);
      darkerColor = Etils.setAlpha(darkerColor, 0.8f);
      titleView.setShadowLayer(titleView.getShadowRadius(), titleView.getShadowDx(), titleView.getShadowDy(), darkerColor);
      timeView.setShadowLayer(timeView.getShadowRadius(), timeView.getShadowDx(), timeView.getShadowDy(), darkerColor);
      timeAmPmView.setShadowLayer(timeAmPmView.getShadowRadius(), timeAmPmView.getShadowDx(), timeAmPmView.getShadowDy(), darkerColor);
      */

      titleView.setTextColor(getResources().getColor(R.color.white));
//      titleView.setTextColor(color);
      timeView.setTextColor(color);
      timeAmPmView.setTextColor(color);

      /*
      Etils.applyColorFilter(dotView.getBackground(), color);
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        ColorDrawable lineViewBackground = (ColorDrawable) lineViewBottom.getBackground();
      lineViewBackground.setAllColors(color);
      } else {
        Etils.applyColorFilter(lineViewBottom.getBackground(), color);
      }
      */

      String titleString = titleView.getText().toString().toLowerCase();
      int iconId = AI.getIconId(titleString);
      if (iconId != -1) {
        Drawable iconDrawable = getResources().getDrawable(iconId);
//        int iconColor = Etils.getIntermediateColor(color, getResources().getColor(R.color.black), 0.9f);
//        Etils.applyColorFilter(iconDrawable, iconColor);
        dotView.setImageDrawable(iconDrawable);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//          dotView.setImageTintMode(PorterDuff.Mode.SRC_IN);
//        }
      }

//      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//        innerDot.setAllColors(color);
//        outerDot.setStroke(Etils.dpToPx(1), color);
//      } else {
//        Etils.applyColorFilter(innerDot, color, true);
//        Etils.applyColorFilter(outerDot, color, true);
//      }
      Etils.applyColorFilter(lineViewBottom.getBackground(), color);

    }
  }

  public Day createCurrentDay() {
    Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
    Day day = new Day(year, dayOfYear);
    return day;
  }

  @Nullable
  public Day getDay() {
    Calendar calendar = Calendar.getInstance();

    int todayIndex = pagerCount - 1; // last item is "today" page
    if (pagerIndex == todayIndex) {
      // no-op: no need to set a date, Calendar will use current time by default
    } else {
      int numDaysPrevious = todayIndex - pagerIndex;
      Date date = new Date();
      long dayMillis = 1 * 1000 * 60 * 60 * 24;
      long dateTimeMillis = date.getTime();
      long previousTimeMillis = dateTimeMillis - dayMillis * numDaysPrevious;
      date.setTime(previousTimeMillis);
      calendar.setTime(date);

      lawg.e("!!!!!!!!!" + " dateTimeMillis: " + dateTimeMillis + " previousTimeMillis: " + previousTimeMillis);
    }

    int year = calendar.get(Calendar.YEAR);
    int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);

    lawg.e("???????????????????????? " + " pagerIndex: " + pagerIndex + " pagerCount: " + pagerCount + " dayOfYear: " + dayOfYear);

    Day day = null;
    List<Day> days = Day.find(Day.class, "year = ? and day_of_year = ?", String.valueOf(year), String.valueOf(dayOfYear));

    if (days.size() > 1) { // sanity check
      String message = "DAFUQ more than 1 Day found...";
      lawg.e(message);
      Etils.showToast(mContext, message);
    }
    if (days.size() == 1) {
      day = days.get(0);
    }
    return day;
  }

}