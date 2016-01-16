package de.florianm.android.routetracker.provider.helper;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import org.joda.time.DateTime;

import de.florianm.android.routetracker.provider.RouteDbSchema.TrackpointTable;
import de.florianm.android.routetracker.provider.SqlUtils;

public class TrackpointTableHelper extends BaseTableHelper {
    private static final String TAG = TrackpointTableHelper.class.getSimpleName();

    public static void onCreate(SQLiteDatabase db) {
        Log.d(TAG, " onCreate for " + TrackpointTable.TABLE_NAME + " db = " + String.valueOf(db));

        String sql = "CREATE TABLE IF NOT EXISTS " + TrackpointTable.TABLE_NAME + " ( " //
                + TrackpointTable._ID + " INTEGER PRIMARY KEY, " //
                + TrackpointTable.CREATED_AT + " TEXT NOT NULL, "
                + TrackpointTable.MODIFIED_AT + " TEXT NOT NULL, "
                + TrackpointTable.DELETED_AT + " TEXT, "
                + TrackpointTable.LATITUDE + " REAL NOT NULL DEFAULT 0.0, "
                + TrackpointTable.LONGITUDE + " REAL NOT NULL DEFAULT 0.0, "
                + TrackpointTable.POINT_TYPE + " TEXT NOT NULL DEFAULT 'TRACK') ";
        try {
            db.execSQL( sql );
        } catch ( SQLException e ) {
            Log.e( TAG, "Failed to create table " + TrackpointTable.TABLE_NAME, e );
        }
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public TrackpointTableHelper(SQLiteOpenHelper openHelper) {
        super(openHelper);
    }

    @Override
    public Uri insert(ContentValues values) {
        removeBaseColumns(values);

        DateTime now = DateTime.now();
        String nowString = SqlUtils.formatDateTime(now);
        values.put(TrackpointTable.CREATED_AT, nowString);
        values.put(TrackpointTable.MODIFIED_AT, nowString);

        SQLiteDatabase db = getWritableDatabase();
        long id = db.insert(TrackpointTable.TABLE_NAME, null, values);
        if(0 < id) {
            return ContentUris.withAppendedId(TrackpointTable.CONTENT_URI, id);
        } else{
            return null;
        }
    }

    @Override
    public Cursor query(String[] projection, String whereClause, String[] whereArgs, String sortOrder) {
        if(null == projection){
            projection = TrackpointTable.PROJECTION;
        }

        if(TextUtils.isEmpty(sortOrder)){
            sortOrder = TrackpointTable.CREATED_AT + " ASC";
        }

        SQLiteDatabase db = getReadableDatabase();
        return db.query(TrackpointTable.TABLE_NAME, projection, whereClause, whereArgs, null, null, sortOrder);
    }

    @Override
    public int update(ContentValues values, String whereClause, String[] whereArgs) {
        removeBaseColumns(values);

        DateTime now = DateTime.now();
        String nowString = SqlUtils.formatDateTime(now);
        values.put(TrackpointTable.MODIFIED_AT, nowString);

        SQLiteDatabase db = getWritableDatabase();
        return db.update(TrackpointTable.TABLE_NAME, values, whereClause, whereArgs);
    }

    @Override
    public int delete(String whereClause, String[] whereArgs) {
        ContentValues values = new ContentValues();

        DateTime now = DateTime.now();
        String nowString = SqlUtils.formatDateTime(now);
        values.put(TrackpointTable.DELETED_AT, nowString);

        SQLiteDatabase db = getWritableDatabase();
        return db.update(TrackpointTable.TABLE_NAME, values, whereClause, whereArgs);
    }

    public Cursor getLastTrackpoint(){
        SQLiteDatabase db = getReadableDatabase();
        return db.query(true,
                TrackpointTable.TABLE_NAME,
                TrackpointTable.PROJECTION,
                null,
                null,
                null,
                null,
                TrackpointTable._ID + " DESC",
                "1");
    }
}
