package mcgill.cpslab.smartpush;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mcgill.cpslab.smartpush.content.SmartpushContent;
import mcgill.cpslab.smartpush.content.SmartpushData;
import mcgill.cpslab.smartpush.content.SmartpushNotification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

public class SmartpushNotificationRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory{
	
	public static final String tag="SmartpushNotificationRemoteViewFactory";
	
	private List<SmartpushNotification> notifications=null;
	private Context mContext;
	
	
	public SmartpushNotificationRemoteViewFactory(Context context,Intent intent,List<SmartpushNotification> notifications){
		this.notifications=notifications;
		this.mContext=context;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDataSetChanged() {
		notifications=SmartpushData.getInstance(this.mContext).getNotifications();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		notifications=null;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(notifications==null){
			return 0;
		}
		Log.d(tag,"The count number is "+notifications.size());
		return notifications.size();
	}

	@Override
	public RemoteViews getViewAt(int position) {
		Log.d(tag,"Is contentView empty?"+(notifications.get(position)==null));
		Log.d(tag,"Notification is from "+notifications.get(position).getPackage_name());
		Log.d(tag,"Notification is "+ notifications.get(position).getName());
		Log.d(tag,"Notification is "+ notifications.get(position).getIntent());
		
		SmartpushNotification notification = notifications.get(position);
		
		Bitmap icon = notification.getIcon();
		if(icon==null){
			Drawable drawable=mContext.getResources().getDrawable(R.drawable.default_icon);
			icon=Bitmap.createScaledBitmap(((BitmapDrawable)drawable).getBitmap(), SmartpushContent.ICON_Hight, SmartpushContent.ICON_Width, false);
		}
		String text=notification.getNotification().toString();
		if(notification.getNotification().tickerText!=null){
			text=notification.getNotification().tickerText.toString();
		}
		
		RemoteViews rv=new RemoteViews(mContext.getPackageName(), R.layout.smartpush_notification_item);
		int imageID=R.id.smartpush_notification_icon;
		int textID=R.id.smartpush_notification_text;
		
		rv.setImageViewBitmap(imageID, icon);
		rv.setTextViewText(textID, text);
		
		if(notification.getIntent()!=null){
			PendingIntent pendingIntent=notification.getNotification().contentIntent;
			Bundle extras = new Bundle();
			extras.putParcelable(SmartpushWidgetProvider.SMARTPUSH_EXTERNAL_NOTIFICATION_INTENT, pendingIntent);
			Intent fillinIntent=new Intent();
			fillinIntent.putExtras(extras);
			//PendingIntent pi=notification.getNotification().contentIntent;
			rv.setOnClickFillInIntent(R.id.smartpush_notification_item, fillinIntent);
			
			Log.d(tag,"Intent is "+pendingIntent.toString());
		}
		//RemoteViews rv=notification.getNotification().contentView;
		return rv;
	}

	@Override
	public RemoteViews getLoadingView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

}
