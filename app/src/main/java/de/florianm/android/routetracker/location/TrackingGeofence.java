package de.florianm.android.routetracker.location;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresPermission;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;

import de.florianm.android.routetracker.R;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static com.google.android.gms.location.LocationServices.GeofencingApi;

/**
 * Created by florian on 16.01.16.
 */
public final class TrackingGeofence {
    private static final String KEY_TRACKING_GEOFENCE_REQUESTID = "TRACKING_GEOFENCE_ID";

    private static String trackingGeofenceRequestId;

    public static String getRequestId(Context context) {
        if (null == trackingGeofenceRequestId) {
            SharedPreferences prefs = getDefaultSharedPreferences(context);
            trackingGeofenceRequestId = prefs.getString(KEY_TRACKING_GEOFENCE_REQUESTID, null);
        }

        return trackingGeofenceRequestId;
    }

    public static void setRequestId(Context context, String requestId){
        trackingGeofenceRequestId = requestId;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(KEY_TRACKING_GEOFENCE_REQUESTID, requestId).commit();
    }

    public static int getLoiteringDelayMillis(Context context) {
        Resources resources = context.getResources();
        String key = resources.getString(R.string.pref_locationDwellTime_key);
        String defaultValue = resources.getString(R.string.pref_locationDwellTime_defaultValue);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String value = pref.getString(key, defaultValue);
        return Integer.parseInt(value);
    }

    public static int getTrackingFenceRadius(Context context) {
        Resources resources = context.getResources();
        String key = resources.getString(R.string.pref_fenceradius_key);
        String defaultValue = resources.getString(R.string.pref_fenceradius_defaultValue);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String value = pref.getString(key, defaultValue);
        return Integer.parseInt(value);
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public static final PendingResult<Status> add(Context context, GoogleApiClient client, Location location) {
        int loiteringDelayMillis = getLoiteringDelayMillis(context);
        int trackingFenceRadius = getTrackingFenceRadius(context);
        String requestId = getRequestId(context);

        Geofence geofence = new Geofence.Builder()
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setCircularRegion(
                        location.getLatitude(),
                        location.getLongitude(),
                        trackingFenceRadius)
                .setLoiteringDelay(loiteringDelayMillis)
                .setRequestId(requestId)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();

        GeofencingRequest request = new GeofencingRequest.Builder()
                .addGeofence(geofence)
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL)
                .build();

        return GeofencingApi.addGeofences(
                client,
                request,
                GeofenceTransitionsIntentService.getGeofencePendingIntent(context));
    }

    public static PendingResult<Status> remove(Context context, GoogleApiClient client) {
        return GeofencingApi.removeGeofences(
                client,
                GeofenceTransitionsIntentService.getGeofencePendingIntent(context));
    }
}
