package de.florianm.android.routetracker;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.squareup.otto.DeadEvent;
import com.squareup.otto.Subscribe;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTimeZone;
import org.joda.time.tz.ZoneInfoProvider;

import java.io.IOException;
import java.util.UUID;

import de.florianm.android.routetracker.eventbus.EventBus;

import static android.preference.PreferenceManager.*;

public class RouteTrackerApplication extends Application {
    private static final String TAG = RouteTrackerApplication.class.getSimpleName();


    private static EventBus eventBus;

    public static EventBus getEventBus() {
        if (null == eventBus) {
            eventBus = new EventBus(TAG);
        }

        return eventBus;
    }

    public static void postEvent(Object event) {
        getEventBus().post(event);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        PreferenceManager.setDefaultValues(this, R.xml.app_preferences, false);
        getEventBus().register(this);
    }

    @Subscribe
    public void onDeadEvent(DeadEvent event) {
        Log.d(TAG, "Event not handled - " + event);
    }
}
