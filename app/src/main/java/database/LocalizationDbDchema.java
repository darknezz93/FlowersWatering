package database;

/**
 * Created by adam on 28.08.16.
 */
public class LocalizationDbDchema {

    public static final class LocalizationTable {
        public static final String NAME = "localizations";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String LATITUDE = "latitude";
            public static final String LONGITUDE = "longitude";
        }
    }
}
