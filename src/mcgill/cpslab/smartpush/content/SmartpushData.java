package mcgill.cpslab.smartpush.content;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;

import android.util.Log;

/*
 * TODO: this class need to handle conflicts and concurrency problems
 */

public class SmartpushData {
	public static final String tag="SmartpushData";
	
	//Singleton
	private static SmartpushData data=null;
	
	public static SmartpushData getInstance(){
		if(data==null){
			data=new SmartpushData();
		}
		return data;
	}
	
	private SmartpushData(){
		app_items=new Hashtable<String, SmartpushApp>();
		notification_items=new Hashtable<String, SmartpushNotification>();
		ordered_app=new ArrayList<SmartpushApp>();
		//contents = new ArrayList<SmartpushContent>();
	}
	
	private Hashtable<String,SmartpushApp> app_items;
	private Hashtable<String,SmartpushNotification> notification_items;
	private ArrayList<SmartpushApp> ordered_app;
	
	public synchronized ArrayList<SmartpushApp> getApps(){
		//TODO: Can create more efficient data structure to maintain the most often accessed list
		Enumeration<SmartpushApp> enumeration=app_items.elements();
		ArrayList<SmartpushApp> list=new ArrayList<SmartpushApp>();
		
		while(enumeration.hasMoreElements()){
			list.add(enumeration.nextElement());
		}
		
		Collections.sort(list,SmartpushContent.ContentComparator);
		//Log.d(tag,"Fisrt element in Data "+list.get(0).getPackage_name());
		this.ordered_app=list;
		
		return list;
	}
	
	public synchronized ArrayList<SmartpushNotification> getNotifications(){
		Enumeration<SmartpushNotification> enumeration=notification_items.elements();
		ArrayList<SmartpushNotification> list=new ArrayList<SmartpushNotification>();
		
		
		while(enumeration.hasMoreElements()){
			SmartpushNotification sn = enumeration.nextElement();
			if(this.getApp(sn.getPackage_name())!=null){
				list.add(sn);
			}
		}
		
		Collections.sort(list,new Comparator<SmartpushNotification>(){

			@Override
			public int compare(SmartpushNotification lhs,
					SmartpushNotification rhs) {
				String lhspackage=lhs.getPackage_name();
				String rhspackage=rhs.getPackage_name();
				int lhsIndex=-1;
				int rhsIndex=-1;
				int size=ordered_app.size();
				for(int i=0;i<size;i++){
					if(ordered_app.get(i).package_name.equals(lhspackage)){
						lhsIndex=i;
					}
					if(ordered_app.get(i).package_name.equals(rhspackage)){
						rhsIndex=i;
					}
				}
				return lhsIndex-rhsIndex;
			}
			
		});
		
		return list;
	}
	
	public synchronized void pushApp(SmartpushApp app){
		app_items.put(app.getId(), app);
	}
	
	public synchronized void pushNotification(SmartpushNotification notification){
		notification_items.put(notification.getId(), notification);
		Log.d(tag, "notification size is "+notification_items.keySet().size());
	}
	
	public synchronized void removeApp(String package_name){
		app_items.remove(SmartpushContent.generateContentID(package_name));
	}
	
	public synchronized void removeNotification(String package_name, int id){
		notification_items.remove(SmartpushContent.generateContentID(package_name, id));
	}
	
	public synchronized SmartpushApp getApp(String package_name){
		return app_items.get(SmartpushContent.generateContentID(package_name));
	}
	
	private synchronized void pushContent(String id, SmartpushContent content){
		
	}

}
