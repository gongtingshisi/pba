package kg.gtss.alarm;

import kg.gtss.utils.Log;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * receiver boot complete broadcast,to register alarms to alarm manager
 * */
public class AlarmBootCompleteReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.v(this, "onReceive " + intent.getAction());
		if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
			Intent i = new Intent(context, AlarmService.class);
			context.startService(i);
		}
	}

}
