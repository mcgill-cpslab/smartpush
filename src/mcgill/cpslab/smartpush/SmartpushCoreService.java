package mcgill.cpslab.smartpush;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import mcgill.cpslab.smartpush.content.SmartpushApp;
import mcgill.cpslab.smartpush.content.SmartpushContent;
import mcgill.cpslab.smartpush.content.SmartpushData;
import mcgill.cpslab.smartpush.content.SmartpushNotification;
import mcgill.cpslab.smartpush.content.SmartpushTrackEntry;
import mcgill.cpslab.smartpush.event.SmartpushAppEventListener;
import mcgill.cpslab.smartpush.event.SmartpushEventDetectionService;
import mcgill.cpslab.smartpush.features.useractivity.SmartpushUserActivityDetector;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
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
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.RemoteViews;

public class SmartpushCoreService extends Service{
	
	public static final String tag="SmartpushCoreService";
	
	public static final String Extra_App_Array="mcgill.cpslab.smartpush.app_array";
	public static final String Extra_Notification_Array="mcgill.cpslab.smartpush.app_array";
	
	public static final int TIMER_UPDATE_INTERVAL_MILLISECONDS = 60000;
	
	public static final int WIDGET_UPDATE_INTERVAL_MINUTES = 1;

	public static final int APP_OREDER_UPDATE_INTERVAL_MINUTES = 1;
	
	public static final int EVENT_DETECTOR_CHECKER_INTERVAL_MINUTES = 1;

	public static final int A_WEEK_INTERVAL_MILLISECONDS = 7*24*60*60*1000;
	
	//Test
	public static final String TRACK_DATA_FILE = "SmartpushTrackData.txt";
	
	private AppWidgetManager appWidgetManager;
	private PackageManager pm;
	private List<ApplicationInfo> packages;
	private SmartpushData data;
	
	private String currentApp;
	private boolean inApp;
	
	private boolean bindToEventDetector=false;
	
	KeyguardManager km=null;
	private KeyguardLock lock=null;
	private boolean needToLock=false;
	
	private SmartpushUserActivityDetector activityDetector = null;
	
	//For test
	private Timer timer;
	
	private TimerTask updateTask = new TimerTask(){
		
		int counterForWidget = 0;
		int counterForEventDetector = 0;
		int counterForAppOrderUpdate = 0;
		
		@Override
		public void run() {
			
			//Test if event detector is bound
			if(counterForEventDetector== 0){
				if(!bindToEventDetector){
					bindToEventDetector = SmartpushEventDetectionService.bindAppEventListener(appListener);
			    	if(bindToEventDetector){
			    		Log.d(tag,"SmartpushEventDetectionService Bind successfully!");
			    	}
			    	else{
			    		Log.d(tag,"Fail to SmartpushEventDetectionService!");
			    	}
				}
				counterForEventDetector = (counterForEventDetector+1)%EVENT_DETECTOR_CHECKER_INTERVAL_MINUTES;
			}
			
			if(counterForAppOrderUpdate == 0){
				Log.d(tag,"Update app order!");
				data.updateAppsOrderByFrequency();
				counterForAppOrderUpdate = (counterForAppOrderUpdate+1)%APP_OREDER_UPDATE_INTERVAL_MINUTES;
			}
			
			if(counterForWidget==0){
				Log.d(tag,"Update Widget");
				updateWidget();
				//This part is for test
				String root = Environment.getExternalStorageDirectory().toString();
			    File myFile = new File(root + "/"+TRACK_DATA_FILE);
			    if(myFile.exists()){
			    	myFile.delete();
			    }
			    try {
			    	PrintWriter writer = new PrintWriter(myFile);
			    	long startTime = new Date().getTime() - A_WEEK_INTERVAL_MILLISECONDS;
			    	ArrayList<SmartpushTrackEntry> entries = data.getTrackEntries(startTime);
			    	
			    	for(SmartpushTrackEntry entry : entries){
			    		Calendar calendar = Calendar.getInstance();
			    		calendar.setTime(new Date(entry.getStart_time()));
			    		writer.println(entry.get_id()+"\t"+entry.getPackage_name() + "\t" + data.getApp(entry.getPackage_name()).getFrequencyRanking() +"\t"+
			    				calendar.get(Calendar.HOUR_OF_DAY)+"\t"+calendar.get(Calendar.MINUTE)+"\t"+
			    				entry.getDuration()+"\t" + entry.getCurrent_user_activity() + "\t"+
			    				entry.getCurrent_user_activity_confidence());
			    	}
			    	writer.flush();
			    	writer.close();

			    } catch (Exception e) {
			    	e.printStackTrace();
			    }
			    counterForWidget = (counterForWidget+1)%WIDGET_UPDATE_INTERVAL_MINUTES;
			}
			
			//Update User activity data;
			Log.d(tag,"Type:" + activityDetector.getCurrentActivity()+",Confidence:"+activityDetector.getCurrentConfidence());
			
		}
		
	};
	
	private final IBinder binder = new ServiceBinder();
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.d(tag, "Smart push core service created");
		
		appWidgetManager=AppWidgetManager.getInstance(this.getApplicationContext());
		data=SmartpushData.getInstance(this.getApplicationContext());
		
		IntentFilter localIntentfilter=new IntentFilter();
		localIntentfilter.addAction(SmartpushNotificationListenerService.Action_Notification_Posted);
		localIntentfilter.addAction(SmartpushNotificationListenerService.Action_Notification_Removed);
		localIntentfilter.addAction(SmartpushWidgetProvider.SMARTPUSH_UNLOCK_SCREEN);
//		localIntentfilter.addAction(SmartpushEventDetectionService.Intent_App_Started);
//		localIntentfilter.addAction(SmartpushEventDetectionService.Intent_App_Stopped);
		LocalBroadcastManager.getInstance(this).registerReceiver(localBoardcastReceiver, localIntentfilter);
		
		IntentFilter globalIntentFilter=new IntentFilter();
		globalIntentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		globalIntentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
		globalIntentFilter.addDataScheme("package");
		
		
		pm=this.getApplicationContext().getPackageManager();
    	packages=pm.getInstalledApplications(PackageManager.GET_META_DATA);
    	Log.d(tag,"Packages size "+packages.size());
    	for(ApplicationInfo app:packages){
			String pac_n=app.packageName;
			SmartpushApp app_item = new SmartpushApp(this.getApplicationContext(), pac_n);
			
			//save intent of applications which can be started externally in array list
			if(app_item.getIntent()!=null){
				//Log.d(tag,"Intent action "+app_item.getIntent().getAction());
				//Log.d(tag,"Intent categories "+app_item.getIntent().getCategories());
				//Log.d(tag,"Intent "+app_item.getIntent().)
				data.pushApp(app_item);
				
			}
		}
    	
    	this.registerReceiver(globalBroadcastReceiver, globalIntentFilter);
    	
    	bindToEventDetector = SmartpushEventDetectionService.bindAppEventListener(appListener);
    	if(bindToEventDetector){
    		Log.d(tag,"SmartpushEventDetectionService Bind successfully!");
    	}
    	else{
    		Log.d(tag,"Fail to SmartpushEventDetectionService!");

    	}
    	
    	activityDetector = SmartpushUserActivityDetector.getInstance(this.getApplicationContext());
    	activityDetector.startUpdates();
    	
    	//For test
    	timer=new Timer();
    	timer.schedule(updateTask, 0, TIMER_UPDATE_INTERVAL_MILLISECONDS);
    	
    	km = (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);
    	lock = km.newKeyguardLock(tag);
	}
	
	private BroadcastReceiver globalBroadcastReceiver=new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.d(tag,"Global Action is "+action);
			//Log.d(tag,"Global Action removed app "+intent.getDataString());
			if(action.equals(Intent.ACTION_PACKAGE_REMOVED)){
				Log.d(tag,intent.getDataString()+" is removed!");
				data.removeApp(intent.getDataString());
				appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetManager.getAppWidgetIds(new ComponentName(context,SmartpushWidgetProvider.class)), R.id.gridview);
				appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetManager.getAppWidgetIds(new ComponentName(context,SmartpushWidgetProvider.class)), R.id.notification_list_view);
			}
			else if(action.equals(Intent.ACTION_PACKAGE_ADDED)){
				Log.d(tag,intent.getDataString()+" is added!");
				String pac_n=intent.getDataString();
				SmartpushApp app = new SmartpushApp(SmartpushCoreService.this.getApplicationContext(),pac_n);
				appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetManager.getAppWidgetIds(new ComponentName(context,SmartpushWidgetProvider.class)), R.id.gridview);
				appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetManager.getAppWidgetIds(new ComponentName(context,SmartpushWidgetProvider.class)), R.id.notification_list_view);
				data.pushApp(app);
			}
		}
		
	};
	
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
			else if(action.equals(SmartpushWidgetProvider.SMARTPUSH_UNLOCK_SCREEN)){
				if(km.isKeyguardLocked()){
					//lock.reenableKeyguard();
					lock.disableKeyguard();
					needToLock=true;
					//lock.reenableKeyguard();
				}
			}
//			else if(action.equals(SmartpushEventDetectionService.Intent_App_Started)){
//				String package_name = (String)intent.getStringExtra(SmartpushEventDetectionService.Intent_Extra_Event_Source);
//				startApp(package_name);
//			}
//			else if(action.equals(SmartpushEventDetectionService.Intent_App_Stopped)){
//				
//			}
		}
		
	};
	
	private void updateWidget(){
		Log.d(tag,"Update Apps");
		appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetManager.getAppWidgetIds(new ComponentName(this.getApplicationContext(),SmartpushWidgetProvider.class)), R.id.gridview);
		appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetManager.getAppWidgetIds(new ComponentName(this.getApplicationContext(),SmartpushWidgetProvider.class)), R.id.notification_list_view);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		LocalBroadcastManager.getInstance(this).unregisterReceiver(localBoardcastReceiver);
		this.unregisterReceiver(globalBroadcastReceiver);
		activityDetector.stopUpdates();
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
			int activityType = 0;
			int activityConfidence = 0;
			if(activityDetector!=null){
				activityType = activityDetector.getCurrentActivity();
				activityConfidence = activityDetector.getCurrentConfidence();
			}
			data.appStarted(packageName,activityType,activityConfidence);
			SmartpushCoreService.this.currentApp=packageName;
			SmartpushCoreService.this.inApp=true;
		}

		@Override
		public void stopApp(String packageName) {
			// TODO Auto-generated method stub
			data.appStopped(packageName);
			SmartpushCoreService.this.inApp=false;
			if(needToLock){
				lock.reenableKeyguard();
				needToLock=false;
			}
		}
		
	};
	
}
