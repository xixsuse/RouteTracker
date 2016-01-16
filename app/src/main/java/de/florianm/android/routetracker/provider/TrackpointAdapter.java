package de.florianm.android.routetracker.provider;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.florianm.android.routetracker.R;
import de.florianm.android.routetracker.location.LocationUtils;

import static de.florianm.android.routetracker.provider.helper.TrackpointTableHelper.TrackpointCursorWrapper;

public class TrackpointAdapter extends CursorAdapter {
    public TrackpointAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.view_trackpoint, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TrackpointCursorWrapper trkptCursor = new TrackpointCursorWrapper(cursor);

        Trackpoint trkpt = trkptCursor.getTrackpoint();

        TextView textView = (TextView) view.findViewById(R.id.locationText);
        textView.setText(LocationUtils.formatLocation(trkpt.getLocation(), ","));

        textView = (TextView) view.findViewById(R.id.timeText);
        textView.setText(SqlUtils.formatDateTime(trkpt.getCreatedAt()));
    }
}
