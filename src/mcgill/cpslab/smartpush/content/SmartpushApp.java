package mcgill.cpslab.smartpush.content;

import android.content.Context;

public class SmartpushApp extends SmartpushContent{

	public SmartpushApp(Context context, String package_name) {
		super(context, package_name);
		this.type=TYPE_APP;
		
	}

}
