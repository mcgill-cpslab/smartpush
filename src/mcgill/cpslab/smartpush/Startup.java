package mcgill.cpslab.smartpush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Startup extends BroadcastReceiver {
	public Startup() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		context.startService(new Intent(context, SmartpushCoreService.class));
	}
}
