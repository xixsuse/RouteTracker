package de.florianm.android.routetracker.provider.helper;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import de.florianm.android.routetracker.provider.RouteDbSchema;

public abstract class BaseTableHelper {
    protected SQLiteOpenHelper openHelper;

    public BaseTableHelper( SQLiteOpenHelper openHelper ) {
        this.openHelper = openHelper;
    }

    public SQLiteOpenHelper getOpenHelper() {
        return openHelper;
    }

    protected synchronized SQLiteDatabase getReadableDatabase() {
        return getOpenHelper().getReadableDatabase();
    }

    protected synchronized SQLiteDatabase getWritableDatabase() {
        return getOpenHelper().getWritableDatabase();
    }

    protected void removeBaseColumns(ContentValues values){
        values.remove(RouteDbSchema.BaseDbTable._ID);
        values.remove(RouteDbSchema.BaseDbTable.CREATED_AT);
        values.remove(RouteDbSchema.BaseDbTable.MODIFIED_AT);
        values.remove(RouteDbSchema.BaseDbTable.DELETED_AT);
    }

    public abstract Uri insert( ContentValues values );

    public abstract Cursor query( String[] projection, String whereClause, String[] whereArgs, String sortOrder );

    public abstract int update( ContentValues values, String whereClause, String[] whereArgs );

    public abstract int delete( String whereClause, String[] whereArgs );
}
