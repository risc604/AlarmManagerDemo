package com.demo.tomcat.alarmmanagerdemo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AlarmService extends Service
{
    private static final String TAG = AlarmService.class.getSimpleName();
    SimpleDateFormat    sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public AlarmService()
    {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.w(TAG, "Thread run(): " + sdf.format(new Date(System.currentTimeMillis())));
            }
        }).start();

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        int offset = 1000 * 10;
        long triggerAtTime = SystemClock.elapsedRealtime() + offset;
        Intent intent1 = new Intent(MainActivity.ACTION_ALARM_SET);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent1, 0);

        if (Build.VERSION.SDK_INT > 22)
            am.setAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendingIntent);
        else
            am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendingIntent);

        return super.onStartCommand(intent, flags, startId);
    }



}
