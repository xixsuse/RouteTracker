package de.florianm.android.routetracker.provider;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by florian on 16.01.16.
 */
public class Trackpoint extends BaseDbEntry{
    private Location location;

    public Trackpoint() {
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
