package com.compscieddy.timetracker;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.compscieddy.eddie_utils.Etils;
import com.compscieddy.eddie_utils.Lawg;
import com.compscieddy.timetracker.models.Day;
import com.compscieddy.timetracker.models.Event;

import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

  private static final Lawg lawg = Lawg.newInstance(MainActivity.class.getSimpleName());

  Day mCurrentDay;
  @Bind(R.id.new_event_input) EditText mNewEventInput;
  @Bind(R.id.new_event_add_button) View mNewEventAddButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    LayoutInflater layoutInflater = getLayoutInflater();
    ViewGroup rootView = (ViewGroup) layoutInflater.inflate(R.layout.activity_main, null);
    setContentView(rootView);
    ButterKnife.bind(this);

    mCurrentDay = getCurrentDay();
    if (mCurrentDay == null) {
      lawg.d("Current Day not found, creating a new one");
      mCurrentDay = createCurrentDay();
    }
    initEvents();

    View itemEventView = layoutInflater.inflate(R.layout.item_event_layout, rootView);

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
      Event newEvent = new Event(mCurrentDay);
      newEvent.setTitle(mNewEventInput.getText().toString());
      newEvent.setColor(mNewEventInput.getCurrentTextColor());
      mNewEventInput.setText("");
    }
  };

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

  }

}

