package mcgill.cpslab.smartpush.features.useractivity;

public interface SmartpushActivityRecognitionCallback {
	
	public void activityChanged(int type, int confidence);
	
}
