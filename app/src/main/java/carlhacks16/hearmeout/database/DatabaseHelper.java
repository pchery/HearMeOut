package carlhacks16.hearmeout.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.ContextThemeWrapper;

import java.util.ArrayList;
import java.util.List;

import carlhacks16.hearmeout.models.Session;

/**
 * Created by paulchery on 4/9/16.
 */
public class DatabaseHelper extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Session.db";

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database){
        database.execSQL(SessionContract.Session.SQL_CREATE_ENTRIES);

    }
    @Override
    public void onConfigure(SQLiteDatabase db){
        db.setForeignKeyConstraintsEnabled(true);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL(SessionContract.Session.SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void createSession(Session session){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SessionContract.Session.VOLUME, session.getVolume());
        values.put(SessionContract.Session.MOVEMENT, session.getMovements());
        values.put(SessionContract.Session.SPEED, session.getSpeed());
        values.put(SessionContract.Session.FILLERS, session.getFillers());

        if(values != null) {
            // Inserting Row
            db.insert(SessionContract.Session.TABLE_NAME, null, values);
        }
        db.close(); // Closing database connection
    }

    public int getLatestSessionId(){
        String LATEST_ID =
                "SELECT " + SessionContract.Session.ID +
                        " FROM " + SessionContract.Session.TABLE_NAME +
                        " WHERE " + SessionContract.Session.ID +
                        " = (SELECT MAX(" + SessionContract.Session.ID + ") FROM " +
                        SessionContract.Session.TABLE_NAME + ");";


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(LATEST_ID, null);
        int id = -1;
        if(cursor != null && cursor.moveToLast()){
            id = cursor.getInt(0);
        }
        return id;
    }


    public Session getLatestSession(){

        String selectQuery = "SELECT * FROM " + SessionContract.Session.TABLE_NAME +
        " WHERE " + SessionContract.Session.ID +
        "=" + getLatestSessionId() +
        ";";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();

        int fillers = cursor.getInt(4);
        int movements = cursor.getInt(3);
        int volume = cursor.getInt(2);
        int speed = cursor.getInt(1);


        Session session = new Session(volume, speed, fillers, movements);


        return session;
    }

    public int count(String table){
        SQLiteDatabase db = this.getReadableDatabase();
        String count =  "SELECT COUNT(*) FROM " + table + ";";
        Cursor mCursor = db.rawQuery(count, null);
        mCursor.moveToFirst();
        int iCount = mCursor.getInt(0);
        return iCount;
    }

    public List<Session> getSessionsHistory(){
        List<Session> history = new ArrayList<Session>();
        if(count(SessionContract.Session.TABLE_NAME) > 0 ) {
            String selectQuery = "SELECT * FROM " + SessionContract.Session.TABLE_NAME + ";";

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            Session session;
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    int fillers = cursor.getInt(4);
                    int movements = cursor.getInt(3);
                    int volume = cursor.getInt(2);
                    int speed = cursor.getInt(1);

                    session = new Session(volume, speed, fillers, movements);

                    history.add(session);
                } while (cursor.moveToNext());
            }
        }
        // return contact list
        return history;

    }

    public void updateSession(int value, String columnName){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(columnName, value);
        String selection = "id = (SELECT MAX(id) FROM " + SessionContract.Session.TABLE_NAME + ")";
        db.update(SessionContract.Session.TABLE_NAME, values, selection, null);

    }
}


