package mcgill.cpslab.smartpush;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;
import android.widget.TextView;

/**
 * Implementation of App Widget functionality. App Widget Configuration
 * implemented in {@link SmartpushConfigureActivity SmartpushConfigureActivity}
 */
public class SmartpushWidgetProvider extends AppWidgetProvider {
	
	public static final String SMARTPUSH_CLICK_ACTION="mcgill.cpslab.smartpush.click.action";
	public static final String SMARTPUSH_EXTERNAL_APP_INTENT="mcgill.cpslab.smartpush.external.intent.app";
	public static final String SMARTPUSH_EXTERNAL_NOTIFICATION_INTENT="mcgill.cpslab.smartpush.external.intent.notification";
	public static final String SMARTPUSH_REMOTEVIEW_TYPE="mcgill.cpslab.smartpush.type";
	
	public static final String SMARTPUSH_REMOTEVIEW_TYPE_APP="mcgill.cpslab.smartpush.type.app";
	public static final String SMARTPUSH_REMOTEVIEW_TYPE_NOTIFICATION="mcgill.cpslab.smartpush.type.notification";
	
	public static final String SMARTPUSH_REMOTEVIEW_CONTENT="mcgill.cpslab.smartpush.content";
	
	public static final String SMARTPUSH_UNLOCK_SCREEN="mcgill.cpslab.smartpush.unlock"; 
	
	private static final String tag="SmartpushWidgetProvider";
	
	private KeyguardLock lock = null;
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		// There may be multiple widgets active, so update all of them
//		final int N = appWidgetIds.length;
//		Log.d(tag, "Smartpush onUpdate");
//		for (int i = 0; i < N; i++) {
//			
//			Intent app_intent = new Intent(context,SmartpushRemoteViewsService.class);
//			app_intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
//			app_intent.putExtra(SMARTPUSH_REMOTEVIEW_TYPE,SMARTPUSH_REMOTEVIEW_TYPE_APP);
//			app_intent.setData(Uri.parse(app_intent.toUri(Intent.URI_INTENT_SCHEME)));
//			
//			Log.d(tag,"create notification intent");
//			
//			Intent notification_intent = new Intent(context,SmartpushRemoteViewsService.class);
//			//notification_intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
//			notification_intent.putExtra(SMARTPUSH_REMOTEVIEW_TYPE,SMARTPUSH_REMOTEVIEW_TYPE_NOTIFICATION);
//			notification_intent.setData(Uri.parse(notification_intent.toUri(Intent.URI_INTENT_SCHEME)));
//			
//			Log.d(tag,"create app intent");
//			
//			RemoteViews rv=new RemoteViews(context.getPackageName(),R.layout.smartpush);
//			
//			//For test Notification bar
//			rv.setRemoteAdapter(R.id.notification_list_view,notification_intent);
//			//rv.setTextViewText(R.id.notification_list_view, "Notification test 1");
//			//rv.setTextViewTextSize(R.id.notification_list_view, TypedValue.COMPLEX_UNIT_SP, 40);
//			//rv.setRemoteAdapter(R.id.notification_list_view, tv);
//			
//			Log.d(tag,"Smartpush AppWidget "+i);
//			
//			rv.setRemoteAdapter(R.id.gridview, app_intent);
//			rv.setEmptyView(R.id.gridview, R.id.empty_view);
//			//Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.sohu.newsclient");
//			//Intent launchIntent=new Intent(context,SmartpushConfigureActivity.class);
//			Intent clickIntent = new Intent(context,SmartpushWidgetProvider.class);
//			clickIntent.setAction(SMARTPUSH_CLICK_ACTION);
//			PendingIntent pendingIntent=PendingIntent.getBroadcast(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//			rv.setPendingIntentTemplate(R.id.gridview, pendingIntent);
//			
//			appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
//		}
		final int N = appWidgetIds.length;
		Log.d(tag, "Smartpush onUpdate");
		for (int i = 0; i < N; i++) {
			
			Intent app_intent = new Intent(context,SmartpushRemoteViewsService.class);
			app_intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
			app_intent.putExtra(SmartpushWidgetProvider.SMARTPUSH_REMOTEVIEW_TYPE,SmartpushWidgetProvider.SMARTPUSH_REMOTEVIEW_TYPE_APP);
			//app_intent.putExtra(SmartpushWidgetProvider.SMARTPUSH_REMOTEVIEW_CONTENT,items);
			app_intent.setData(Uri.parse(app_intent.toUri(Intent.URI_INTENT_SCHEME)));
			
			Log.d(tag,"create notification intent");
			
			Intent notification_intent = new Intent(context,SmartpushRemoteViewsService.class);
			notification_intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
			notification_intent.putExtra(SmartpushWidgetProvider.SMARTPUSH_REMOTEVIEW_TYPE,SmartpushWidgetProvider.SMARTPUSH_REMOTEVIEW_TYPE_NOTIFICATION);
			notification_intent.setData(Uri.parse(notification_intent.toUri(Intent.URI_INTENT_SCHEME)));
			
			
			Log.d(tag,"create app intent");
			
			RemoteViews rv=new RemoteViews(context.getPackageName(),R.layout.smartpush);
			
			Log.d(tag,"Smartpush AppWidget "+i);
			
			//For test Notification bar
			rv.setRemoteAdapter(R.id.notification_list_view,notification_intent);
			//rv.setTextViewText(R.id.notification_list_view, "Notification test 1");
			//rv.setTextViewTextSize(R.id.notification_list_view, TypedValue.COMPLEX_UNIT_SP, 40);
			//rv.setRemoteAdapter(R.id.notification_list_view, rv);
			rv.setRemoteAdapter(R.id.gridview, app_intent);
			
			
			rv.setEmptyView(R.id.gridview, R.id.empty_view);
			rv.setEmptyView(R.id.notification_list_view, R.id.empty_view);
			//rv.setTextViewText(R.id.empty_view, "APP_NOTIFICATION");
			//Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.sohu.newsclient");
			//Intent launchIntent=new Intent(context,SmartpushConfigureActivity.class);
			Intent clickIntent = new Intent(context,SmartpushWidgetProvider.class);
			clickIntent.setAction(SmartpushWidgetProvider.SMARTPUSH_CLICK_ACTION);
			PendingIntent pendingIntent=PendingIntent.getBroadcast(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			rv.setPendingIntentTemplate(R.id.gridview, pendingIntent);
			rv.setPendingIntentTemplate(R.id.notification_list_view,pendingIntent);
			
			appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
		}
		//super.onUpdate(context, appWidgetManager, appWidgetIDs);
	}
	
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		// When the user deletes the widget, delete the preference associated
		// with it.
		final int N = appWidgetIds.length;
		for (int i = 0; i < N; i++) {
			SmartpushConfigureActivity
					.deleteTitlePref(context, appWidgetIds[i]);
		}
	}

	@Override
	public void onEnabled(Context context) {
		// Enter relevant functionality for when the first widget is created
		
		Intent intent = new Intent(context,SmartpushCoreService.class);
		context.startService(intent);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if(intent.getAction().equals(SMARTPUSH_CLICK_ACTION)){
			Log.d(tag,"On Receive, Smart_Click_Action");
			Bundle bundle=intent.getExtras();
			Intent activityIntent = (Intent)bundle.getParcelable(SMARTPUSH_EXTERNAL_APP_INTENT);
			//activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			PendingIntent pendingIntent = (PendingIntent)bundle.getParcelable(SMARTPUSH_EXTERNAL_NOTIFICATION_INTENT);
			//pendingIntent.
			//Log.d(tag, "On Receive "+activityIntent.toString());
			KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
			if(activityIntent!=null){
				context.startActivity(activityIntent);
			}
			if(pendingIntent!=null){
				try {
					pendingIntent.send();
				} catch (CanceledException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(km.isKeyguardLocked()){
				LocalBroadcastManager lbm=LocalBroadcastManager.getInstance(context);
				boolean b = lbm.sendBroadcast(new Intent(SMARTPUSH_UNLOCK_SCREEN));
				Log.d(tag,"lbm "+b);
			}
		}
		super.onReceive(context, intent);
	}
	
	@Override
	public void onDisabled(Context context) {
		// Enter relevant functionality for when the last widget is disabled
	}

}
