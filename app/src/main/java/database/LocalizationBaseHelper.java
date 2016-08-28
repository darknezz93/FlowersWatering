package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by adam on 28.08.16.
 */
public class LocalizationBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 2;
    private static final String DATABASE_NAME = "flowerBase.db";

    public LocalizationBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
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
