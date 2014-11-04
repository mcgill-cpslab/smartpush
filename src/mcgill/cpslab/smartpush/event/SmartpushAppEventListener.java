package mcgill.cpslab.smartpush.event;

public interface SmartpushAppEventListener {
	
	public void startApp(String packageName);
	
	public void stopApp(String packageName);

}
