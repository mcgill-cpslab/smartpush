package mcgill.cpslab.smartpush.content;

import java.util.ArrayList;

public class SmartpushData {
	
	//Singleton
	private static SmartpushData data=null;
	
	public static SmartpushData getInstance(){
		if(data==null){
			data=new SmartpushData();
		}
		return data;
	}
	
	private SmartpushData(){
		app_items=new ArrayList<SmartpushApp>();
		notification_items=new ArrayList<SmartpushNotification>();
	}
	
	private ArrayList<SmartpushApp> app_items;
	private ArrayList<SmartpushNotification> notification_items;
	
	public synchronized ArrayList<SmartpushApp> getApp_items() {
		return app_items;
	}

	public synchronized ArrayList<SmartpushNotification> getNotification_items() {
		return notification_items;
	}
	
}
