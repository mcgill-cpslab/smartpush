package mcgill.cpslab.smartpush.content;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import mcgill.cpslab.smartpush.content.database.SmartpushDatabaseHelper;

import android.content.Context;
import android.util.Log;

/*
 * TODO: this class need to handle conflicts and concurrency problems
 */

public class SmartpushData {
	public static final String tag="SmartpushData";
	
	public static int COUNTER_TO_COMMIT_TO_DB = 5;
	
	//Singleton
	private static SmartpushData data=null;
	
	public static SmartpushData getInstance(Context context){
		if(data==null){
			data=new SmartpushData(context);
		}
		return data;
	}
	
	private SmartpushData(Context context){
		this.context = context;
		app_items=new Hashtable<String, SmartpushApp>();
		notification_items=new Hashtable<String, SmartpushNotification>();
		ordered_apps=new ArrayList<SmartpushApp>();
		number_of_apps_to_return=9;
		number_of_notifications_to_return=2;
		
		dbHelper = SmartpushDatabaseHelper.getInstance(this.context);
		//contents = new ArrayList<SmartpushContent>();
		
		
	}
	
	private Context context = null;
	private SmartpushDatabaseHelper dbHelper=null;
	private Hashtable<String,SmartpushApp> app_items;
	private Hashtable<String,SmartpushNotification> notification_items;
	//Apps ordered by frequency
	private ArrayList<SmartpushApp> ordered_apps = null;
	private int number_of_apps_to_return;
	private int number_of_notifications_to_return;
	
	//TrackEntry
	private SmartpushTrackEntry currentTrackEntry = null;
	
	public synchronized void updateAppsOrderByFrequency(){
		Enumeration<SmartpushApp> enumeration=app_items.elements();
		ArrayList<SmartpushApp> list=new ArrayList<SmartpushApp>();
		
		while(enumeration.hasMoreElements()){
			list.add(enumeration.nextElement());
		}
		
		Collections.sort(list,SmartpushContent.ContentComparator);
		//Log.d(tag,"Fisrt element in Data "+list.get(0).getPackage_name());
		this.ordered_apps=list;
		for(int i=0;i<ordered_apps.size();i++){
			SmartpushApp app = ordered_apps.get(i);
			app_items.get(app.getPackage_name()).setFrequencyRanking(i);
		}
	}
	
	public synchronized List<SmartpushApp> getApps(){
		//TODO: Can create more efficient data structure to maintain the most often accessed list
		//updateAppsOrderByFrequency();
		
		int number_to_return = (number_of_apps_to_return+1)<ordered_apps.size()?(number_of_apps_to_return+1):ordered_apps.size();
		Log.d(tag,"Number to return is "+number_to_return);
		if(number_to_return<2)
			return ordered_apps;
		return ordered_apps.subList(1, number_to_return);
	}
	
	public synchronized List<SmartpushNotification> getNotifications(){
		Enumeration<SmartpushNotification> enumeration=notification_items.elements();
		ArrayList<SmartpushNotification> list=new ArrayList<SmartpushNotification>();
		
		
		while(enumeration.hasMoreElements()){
			SmartpushNotification sn = enumeration.nextElement();
			if(this.getApp(sn.getPackage_name())!=null){
				list.add(sn);
			}
		}
		
		int number_to_return = number_of_notifications_to_return<list.size()?number_of_notifications_to_return:list.size();
		
		if(ordered_apps.size() <= 0){
			return list.subList(0, number_to_return);
		}
		
		Collections.sort(list,new Comparator<SmartpushNotification>(){

			@Override
			public int compare(SmartpushNotification lhs,
					SmartpushNotification rhs) {
				String lhspackage=lhs.getPackage_name();
				String rhspackage=rhs.getPackage_name();
				int lhsIndex=-1;
				int rhsIndex=-1;
				int size=ordered_apps.size();
				for(int i=0;i<size;i++){
					if(ordered_apps.get(i).package_name.equals(lhspackage)){
						lhsIndex=i;
					}
					if(ordered_apps.get(i).package_name.equals(rhspackage)){
						rhsIndex=i;
					}
				}
				return lhsIndex-rhsIndex;
			}
			
		});
				
		return list.subList(0, number_to_return);
	}
	
	public synchronized void pushApp(SmartpushApp app){
		//Log.d(tag,"Before adding app, the size is "+app_items.keySet().size());
		SmartpushApp existApp = dbHelper.getApp(app.getPackage_name());
		if(existApp==null){
			dbHelper.insertApp(app);
			app_items.put(app.getId(), app);
		}
		else{
			app_items.put(app.getId(), existApp);
		}
		//Log.d(tag,"After adding app, the size is "+app_items.keySet().size());
	}
	
	public synchronized void pushNotification(SmartpushNotification notification){
		notification_items.put(notification.getId(), notification);
		Log.d(tag, "notification size is "+notification_items.keySet().size());
	}
	
	public synchronized void removeApp(String package_name){
		Log.d(tag,"Before removing app, the size is "+app_items.keySet().size());
		app_items.remove(SmartpushContent.generateContentID(package_name));
		//dbHelper.deleteApp(package_name);
		Log.d(tag,"After removing app, the size is "+app_items.keySet().size());
	}
	
	public synchronized void removeNotification(String package_name, int id){
		notification_items.remove(SmartpushContent.generateContentID(package_name, id));
	}
	
	public synchronized SmartpushApp getApp(String package_name){
		return app_items.get(SmartpushContent.generateContentID(package_name));
	}
	
	private synchronized void pushContent(String id, SmartpushContent content){
		
	}
	
	public ArrayList<SmartpushTrackEntry> getTrackEntries(long startTime){
		return dbHelper.getTrackEntries(startTime);
	}
	
	public void appStarted(String package_name, int activityType, int confidence){
		SmartpushApp app = this.getApp(package_name);
		app.startToUse();
		//For test
		//if(app.getFrequency()%COUNTER_TO_COMMIT_TO_DB==0){
		dbHelper.updateApp(app);
		//}
		this.currentTrackEntry = new SmartpushTrackEntry();
		this.currentTrackEntry.setPackage_name(package_name);
		this.currentTrackEntry.setStart_time((new Date()).getTime());
		this.currentTrackEntry.setCurrent_user_activity(activityType);
		this.currentTrackEntry.setCurrent_user_activity_confidence(confidence);
	}
	public void appStopped(String package_name){
		this.getApp(package_name).stopToUse();
		if(this.currentTrackEntry!=null){
			this.currentTrackEntry.setStop_time(new Date().getTime());
			
			if(ordered_apps.size()<=0){
				dbHelper.submitTrackEntry(this.currentTrackEntry);
			}
			else{
				if(!package_name.equals(ordered_apps.get(0).getPackage_name())){
					dbHelper.submitTrackEntry(this.currentTrackEntry);
				}
			}
			
			this.currentTrackEntry = null;
		}
	}

}
