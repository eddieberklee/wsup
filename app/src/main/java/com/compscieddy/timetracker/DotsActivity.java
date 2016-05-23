package com.compscieddy.timetracker;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
import com.facebook.rebound.SpringSystem;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DotsActivity extends AppCompatActivity {

  private static final Lawg lawg = Lawg.newInstance(DotsActivity.class.getSimpleName());

  @Bind(R.id.new_event_input) ForadayEditText mNewEventInput;
  @Bind(R.id.new_event_add_button) View mNewEventAddButton;
  @Bind(R.id.events_container) LinearLayout mEventsContainer;
  @Bind(R.id.root_view) View mRootView;
  @Bind(R.id.events_scroll_view) LockableScrollView mEventsScrollView;
  @Bind(R.id.new_event_dot) View mNewEventDot;

  Day mCurrentDay;
  List<Event> mEvents;
  private LayoutInflater mLayoutInflater;
  private int currentLayout = 0;
  private int[] layouts = new int[] {
    R.layout.item_event_revamped_block_layout,
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
  private Handler mHandler;
  private Runnable mUpdateDurationRunnable = new Runnable() {
    @Override
    public void run() {
      int childCount = mEventsContainer.getChildCount();
      int lastItemIndex = childCount - 1;
      ViewGroup lastEventView = (ViewGroup) mEventsContainer.getChildAt(lastItemIndex);
      TextView eventDuration = ButterKnife.findById(lastEventView, R.id.event_duration);
      String durationString = getDurationString(lastItemIndex, mEvents);
      eventDuration.setText(durationString);
      mHandler.postDelayed(mUpdateDurationRunnable, 1000);
    }
  };
  private boolean isAddOpened = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mLayoutInflater = getLayoutInflater();
    ViewGroup rootView = (ViewGroup) mLayoutInflater.inflate(R.layout.activity_dots, null);
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
        isAddOpened = !isAddOpened;
        final float screenWidth = Etils.getScreenWidth(DotsActivity.this);
        final float finalRotation = -45f;
        SpringSystem springSystem = SpringSystem.create();
        Spring spring = springSystem.createSpring();
        spring.addListener(new SimpleSpringListener() {
          @Override
          public void onSpringUpdate(Spring spring) {
            float value = (float) spring.getCurrentValue();
            lawg.e("new transX: " + (-screenWidth * (1 - value)) + " value: " + value);
            if (isAddOpened) {
              mNewEventInput.setTranslationX(-screenWidth * (1 - value));
              mNewEventInput.setAlpha(value);
              mNewEventDot.setRotation(finalRotation * (value));
            } else {
              mNewEventInput.setTranslationX(-screenWidth * (value));
              mNewEventInput.setAlpha(1 - value);
              mNewEventDot.setRotation(finalRotation * (1 - value));
            }

            if (value == 1.0f && isAddOpened) {
              mNewEventInput.requestFocus();
              Etils.showKeyboard(DotsActivity.this);
            }
            if (value == 1.0f && !isAddOpened) {
              mNewEventInput.setVisibility(View.GONE);
              mNewEventInput.clearFocus();
              Etils.hideKeyboard(DotsActivity.this, mNewEventInput);
            }
          }
        });

        if (isAddOpened) {
          mNewEventInput.setTranslationX(-screenWidth);
          mNewEventInput.setAlpha(0);
          mNewEventInput.setVisibility(View.VISIBLE);
        } else {
          mNewEventInput.setTranslationX(0);
          mNewEventInput.setAlpha(1);

        }
        spring.setEndValue(1);
      }
    });

    mHandler = new Handler(Looper.getMainLooper());

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

    mNewEventInput.addTextChangedListener(mEventInputTextWatcher);
    mNewEventAddButton.setOnClickListener(mAddButtonOnClickListener);

//    mActivityBackground.setColorFilter(getResources().getColor(R.color.white_transp_20), PorterDuff.Mode.OVERLAY);
//    mEventsScrollView.setBackgroundColor(getResources().getColor(R.color.white_transp_20));

  }

  @Override
  protected void onPause() {
    super.onPause();
    mHandler.removeCallbacks(mUpdateDurationRunnable);
  }

  @Override
  protected void onResume() {
    super.onResume();
    mHandler.postDelayed(mUpdateDurationRunnable, 1000);
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
    int lighterColor = Etils.getIntermediateColor(randomColor, getResources().getColor(R.color.white), 0.3f);

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
      Etils.showToast(DotsActivity.this, message);
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
      durationView.setText(getDurationString(i, mEvents));

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
      int iconId = getIconId(titleString);
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

  public String getDurationString(int i, List<Event> events) {
    long endTime;
    if (i == events.size() - 1) {
      endTime = System.currentTimeMillis();
    } else {
      Event event = events.get(i + 1);
      endTime = event.getTimeMillis();
    }
    long timeDuration = endTime - events.get(i).getTimeMillis();
    if (false) lawg.e("eventTIME: " + events.get(i).getTimeMillis() + " i: " + i + " timeDuration: " + timeDuration + " final: " + Utils.getFormattedDuration(timeDuration));
    return Utils.getFormattedDuration(timeDuration);
  }

  public int getIconId(String title) {

    /** There should be a priority to this "AI".
     *  Maybe verbs/actions, objects, then location (home, office)
     */

    int iconId = -1;
    if (Utils.containsAtLeastOne(title, new String[]{
        "running", "run", "ran", "jog", "marathon"})) {
      iconId = R.drawable.ic_directions_run_white_48dp;
    } else if (Utils.containsAtLeastOne(title, new String[] {
        "drive", "car", "jeep", "convertible", "truck", "sedan", "coupe", "hatchback"})) {
      iconId = R.drawable.ic_directions_car_white_48dp;
    } else if (Utils.containsAtLeastOne(title, new String[] {
        "gym", "weight", "curls", "bench", "yoked", "buff", "workout", "working out", "work out"})) {
      iconId = R.drawable.ic_fitness_center_white_48dp;
    } else if (Utils.containsAtLeastOne(title, new String[] {
        "beach", "sand", "waves"})) {
      iconId = R.drawable.ic_beach_access_white_48dp;
    } else if (Utils.containsAtLeastOne(title, new String[] {
        "cafe", "coffee", "starbucks", "peets", "peet's", "philz"})) {
      iconId = R.drawable.ic_local_cafe_white_48dp;
    } else if (Utils.containsAtLeastOne(title, new String[] {
        "shopping", "mall", "grocer", "shop", "buy", "amazon", "costco", "safeway"})) {
      iconId = R.drawable.ic_shopping_cart_white_48dp;
    } else if (Utils.containsAtLeastOne(title, new String[] {
        "call", "phone", "buzz", "ring"})) {
      iconId = R.drawable.ic_call_white_48dp;
    } else if (Utils.containsAtLeastOne(title, new String[] {
        "note", "idea", "organize", "track"})) {
      iconId = R.drawable.ic_note_white_48dp;
    } else if (Utils.containsAtLeastOne(title, new String[] {
        "music", "sing", "audio", "song", "band", "concert", "show", "jazz"})) {
      iconId = R.drawable.ic_music_note_white_48dp;
    } else if (Utils.containsAtLeastOne(title, new String[] {
        "read", "book", "page", "article"})) {
      iconId = R.drawable.ic_book_white_48dp;
    } else if (Utils.containsAtLeastOne(title, new String[] {
        "movie", "theater"})) {
      iconId = R.drawable.ic_theaters_white_48dp;
    } else if (Utils.containsAtLeastOne(title, new String[] {
        "tv", "game of thrones"})) {
      iconId = R.drawable.ic_live_tv_white_48dp;
    } else if (Utils.containsAtLeastOne(title, new String[] {
        "park", "picnic", "nature"})) {
      iconId = R.drawable.ic_nature_people_white_48dp;
    } else if (Utils.containsAtLeastOne(title, new String[] {
        "draw", "sketch", "doodle", "art", "museum", "design"})) {
      iconId = R.drawable.ic_gesture_white_48dp;
    } else if (Utils.containsAtLeastOne(title, new String[] {
        "paint", "watercolor"})) {
      iconId = R.drawable.ic_palette_white_48dp;
    } else if (Utils.containsAtLeastOne(title, new String[] {
        "bike", "biking"})) {
      iconId = R.drawable.ic_directions_bike_white_48dp;
    } else if (Utils.containsAtLeastOne(title, new String[] {
        "picture", "photo", "camera", "instagram", "lightroom", "aperture"})) {
      iconId = R.drawable.ic_camera_alt_white_48dp;
    } else if (Utils.containsAtLeastOne(title, new String[] {
        "sun", "tan"})) {
      iconId = R.drawable.ic_wb_sunny_white_48dp;
    } else if (Utils.containsAtLeastOne(title, new String[] {
        "drinks", "alcohol", "beer", "shots", "vodka", "whiskey", "wine", "bar", "lounge", "club"})) {
      iconId = R.drawable.ic_local_bar_white_48dp;
    } else if (Utils.containsAtLeastOne(title, new String[] {
        "eat", "dinner", "lunch", "food", "takeout", "eat24", "pizza", "cooking", "recipe", "restaurant"})) {
      iconId = R.drawable.ic_local_dining_white_48dp;
    } else if (Utils.containsAtLeastOne(title, new String[] {
        "birthday", "celebration"})) {
      iconId = R.drawable.ic_cake_white_48dp;
    } else if (Utils.containsAtLeastOne(title, new String[] {
        "build", "fix"})) {
      iconId = R.drawable.ic_build_white_48dp;
    } else if (Utils.containsAtLeastOne(title, new String[] {
        "love", "favorite", "best"})) {
      iconId = R.drawable.ic_favorite_white_48dp;
    }
    return iconId;
  }

}

