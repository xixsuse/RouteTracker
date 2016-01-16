package de.florianm.android.routetracker;

import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvingResultCallbacks;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;

import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.florianm.android.routetracker.google.GoogleApiClientActivity;
import de.florianm.android.routetracker.location.LocationUtils;
import de.florianm.android.routetracker.location.TrackingGeofence;
import de.florianm.android.routetracker.preference.AppPreferenceActivity;
import de.florianm.android.routetracker.provider.RouteDbSchema;
import de.florianm.android.routetracker.provider.RouteDbSchema.TrackpointTable;
import de.florianm.android.routetracker.provider.TrackpointAdapter;

public class MainActivity extends GoogleApiClientActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Bind(android.R.id.list)
    protected ListView listView;

    private TrackpointAdapter trackpointAdapter;
    private LoaderManager.LoaderCallbacks<Cursor> cursorLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(getApplicationContext(), TrackpointTable.CONTENT_URI, TrackpointTable.PROJECTION, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            trackpointAdapter.swapCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            trackpointAdapter.swapCursor(null);
        }
    };

    @NonNull
    @Override
    protected GoogleApiClient createGoogleApiClient(GoogleApiClient.Builder builder) {
        return builder.addApi(LocationServices.API).build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);



        trackpointAdapter = new TrackpointAdapter(this, null);
        listView.setAdapter(trackpointAdapter);
        getLoaderManager().initLoader(0, null, cursorLoaderCallbacks);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(0, null, cursorLoaderCallbacks);
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

    @Override
    public void onConnected(Bundle bundle) {
        super.onConnected(bundle);

        String requestId = TrackingGeofence.getRequestId(this);
        if(null == requestId) {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(null != location) {
                requestId = UUID.randomUUID().toString();
                TrackingGeofence.setRequestId(this, requestId);
                TrackingGeofence.add(this, getGoogleApiClient(), location).setResultCallback(new ResolvingResultCallbacks<Status>(this, 0) {
                    @Override
                    public void onSuccess(@NonNull Status status) {

                    }

                    @Override
                    public void onUnresolvableFailure(@NonNull Status status) {

                    }
                });
                Log.d(TAG, "create new tracking geofence - " + LocationUtils.formatLocation(location, ","));
                addTrackpoint(location);
            } else{
                Log.w(TAG, "Not location to create a new tracking geofence");
            }
        }
    }

    private void addTrackpoint(Location location){
        ContentValues values = new ContentValues();
        values.put(TrackpointTable.LATITUDE, location.getLatitude());
        values.put(TrackpointTable.LONGITUDE, location.getLongitude());
        ContentResolver contentResolver = getContentResolver();
        contentResolver.insert(TrackpointTable.CONTENT_URI, values);
    }
}
