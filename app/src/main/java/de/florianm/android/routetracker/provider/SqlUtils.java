package de.florianm.android.routetracker.provider;

import android.text.TextUtils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class SqlUtils {
    private static final SimpleDateFormat SQL_DATETIME_FMT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String formatDateTime(Date dateTime) {
        if (null == dateTime) return null;
        return SQL_DATETIME_FMT.format(dateTime);
    }

    public static Date parseDateTime(String value) throws ParseException {
        if (TextUtils.isEmpty(value)) return null;
        return SQL_DATETIME_FMT.parse(value);
    }

}
