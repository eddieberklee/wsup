package com.compscieddy.timetracker;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.compscieddy.eddie_utils.Lawg;

/**
 * Created by elee on 5/25/16.
 */
public class DotsPagerAdapter extends FragmentStatePagerAdapter {

  private static final Lawg lawg = Lawg.newInstance(DotsPagerAdapter.class.getSimpleName());

  public static int NUM_PAGES;

  public DotsPagerAdapter(FragmentManager fm, int numPages) {
    super(fm);
    NUM_PAGES = numPages;
  }

  @Override
  public Fragment getItem(int position) {
    if (false) lawg.e("getday position: " + position);
    return DotsPageFragment.newInstance(position, NUM_PAGES);
  }

  @Override
  public int getCount() {
    return NUM_PAGES;
  }

}
