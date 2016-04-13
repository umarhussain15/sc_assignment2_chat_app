package www.seu.com.lab4_sc;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * Created by Umar on 11-Mar-16.
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("triggerd","alarm");
        try {
            if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS) {
                Log.wtf("incoming intent for alarm",intent.getStringExtra("username")+"" +intent.getStringExtra("receiver"));
                Intent i = new Intent(context, RetriveService.class);
                i.putExtra("username",intent.getStringExtra("username"));
                i.putExtra("lastmessage",intent.getStringExtra("lastmessage"));
                i.putExtra("receiver", intent.getStringExtra("receiver"));
                context.startService(i);
                SetAlarm(context,intent);
            }


        } catch (Exception e) {
            //context.startService(new Intent(context, BGLocationManager.class));
        }
    }
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void SetAlarm(Context context,Intent intent)
    {
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, AlarmReceiver.class);
        i.putExtra("username",intent.getStringExtra("username"));
        i.putExtra("lastmessage",intent.getStringExtra("lastmessage"));
        i.putExtra("receiver",intent.getStringExtra("receiver"));
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        am.setExact(AlarmManager.ELAPSED_REALTIME, 10000, pi);
        //am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * 10, pi); // Millisec * Second * Minute
    }
}
