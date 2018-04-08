package com.demo.tomcat.alarmmanagerdemo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

// https://www.sitepoint.com/scheduling-background-tasks-android/


public class MainActivity extends AppCompatActivity
{
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String ACTION_ALARM_SET = "com.demo.tomcat.alarmmanagerdemo.ACTION_ALARM_SET";
    public static final String ACTION_ALARM_CANCLE = "com.demo.tomcat.alarmmanagerdemo.ACTION_ALARM_CANCLE";

    AlarmReceiver   alarmReceiver;
    AlarmManager    am;
    PendingIntent   pi;
    Calendar        cal;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.w(TAG, "onCreate(), ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initControl();
    }

    @Override
    protected void onStart()
    {
        Log.w(TAG, "onStart(), ");
        super.onStart();
        if (alarmReceiver == null)
        {
            alarmReceiver = new AlarmReceiver();
            registerReceiver(alarmReceiver, getIntentFilter());
            Log.w(TAG, " registerReceiver, alarmReceiver: " + alarmReceiver);
        }
    }

    @Override
    protected void onStop()
    {
        Log.w(TAG, "onStop(), ");
        super.onStop();


    }

    @Override
    protected void onDestroy()
    {
        Log.w(TAG, "onDestroy(), ");
        super.onDestroy();
        if (alarmReceiver != null)
        {
            unregisterReceiver(alarmReceiver);
            Log.e(TAG, " unregisterReceiver alarmReceiver !! ");
        }
    }


    //------------------ User function ------------------//
    private void initView()
    {

    }

    private void initControl()
    {
        setAlarm(10);
    }

    private void setAlarm(int n)
    {
        long actionTime = 70;

        for (int i=0; i<n; i++)
        {
            cal = Calendar.getInstance();
            //cal.set(2018, 4-1, 6, 1, 20, 0);
            cal.setTimeInMillis(System.currentTimeMillis() + actionTime);

            Intent intent = new Intent(ACTION_ALARM_SET);
            am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            pi = PendingIntent.getBroadcast(this, i, intent, PendingIntent.FLAG_ONE_SHOT);
            am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
        }

    }

    private void cancelAlarm(int n)
    {
        for (int i = 0; i < n; i++)
        {
            Intent intent = new Intent(ACTION_ALARM_CANCLE);
            am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            pi = PendingIntent.getBroadcast(this, i, intent, PendingIntent.FLAG_ONE_SHOT);
            am.cancel(pi);

            pi = null;
            am = null;
        }
    }

    private IntentFilter getIntentFilter()
    {
        final IntentFilter filter = new IntentFilter();

        Log.w(TAG, "getIntentFilter(), add action to filter !! ");
        filter.addAction(ACTION_ALARM_SET);

        return filter;
    }

    int timerCounts=0;

    //------------------ inner class -------------------//
    private class AlarmReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Log.w(TAG, "onReceive(), timerCounts: " + timerCounts);
            String action = intent.getAction();
            String message = "";

            if (action.equalsIgnoreCase(ACTION_ALARM_SET))
            {
                timerCounts++;
                //Toast.makeText(context, "Times Up!!", Toast.LENGTH_LONG).show();
                message = "Times Up!!";
            }
            else if (action.equalsIgnoreCase(ACTION_ALARM_CANCLE))
            {
                //Toast.makeText(context, "Cancel Alarm ~~", Toast.LENGTH_LONG).show();
                message = "Cancel Alarm ~~";
            }
            else
            {
                //Toast.makeText(context, "Error !! action unknow.", Toast.LENGTH_LONG).show();
                message = "Error !! action unknow.";
            }

            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }
}
