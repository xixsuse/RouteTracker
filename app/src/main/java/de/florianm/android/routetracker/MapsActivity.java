package de.florianm.android.routetracker;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import de.florianm.android.routetracker.provider.RouteDbSchema;
import de.florianm.android.routetracker.provider.RouteDbSchema.TrackpointTable;
import de.florianm.android.routetracker.provider.Trackpoint;
import de.florianm.android.routetracker.provider.helper.TrackpointTableHelper.TrackpointCursorWrapper;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;

    private LoaderManager.LoaderCallbacks<Cursor> cursorLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(
                    getApplicationContext(),
                    TrackpointTable.CONTENT_URI,
                    TrackpointTable.PROJECTION,
                    "date(" + TrackpointTable.CREATED_AT + ") = date('now')",
                    null,
                    TrackpointTable.CREATED_AT + " ASC");
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            createTrack(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
        }
    };

    private void createTrack(Cursor data) {
        int width = getResources().getDimensionPixelSize(R.dimen.mapTrackWidth);
        int color = getResources().getColor(R.color.colorMapTrack);
        PolylineOptions po = new PolylineOptions()
                .color(color)
                .width(width)
                .clickable(false)
                .zIndex(0);

        TrackpointCursorWrapper cursorWrapper = new TrackpointCursorWrapper(data);
        while (cursorWrapper.moveToNext()) {
            Trackpoint tp = cursorWrapper.getTrackpoint();
            Location location = tp.getLocation();
            LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
            po.add(point);
        }

        googleMap.addPolyline(po);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        this.googleMap.setMyLocationEnabled(true);
        getLoaderManager().restartLoader(0, null, cursorLoaderCallbacks);

    }
}
