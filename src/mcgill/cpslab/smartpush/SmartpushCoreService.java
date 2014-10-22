package mcgill.cpslab.smartpush;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import mcgill.cpslab.smartpush.content.SmartpushApp;
import mcgill.cpslab.smartpush.content.SmartpushContent;
import mcgill.cpslab.smartpush.content.SmartpushData;
import mcgill.cpslab.smartpush.content.SmartpushNotification;
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
	
	//For test
	private Timer timer;
	
	private TimerTask updateTask = new TimerTask(){

		@Override
		public void run() {
			// TODO Auto-generated method stub
			updateWidget();
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
		
		ArrayList<SmartpushApp> apps=data.getApp_items();
		
		//for test
		ArrayList<SmartpushNotification> notifications=data.getNotification_items();
		
		IntentFilter intentfilter=new IntentFilter();
		intentfilter.addAction(SmartpushNotificationDetectionService.Action_Notification);
		LocalBroadcastManager.getInstance(this).registerReceiver(localBoardcastReceiver, intentfilter);
		
		pm=this.getApplicationContext().getPackageManager();
    	packages=pm.getInstalledApplications(PackageManager.GET_META_DATA);
    	Log.d(tag,"Packages size "+packages.size());
    	for(ApplicationInfo app:packages){
			String pac_n=app.packageName;
			SmartpushApp app_item = new SmartpushApp(this.getApplicationContext(), pac_n);
			
			//save intent of applications which can be started externally in array list
			if(app_item.getIntent()!=null){
				apps.add(app_item);
			}
		}
    	//For test
    	timer=new Timer();
    	//timer.schedule(updateTask, 0, 5000);
	}


	private BroadcastReceiver localBoardcastReceiver=new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action=intent.getAction();
			Log.d(tag,"action is "+action);
			if(action==SmartpushNotificationDetectionService.Action_Notification){
				String package_name=(String)intent.getStringExtra(SmartpushNotificationDetectionService.BR_Source);
				if(package_name!=null){
					Log.d(tag,"Notification from "+package_name);
					ArrayList<SmartpushNotification> notifications=data.getNotification_items();
					Notification ntf=(Notification)intent.getParcelableExtra(SmartpushNotificationDetectionService.BR_Notification);
					//String package_name=(String)intent.getStringExtra(SmartpushNotificationDetectionService.BR_Source);
					Log.d(tag,"Notifications size:"+notifications.size());
					SmartpushNotification notification=new SmartpushNotification(SmartpushCoreService.this.getApplicationContext(),package_name);
					notification.setNotification(ntf);
					notifications.add(notification);
					appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetManager.getAppWidgetIds(new ComponentName(context,SmartpushWidgetProvider.class)), R.id.notification_list_view);
				}
				else{
					Log.d(tag, "Package_name is null");
				}
			}
		}
		
	};
	
	private void updateWidget(){
//		ComponentName provider = new ComponentName(this.getApplicationContext(),SmartpushWidgetProvider.class);
//		int[] appWidgetIds=appWidgetManager.getAppWidgetIds(provider);
//		final int N = appWidgetIds.length;
//		Log.d(tag, "Smartpush onUpdate");
//		for (int i = 0; i < N; i++) {
//			
//			Intent app_intent = new Intent(this.getApplicationContext(),SmartpushRemoteViewsService.class);
//			app_intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
//			app_intent.putExtra(SmartpushWidgetProvider.SMARTPUSH_REMOTEVIEW_TYPE,SmartpushWidgetProvider.SMARTPUSH_REMOTEVIEW_TYPE_APP);
//			//app_intent.putExtra(SmartpushWidgetProvider.SMARTPUSH_REMOTEVIEW_CONTENT,items);
//			app_intent.setData(Uri.parse(app_intent.toUri(Intent.URI_INTENT_SCHEME)));
//			
//			Log.d(tag,"create notification intent");
//			
//			Intent notification_intent = new Intent(this.getApplicationContext(),SmartpushRemoteViewsService.class);
//			notification_intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
//			notification_intent.putExtra(SmartpushWidgetProvider.SMARTPUSH_REMOTEVIEW_TYPE,SmartpushWidgetProvider.SMARTPUSH_REMOTEVIEW_TYPE_NOTIFICATION);
//			notification_intent.setData(Uri.parse(notification_intent.toUri(Intent.URI_INTENT_SCHEME)));
//			
//			
//			Log.d(tag,"create app intent");
//			
//			RemoteViews rv=new RemoteViews(this.getPackageName(),R.layout.smartpush);
//			
//			Log.d(tag,"Smartpush AppWidget "+i);
//			
//			//For test Notification bar
//			rv.setRemoteAdapter(R.id.notification_list_view,notification_intent);
//			//rv.setTextViewText(R.id.notification_list_view, "Notification test 1");
//			//rv.setTextViewTextSize(R.id.notification_list_view, TypedValue.COMPLEX_UNIT_SP, 40);
//			//rv.setRemoteAdapter(R.id.notification_list_view, rv);
//			rv.setRemoteAdapter(R.id.gridview, app_intent);
//			
//			
//			rv.setEmptyView(R.id.gridview, R.id.empty_view);
//			rv.setEmptyView(R.id.notification_list_view, R.id.empty_view);
//			//rv.setTextViewText(R.id.empty_view, "APP_NOTIFICATION");
//			//Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.sohu.newsclient");
//			//Intent launchIntent=new Intent(context,SmartpushConfigureActivity.class);
//			Intent clickIntent = new Intent(this.getApplicationContext(),SmartpushWidgetProvider.class);
//			clickIntent.setAction(SmartpushWidgetProvider.SMARTPUSH_CLICK_ACTION);
//			PendingIntent pendingIntent=PendingIntent.getBroadcast(this.getApplicationContext(), 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//			rv.setPendingIntentTemplate(R.id.gridview, pendingIntent);
//			rv.setPendingIntentTemplate(R.id.notification_list_view,pendingIntent);
//			
//			
//			appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
//		}
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
	
}
