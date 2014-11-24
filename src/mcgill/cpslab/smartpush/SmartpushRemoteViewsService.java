package mcgill.cpslab.smartpush;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import mcgill.cpslab.smartpush.SmartpushCoreService.ServiceBinder;
import mcgill.cpslab.smartpush.content.SmartpushApp;
import mcgill.cpslab.smartpush.content.SmartpushContent;
import mcgill.cpslab.smartpush.content.SmartpushData;
import mcgill.cpslab.smartpush.content.SmartpushNotification;

import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViewsService;

public class SmartpushRemoteViewsService extends RemoteViewsService {
	public static final String tag="SmartpushService";
	
	private SmartpushCoreService coreService=null;
	private SmartpushData data;

	public SmartpushRemoteViewsService() {
		
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(tag,"on Create");
		data = SmartpushData.getInstance(this.getApplicationContext());
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	
	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		String type = intent.getStringExtra(SmartpushWidgetProvider.SMARTPUSH_REMOTEVIEW_TYPE);
		List<SmartpushApp> apps=data.getApps();
		List<SmartpushNotification> notifications=data.getNotifications();
		Log.d(tag,"Notification size is "+notifications.size());
		Log.d(tag, "Type is "+type);
		if(type.equals(SmartpushWidgetProvider.SMARTPUSH_REMOTEVIEW_TYPE_NOTIFICATION)){
			Log.d(tag,"In branch type notification");
			return new SmartpushNotificationRemoteViewFactory(this.getApplicationContext(),intent,notifications);
		}
		else{
			if(coreService==null){
				Log.d(tag,"Core Service is null");
			}
			else{
				Log.d(tag,"Core Service is not null");
			}
			if(apps!=null){
				Log.d(tag,"App items");
			}
			return new SmartpushAppRemoteViewFactory(this.getApplicationContext(),intent,apps);
		}
	}
}