package mcgill.cpslab.smartpush;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

public class SmartpushService extends RemoteViewsService {
	public final String tag="SmartpushService";
	
	public SmartpushService() {
		Log.d(tag, "Smartpush constructor");
	}

	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		// TODO Auto-generated method stub
		return new SmartpushRemoteViewFactory(this.getApplicationContext(),intent);
	}
}

class SmartpushRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory{

	public final String tag="SmartpushRemoteViewFactory";
	
	private Context mContext;
    private int mAppWidgetId;
    // TODO make the number configurable. 
    private int mCount=100;
    private ArrayList<SmartpushContent> items;
    
    //For testing
    private final PackageManager pm;
    private List<ApplicationInfo> packages;
    
    public SmartpushRemoteViewFactory(Context context,Intent intent){
    	mContext=context;
    	mAppWidgetId=intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID);
    	items=new ArrayList<SmartpushContent>();
    	Log.d(tag, "Factory constructor");
    	//For testing
    	pm=mContext.getPackageManager();
    	packages=pm.getInstalledApplications(PackageManager.GET_META_DATA);
    	Log.d(tag,"Packages size "+packages.size());
    }
	
	@Override
	public void onCreate() {
		// TODO create a list of reasonable items in array list
		// The following code is for testing
		for(ApplicationInfo app:packages){
			
			String pac_n=app.packageName;
			String app_name=pm.getApplicationLabel(app).toString();
			Log.d(tag,"package name:"+pac_n);
			SmartpushContent content = new SmartpushContent(pac_n);
			content.setName(app_name);
			content.setIntent(pm.getLaunchIntentForPackage(pac_n));
			
			try {
				content.setDrawable(pm.getApplicationIcon(pac_n));
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(pac_n==null){
				Log.d(tag,"Package name is NULL");
			}
			else{
				Log.d(tag, ""+pac_n);
			}
			if(content.getIntent()!=null){
				items.add(content);
			}
		}
	}
	
	@Override
	public void onDataSetChanged() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		items.clear();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mCount<items.size()?mCount:items.size();
	}

	@Override
	public RemoteViews getViewAt(int position) {
		Log.d(tag, "In getViewAt()");
		SmartpushContent content=items.get(position);
		Drawable drawable=mContext.getResources().getDrawable(R.drawable.default_icon);
		Drawable content_drawable = content.getDrawable();
		if(content_drawable!=null){
			drawable=content_drawable;
		}
		
		Bitmap icon = Bitmap.createScaledBitmap(((BitmapDrawable)drawable).getBitmap(), 100, 100, false);
		Log.d(tag,"Content package name:"+content.getPackage_name());

		String text=content.getName();
		RemoteViews rv=new RemoteViews(mContext.getPackageName(), R.layout.smartpush_item);
		int imageID=R.id.smartpush_icon;
		int textID=R.id.smartpush_text;

		rv.setImageViewBitmap(imageID, icon);
		rv.setTextViewText(textID, text);

		if(content.getIntent()!=null){
			Intent intent=content.getIntent();
			Bundle extras = new Bundle();
			extras.putParcelable(Smartpush.SMARTPUSH_EXTERNAL_INTENT, intent);
			Intent fillinIntent=new Intent();
			fillinIntent.putExtras(extras);
			
			rv.setOnClickFillInIntent(R.id.smartpush_item, fillinIntent);
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
