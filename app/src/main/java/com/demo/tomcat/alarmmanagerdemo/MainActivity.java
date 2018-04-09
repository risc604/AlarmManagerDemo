package com.demo.tomcat.alarmmanagerdemo;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

// https://www.sitepoint.com/scheduling-background-tasks-android/
// http://www.cnblogs.com/happyhacking/p/5397391.html


public class MainActivity extends AppCompatActivity
{
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String ACTION_ALARM_SET = "com.demo.tomcat.alarmmanagerdemo.ACTION_ALARM_SET";
    public static final String ACTION_ALARM_CANCLE = "com.demo.tomcat.alarmmanagerdemo.ACTION_ALARM_CANCLE";

    TextView    tvMessage;
    Button      btnSwitch;

    AlarmReceiver   alarmReceiver;
    AlarmManager    am;
    PendingIntent   pi;
    Calendar        cal;
    private static boolean swStatus = false;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.w(TAG, "onCreate(), ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();;
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.w(TAG, "onActivityResult(), requestCode: " + requestCode +
                    ", resultCode: " + resultCode);
        super.onActivityResult(requestCode, resultCode, data);

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

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void OnClickedOnOff(View view)
    {
        Log.w(TAG, "OnClickedOnOff(), swStatus: " + swStatus);

        if (!swStatus)
        {
            btnSwitch.setText("Stop Alarm");
            swStatus = true;
            startAlarm();
        }
        else
        {
            btnSwitch.setText("Start Alarm");
            swStatus = false;
            //Intent intent = new Intent(ACTION_ALARM_CANCLE);
            //sendBroadcast(new Intent(ACTION_ALARM_CANCLE));
            cancelAlarm(1);
            alarmReceiver.abortBroadcast();
            alarmReceiver.clearAbortBroadcast();
        }
    }



    //------------------ User function ------------------//
    private void initView()
    {
        tvMessage = findViewById(R.id.textMessage);
        btnSwitch = findViewById(R.id.OnOffSwitch);
        //btnSwitch.setText("Start");

    }

    private void initControl()
    {
        //setAlarm(10);
        //initAlarm();
    }

    Calendar calendar;
    private void initAlarm()
    {
        Intent  alarmIntent = new Intent(ACTION_ALARM_SET);
        //pi = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_ONE_SHOT);
        pi = PendingIntent.getBroadcast(MainActivity.this, 0, alarmIntent, PendingIntent.FLAG_ONE_SHOT);

        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        //calendar.set(Calendar.HOUR_OF_DAY, 15);
        //calendar.set(Calendar.MINUTE, 18);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void startAlarm()
    {
        String msg = "";
        if (am == null)
        {
            int interval = 1000 * 10;
            Intent  alarmIntent = new Intent(ACTION_ALARM_SET);
            //pi = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_ONE_SHOT);
            pi = PendingIntent.getBroadcast(MainActivity.this, 0,
                                                alarmIntent, PendingIntent.FLAG_ONE_SHOT);
            am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (Build.VERSION.SDK_INT < 23) {
                am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + interval, pi);
                //am.setRepeating(AlarmManager.RTC_WAKEUP,
                //        calendar.getTimeInMillis() + interval, interval, pi);
            }
            else
            {
                am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + interval, pi);
                //am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, interval, pi);
            }

            msg = "Alarm set";

            //Toast.makeText(this, "Alarm set", Toast.LENGTH_SHORT).show();
        }
        else
        {
            msg = "Error!! Alarm NOT null";
            //Toast.makeText(this, "Error!! Alarm NOT null", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

    }

    public void startAlarmService()
    {
        AlarmService alarmService = new AlarmService();

    }

    public void cancleAlarm()
    {

    }

    //private void setAlarm(int n)
    //{
    //    long actionTime = 70;
    //
    //    for (int i=0; i<n; i++)
    //    {
    //        cal = Calendar.getInstance();
    //        //cal.set(2018, 4-1, 6, 1, 20, 0);
    //        cal.setTimeInMillis(System.currentTimeMillis() + actionTime);
    //
    //        Intent intent = new Intent(ACTION_ALARM_SET);
    //        am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
    //        pi = PendingIntent.getBroadcast(this, i, intent, PendingIntent.FLAG_ONE_SHOT);
    //        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
    //    }
    //
    //}

    private void cancelAlarm(int n)
    {
        //for (int i = 0; i < n; i++)
        //{
        //    Intent intent = new Intent(ACTION_ALARM_CANCLE);
        //    am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        //    pi = PendingIntent.getBroadcast(this, i, intent, PendingIntent.FLAG_ONE_SHOT);

        if (am != null) {
            am.cancel(pi);

            pi = null;
            am = null;
        }
            tvMessage.setText("");
        //}
    }

    private IntentFilter getIntentFilter()
    {
        final IntentFilter filter = new IntentFilter();

        Log.w(TAG, "getIntentFilter(), add action to filter !! ");
        filter.addAction(ACTION_ALARM_SET);

        return filter;
    }

    int timerCounts=0;
    SimpleDateFormat    sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    //------------------ inner class -------------------//
    private class AlarmReceiver extends BroadcastReceiver
    {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceive(Context context, Intent intent)
        {
            //Log.w(TAG, "onReceive(), timerCounts: " + timerCounts);
            String action = intent.getAction();
            String message = "";
            Date thisDate = new Date(System.currentTimeMillis());
            Log.w(TAG, "onReceive(), Action: " + action);

            if (action.equalsIgnoreCase(ACTION_ALARM_SET))
            {
                Log.w(TAG, " timerCounts: " + timerCounts + ", " + sdf.format(thisDate));
                timerCounts++;
                //Toast.makeText(context, "Times Up!!", Toast.LENGTH_LONG).show();
                message = "Times Up!! " + timerCounts + ", " + sdf.format(thisDate);
                //Intent intent1 = new Intent(context, AlarmService.class);
                //context.startService(intent1);
                //if (swStatus)
                //{
                    cancelAlarm(1);

                    //initAlarm();
                    startAlarm();
                //}
            }
            else if (action.equalsIgnoreCase(ACTION_ALARM_CANCLE))
            {
                //Toast.makeText(context, "Cancel Alarm ~~", Toast.LENGTH_LONG).show();
                message = "Cancel Alarm ~~";
                cancelAlarm(1);
                clearAbortBroadcast();
                //alarmReceiver.clearAbortBroadcast();
                //alarmReceiver = null;
            }
            else
            {
                //Toast.makeText(context, "Error !! action unknow.", Toast.LENGTH_LONG).show();
                message = "Error !! action unknow.";
            }
            tvMessage.setText(timerCounts + ", " + sdf.format(thisDate));

            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }
}
