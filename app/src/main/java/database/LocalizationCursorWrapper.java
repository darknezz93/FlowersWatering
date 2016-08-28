package database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;
import java.util.UUID;

import criminal.com.criminalintent.Flower;
import criminal.com.criminalintent.Localization;

/**
 * Created by adam on 28.08.16.
 */
public class LocalizationCursorWrapper extends CursorWrapper {

    public LocalizationCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Localization getLocalization() {
        String uuidString = getString(getColumnIndex(LocalizationDbDchema.LocalizationTable.Cols.UUID));
        double latitude = getDouble(getColumnIndex(LocalizationDbDchema.LocalizationTable.Cols.LATITUDE));
        double longitude = getDouble(getColumnIndex(LocalizationDbDchema.LocalizationTable.Cols.LONGITUDE));



        Localization localization = new Localization(Integer.valueOf(uuidString));
        localization.setLatitude(latitude);
        localization.setLongitude(longitude);

        return localization;
    }

}
