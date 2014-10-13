package mcgill.cpslab.smartpush;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality. App Widget Configuration
 * implemented in {@link SmartpushConfigureActivity SmartpushConfigureActivity}
 */
public class Smartpush extends AppWidgetProvider {
	
	public static final String SMARTPUSH_CLICK_ACTION="com.smartpush.click.action";
	public static final String SMARTPUSH_EXTERNAL_INTENT="com.smartpush.external.intent";
	
	private static final String tag="Smartpush";
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		// There may be multiple widgets active, so update all of them
		final int N = appWidgetIds.length;
		Log.d(tag, "Smartpush onUpdate");
		for (int i = 0; i < N; i++) {
			
			Intent intent = new Intent(context,SmartpushService.class);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
			intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
			RemoteViews rv=new RemoteViews(context.getPackageName(),R.layout.smartpush);
			Log.d(tag,"Smartpush AppWidget "+i);
			rv.setRemoteAdapter(R.id.gridview, intent);
			rv.setEmptyView(R.id.gridview, R.id.empty_view);
			//Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.sohu.newsclient");
			//Intent launchIntent=new Intent(context,SmartpushConfigureActivity.class);
			Intent clickIntent = new Intent(context,Smartpush.class);
			clickIntent.setAction(SMARTPUSH_CLICK_ACTION);
			PendingIntent pendingIntent=PendingIntent.getBroadcast(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			rv.setPendingIntentTemplate(R.id.gridview, pendingIntent);
			
			appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
		}
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
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if(intent.getAction().equals(SMARTPUSH_CLICK_ACTION)){
			Log.d(tag,"On Receive, Smart_Click_Action");
			Bundle bundle=intent.getExtras();
			Intent activityIntent = (Intent)bundle.getParcelable(SMARTPUSH_EXTERNAL_INTENT);
			Log.d(tag, "On Receive "+activityIntent.toString());
			context.startActivity(activityIntent);
		}
		super.onReceive(context, intent);
	}
	
	@Override
	public void onDisabled(Context context) {
		// Enter relevant functionality for when the last widget is disabled
	}

	static void updateAppWidget(Context context,
			AppWidgetManager appWidgetManager, int appWidgetId) {
		/*
		CharSequence widgetText = SmartpushConfigureActivity.loadTitlePref(
				context, appWidgetId);
		// Construct the RemoteViews object
		RemoteViews views = new RemoteViews(context.getPackageName(),
				R.layout.smartpush);
		views.setTextViewText(R.id.appwidget_text, widgetText);

		// Instruct the widget manager to update the widget
		appWidgetManager.updateAppWidget(appWidgetId, views);*/
	}
}
