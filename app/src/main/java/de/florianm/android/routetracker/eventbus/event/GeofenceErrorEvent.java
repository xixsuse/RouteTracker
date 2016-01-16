package de.florianm.android.routetracker.eventbus.event;

import android.content.Context;

import com.google.android.gms.location.GeofencingEvent;

import de.florianm.android.routetracker.location.GeofenceErrorMessages;

public class GeofenceErrorEvent {
    private int errorCode;
    private CharSequence message;

    public GeofenceErrorEvent(Context context, int errorCode) {
        this.errorCode = errorCode;
        this.message = GeofenceErrorMessages.getMessage(context, errorCode);
    }

    public int getErrorCode() {
        return errorCode;
    }

    public CharSequence getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GeofenceErrorEvent that = (GeofenceErrorEvent) o;

        return getErrorCode() == that.getErrorCode();
    }

    @Override
    public int hashCode() {
        return getErrorCode();
    }

    @Override
    public String toString() {
        return "GeofenceErrorEvent{" +
                "errorCode=" + errorCode +
                ", message=" + message +
                '}';
    }
}
