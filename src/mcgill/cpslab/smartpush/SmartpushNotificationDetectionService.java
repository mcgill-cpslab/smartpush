package mcgill.cpslab.smartpush;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

public class SmartpushNotificationDetectionService extends AccessibilityService {
	
	public static final String tag="SmartpushNotificationDetectionService";
	
	public static final String BR_Notification="mcgill.cpslab.smartpush.notification";
	public static final String Action_Notification="mcgill.cpslab.smartpush.action.notification";
	public static final String BR_Source="mcgill.cpslab.smartpush.source";
	
	private LocalBroadcastManager lbm=null;
	
	public SmartpushNotificationDetectionService() {
		
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		lbm=LocalBroadcastManager.getInstance(this);
	}



	@Override
	protected void onServiceConnected() {
		super.onServiceConnected();
		Log.d(tag,"onServiceConnected");
		AccessibilityServiceInfo info = new AccessibilityServiceInfo();
		info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
		info.notificationTimeout = 1;
		info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
		setServiceInfo(info);
	}
	
	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		Log.d(tag,"onAccessibilityEvent");
		if(event.getEventType()==AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED){
			Log.d(tag,"Notification is from "+event.getPackageName().toString());
			//Log.d(tag,"Notification is " + event.getSource().getText());
			Notification ntf=(Notification)event.getParcelableData();
			
			if(ntf==null){
				Log.d(tag, "Notification is null");
			}
			else{
				Log.d(tag, "Notification summery:" + ntf.tickerText);
				Intent intent = new Intent();
				intent.setAction(Action_Notification);
				intent.putExtra("Time", event.getEventTime());
				intent.putExtra(BR_Notification, ntf);
				intent.putExtra(BR_Source, event.getPackageName());
				boolean b = LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
				Log.d(tag, "Localbroadcast manager is " + b);
			}
			//Toast.makeText(getApplication(), "Event has a list with "+ event.getText().size() + "charsequence. ", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onInterrupt() {
		Log.d(tag,"onInterrupt");
	}
}
