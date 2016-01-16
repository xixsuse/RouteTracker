package de.florianm.android.routetracker.preference;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import de.florianm.android.routetracker.R;

public class AppPreferenceFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.app_preferences);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_app_preferences, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.setGroupVisible(R.id.group_preferences, isVisible());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int itemId = item.getItemId();
        switch (itemId){
            case R.id.action_reset_to_default:
                resetPreferencesToDefault();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    protected void resetPreferencesToDefault(){
        PreferenceManager.setDefaultValues(getActivity(), R.xml.app_preferences, true);
        getPreferenceScreen().removeAll();
        addPreferencesFromResource(R.xml.app_preferences);
    }
}
