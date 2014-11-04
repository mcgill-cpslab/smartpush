package mcgill.cpslab.smartpush;

import java.util.ArrayList;
import java.util.Enumeration;

import mcgill.cpslab.smartpush.content.SmartpushApp;
import mcgill.cpslab.smartpush.content.SmartpushContent;
import mcgill.cpslab.smartpush.content.SmartpushData;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

public class SmartpushAppRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory{

	public static final String tag="SmartpushAppRemoteViewFactory";
	
	private Context mContext;
    private int mAppWidgetId;
    private ArrayList<SmartpushApp> items;
    
    public SmartpushAppRemoteViewFactory(Context context,Intent intent,ArrayList<SmartpushApp> items){
    	mContext=context;
    	mAppWidgetId=intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID);
    	this.items=items;
    	Log.d(tag, "Factory constructor");
    }
	
	@Override
	public void onCreate() {
		// TODO create a list of reasonable items in array list
	}
	
	@Override
	public void onDataSetChanged() {
		// TODO Auto-generated method stub
		items=SmartpushData.getInstance().getApps();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		items=null;
	}

	@Override
	public int getCount() {
		//use the smaller one between mCount and size of items as the size of the adapter.
		return items.size();
	}

	@Override
	public RemoteViews getViewAt(int position) {
		Log.d(tag, "In getViewAt()");
		//fetch the content
		SmartpushApp content=items.get(position);
		Log.d(tag,"Content package name:"+content.getPackage_name());
		
		//Get the icon
		Bitmap icon = content.getIcon();
		if(icon==null){
			Drawable drawable=mContext.getResources().getDrawable(R.drawable.default_icon);
			icon=Bitmap.createScaledBitmap(((BitmapDrawable)drawable).getBitmap(), SmartpushContent.ICON_Hight, SmartpushContent.ICON_Width, false);
		}
		
		//Get the text
		String text=content.getName();
		
		//Create new remote view
		RemoteViews rv=new RemoteViews(mContext.getPackageName(), R.layout.smartpush_app_item);
		int imageID=R.id.smartpush_app_icon;
		int textID=R.id.smartpush_app_text;
		//Set icon and text
		rv.setImageViewBitmap(imageID, icon);
		rv.setTextViewText(textID, text);
		//Set up intent for each icon
		if(content.getIntent()!=null){
			Intent intent=content.getIntent();
			Bundle extras = new Bundle();
			extras.putParcelable(SmartpushWidgetProvider.SMARTPUSH_EXTERNAL_APP_INTENT, intent);
			Intent fillinIntent=new Intent();
			fillinIntent.putExtras(extras);
			
			rv.setOnClickFillInIntent(R.id.smartpush_app_item, fillinIntent);
			Log.d(tag,"Intent is "+content.getIntent().toString());
		}
		
        System.out.println("Loading view " + position);
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
