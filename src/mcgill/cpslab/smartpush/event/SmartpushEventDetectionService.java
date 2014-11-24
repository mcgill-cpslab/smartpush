package mcgill.cpslab.smartpush.event;

import java.util.ArrayList;

import mcgill.cpslab.smartpush.content.SmartpushData;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

public class SmartpushEventDetectionService extends AccessibilityService {
	
	public static final String tag="SmartpushEventDetectionService";
	
	public static final String View_Clicked="mcgill.cpslab.smartpush.SmartpushEventDetectionService.viewclicked";
	public static final String Intent_Extra_Event_Source="mcgill.cpslab.smartpush.SmartpushEventDetectionService.extra.eventsource";
	public static final String Intent_Extra_Event_Time="mcgill.cpslab.smartpush.SmartpushEventDetectionService.extra.eventtime";
	
	public static final String Intent_App_Started="mcgill.cpslab.smartpush.SmartpushEventDetectionService.action.appstarted";
	public static final String Intent_App_Stopped="mcgill.cpslab.smartpush.SmartpushEventDetectionService.action.appstopped";
	
	//Singleton
	private static SmartpushEventDetectionService service=null;
	public static boolean bindAppEventListener(SmartpushAppEventListener sael){
		if(service==null){
			return false;
		}
		else{
			service.registerAppEventListener(sael);
			return true;
		}
	}
	
	private LocalBroadcastManager lbm=null;
	
	private String currentPackage=null;
	private boolean inApp=false;
	
	private ArrayList<SmartpushAppEventListener> appListeners;
	
	public SmartpushEventDetectionService() {
		appListeners=new ArrayList<SmartpushAppEventListener>();
	}
	
	public void registerAppEventListener(SmartpushAppEventListener sael){
		appListeners.add(sael);
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		lbm=LocalBroadcastManager.getInstance(this);
		service=this;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		service=null;
		super.onDestroy();
	}

	@Override
	protected void onServiceConnected() {
		super.onServiceConnected();
		Log.d(tag,"onServiceConnected");
		AccessibilityServiceInfo info = new AccessibilityServiceInfo();
		//info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED | AccessibilityEvent.TYPE_ANNOUNCEMENT;
		info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
		info.notificationTimeout = 1;
		info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
		setServiceInfo(info);
	}
	
//	private boolean sendAppStartedBroadCast(String packageName){
//		Intent intent=new Intent();
//		intent.setAction(Intent_App_Started);
//		intent.putExtra(Intent_Extra_Event_Source, packageName);
//		boolean app_started_bool = lbm.sendBroadcast(intent);
//		return app_started_bool;
//	}
//	
//	private boolean sendAppStoppedBroadCast(String packageName){
//		Intent intent=new Intent();
//		intent.setAction(Intent_App_Stopped);
//		intent.putExtra(Intent_Extra_Event_Source, packageName);
//		boolean app_stopped_bool = lbm.sendBroadcast(intent);
//		return app_stopped_bool;
//	}
	
	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		//Log.d(tag,"onAccessibilityEvent");
		//Log.d(tag,"Event Type is "+event.getEventType());
		//Log.d(tag,"Event source is "+event.getPackageName());
		if(event.getEventType()==AccessibilityEvent.TYPE_VIEW_CLICKED 
				|| event.getEventType()==AccessibilityEvent.TYPE_VIEW_SCROLLED
				|| event.getEventType()==AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
				|| event.getEventType()==AccessibilityEvent.TYPE_VIEW_SELECTED
				|| event.getEventType()==AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED){
			
			String packageName=(String)event.getPackageName();
			//Log.d(tag,"inApp "+inApp);
			if(!inApp){
				//Log.d(tag,"inApp "+packageName);
				if(SmartpushData.getInstance(this.getApplicationContext()).getApp(packageName)!=null){
					this.currentPackage=packageName;
					inApp=true;
					Log.d(tag,"StartApp "+packageName);
//					boolean app_started_bool = this.sendAppStartedBroadCast(packageName);
//					Log.d(tag, "App Started bool " +app_started_bool);
					
					for(SmartpushAppEventListener spael:appListeners){
						spael.startApp(packageName);
					}
				}
			}
			else{
				if(!packageName.equals(this.currentPackage)){
					for(SmartpushAppEventListener spael:appListeners){
						spael.stopApp(this.currentPackage);
					}
					Log.d(tag,"StopApp "+this.currentPackage);
//					boolean app_stopped_bool = this.sendAppStoppedBroadCast(this.currentPackage);
//					Log.d(tag, "App Stopped bool " +app_stopped_bool);
					inApp=false;
					if(SmartpushData.getInstance(this.getApplicationContext()).getApp(packageName)!=null){
						this.currentPackage=packageName;
						
						for(SmartpushAppEventListener spael:appListeners){
							spael.startApp(packageName);
						}
						inApp=true;
						Log.d(tag,"StartApp "+packageName);
//						boolean app_started_bool = this.sendAppStartedBroadCast(packageName);
//						Log.d(tag, "App Started bool " +app_started_bool);
					}
				}
			}
		}
//		if(event.getEventType()==AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED){
//			Log.d(tag,"Notification is from "+event.getPackageName().toString());
//			//Log.d(tag,"Notification is " + event.getSource().getText());
//			Notification ntf=(Notification)event.getParcelableData();
//			
//			Log.d(tag,"The accessibility event action is ");
//			
//			if(ntf==null){
//				Log.d(tag, "Notification is null");
//			}
//			else{
//				Log.d(tag, "Notification summery:" + ntf.tickerText);
////				Intent intent = new Intent();
////				intent.setAction(Action_Notification);
////				intent.putExtra("Time", event.getEventTime());
////				intent.putExtra(BR_Notification, ntf);
////				intent.putExtra(BR_Source, event.getPackageName());
////				boolean b = LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
////				Log.d(tag, "Localbroadcast manager is " + b);
//			}
//			//Toast.makeText(getApplication(), "Event has a list with "+ event.getText().size() + "charsequence. ", Toast.LENGTH_LONG).show();
//		}
//		else if(event.getEventType()==AccessibilityEvent.TYPE_ANNOUNCEMENT){
////			Log.d(tag,"Announcement source "+event.getSource());
////			Log.d(tag,"Announcement class name "+event.getClassName());
////			Log.d(tag,"Announcement text "+event.getText());
////			Log.d(tag,"Announcement Package name "+event.getPackageName());
////			Log.d(tag,"Announcement get parcelable data "+event.getParcelableData());
////			Log.d(tag,"Announcement get contents "+event.describeContents());
//		}
//		else if(event.getEventType()==AccessibilityEvent.TYPE_VIEW_CLICKED){
//			Log.d(tag,"View Clicked Source "+event.getSource());
//			Log.d(tag,"View Clicked PackageName "+event.getPackageName());
//			Log.d(tag,"View Clicked Text "+event.getText());
//			Log.d(tag,"View Clicked Class Name "+event.getClassName());
//			Log.d(tag,"View Clicked Content Discription "+event.getContentDescription());
//			Log.d(tag,"View Clicked Parcelable Data "+event.getParcelableData());
//			Intent intent = new Intent();
//			intent.setAction(View_Clicked);
//			intent.putExtra(Intent_Extra_Event_Time, event.getEventTime());
//			intent.putExtra(Intent_Extra_Event_Source, event.getPackageName());
//			//intent.putExtra(BR_Source, event.getPackageName());
//			boolean b = LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
//			Log.d(tag, "Localbroadcast manager is " + b);
//		}
//		else if(event.getEventType()==AccessibilityEvent.TYPE_VIEW_SCROLLED){
//			
//		}
	}

	@Override
	public void onInterrupt() {
		Log.d(tag,"onInterrupt");
	}
}
