package mcgill.cpslab.smartpush;

import android.content.Intent;
import android.graphics.drawable.Drawable;

public class SmartpushContent {
	
	/**
	 * Type of the content
	 */
	public static final String APP="app";
	public static final String Notification="notification";
	
	private String name="";
	private String package_name="";
	private Drawable drawable=null;
	private Intent intent=null;
	/**
	 * Constructor
	 */
	public SmartpushContent(String package_name){
		//this.name=name;
		this.package_name=package_name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the package_name
	 */
	public String getPackage_name() {
		return package_name;
	}

	/**
	 * @return the intent
	 */
	public Intent getIntent() {
		return intent;
	}

	/**
	 * @param intent the intent to set
	 */
	public void setIntent(Intent intent) {
		this.intent = intent;
	}

	/**
	 * @return the drawable
	 */
	public Drawable getDrawable() {
		return drawable;
	}

	/**
	 * @param drawable the drawable to set
	 */
	public void setDrawable(Drawable drawable) {
		this.drawable = drawable;
	}
	
}
