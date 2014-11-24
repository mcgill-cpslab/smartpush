package mcgill.cpslab.smartpush.content.database;

import android.provider.BaseColumns;

public final class SmartpushAppContract {
	
	public SmartpushAppContract(){}
	
	public static abstract class AppEntry implements BaseColumns{
		
		public static final String TABLE_NAME="app_entry";
		public static final String COLUMN_APP_PACKAGE_NAME="app_package_name";
		public static final String COLUMN_APP_FREQUENCY="app_package_freq";
		
	}
	
	public static abstract class TrackEntry implements BaseColumns{
		
		public static final String TABLE_NAME="track_entry";
		public static final String COLUMN_APP_PACKAGE_NAME="app_package_name";
		public static final String COLUMN_APP_START_TIME = "app_start_time";
		public static final String COLUMN_APP_STOP_TIME = "app_stop_time";
		public static final String COLUMN_APP_CURRENT_USER_ACTIVITY = "app_current_user_activity";
		public static final String COLUMN_APP_CURRENT_USER_ACTIVITY_CONFIDENCE = "app_current_user_activity_confidence";
		
	}
}
