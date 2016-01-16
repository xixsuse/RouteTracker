package de.florianm.android.routetracker.provider;

import android.text.TextUtils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public final class SqlUtils {
    private static final DateTimeFormatter SQL_DATETIME_FMT = DateTimeFormat
            .forPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(DateTimeZone.UTC);

    public static String formatDateTime(DateTime dateTime) {
        if (null == dateTime) return null;
        return SQL_DATETIME_FMT.print(dateTime);
    }

    public static DateTime parseDateTime(String value) {
        if (TextUtils.isEmpty(value)) return null;
        return SQL_DATETIME_FMT.parseDateTime(value);
    }

}
