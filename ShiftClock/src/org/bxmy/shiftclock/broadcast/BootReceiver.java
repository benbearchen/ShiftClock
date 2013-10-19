package org.bxmy.shiftclock.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.i("shiftclock", "receive boot completed");
        } else if (action.equals(Intent.ACTION_SHUTDOWN)) {
            Log.i("shiftclock", "receive system shutdown");
            Intent shutdownIntent = new Intent();
            shutdownIntent.setAction(BroadcastName.ACTION_SHUTDOWN);
            context.sendBroadcast(shutdownIntent);
        }
    }
}
