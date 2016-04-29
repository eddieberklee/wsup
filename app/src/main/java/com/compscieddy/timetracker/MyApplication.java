package com.compscieddy.timetracker;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.orm.SugarContext;

/**
 * Created by elee on 4/29/16.
 */
public class MyApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();

    SugarContext.init(this);
    Stetho.initializeWithDefaults(this);

  }

  @Override
  public void onTerminate() {
    super.onTerminate();
    SugarContext.terminate();
  }

}
