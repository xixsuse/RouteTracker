package de.florianm.android.routetracker.location;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public final class LocationUtils {
    public static String formatGeoCoordinates(final double latitude, final double longitude, String separator) {
        String latitudeString = formatLocationComponent(latitude);
        latitudeString += " " + (0 < Math.signum(latitude) ? "N" : "S");

        String longitudeString = formatLocationComponent(longitude);
        longitudeString += " " + (0 < Math.signum(longitude) ? "E" : "W");

        return latitudeString + separator + longitudeString;
    }

    public static String formatLocationComponent(double coordinate) {
        String raw = Location.convert(
                Math.max(-180.0, Math.min(180.0, coordinate)),
                Location.FORMAT_SECONDS);

        raw = raw.replace(",", ".");
        final String[] rawSegments = raw.split(":");
        return String.format("%3d\u00B0 %2d\u0027 %2.1f\"",
                Integer.parseInt(rawSegments[0]),
                Integer.parseInt(rawSegments[1]),
                Double.parseDouble(rawSegments[2]));
    }

    public static String formatLocation(Location location, String separator) {
        return formatGeoCoordinates(location.getLatitude(), location.getLongitude(), separator);
    }

    public static String formatLatLon(LatLng location, String separator) {
        return formatGeoCoordinates(location.latitude, location.longitude, separator);
    }
}
