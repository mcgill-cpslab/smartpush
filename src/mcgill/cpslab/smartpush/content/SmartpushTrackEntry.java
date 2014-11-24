package mcgill.cpslab.smartpush.content;

import mcgill.cpslab.smartpush.content.database.SmartpushDatabaseHelper;

public class SmartpushTrackEntry {
	
	private long _id;
	private String package_name;
	private long start_time;
	private long stop_time;
	private int current_user_activity;
	private int current_user_activity_confidence;
	
	public SmartpushTrackEntry(){
		
	}
	
	/**
	 * @return the _id
	 */
	public long get_id() {
		return _id;
	}
	/**
	 * @param _id the _id to set
	 */
	public void set_id(long _id) {
		this._id = _id;
	}
	/**
	 * @return the package_name
	 */
	public String getPackage_name() {
		return package_name;
	}
	/**
	 * @param package_name the package_name to set
	 */
	public void setPackage_name(String package_name) {
		this.package_name = package_name;
	}
	/**
	 * @return the start_time
	 */
	public long getStart_time() {
		return start_time;
	}
	/**
	 * @param start_time the start_time to set
	 */
	public void setStart_time(long start_time) {
		this.start_time = start_time;
	}
	/**
	 * @return the stop_time
	 */
	public long getStop_time() {
		return stop_time;
	}
	/**
	 * @param stop_time the stop_time to set
	 */
	public void setStop_time(long stop_time) {
		this.stop_time = stop_time;
	}
	/**
	 * @return the current_user_activity
	 */
	public int getCurrent_user_activity() {
		return current_user_activity;
	}
	/**
	 * @param current_user_activity the current_user_activity to set
	 */
	public void setCurrent_user_activity(int current_user_activity) {
		this.current_user_activity = current_user_activity;
	}
	/**
	 * @return the current_user_activity_confidence
	 */
	public int getCurrent_user_activity_confidence() {
		return current_user_activity_confidence;
	}
	/**
	 * @param current_user_activity_confidence the current_user_activity_confidence to set
	 */
	public void setCurrent_user_activity_confidence(
			int current_user_activity_confidence) {
		this.current_user_activity_confidence = current_user_activity_confidence;
	}
	
	public long getDuration(){
		return (this.stop_time-this.start_time);
	}
	
	public void submit(SmartpushDatabaseHelper db){
		
	}
	
}
