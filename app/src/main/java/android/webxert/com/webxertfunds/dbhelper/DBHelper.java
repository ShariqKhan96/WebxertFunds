package android.webxert.com.webxertfunds.dbhelper;

import android.app.job.JobScheduler;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.webxert.com.webxertfunds.sqlitemodel.Donor;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

public class DBHelper extends SQLiteOpenHelper {

    public static String DATABASE_NAME = "webxert_funds.db";
    public static String SELECTED_TABLE = "Donations";


    public DBHelper(Context context) {

        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //db.execSQL("create table " + SELECTED_TABLE + " (personId TEXT PRIMARY KEY, personName TEXT);");
        db.execSQL("Create table Donations (id INTEGER PRIMARY KEY AUTOINCREMENT, donator TEXT,value INTEGER)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
        db.execSQL("DROP TABLE IF EXISTS Donations");
    }


    //FAVORITES

    public void addDonation(String donator, int value) {

        //check if roster already exist or not, if exist update time else create new'
        if (isReminderExist(donator)) {
            updateReminder(donator, value);
        } else {
            SQLiteDatabase db = this.getWritableDatabase();
            String query = String.format("INSERT INTO " + SELECTED_TABLE + " (donator, value) VALUES('%s'," + value + ");",
                    donator);
            try {
                db.execSQL(query);
                Log.e("INSERT", "DONATION INSERTED SUCCESSFULLY");
            } catch (Exception e) {
                Log.e("INSERTDONATEERROR", e.getMessage());
            }
        }

    }

    private void updateReminder(String donator, int value) {
        SQLiteDatabase database = this.getWritableDatabase();

        int previousTotal = getPreviousTotal(donator);
        Log.e("prTotla", previousTotal + "");
        Log.e("value", value + "");
        int newTotal = previousTotal + value;

        Log.e("newTotal", newTotal + "");
        String query = String.format("UPDATE " + SELECTED_TABLE + " SET value='%s' WHERE donator='%s'", newTotal, donator);
        try {
            database.execSQL(query);
            Log.e("TOTAL", "DONATIONS UPDATED SUCCESSFULLY");
        } catch (Exception e) {
            Log.e("Update DONATIONS", e.getMessage());
        }
    }

    private int getPreviousTotal(String donator) {
        String query = String.format("SELECT value FROM " + SELECTED_TABLE + " WHERE donator='%s'", donator);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst())
            return cursor.getInt(0);
        else return 0;

    }

    private boolean isReminderExist(String donator) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = String.format("SELECT * FROM " + SELECTED_TABLE + " where donator='%s'", donator);
        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();
        cursor.close();
        if (count > 0)
            return true;
        else return false;

    }

    public void deleteAll(String donor) {
        String query = String.format("DELETE FROM Donations WHERE donator='%s'", donor);
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + SELECTED_TABLE);

    }


    public List<Donor> getDonors() {
        List<Donor> donors = new ArrayList<>();
        String query = String.format("SELECT donator,value FROM Donations");
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String name = cursor.getString(cursor.getColumnIndex("donator"));
                int value = cursor.getInt(cursor.getColumnIndex("value"));

                donors.add(new Donor(name, value));
                cursor.moveToNext();
            }
        }
        return donors;
    }

//    public int reminderCount(String employee_id) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        String Query = String.format("SELECT * FROM " + REMINDER_TABLE + " WHERE employee_id='%s'", employee_id);
//        Cursor cursor = db.rawQuery(Query, null);
//        if (cursor.getCount() == 0) {
//            cursor.close();
//            return 0;
//        } else {
//            int count = cursor.getCount();
//            cursor.close();
//            return count;
//        }
//
//    }
//
//    public void deleteClockInReminder(String roster_id, String employee_id) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        String QUERY = String.format("DELETE FROM " + REMINDER_TABLE + " WHERE roster_id='%s' AND clock_in='1' AND employee_id='%s'", roster_id, employee_id);
//        try {
//            db.execSQL(QUERY);
//
//        } catch (Exception e) {
//            Log.e("DELETING EXCP", e.getMessage());
//
//        }
//    }
//
//    public void deleteClockOutReminder(String roster_id, String employee_id) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        String QUERY = String.format("DELETE FROM " + REMINDER_TABLE + " WHERE roster_id='%s' AND clock_out='1' AND employee_id='%s'", roster_id, employee_id);
//        try {
//            db.execSQL(QUERY);
//
//        } catch (Exception e) {
//            Log.e("DELETING EXCP", e.getMessage());
//
//        }
//    }
//
//    public void removeSelected(String personId, String personName) {
//
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        String Query = String.format("DELETE FROM " + SELECTED_TABLE + " WHERE personId='%s' and personName='%s'", personId, personName);
//        db.execSQL(Query);
//
//    }
//
//    public boolean isSselected(String personId, String personName) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        String Query = String.format("SELECT * FROM " + SELECTED_TABLE + " WHERE personId='%s' and personName='%s'", personId, personName);
//        Cursor cursor = db.rawQuery(Query, null);
//        if (cursor.getCount() <= 0) {
//            cursor.close();
//            return false;
//        }
//        cursor.close();
//        return true;
//    }
//
//    public int retreiveSelected() {
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor result = db.rawQuery("Select * from " + SELECTED_TABLE, null);
//        return result.getCount();
//    }
//
//    public Cursor getReminders(String emp_id) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        String Query = String.format("SELECT roster_id,employee_id,time,clock_in,clock_out FROM reminders WHERE employee_id='%s' and time>='%s'", emp_id, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Calendar.getInstance().getTime()));
//        Cursor cursor = db.rawQuery(Query, null);
//        if (cursor != null)
//            return cursor;
//        else return null;
//    }
//
//    public void checkToContinue(String employee_id, Context context) {
//        String current_date_time = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Calendar.getInstance().getTime());
//        SQLiteDatabase db = this.getWritableDatabase();
//        String query = String.format("SELECT roster_id from reminders WHERE time>='%s' AND employee_id='%s'", current_date_time, employee_id);
//        Cursor cursor = db.rawQuery(query, null);
//        if (cursor.getCount() == 0) {
//            JobScheduler scheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
//            scheduler.cancel(ConstantManager.ALARM_ID);
//        }
//    }
//
//    public void deleteReminders(String id) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        String query = String.format("DELETE FROM reminders where employee_id='%s'", id);
//        Cursor cursor = db.rawQuery(query, null);
//        Log.e("CurosrCount", cursor.getCount() + "");
//        cursor.close();
//
//    }
}