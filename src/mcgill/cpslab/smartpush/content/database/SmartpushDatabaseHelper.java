package mcgill.cpslab.smartpush.content.database;

import java.util.ArrayList;

import mcgill.cpslab.smartpush.content.SmartpushApp;
import mcgill.cpslab.smartpush.content.SmartpushContent;
import mcgill.cpslab.smartpush.content.SmartpushTrackEntry;
import mcgill.cpslab.smartpush.content.database.SmartpushAppContract.AppEntry;
import mcgill.cpslab.smartpush.content.database.SmartpushAppContract.TrackEntry;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SmartpushDatabaseHelper extends SQLiteOpenHelper {
	
	public static final String tag = "SmartpushDatabaseHelper";
	
	public static final int DATABASE_VERSION = 2;
	public static final String DATABASE_NAME = "Smartpush.db";
	
	//Setting for App entry
	private static final String SQL_CREATE_TABLE_APP_ENTRIES = 
			"CREATE TABLE " + AppEntry.TABLE_NAME +" (" + 
					AppEntry.COLUMN_APP_PACKAGE_NAME + " TEXT NOT NULL," + 
					AppEntry.COLUMN_APP_FREQUENCY + " INTEGER," + 
					"PRIMARY KEY (" + AppEntry.COLUMN_APP_PACKAGE_NAME +")" +
					" )";
	private static final String SQL_DELETE_TABLE_APP_ENTRIES =
		    "DROP TABLE IF EXISTS " + AppEntry.TABLE_NAME;
	
	private static final String[] APP_PROJECTION= {
		AppEntry.COLUMN_APP_PACKAGE_NAME,
		AppEntry.COLUMN_APP_FREQUENCY
		};
	
	private static final String FREQ_ORDER = AppEntry.COLUMN_APP_FREQUENCY + " DESC";
	
	//Setting for Track entry
	private static final String SQL_CREATE_TABLE_TRACK_ENTRIES = 
			"CREATE TABLE " + TrackEntry.TABLE_NAME +" (" + 
					TrackEntry._ID + " INTEGER PRIMARY KEY," +
					TrackEntry.COLUMN_APP_PACKAGE_NAME + " TEXT NOT NULL," + 
					TrackEntry.COLUMN_APP_START_TIME + " INTEGER NOT NULL," +
					TrackEntry.COLUMN_APP_STOP_TIME + " INTEGER NOT NULL," + 
					TrackEntry.COLUMN_APP_CURRENT_USER_ACTIVITY + " INTEGER NOT NULL,"+
					TrackEntry.COLUMN_APP_CURRENT_USER_ACTIVITY_CONFIDENCE + " INTEGER NOT NULL"+
					" )";
	
	private static final String SQL_DELETE_TABLE_TRACK_ENTRIES = 
			"DROP TABLE IF EXISTS " + TrackEntry.TABLE_NAME;
	
	private static final String[] TRACK_PROJECTION = {
		TrackEntry._ID,
		TrackEntry.COLUMN_APP_PACKAGE_NAME,
		TrackEntry.COLUMN_APP_START_TIME,
		TrackEntry.COLUMN_APP_STOP_TIME,
		TrackEntry.COLUMN_APP_CURRENT_USER_ACTIVITY,
		TrackEntry.COLUMN_APP_CURRENT_USER_ACTIVITY_CONFIDENCE
	};
	
	private static final String TIME_ORDER = TrackEntry.COLUMN_APP_START_TIME + " DESC";
	
	private Context context;
	
	private static SmartpushDatabaseHelper dbHelper = null;
	
	public static SmartpushDatabaseHelper getInstance(Context context){
		if(dbHelper==null){
			dbHelper = new SmartpushDatabaseHelper(context);
		}
		return dbHelper;
	}
	
	private SmartpushDatabaseHelper(Context context){
		super(context,DATABASE_NAME,null,DATABASE_VERSION);
		this.context=context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
		db.execSQL(SQL_CREATE_TABLE_APP_ENTRIES);
		db.execSQL(SQL_CREATE_TABLE_TRACK_ENTRIES);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		if(newVersion>oldVersion){
			db.execSQL(SQL_DELETE_TABLE_APP_ENTRIES);
			db.execSQL(SQL_DELETE_TABLE_TRACK_ENTRIES);
			onCreate(db);
		}
		
	}
	
	//App entries functions
	public ArrayList<SmartpushApp> getApps(){
		ArrayList<SmartpushApp> apps = new ArrayList<SmartpushApp>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.query(
				AppEntry.TABLE_NAME, 
				APP_PROJECTION,
				null,
				null, 
				null, 
				null, 
				FREQ_ORDER);
		boolean hasValue = c.moveToFirst();
		while(hasValue){
			int package_name_index = c.getColumnIndexOrThrow(AppEntry.COLUMN_APP_PACKAGE_NAME);
			String package_name = c.getString(package_name_index);
			int freq_index = c.getColumnIndexOrThrow(AppEntry.COLUMN_APP_FREQUENCY);
			int freq = c.getInt(freq_index);
			SmartpushApp app = new SmartpushApp(context, package_name);
			app.setFrequency(freq);
			apps.add(app);
			hasValue = c.moveToNext();
		}
		return apps;
	}
	
	public SmartpushApp getApp(String package_name){
		SQLiteDatabase db = this.getReadableDatabase();
		String whereClause = AppEntry.COLUMN_APP_PACKAGE_NAME + " =?";
		String[] whereArgs = new String[]{package_name};
		Log.d(tag,"Get app "+package_name);
		Cursor c = db.query(
				AppEntry.TABLE_NAME, 
				APP_PROJECTION,
				whereClause,
				whereArgs, 
				null, 
				null, 
				FREQ_ORDER);
		boolean hasValue = c.moveToFirst();
		Log.d(tag, "GetApp hasvalue? " + hasValue);
		if(hasValue){
			int freq_index = c.getColumnIndexOrThrow(AppEntry.COLUMN_APP_FREQUENCY);
			int freq = c.getInt(freq_index);
			SmartpushApp app = new SmartpushApp(context,package_name);
			app.setFrequency(freq);
			return app;
		}
		else{
			return null;
		}
	}
	
	public long insertApp(SmartpushContent app){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(AppEntry.COLUMN_APP_PACKAGE_NAME, app.getPackage_name());
		values.put(AppEntry.COLUMN_APP_FREQUENCY, app.getFrequency());
		//Log.d(tag, "Insert app " + app.getPackage_name());
		long newID = db.insert(AppEntry.TABLE_NAME, null, values);
		//Log.d(tag, "Insert app New ID " + newID);
		return newID;
	}
	
	public void deleteApp(String package_name){
		String whereClause = AppEntry.COLUMN_APP_PACKAGE_NAME + " =?";
		String[] whereArgs = new String[]{package_name};
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(AppEntry.TABLE_NAME, whereClause, whereArgs);
	}
	
	public void updateApp(SmartpushContent app){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(AppEntry.COLUMN_APP_FREQUENCY, app.getFrequency());
		String whereClause = AppEntry.COLUMN_APP_PACKAGE_NAME + " =?";
		String[] whereArgs = new String[]{app.getPackage_name()};
		Log.d(tag,"Update app "+ app.getPackage_name());
		int count = db.update(AppEntry.TABLE_NAME, values, whereClause, whereArgs);
		Log.d(tag,"Update number of entries " + count);
	}
	
	//Track entries functions
	public long submitTrackEntry(SmartpushTrackEntry entry){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(TrackEntry.COLUMN_APP_PACKAGE_NAME, entry.getPackage_name());
		values.put(TrackEntry.COLUMN_APP_START_TIME, entry.getStart_time());
		values.put(TrackEntry.COLUMN_APP_STOP_TIME, entry.getStop_time());
		values.put(TrackEntry.COLUMN_APP_CURRENT_USER_ACTIVITY, entry.getCurrent_user_activity());
		values.put(TrackEntry.COLUMN_APP_CURRENT_USER_ACTIVITY_CONFIDENCE, entry.getCurrent_user_activity());
		long newID = db.insert(TrackEntry.TABLE_NAME, null, values);
		Log.d(tag,"Entry " + newID +" inserted!");
		return newID;
	}
	
	public ArrayList<SmartpushTrackEntry> getTrackEntries(long startTime){
		ArrayList<SmartpushTrackEntry> entries = new ArrayList<SmartpushTrackEntry>();
		SQLiteDatabase db = this.getReadableDatabase();
		String whereClause = TrackEntry.COLUMN_APP_START_TIME + " >?";
		String[] whereArgs = new String[]{""+startTime};
		Cursor c = db.query(
				TrackEntry.TABLE_NAME, 
				TRACK_PROJECTION,
				whereClause,
				whereArgs, 
				null, 
				null, 
				TIME_ORDER);
		boolean hasValue = c.moveToFirst();
		Log.d(tag,"Get Track Entries "+c.getCount());
		while(hasValue){
			int _id_index = c.getColumnIndexOrThrow(TrackEntry._ID);
			long _id = c.getLong(_id_index);
			int package_name_index = c.getColumnIndexOrThrow(TrackEntry.COLUMN_APP_PACKAGE_NAME);
			String package_name = c.getString(package_name_index);
			int start_time_index = c.getColumnIndexOrThrow(TrackEntry.COLUMN_APP_START_TIME);
			long start_time = c.getLong(start_time_index);
			int stop_time_index = c.getColumnIndexOrThrow(TrackEntry.COLUMN_APP_STOP_TIME);
			long stop_time = c.getLong(stop_time_index);
			int user_activity_index = c.getColumnIndexOrThrow(TrackEntry.COLUMN_APP_CURRENT_USER_ACTIVITY);
			int user_activity = c.getInt(user_activity_index);
			int user_activity_confidence_index = c.getColumnIndexOrThrow(TrackEntry.COLUMN_APP_CURRENT_USER_ACTIVITY_CONFIDENCE);
			int user_activity_confidence = c.getInt(user_activity_confidence_index);
			
			SmartpushTrackEntry entry = new SmartpushTrackEntry();
			entry.set_id(_id);
			entry.setPackage_name(package_name);
			entry.setStart_time(start_time);
			entry.setStop_time(stop_time);
			entry.setCurrent_user_activity(user_activity);
			entry.setCurrent_user_activity_confidence(user_activity_confidence);
			entries.add(entry);
			
			hasValue = c.moveToNext();
		}
		return entries;
	}
	
}
