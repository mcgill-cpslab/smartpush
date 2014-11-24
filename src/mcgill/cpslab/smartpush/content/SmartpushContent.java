package mcgill.cpslab.smartpush.content;

import java.util.Comparator;
import java.util.Date;

import mcgill.cpslab.smartpush.content.database.SmartpushDatabaseHelper;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class SmartpushContent implements Parcelable, Comparable<SmartpushContent>{
	
	/**
	 * Type of the content
	 */
	public static final String TYPE_APP="app";
	public static final String TYPE_Notification="notification";
	public static final String TYPE_Unknown="unknown";
	
	public static int ICON_Hight=100;
	public static int ICON_Width=100;
	
	public static final String tag="SmartpushContent"; 
	
	public static String generateContentID(String package_name){
		return package_name;
	}
	
	public static String generateContentID(String package_name, int id){
		return package_name+"_"+id;
	}
	
	protected String name="";
	protected String package_name="";
	protected Bitmap icon=null;
	protected Intent intent=null;
	
	protected String id=null;

	protected Context context=null;
	
	protected String type;
	
	protected boolean inUse=false;
	protected int frequency=0;
	protected Date lastAccessed=new Date();
	protected long lastDuration=0;
	
	//For ranking
	private int priority;
	private int frequencyRanking;
	
	/**
	 * Constructor
	 */
	public SmartpushContent(Context context, String package_name){
		//this.name=name;
		this.package_name=package_name;
		this.context=context;
		final PackageManager pm = context.getPackageManager();
		ApplicationInfo ai;
		try {
		    ai = pm.getApplicationInfo(package_name, 0);
		    name = pm.getApplicationLabel(ai).toString();
		    intent=pm.getLaunchIntentForPackage(package_name);
		    try {
				Drawable drawable=pm.getApplicationIcon(package_name);
				//Bitmap icon=Bitmap.createScaledBitmap(((BitmapDrawable)drawable).getBitmap(), SmartpushContent.ICON_Hight, SmartpushContent.ICON_Width, false);
				Bitmap bitmap = Bitmap.createBitmap(SmartpushContent.ICON_Width,SmartpushContent.ICON_Hight, Config.ARGB_8888);
			    Canvas canvas = new Canvas(bitmap); 
			    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
			    drawable.draw(canvas);
				icon=bitmap;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		} catch (final NameNotFoundException e) {
		    ai = null;
		}
		
		this.type=TYPE_Unknown;
		this.id=generateContentID(package_name);
	}
	
	private SmartpushContent(Parcel in){
		name=in.readString();
		package_name=in.readString();
		type=in.readString();
		icon=in.readParcelable(Bitmap.class.getClassLoader());
		intent=in.readParcelable(Intent.class.getClassLoader());
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
	public Bitmap getIcon() {
		return icon;
	}

	/**
	 * @param drawable the drawable to set
	 */
	public void setIcon(Bitmap icon) {
		this.icon = icon;
	}

	/**
	 * @return the id
	 */
	public synchronized String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public synchronized void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the frequency
	 */
	public synchronized int getFrequency() {
		return frequency;
	}

	/**
	 * @param frequency the frequency to set
	 */
	public synchronized void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public int getFrequencyRanking() {
		return frequencyRanking;
	}

	public void setFrequencyRanking(int frequencyRanking) {
		this.frequencyRanking = frequencyRanking;
	}

	public synchronized void startToUse(){
		this.inUse=true;
		this.lastAccessed=new Date();
		this.frequency++;
		Log.d(tag,"Content "+this.package_name+ " frequency "+this.frequency);
	}
	
	public synchronized void stopToUse(){
		this.inUse=false;
		this.lastDuration=(new Date()).getTime()-this.lastAccessed.getTime();
	}

	@Override
	public int compareTo(SmartpushContent another) {
		//TODO: Implement the comparison method here
		return another.frequency-this.frequency;
	}
	
	public static Comparator<SmartpushContent> ContentComparator=new Comparator<SmartpushContent>(){

		@Override
		public int compare(SmartpushContent lhs, SmartpushContent rhs) {
			// TODO Compare Here
			//Log.d(tag,"LHS compareto RHS "+ lhs.compareTo(rhs));
			return lhs.compareTo(rhs);
		}
		
	};
	
	public static final Parcelable.Creator<SmartpushContent> CREATOR=new Parcelable.Creator<SmartpushContent>() {

		@Override
		public SmartpushContent createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new SmartpushContent(source);
		}

		@Override
		public SmartpushContent[] newArray(int size) {
			// TODO Auto-generated method stub
			return new SmartpushContent[size];
		}
	};
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(name);
		dest.writeString(package_name);
		dest.writeString(type);
		dest.writeParcelable(icon, flags);
		dest.writeParcelable(intent,flags);
	}
	
}
