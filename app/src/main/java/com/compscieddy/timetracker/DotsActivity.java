package com.compscieddy.timetracker;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.compscieddy.eddie_utils.Etils;
import com.compscieddy.eddie_utils.Lawg;
import com.viewpagerindicator.PageIndicator;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DotsActivity extends AppCompatActivity {

  private static final Lawg lawg = Lawg.newInstance(DotsActivity.class.getSimpleName());

  @Bind(R.id.page_indicator) PageIndicator mPageIndicator;
  @Bind(R.id.dots_pages_viewpager) ViewPager mViewPager;
  @Bind(R.id.top_bar_page_titles) ViewGroup mTopBarPageTitles;

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
    LayoutInflater layoutInflater = getLayoutInflater();
    ViewGroup rootView = (ViewGroup) layoutInflater.inflate(R.layout.activity_dots, null);
    setContentView(rootView);
    ButterKnife.bind(this);

    final int numDays = 3;
    DotsPagerAdapter pagerAdapter = new DotsPagerAdapter(getSupportFragmentManager(), numDays);
    mViewPager.setAdapter(pagerAdapter);
    mPageIndicator.setViewPager(mViewPager);
    mPageIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      final float normalTextSize = getResources().getDimensionPixelSize(R.dimen.normal_page_title_text_size);
      final float selectedTextSize = getResources().getDimensionPixelSize(R.dimen.selected_page_title_text_size);
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
      @Override
      public void onPageSelected(int position) {
        for (int i = 0; i < mTopBarPageTitles.getChildCount(); i++) {
          View child = mTopBarPageTitles.getChildAt(i);
          if (!(child instanceof TextView)) { continue; }
          TextView title = (TextView) mTopBarPageTitles.getChildAt(i);
          if (i == position) {
            title.setTextSize(TypedValue.COMPLEX_UNIT_PX, selectedTextSize);
          } else {
            title.setTextSize(TypedValue.COMPLEX_UNIT_PX, normalTextSize);
          }
        }
      }
      @Override
      public void onPageScrollStateChanged(int state) {}
    });
    mViewPager.post(new Runnable() {
      @Override
      public void run() {
        if (false) lawg.e(" mViewPager.getChildCount(): " + mViewPager.getChildCount()); // this actually gets the wrong number of children womp :(
        mViewPager.setCurrentItem(numDays, true);
      }
    });

    setNewEventRandomColor();

//    mActivityBackground.setColorFilter(getResources().getColor(R.color.white_transp_20), PorterDuff.Mode.OVERLAY);
//    mEventsScrollView.setBackgroundColor(getResources().getColor(R.color.white_transp_20));
  }

  public void setNewEventRandomColor() {
    int randomColor = getResources().getColor(colors[(int) Math.round(Math.random() * (colors.length - 1))]);
    int lighterColor = Etils.getIntermediateColor(randomColor, getResources().getColor(R.color.white), 0.3f);

    // TODO: don't allow this color to be the color of the previous event (if prev exists)
  }

}

