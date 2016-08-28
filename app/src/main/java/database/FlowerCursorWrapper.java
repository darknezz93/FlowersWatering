package database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;
import java.util.UUID;

import criminal.com.criminalintent.Flower;
import database.FlowerDbSchema.FlowerTable;

/**
 * Created by adam on 22.08.16.
 */
public class FlowerCursorWrapper extends CursorWrapper {

    public FlowerCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Flower getFlower() {
        String uuidString = getString(getColumnIndex(FlowerTable.Cols.UUID));
        String title = getString(getColumnIndex(FlowerTable.Cols.NAME));
        long startDate = getLong(getColumnIndex(FlowerTable.Cols.START_DATE));
        long endDate = getLong(getColumnIndex(FlowerTable.Cols.END_DATE));
        int days = getInt(getColumnIndex(FlowerTable.Cols.DAYS));
        int isNotification = getInt(getColumnIndex(FlowerTable.Cols.NOTIFICATION));

        Flower flower = new Flower(UUID.fromString(uuidString));
        flower.setName(title);
        flower.setStartDate(new Date(startDate));
        if(endDate >= startDate) {
            flower.setEndDate(new Date(endDate));
        } else {
            flower.setEndDate(null);
        }
        flower.setDays(days);
        flower.setNotification(isNotification != 0);

        return flower;
    }
}
