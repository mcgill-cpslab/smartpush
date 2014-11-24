package mcgill.cpslab.smartpush.features.useractivity;

import java.util.ArrayList;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class SmartpushActivityRecognitionIntentService extends IntentService {

	public static final String tag = "SmartpushActivityRecognitionIntentService";
	
	private SmartpushActivityRecognitionCallback callback = null;
	
	public SmartpushActivityRecognitionIntentService(){
		super("SmartpushActivityRecognitionIntentService");
		Log.d(tag, "in Constructor");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(tag, "On HandleIntent");
		callback = SmartpushUserActivityDetector.getInstance(getApplicationContext());
		// If the incoming intent contains an update
        if (ActivityRecognitionResult.hasResult(intent)) {
            // Get the update
        	Log.d(tag,"Has result");
            ActivityRecognitionResult result =
                    ActivityRecognitionResult.extractResult(intent);
            // Get the most probable activity
            DetectedActivity mostProbableActivity =
                    result.getMostProbableActivity();
            /*
             * Get the probability that this activity is the
             * the user's actual activity
             */
            int confidence = mostProbableActivity.getConfidence();
            /*
             * Get an integer describing the type of activity
             */
            int activityType = mostProbableActivity.getType();
            //String activityName = getNameFromType(activityType);
            callback.activityChanged(activityType, confidence);
        } else {
            /*
             * This implementation ignores intents that don't contain
             * an activity update. If you wish, you can report them as
             * errors.
             */
        	Log.d(tag,"Has no result");
        	callback.activityChanged(DetectedActivity.UNKNOWN, 0);
        }
	}
	
	private String getNameFromType(int activityType) {
        switch(activityType) {
            case DetectedActivity.IN_VEHICLE:
                return "in_vehicle";
            case DetectedActivity.ON_BICYCLE:
                return "on_bicycle";
            case DetectedActivity.ON_FOOT:
                return "on_foot";
            case DetectedActivity.STILL:
                return "still";
            case DetectedActivity.UNKNOWN:
                return "unknown";
            case DetectedActivity.TILTING:
                return "tilting";
        }
        return "unknown";
    }

}
