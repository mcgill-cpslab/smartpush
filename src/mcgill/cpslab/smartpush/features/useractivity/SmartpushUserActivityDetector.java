package mcgill.cpslab.smartpush.features.useractivity;

import java.util.Date;

import mcgill.cpslab.smartpush.R;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.DetectedActivity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class SmartpushUserActivityDetector implements ConnectionCallbacks, 
			OnConnectionFailedListener, SmartpushActivityRecognitionCallback{
	
	public static final String tag = "SmartpushUserActivityDetector";
	
	public static final int MILLISECONDS_PER_SECOND = 1000;
    public static final int DETECTION_INTERVAL_SECONDS = 60;
    public static final int DETECTION_INTERVAL_MILLISECONDS =
            MILLISECONDS_PER_SECOND * DETECTION_INTERVAL_SECONDS;
    public static final int RESTART_CHECK_INTERVAL_SECONDS = 30;
    public static final int RESTART_CHECK_INTERVAL_MILLISECONDS = 
    		RESTART_CHECK_INTERVAL_SECONDS * MILLISECONDS_PER_SECOND;
    
    public static final int SMARTPUSH_ACTIVITY_UNKNOWN = 0;
    public static final int SMARTPUSH_ACTIVITY_IN_VEHICLE = 1;
    public static final int SMARTPUSH_ACTIVITY_ON_BICYCLE = 2;
    public static final int SMARTPUSH_ACTIVITY_ON_FOOT = 3;
    public static final int SMARTPUSH_ACTIVITY_STILL = 4;
    public static final int SMARTPUSH_ACTIVITY_TILTING = 5;
    
    private PendingIntent mActivityRecognitionPendingIntent;
    public enum REQUEST_TYPE {START, STOP}
    
    private ActivityRecognitionClient mActivityRecognitionClient;
	
	private boolean mInProgress;
	private Context mContext;
	
	private REQUEST_TYPE mRequestType;
	
	private int currentType = SMARTPUSH_ACTIVITY_UNKNOWN;
	private int currentConfidence = 0;
	private long lastUpdateTime=0;
	
	//Singleton
	public static SmartpushUserActivityDetector detector=null;
	public static SmartpushUserActivityDetector getInstance(Context context){
		if(detector == null){
			detector = new SmartpushUserActivityDetector(context);
		}
		return detector;
	}
	
	private SmartpushUserActivityDetector(Context context){
		mInProgress = false;
		mRequestType = REQUEST_TYPE.STOP;
		mContext = context;
		onCreate();
	}
	
	private void onCreate(){
		mActivityRecognitionClient =
                new ActivityRecognitionClient(mContext, this, this);
	}
	
	public int getCurrentActivity(){
		if(!this.checkActivityDetectorWorking()){
			startUpdates();
		}
		return currentType;
	}
	
	public int getCurrentConfidence(){
		if(!this.checkActivityDetectorWorking()){
			startUpdates();
		}
		return currentConfidence;
	}
	
	private boolean checkActivityDetectorWorking(){
		long time = new Date().getTime();
		if(time-this.lastUpdateTime>RESTART_CHECK_INTERVAL_MILLISECONDS){
			return false;
		}
		return true;
	}
	
	public void startUpdates(){
		
		Log.d(tag,"Start updates");
		
		mRequestType = REQUEST_TYPE.START;
		
		if (!servicesConnected()) {
            return;
        }
        // If a request is not already underway
        if (!mInProgress) {
            // Indicate that a request is in progress
            mInProgress = true;
            // Request a connection to Location Services
            mActivityRecognitionClient.connect();
        //
        } else {
            /*
             * A request is already underway. You can handle
             * this situation by disconnecting the client,
             * re-setting the flag, and then re-trying the
             * request.
             */
        }
	}
	
	public void stopUpdates(){
		
		Log.d(tag,"Stop updates");
		
		mRequestType = REQUEST_TYPE.STOP;
		
		if (!servicesConnected()) {
            return;
        }
        // If a request is not already underway
        if (!mInProgress) {
            // Indicate that a request is in progress
            mInProgress = true;
            // Request a connection to Location Services
            mActivityRecognitionClient.connect();
        //
        } else {
            /*
             * A request is already underway. You can handle
             * this situation by disconnecting the client,
             * re-setting the flag, and then re-trying the
             * request.
             */
        }
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		this.mInProgress=false;
		
		Log.d(tag, "Connection Failed"+result.toString());
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		Log.d(tag,"on Connected");
		
		//test
		//issueNotification("SmartpushActivityDetector", "Google Service connected");
		
		switch(this.mRequestType){
		case START:
			Log.d(tag,"Start Request");
			Intent intent = new Intent(
	                mContext, SmartpushActivityRecognitionIntentService.class);
			
			mActivityRecognitionPendingIntent =
	                PendingIntent.getService(mContext, 0, intent,
	                PendingIntent.FLAG_UPDATE_CURRENT);
			
			mActivityRecognitionClient.requestActivityUpdates(
                    DETECTION_INTERVAL_MILLISECONDS,
                    mActivityRecognitionPendingIntent);
			break;
		case STOP:
			Log.d(tag,"Stop Request");
			
			mActivityRecognitionClient.removeActivityUpdates(
                    mActivityRecognitionPendingIntent);
			break;
		default:
			break;
			
		}
		mInProgress = false;
        mActivityRecognitionClient.disconnect();
	}

	@Override
	public void onDisconnected() {
		Log.d(tag,"on Disconnected");
		mInProgress = false;
		mActivityRecognitionClient = null;
	}
	
	private boolean servicesConnected(){
		
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);
		
		if(resultCode == ConnectionResult.SUCCESS){
			Log.d(tag, "Google Play Service is available!");
			return true;
		}
		else{
			Log.d(tag, "Google Play Service is unavailable!");
			return false;
		}
	}
	
	public int TranslateToSmartpushType(int activityType) {
        switch(activityType) {
            case DetectedActivity.IN_VEHICLE:
                return SMARTPUSH_ACTIVITY_IN_VEHICLE;
            case DetectedActivity.ON_BICYCLE:
                return SMARTPUSH_ACTIVITY_ON_BICYCLE;
            case DetectedActivity.ON_FOOT:
                return SMARTPUSH_ACTIVITY_ON_FOOT;
            case DetectedActivity.STILL:
                return SMARTPUSH_ACTIVITY_STILL;
            case DetectedActivity.UNKNOWN:
                return SMARTPUSH_ACTIVITY_UNKNOWN;
            case DetectedActivity.TILTING:
                return SMARTPUSH_ACTIVITY_TILTING;
        }
        return SMARTPUSH_ACTIVITY_UNKNOWN;
    }

	@Override
	public void activityChanged(int type, int confidence) {
		
		Log.d(tag, "Activity Changed: Type-"+type+",Confidence-"+confidence);
		
		this.currentType = TranslateToSmartpushType(type);
		this.currentConfidence = confidence;
		
		this.lastUpdateTime = new Date().getTime();
		
		issueNotification("SmartpushActivityDetector",getNameFromType(type)+":"+confidence);
		
	}
	
	private String getNameFromType(int activityType) {
        switch(activityType) {
            case DetectedActivity.IN_VEHICLE:
                return "in_vehicle";
            case DetectedActivity.ON_BICYCLE:
                return "on_bicycle";
            case DetectedActivity.ON_FOOT:
                return "on_foot";
            case DetectedActivity.STILL:
                return "still";
            case DetectedActivity.UNKNOWN:
                return "unknown";
            case DetectedActivity.TILTING:
                return "tilting";
        }
        return "unknown";
    }
	
	public void issueNotification(String title, String text){
		NotificationCompat.Builder mBuilder =
			    new NotificationCompat.Builder(mContext)
			    .setSmallIcon(R.drawable.default_icon)
			    .setContentTitle(title)
			    .setContentText(text);
		int mNotificationId = 001;
		NotificationManager mNotifyMgr = 
		        (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotifyMgr.notify(mNotificationId, mBuilder.build());
	}

	public long getLastUpdateTime() {
		return lastUpdateTime;
	}

}
