package de.florianm.android.routetracker.location;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.LocationServices;

import java.util.List;
import java.util.concurrent.TimeUnit;

import de.florianm.android.routetracker.RouteTrackerApplication;
import de.florianm.android.routetracker.eventbus.event.GeofenceErrorEvent;
import de.florianm.android.routetracker.provider.RouteDbSchema.TrackpointTable;


public class GeofenceTransitionsIntentService extends IntentService {
    private static final String TAG = GeofenceTransitionsIntentService.class.getSimpleName();

    private static final String ACTION_FENCE_TRANSITION = "de.florianm.android.routetracker.location.ACTION_FENCE_TRANSITION";
    public static final int CLIENT_CONNECTION_TIMEOUT = 10 * 60 * 1000;

    private static PendingIntent geofencePendingIntent;

    public static PendingIntent getGeofencePendingIntent(Context context) {
        if (null == geofencePendingIntent) {
            Intent intent = new Intent(context, GeofenceTransitionsIntentService.class);
            intent.setAction(ACTION_FENCE_TRANSITION);
            return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        return geofencePendingIntent;
    }

    private GoogleApiClient googleApiClient;

    public GeofenceTransitionsIntentService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        if (ACTION_FENCE_TRANSITION.equals(action)) {
            GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
            if (geofencingEvent.hasError()) {
                RouteTrackerApplication.postEvent(new GeofenceErrorEvent(
                        getApplicationContext(),
                        geofencingEvent.getErrorCode()));
                return;
            } else {
                onGeofencingEvent(geofencingEvent);
            }
        }
    }

    private void onGeofencingEvent(GeofencingEvent geofencingEvent) {
        googleApiClient.blockingConnect(CLIENT_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);
        if (!googleApiClient.isConnected()) {
            Log.e(TAG, "GoogleApiClient was not connected due timeout");
            return;
        }

        List<Geofence> geofences = geofencingEvent.getTriggeringGeofences();
        if (isTrackingGeofence(geofences)) {
            int transitionType = geofencingEvent.getGeofenceTransition();
            Location location = geofencingEvent.getTriggeringLocation();

            if (Geofence.GEOFENCE_TRANSITION_DWELL == transitionType) {
                onDwellInTrackingGeofence(location);
            } else if (Geofence.GEOFENCE_TRANSITION_EXIT == transitionType) {
                onExitTrackingGeofence(location);
            }
        }
    }

    private boolean isTrackingGeofence(List<Geofence> geofences) {
        if (null == geofences || geofences.isEmpty()) {
            return false;
        }

        String trackingGeofenceId = TrackingGeofence.getRequestId(this);
        for (Geofence geofence : geofences) {
            if (trackingGeofenceId.equals(geofence.getRequestId())) {
                return true;
            }
        }

        return false;
    }

    private void onDwellInTrackingGeofence(Location location) {

    }

    private void onExitTrackingGeofence(Location location) {
        TrackingGeofence.remove(this, googleApiClient).await();
        TrackingGeofence.add(this, googleApiClient, location).await();

        addTrackpoint(location);
    }

    private void addTrackpoint(Location location){
        ContentValues values = new ContentValues();
        values.put(TrackpointTable.LATITUDE, location.getLatitude());
        values.put(TrackpointTable.LONGITUDE, location.getLongitude());
        ContentResolver contentResolver = getContentResolver();
        contentResolver.insert(TrackpointTable.CONTENT_URI, values);
    }
}
