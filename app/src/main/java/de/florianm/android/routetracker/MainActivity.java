package de.florianm.android.routetracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import de.florianm.android.routetracker.preference.AppPreferenceActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int itemId = item.getItemId();
        switch (itemId){
            case R.id.action_preferences:
                openPreferencesActivity();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void openPreferencesActivity() {
        Intent intent = new Intent(this, AppPreferenceActivity.class);
        startActivity(intent);
    }
}
