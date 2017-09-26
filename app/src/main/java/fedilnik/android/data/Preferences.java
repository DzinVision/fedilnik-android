package fedilnik.android.data;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

public class Preferences {
    private static final String PREFS_NAME = "DefaultPreferences";
    private static final String DATE_KEY = "date";

    public static void setLastOpenedDate(Date date, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(DATE_KEY, date.getTime());
        editor.apply();
    }

    public static Date getLastOpenedDate(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, 0);
        long dateTime = sharedPreferences.getLong(DATE_KEY, -1);
        if (dateTime == -1) return null;
        return new Date(dateTime);
    }
}
