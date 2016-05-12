package com.compscieddy.timetracker;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringSystem;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

  private static final Lawg lawg = Lawg.newInstance(MainActivity.class.getSimpleName());

  @Bind(R.id.new_event_input) ForadayEditText mNewEventInput;
  @Bind(R.id.new_event_add_button) TextView mNewEventAddButton;
  @Bind(R.id.events_container) LinearLayout mEventsContainer;
  @Bind(R.id.root_view) View mRootView;
  @Bind(R.id.events_scroll_view) LockableScrollView mEventsScrollView;
  @Bind(R.id.switch_to_blocks) View mSwitchToBlocks;
  @Bind(R.id.switch_to_dots) View mSwitchToDots;

  Day mCurrentDay;
  List<Event> mEvents;
  private LayoutInflater mLayoutInflater;
  private int currentLayout = 0;
  private int[] layouts = new int[] {
    R.layout.item_event_layout,
    R.layout.item_event_layout_time_left
  };

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
//          mEventsScrollView.setScrollable(false);
        } else {
          if (DEBUG_KEYB_DETECT) lawg.d("Setting scrollable true");
//          mEventsScrollView.setScrollable(true);
        }
      }
    });

    mRootView.setOnTouchListener(new View.OnTouchListener() {
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
    });

    mSwitchToBlocks.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, BlocksActivity.class);
        startActivity(intent);
      }
    });
    mSwitchToDots.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, DotsActivity.class);
        startActivity(intent);
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
      ColorDrawable drawable = (ColorDrawable) mNewEventInput.getBackground();
      newEvent.setColor(drawable.getColor());
      mNewEventInput.setText("");

      setNewEventRandomColor();

      initEvents();
    }
  };

  public void setNewEventRandomColor() {
    int randomColor = getResources().getColor(colors[(int) Math.round(Math.random() * (colors.length - 1))]);
    mNewEventInput.setBackgroundColor(randomColor);
    int alphaRandomColor = Etils.setAlpha(randomColor, 0.3f);
    mNewEventInput.setHintTextColor(alphaRandomColor);
    mNewEventAddButton.setTextColor(randomColor);

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

      int minHeight = getResources().getDimensionPixelSize(R.dimen.min_event_height);
      int maxHeight = Etils.dpToPx(150);
      lawg.d(" minutesDifference: " + minutesDifference);
      if (minutesDifference <= 30) { // 30 minutes
        height = minHeight;
      } else if (minutesDifference <= 60 * 4) { // 4 hours
        height = (int) Utils.mapValue(minutesDifference, 30, 60*4, minHeight, maxHeight);
      } else {
        height = maxHeight;
      }

      final View eventLayout = mLayoutInflater.inflate(R.layout.item_event_layout, null);
      ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
      mEventsContainer.addView(eventLayout, layoutParams);

      eventLayout.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          boolean isOpen = eventLayout.getHeight() > getResources().getDimensionPixelSize(R.dimen.min_event_height);
          expandEvent(eventLayout, isOpen);
        }
      });

      int color = event.getColor();

      TextView titleView = ButterKnife.findById(eventLayout, R.id.event_title);
      TextView timeView = ButterKnife.findById(eventLayout, R.id.event_time);
      TextView timeAmPmView = ButterKnife.findById(eventLayout, R.id.event_am_pm);
      ViewGroup backgroundContainer = ButterKnife.findById(eventLayout, R.id.background_container);

      titleView.setText(event.getTitle());
      timeView.setText(event.getTimeText());
      timeAmPmView.setText(event.getTimeAmPmText());
      backgroundContainer.setBackgroundColor(color);

      updateTitleViewMaxLines(titleView, height);

    }
  }

  private void updateTitleViewMaxLines(TextView titleView, float height) {
    // todo: refactor to make this automatically adjust correctly
    float minHeight = getResources().getDimensionPixelSize(R.dimen.min_event_height);
    if (height <= minHeight * 1.8f) {
      titleView.setMaxLines(1);
    } else if (height <= minHeight * 2.8f) {
      titleView.setMaxLines(2);
    } else {
      titleView.setMaxLines(3);
    }
  }

  private void expandEvent(final View eventLayout, final boolean isOpen) {
    final float distanceToOpen;
    final float startingHeight;
    final float minEventHeight = getResources().getDimensionPixelSize(R.dimen.min_event_height);
    final TextView title = ButterKnife.findById(eventLayout, R.id.event_title);
    boolean isEllipsized = Utils.isTextViewEllipsized(title);
    final boolean shouldClose = !isOpen && !isEllipsized;
    lawg.d("expandEvent happened." + " isOpen: " + isOpen + " isEllipsized: " + isEllipsized);
    if (isOpen) {
      // current height to normal
      startingHeight = eventLayout.getHeight();
      distanceToOpen = minEventHeight - startingHeight;
    } else {
      startingHeight = minEventHeight;
      if (isEllipsized) {
        distanceToOpen = Etils.dpToPx(30); // space needed for 2 lines to be expanded
      } else {
        distanceToOpen = Etils.dpToPx(4); // this is an arbitrary minimum I chose
      }
    }

    final SpringSystem springSystem = SpringSystem.create();
    Spring spring = springSystem.createSpring();
    spring.addListener(new SimpleSpringListener() {
      @Override
      public void onSpringAtRest(Spring spring) {
        super.onSpringAtRest(spring);
        if (shouldClose) {
          Spring spring2 = springSystem.createSpring();
          final float difference = eventLayout.getHeight() - minEventHeight;
          final float currentHeight = eventLayout.getHeight();
          spring2.addListener(new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
              float value = (float) spring.getCurrentValue(); // 0 -> 1
              ViewGroup.LayoutParams layoutParams = eventLayout.getLayoutParams();
              layoutParams.height = (int) (currentHeight - difference * value);
              eventLayout.setLayoutParams(layoutParams);
            }
          });
        }
      }
      @Override
      public void onSpringUpdate(Spring spring) {
        float value = (float) spring.getCurrentValue(); // 0 -> 1
//            lawg.d(" value: " + value);
        ViewGroup.LayoutParams layoutParams = eventLayout.getLayoutParams();
        layoutParams.height = (int) (startingHeight + distanceToOpen * value);

        updateTitleViewMaxLines(title, layoutParams.height);
        if (isOpen) {
          title.setEllipsize(TextUtils.TruncateAt.END);
        } else {
          title.setEllipsize(null);
        }

        eventLayout.setLayoutParams(layoutParams);
      }
    });
    spring.setEndValue(1);

  }

}

