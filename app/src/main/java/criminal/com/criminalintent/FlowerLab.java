package criminal.com.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import database.FlowerBaseHelper;
import database.FlowerCursorWrapper;
import database.FlowerDbSchema.FlowerTable;

/**
 * Created by adam on 07.08.16.
 */
public class FlowerLab {

    private static FlowerLab sFlowerLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;


    public static FlowerLab get(Context context) {
        if (sFlowerLab == null) {
            sFlowerLab = new FlowerLab(context);
        }
        return sFlowerLab;
    }

    private FlowerLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new FlowerBaseHelper(mContext).getWritableDatabase();
    }

    public void addFlower(Flower c) {
        ContentValues values = getContentValues(c);
        mDatabase.insert(FlowerTable.NAME, null, values);
    }

    public void deleteFlower(Flower flower) {
        String uuidString = flower.getId().toString();

        ContentValues values = getContentValues(flower);
        mDatabase.delete(FlowerTable.NAME,
                FlowerTable.Cols.UUID + " = ?",
                new String[]{uuidString});

    }

    public List<Flower> getFlowers() {
        List<Flower> flowers = new ArrayList<>();

        FlowerCursorWrapper cursor = (FlowerCursorWrapper) queryCrimes(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                flowers.add(cursor.getFlower());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return flowers;
    }

    public Flower getFlower(UUID id) {
        FlowerCursorWrapper cursor = (FlowerCursorWrapper) queryCrimes(
                FlowerTable.Cols.UUID + " = ?",
                new String[] { id.toString() }
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getFlower();
        } finally {
            cursor.close();
        }
    }

    public List<Flower> getNotificationFlowers() {
        List<Flower> flowers = new ArrayList<>();
        FlowerCursorWrapper cursor = (FlowerCursorWrapper) queryNotificationFlowers();

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                flowers.add(cursor.getFlower());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return flowers;
    }

    public void updateFlower(Flower flower) {
        String uuidString = flower.getId().toString();
        ContentValues values = getContentValues(flower);

        mDatabase.update(FlowerTable.NAME, values,
                FlowerTable.Cols.UUID + " = ?",
                new String[]{uuidString});
    }

    private static ContentValues getContentValues(Flower flower) {
        ContentValues values = new ContentValues();
        values.put(FlowerTable.Cols.UUID, flower.getId().toString());
        values.put(FlowerTable.Cols.NAME, flower.getName());
        values.put(FlowerTable.Cols.START_DATE, flower.getStartDate().getTime());
        if(flower.getEndDate() != null) {
            values.put(FlowerTable.Cols.END_DATE, flower.getEndDate().getTime());
        } else {
            values.put(FlowerTable.Cols.END_DATE, (byte[]) null);
        }
        values.put(FlowerTable.Cols.DAYS, flower.getDays());
        values.put(FlowerTable.Cols.NOTIFICATION, flower.isNotification() ? 1 : 0);

        return values;
    }

    private Cursor queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                FlowerTable.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null  // orderBy
        );

        return new FlowerCursorWrapper(cursor);
    }

    private Cursor queryNotificationFlowers() {
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM flowers WHERE NOTIFICATION=1", null);
        return new FlowerCursorWrapper(cursor);
    }

    public File getPhotoFile(Flower flower) {
        File externalFilesDir = Environment.getExternalStorageDirectory();

        if (externalFilesDir == null) {
            return null;
        }
        return new File(externalFilesDir, flower.getPhotoFilename());
    }


}
