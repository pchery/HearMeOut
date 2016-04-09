package carlhacks16.hearmeout.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by paulchery on 4/9/16.
 */
public class SessionContract{

    public SessionContract(){};

    public static abstract class Session implements BaseColumns {

        public static final String TABLE_NAME = "session";
        public static final String ID = "id";
        public static final String SPEED = "speed";
        public static final String VOLUME = "volume";
        public static final String MOVEMENT = "movement";
        public static final String FILLERS = "fillers";

        private static final String TEXT_TYPE = " TEXT";
        private static final String REAL_TYPE = " REAL";
        private static final String BLOB_TYPE = " BLOB";
        private static final String COMMA_SEP = ",";

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + Session.TABLE_NAME + " (" +
                        Session.ID + " INTEGER PRIMARY KEY, " +
                        Session.SPEED + REAL_TYPE + COMMA_SEP +
                        Session.VOLUME + REAL_TYPE + COMMA_SEP +
                        Session.MOVEMENT + REAL_TYPE + COMMA_SEP +
                        Session.FILLERS + REAL_TYPE +
                        ");";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + Session.TABLE_NAME;


    }
}
