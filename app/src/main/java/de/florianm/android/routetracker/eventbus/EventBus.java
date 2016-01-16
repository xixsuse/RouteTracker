package de.florianm.android.routetracker.eventbus;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Otto {@link Bus} that post all event on the main thread.
 */
public class EventBus extends Bus {

    private static final String TAG = EventBus.class.getSimpleName();

    private final Handler handler = new Handler(Looper.getMainLooper());

    public EventBus() {
    }

    public EventBus(String identifier) {
        super(identifier);
    }

    @Override
    public void register(Object object) {
        Log.d(TAG, "register - object=" + object);
        super.register(object);
    }

    @Override
    public void unregister(Object object) {
        Log.d(TAG, "unregister - object=" + object);
        super.unregister(object);
    }

    @Override
    public void post(final Object event) {
        Log.d(TAG, "post - event=" + event);
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.post(event);
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    EventBus.super.post(event);
                }
            });
        }
    }
}
