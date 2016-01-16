package de.florianm.android.routetracker.preference;

import android.support.v4.app.Fragment;

import de.florianm.android.routetracker.SingleFragmentActivity;

public class AppPreferenceActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new AppPreferenceFragment();
    }


}
