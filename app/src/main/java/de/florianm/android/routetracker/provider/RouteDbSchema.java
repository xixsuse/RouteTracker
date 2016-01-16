package de.florianm.android.routetracker.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class RouteDbSchema {
    public static final String AUTHORITY = "de.florianm.android.routetracker.provider";
    private static final Uri AUTHORITY_URI = Uri.parse(ContentResolver.SCHEME_CONTENT + "://" + AUTHORITY);

    public static abstract class BaseDbTable implements BaseColumns {
        public static final String CREATED_AT = "created_at";
        public static final String MODIFIED_AT = "modified_at";
        public static final String DELETED_AT = "deleted_at";
    }

    public static class TrackpointTable extends BaseDbTable {
        public static final String TABLE_NAME = "TRACKPOINT";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, TABLE_NAME);

        public static final String TYPE_DIRECTORY = "vnd.android.cursor.dir/" + AUTHORITY + ".trackpoints";
        public static final String TYPE_ITEM = "vnd.android.cursor.item/" + AUTHORITY + ".trackpoint";

        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";

        public static final String[] PROJECTION = {
                _ID,
                CREATED_AT,
                MODIFIED_AT,
                DELETED_AT,
                LATITUDE,
                LONGITUDE
        };
    }
}
