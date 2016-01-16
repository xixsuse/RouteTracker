package de.florianm.android.routetracker;

import android.app.Application;
import android.support.v7.preference.PreferenceManager;

import net.danlew.android.joda.JodaTimeAndroid;

import de.florianm.android.routetracker.eventbus.EventBus;

public class RouteTrackerApplication extends Application {

    private static final String TAG = RouteTrackerApplication.class.getSimpleName();

    private static EventBus eventBus;

    public static EventBus getEventBus() {
        if(null == eventBus){
            eventBus = new EventBus(TAG);
        }

        return eventBus;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        JodaTimeAndroid.init(this);
        PreferenceManager.setDefaultValues(this, R.xml.app_preferences, false);
    }
}
