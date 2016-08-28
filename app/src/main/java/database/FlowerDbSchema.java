package database;

/**
 * Created by adam on 22.08.16.
 */
public class FlowerDbSchema {

    public static final class FlowerTable {
        public static final String NAME = "flowers";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String NAME = "name";
            public static final String START_DATE = "start_date";
            public static final String END_DATE = "end_date";
            public static final String DAYS = "days";
            public static final String NOTIFICATION = "notification";
        }
    }
}
