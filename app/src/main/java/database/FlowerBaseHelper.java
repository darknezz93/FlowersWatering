package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import database.FlowerDbSchema.FlowerTable;

/**
 * Created by adam on 22.08.16.
 */
public class FlowerBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 2;
    private static final String DATABASE_NAME = "flowerBase.db";

    public FlowerBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + FlowerTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                FlowerTable.Cols.UUID + ", " +
                FlowerTable.Cols.NAME + ", " +
                FlowerTable.Cols.START_DATE + ", " +
                FlowerTable.Cols.END_DATE + ", " +
                FlowerTable.Cols.DAYS + ", " +
                FlowerTable.Cols.NOTIFICATION + ")"
        );

        db.execSQL("create table " + LocalizationDbDchema.LocalizationTable.NAME + "(" +
                        " _id integer primary key autoincrement, " +
                        LocalizationDbDchema.LocalizationTable.Cols.UUID + ", " +
                        LocalizationDbDchema.LocalizationTable.Cols.LATITUDE + ", " +
                        LocalizationDbDchema.LocalizationTable.Cols.LONGITUDE +  ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
