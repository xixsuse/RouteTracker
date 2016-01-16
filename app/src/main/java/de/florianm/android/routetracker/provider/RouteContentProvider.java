package de.florianm.android.routetracker.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.database.DatabaseUtilsCompat;
import android.util.Log;

import de.florianm.android.routetracker.provider.RouteDbSchema.TrackpointTable;
import de.florianm.android.routetracker.provider.helper.BaseTableHelper;
import de.florianm.android.routetracker.provider.helper.DatabaseHelper;
import de.florianm.android.routetracker.provider.helper.TrackpointTableHelper;

public class RouteContentProvider extends ContentProvider {

    private static final String TAG = RouteContentProvider.class.getSimpleName();

    private static final int CODE_TRACKPOINT = 1;
    private static final int CODE_TRACKPOINT_ID = 2;
    private static final int CODE_TRACKPOINT_LAST = 3;

    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        MATCHER.addURI(RouteDbSchema.AUTHORITY, TrackpointTable.TABLE_NAME, CODE_TRACKPOINT);
        MATCHER.addURI(RouteDbSchema.AUTHORITY, TrackpointTable.TABLE_NAME + "/#", CODE_TRACKPOINT_ID);
        MATCHER.addURI(RouteDbSchema.AUTHORITY, TrackpointTable.TABLE_NAME + "/last", CODE_TRACKPOINT_LAST);
    }

    private TrackpointTableHelper trackpointTableHelper;

    @Override
    public String getType(@NonNull Uri uri) {
        switch (MATCHER.match(uri)) {
            case CODE_TRACKPOINT:
                return TrackpointTable.TYPE_DIRECTORY;
            case CODE_TRACKPOINT_ID:
                return TrackpointTable.TYPE_ITEM;
            default:
                Log.w(TAG, "No type found for URI - " + uri);
                return null;
        }
    }

    @Override
    public boolean onCreate() {
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        trackpointTableHelper = new TrackpointTableHelper(databaseHelper);
        return true;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        BaseTableHelper helper;
        switch (MATCHER.match(uri)) {
            case CODE_TRACKPOINT:
                helper = trackpointTableHelper;
                break;
            default:
                return null;
        }

        Uri insertedUri = helper.insert(values);
        if(null == insertedUri){
            return null;
        } else{
            notifyContentChanged(insertedUri);
            return insertedUri;
        }
    }

    private void notifyContentChanged(Uri uri) {
        Context context = getContext();
        if(null != context) {
            ContentResolver contentResolver = context.getContentResolver();
            contentResolver.notifyChange(uri, null);
        }
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        BaseTableHelper helper;
        switch (MATCHER.match(uri)) {
            case CODE_TRACKPOINT_ID:
                long id = ContentUris.parseId(uri);
                selection = DatabaseUtilsCompat.concatenateWhere(selection, TrackpointTable._ID + "=?");
                selectionArgs = DatabaseUtilsCompat.appendSelectionArgs(selectionArgs, new String[]{Long.toString(id)});
            case CODE_TRACKPOINT:
                helper = trackpointTableHelper;
                break;
            case CODE_TRACKPOINT_LAST:
                return trackpointTableHelper.getLastTrackpoint();
            default:
                return null;
        }

        return helper.query(projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        BaseTableHelper helper;
        switch (MATCHER.match(uri)) {
            case CODE_TRACKPOINT_ID:
                long id = ContentUris.parseId(uri);
                selection = DatabaseUtilsCompat.concatenateWhere(selection, TrackpointTable._ID + "=?");
                selectionArgs = DatabaseUtilsCompat.appendSelectionArgs(selectionArgs, new String[]{Long.toString(id)});
            case CODE_TRACKPOINT:
                helper = trackpointTableHelper;
                break;
            default:
                return 0;
        }

        return helper.update(values, selection, selectionArgs);
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        BaseTableHelper helper;
        switch (MATCHER.match(uri)) {
            case CODE_TRACKPOINT_ID:
                long id = ContentUris.parseId(uri);
                selection = DatabaseUtilsCompat.concatenateWhere(selection, TrackpointTable._ID + "=?");
                selectionArgs = DatabaseUtilsCompat.appendSelectionArgs(selectionArgs, new String[]{Long.toString(id)});
            case CODE_TRACKPOINT:
                helper = trackpointTableHelper;
                break;
            default:
                return 0;
        }

        return helper.delete(selection, selectionArgs);
    }
}
