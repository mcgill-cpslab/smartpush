package mcgill.cpslab.smartpush;

import java.util.ArrayList;
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

	public SmartpushRemoteViewsService() {
		
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(tag,"on Create");	}
	
	@Override
	public void onDestroy() {
		
		super.onDestroy();
	}
	
	
	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		String type = intent.getStringExtra(SmartpushWidgetProvider.SMARTPUSH_REMOTEVIEW_TYPE);
		ArrayList<SmartpushApp> app_items=SmartpushData.getInstance().getApp_items();
		ArrayList<SmartpushNotification> notification_items=SmartpushData.getInstance().getNotification_items();
		Log.d(tag, "Type is "+type);
		if(type.equals(SmartpushWidgetProvider.SMARTPUSH_REMOTEVIEW_TYPE_NOTIFICATION)){
			Log.d(tag,"In branch type notification");
			return new SmartpushNotificationRemoteViewFactory(this.getApplicationContext(),intent,notification_items);
		}
		else{
			if(coreService==null){
				Log.d(tag,"Core Service is null");
			}
			else{
				Log.d(tag,"Core Service is not null");
			}
			if(app_items!=null){
				Log.d(tag,"The number of items is "+app_items.size());
			}
			return new SmartpushAppRemoteViewFactory(this.getApplicationContext(),intent,app_items);
		}
	}
}