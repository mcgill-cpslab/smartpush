package mcgill.cpslab.smartpush;

import android.content.Intent;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class SmartpushNotificationListenerService extends NotificationListenerService {
	public static final String BR_Notification="mcgill.cpslab.smartpush.notification";
	public static final String BR_Notification_Id="mcgill.cpslab.smartpush.notification.id";
	public static final String Action_Notification_Posted="mcgill.cpslab.smartpush.action.notification.posted";
	public static final String Action_Notification_Removed="mcgill.cpslab.smartpush.action.notification.removed";
	public static final String BR_Source="mcgill.cpslab.smartpush.source";
	
	private static final String tag = "SmartpushNotificationListenerService";
	private LocalBroadcastManager lbm=null;
	
	public SmartpushNotificationListenerService() {
		
	}

	
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		lbm=LocalBroadcastManager.getInstance(this);
	}



	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}



	@Override
	public void onNotificationPosted(StatusBarNotification sbn) {
		
		Log.d(tag,"Notification text is "+sbn.getNotification());
		Log.d(tag,"Notification id is " + sbn.getId());
		Intent intent = new Intent();
		intent.setAction(Action_Notification_Posted);
		intent.putExtra("Time", sbn.getPostTime());
		intent.putExtra(BR_Notification, sbn.getNotification());
		intent.putExtra(BR_Source, sbn.getPackageName());
		intent.putExtra(BR_Notification_Id, sbn.getId());
		boolean b = LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
		Log.d(tag, "Localbroadcast manager is " + b);
		
		Log.d(tag,"Incomming notification "+sbn.getPackageName());
	}

	@Override
	public void onNotificationRemoved(StatusBarNotification sbn) {
		Log.d(tag, "Dismissed notification "+sbn.getPackageName());
		Log.d(tag,"Dismissed is Ongoing "+sbn.isOngoing());
		Log.d(tag,"Dismissed describe Content "+sbn.describeContents());
		Log.d(tag,"Dismissed tag "+sbn.getTag());
		Log.d(tag,"Dismissed id "+sbn.getId());
		Intent intent=new Intent();
		intent.setAction(Action_Notification_Removed);
		intent.putExtra("Time", sbn.getPostTime());
		intent.putExtra(BR_Source, sbn.getPackageName());
		intent.putExtra(BR_Notification_Id,sbn.getId());
		boolean b = LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
		Log.d(tag,"Localbroadcast manager is "+ b);
	}

}
