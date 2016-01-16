package de.florianm.android.routetracker.location;

import android.content.Context;
import android.content.res.Resources;

import com.google.android.gms.location.GeofenceStatusCodes;

import de.florianm.android.routetracker.R;

public class GeofenceErrorMessages {
    public static CharSequence getMessage(Context context, int errorCode) {
        Resources resources = context.getResources();
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return resources.getString(R.string.error_location_geofence_notavailable);
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return resources.getString(R.string.error_location_geofence_toomany);
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return resources.getString(R.string.error_location_geofence_toomanypendingintents);
            default:
                return resources.getString(R.string.error_location_geofence_unknown, errorCode);
        }
    }
}
