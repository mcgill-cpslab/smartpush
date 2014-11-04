package mcgill.cpslab.smartpush;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import mcgill.cpslab.smartpush.content.SmartpushApp;
import mcgill.cpslab.smartpush.content.SmartpushContent;
import mcgill.cpslab.smartpush.content.SmartpushData;
import mcgill.cpslab.smartpush.content.SmartpushNotification;
import mcgill.cpslab.smartpush.event.SmartpushAppEventListener;
import mcgill.cpslab.smartpush.event.SmartpushEventDetectionService;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.RemoteViews;

public class SmartpushCoreService extends Service{
	
	public static final String tag="SmartpushCoreService";
	
	public static final String Extra_App_Array="mcgill.cpslab.smartpush.app_array";
	public static final String Extra_Notification_Array="mcgill.cpslab.smartpush.app_array";
	
	
	private AppWidgetManager appWidgetManager;
	private PackageManager pm;
	private List<ApplicationInfo> packages;
	private SmartpushData data;
	
	private String currentApp;
	private boolean inApp;
	
	private boolean bindToEventDetector=false;
	
	//For test
	private Timer timer;
	
	private TimerTask updateTask = new TimerTask(){

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(bindToEventDetector){
				updateWidget();
			}
			else{
				bindToEventDetector = SmartpushEventDetectionService.bindAppEventListener(appListener);
		    	if(bindToEventDetector){
		    		Log.d(tag,"SmartpushEventDetectionService Bind successfully!");
		    	}
		    	else{
		    		Log.d(tag,"Fail to SmartpushEventDetectionService!");

		    	}
			}
			
		}
		
	};
	
	private final IBinder binder = new ServiceBinder();
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.d(tag, "Smart push core service created");
		
		appWidgetManager=AppWidgetManager.getInstance(this.getApplicationContext());
		data=SmartpushData.getInstance();
		
		IntentFilter intentfilter=new IntentFilter();
		intentfilter.addAction(SmartpushNotificationListenerService.Action_Notification_Posted);
		intentfilter.addAction(SmartpushNotificationListenerService.Action_Notification_Removed);
		intentfilter.addAction(SmartpushEventDetectionService.View_Clicked);
		LocalBroadcastManager.getInstance(this).registerReceiver(localBoardcastReceiver, intentfilter);
		
		pm=this.getApplicationContext().getPackageManager();
    	packages=pm.getInstalledApplications(PackageManager.GET_META_DATA);
    	Log.d(tag,"Packages size "+packages.size());
    	for(ApplicationInfo app:packages){
			String pac_n=app.packageName;
			SmartpushApp app_item = new SmartpushApp(this.getApplicationContext(), pac_n);
			
			//save intent of applications which can be started externally in array list
			if(app_item.getIntent()!=null){
				Log.d(tag,"Intent action "+app_item.getIntent().getAction());
				Log.d(tag,"Intent categories "+app_item.getIntent().getCategories());
				//Log.d(tag,"Intent "+app_item.getIntent().)
				data.pushApp(app_item);
			}
		}
    	bindToEventDetector = SmartpushEventDetectionService.bindAppEventListener(appListener);
    	if(bindToEventDetector){
    		Log.d(tag,"SmartpushEventDetectionService Bind successfully!");
    	}
    	else{
    		Log.d(tag,"Fail to SmartpushEventDetectionService!");

    	}
    	//For test
    	timer=new Timer();
    	timer.schedule(updateTask, 0, 10000);
	}


	private BroadcastReceiver localBoardcastReceiver=new BroadcastReceiver(){
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action=intent.getAction();
			Log.d(tag,"action is "+action);
			if(action.equals(SmartpushNotificationListenerService.Action_Notification_Posted)){
				String package_name=(String)intent.getStringExtra(SmartpushNotificationListenerService.BR_Source);
				if(package_name!=null){
					Log.d(tag,"Notification from "+package_name);
					Notification ntf=(Notification)intent.getParcelableExtra(SmartpushNotificationListenerService.BR_Notification);
					int id=intent.getIntExtra(SmartpushNotificationListenerService.BR_Notification_Id, 0);
					//String package_name=(String)intent.getStringExtra(SmartpushNotificationDetectionService.BR_Source);
					Log.d(tag,"Notifications id is " + id);
					SmartpushNotification notification=new SmartpushNotification(SmartpushCoreService.this.getApplicationContext(),package_name,id);
					notification.setNotification(ntf);
					data.pushNotification(notification);
					appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetManager.getAppWidgetIds(new ComponentName(context,SmartpushWidgetProvider.class)), R.id.notification_list_view);
				}
				else{
					Log.d(tag, "Package_name is null");
				}
			}
			else if(action.equals(SmartpushNotificationListenerService.Action_Notification_Removed)){
				String package_name=(String)intent.getStringExtra(SmartpushNotificationListenerService.BR_Source);
				int id=intent.getIntExtra(SmartpushNotificationListenerService.BR_Notification_Id, 0);
				data.removeNotification(package_name, id);
				appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetManager.getAppWidgetIds(new ComponentName(context,SmartpushWidgetProvider.class)), R.id.notification_list_view);
			}
			else if(action.equals(SmartpushEventDetectionService.View_Clicked)){
				
			}
		}
		
	};
	
	private void updateWidget(){
		Log.d(tag,"Update Apps");
		appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetManager.getAppWidgetIds(new ComponentName(this.getApplicationContext(),SmartpushWidgetProvider.class)), R.id.gridview);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		LocalBroadcastManager.getInstance(this).unregisterReceiver(localBoardcastReceiver);
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(tag, "onBind");
		return binder;
	}
	
	public class ServiceBinder extends Binder{
		public SmartpushCoreService getService(){
			return SmartpushCoreService.this;
		}
	}
	
	private SmartpushAppEventListener appListener=new SmartpushAppEventListener(){

		@Override
		public void startApp(String packageName) {
			// TODO Auto-generated method stub
			SmartpushData.getInstance().getApp(packageName).startToUse();
			SmartpushCoreService.this.currentApp=packageName;
			SmartpushCoreService.this.inApp=true;
		}

		@Override
		public void stopApp(String packageName) {
			// TODO Auto-generated method stub
			SmartpushData.getInstance().getApp(packageName).stopToUse();
			
			SmartpushCoreService.this.inApp=false;
		}
		
	};
	
}
