package criminal.com.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import database.FlowerBaseHelper;
import database.FlowerCursorWrapper;
import database.FlowerDbSchema;
import database.LocalizationBaseHelper;
import database.LocalizationCursorWrapper;
import database.LocalizationDbDchema;

/**
 * Created by adam on 28.08.16.
 */
public class LocalizationLab {

    private static LocalizationLab sLocalizationLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;


    public static LocalizationLab get(Context context) {
        if (sLocalizationLab == null) {
            sLocalizationLab = new LocalizationLab(context);
        }
        return sLocalizationLab;
    }

    private LocalizationLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new LocalizationBaseHelper(mContext).getWritableDatabase();
    }

    public void addLocalization(Localization localization) {
        Localization locale = getLocalization(localization.getId());
        if(locale != null) {
            updateLocalization(localization);
        } else {
            ContentValues values = getContentValues(localization);
            mDatabase.insert(LocalizationDbDchema.LocalizationTable.NAME, null, values);
        }
    }

    public void deleteLocalization(Localization localization) {
        String uuidString = String.valueOf(localization.getId());

        ContentValues values = getContentValues(localization);
        mDatabase.delete(LocalizationDbDchema.LocalizationTable.NAME,
                LocalizationDbDchema.LocalizationTable.Cols.UUID + " = ?",
                new String[]{uuidString});

    }

    public List<Localization> getLocalizations() {
        List<Localization> localizations = new ArrayList<>();

        LocalizationCursorWrapper cursor = (LocalizationCursorWrapper) queryLocalizations(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                localizations.add(cursor.getLocalization());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return localizations;
    }

    public Localization getLocalization(int id) {
        LocalizationCursorWrapper cursor = (LocalizationCursorWrapper) queryLocalizations(
                LocalizationDbDchema.LocalizationTable.Cols.UUID + " = ?",
                new String[]{String.valueOf(id)}
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getLocalization();
        } finally {
            cursor.close();
        }
    }

    public void updateLocalization(Localization localization) {
        String uuidString = String.valueOf(localization.getId());
        ContentValues values = getContentValues(localization);

        mDatabase.update(LocalizationDbDchema.LocalizationTable.NAME, values,
                LocalizationDbDchema.LocalizationTable.Cols.UUID + " = ?",
                new String[]{uuidString});
    }

    private static ContentValues getContentValues(Localization localization) {
        ContentValues values = new ContentValues();
        values.put(LocalizationDbDchema.LocalizationTable.Cols.UUID, localization.getId());
        values.put(LocalizationDbDchema.LocalizationTable.Cols.LATITUDE, localization.getLatitude());
        values.put(LocalizationDbDchema.LocalizationTable.Cols.LONGITUDE, localization.getLongitude());

        return values;
    }

    private Cursor queryLocalizations(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                LocalizationDbDchema.LocalizationTable.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null  // orderBy
        );

        return new LocalizationCursorWrapper(cursor);
    }
}
