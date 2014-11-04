package mcgill.cpslab.smartpush.content;

import android.app.Notification;
import android.content.Context;

public class SmartpushNotification extends SmartpushContent{

	private Notification notification;
	
	public SmartpushNotification(Context context, String package_name, int id) {
		super(context, package_name);
		this.type=TYPE_Notification;
		this.id=generateContentID(package_name,id);
	}

	public Notification getNotification() {
		return notification;
	}

	public void setNotification(Notification notification) {
		this.notification = notification;
	}

}
