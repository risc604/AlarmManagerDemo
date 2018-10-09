package com.demo.tomcat.alarmmanagerdemo;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
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
    public static final String ACTION_ALARM_CANCEL = "com.demo.tomcat.alarmmanagerdemo.ACTION_ALARM_CANCEL";

    private static boolean swStatus = false;
    int timerCounts=0;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat    sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    TextView    tvMessage;
    Button      btnSwitch;

    AlarmManager    am;
    PendingIntent   pi;
    //AlarmService alarmService;
    //AlarmReceiver   alarmReceiver;

    //------------------ inner class -------------------//
    //private class AlarmReceiver extends BroadcastReceiver
    //{
    //    @SuppressLint("SetTextI18n")
    //    @RequiresApi(api = Build.VERSION_CODES.M)
    //    @Override
    //    public void onReceive(Context context, Intent intent)
    //    {
    //        String action = intent.getAction();
    //        String message = "";
    //        Date thisDate = new Date(System.currentTimeMillis());
    //        Log.w(TAG, "onReceive(), Action: " + action);
    //
    //        if (action == null)
    //            return;
    //
    //        if (action.equalsIgnoreCase(ACTION_ALARM_SET))
    //        {
    //            Log.w(TAG, " timerCounts: " + timerCounts + ", " + sdf.format(thisDate));
    //            timerCounts++;
    //            message = "Times Up!! " + timerCounts + ", " + sdf.format(thisDate);
    //            cancelAlarm(1);
    //            startAlarm();
    //            tvMessage.setText(timerCounts + ", " + sdf.format(thisDate));
    //        }
    //        else if (action.equalsIgnoreCase(ACTION_ALARM_CANCEL))
    //        {
    //            message = "Cancel Alarm ~~";
    //            cancelAlarm(1);
    //            clearAbortBroadcast();
    //            timerCounts = 0;
    //        }
    //        else
    //        {
    //            message = "Error !! action unknow.";
    //        }
    //
    //        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    //    }
    //}

    private final BroadcastReceiver alarmReceiver = new BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            String message = "";
            Date thisDate = new Date(System.currentTimeMillis());
            Log.w(TAG, "onReceive(), Action: " + action);

            if (action == null)
                return;

            if (action.equalsIgnoreCase(ACTION_ALARM_SET))
            {
                Log.w(TAG, " timerCounts: " + timerCounts + ", " + sdf.format(thisDate));
                timerCounts++;
                message = "Times Up!! " + timerCounts + ", " + sdf.format(thisDate);
                cancelAlarm(1);
                notificationDialog(message);
                startAlarm();
                tvMessage.setText(timerCounts + ", " + sdf.format(thisDate));
            }
            else if (action.equalsIgnoreCase(ACTION_ALARM_CANCEL))
            {
                message = "Cancel Alarm ~~";
                cancelAlarm(1);
                clearAbortBroadcast();
                manager.cancelAll();
                manager = null;
                timerCounts = 0;
            }
            else
            {
                message = "Error !! action unknow.";
            }

            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.w(TAG, "onCreate(), ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        //alarmService = new AlarmService();
        //initControl();
    }

    @Override
    protected void onStart()
    {
        Log.w(TAG, "onStart(), ");
        super.onStart();
        //if (alarmReceiver == null)
        {
            //alarmReceiver = new AlarmReceiver();
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
            //alarmReceiver.clearAbortBroadcast();
            unregisterReceiver(alarmReceiver);
            Log.e(TAG, " unregisterReceiver alarmReceiver !! ");
        }
        //cancelAlarm(1);
    }

    @SuppressLint("SetTextI18n")
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
            sendBroadcast(new Intent(ACTION_ALARM_CANCEL));
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
    {}

    public void startAlarm()
    {
        String msg;
        if (am == null)
        {
            int interval = 1000 * 30;
            Intent  alarmIntent = new Intent(ACTION_ALARM_SET);
            pi = PendingIntent.getBroadcast(MainActivity.this, 0,
                                                alarmIntent, PendingIntent.FLAG_ONE_SHOT);
            am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            // if (Build.VERSION.SDK_INT < 23)
            // {
            //     //am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + interval, pi);
            //     am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+interval, pi);
            // }
            // else
            // {
            //     //am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
            //     am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
            //             System.currentTimeMillis() + interval, pi);
            // }
            //
            long triggerTime = System.currentTimeMillis() + interval;
            AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(triggerTime, pi);
            am.setAlarmClock(alarmClockInfo, pi);
            msg = "Alarm set";
        }
        else
        {
            msg = "Error!! Alarm NOT null";
        }
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

    }

    private void cancelAlarm(int n)
    {
        if (am != null)
        {
            am.cancel(pi);

            pi = null;
            am = null;
        }

        tvMessage.setText("");
    }

    private IntentFilter getIntentFilter()
    {
        final IntentFilter filter = new IntentFilter();
        Log.w(TAG, "getIntentFilter(), add action to filter !! ");

        filter.addAction(ACTION_ALARM_SET);
        filter.addAction(ACTION_ALARM_CANCEL);
        return filter;
    }

    public void startAlarmService()
    {
        AlarmService alarmService = new AlarmService();

    }

    long AlarmCounts = 0L;
    NotificationManager manager;
    public void notificationDialog(String noteMSG)
    {
        Log.i(TAG, "notificationDialog(), noteMSG: " + noteMSG +
                ", AlarmCounts: " + (++AlarmCounts));
        //if ((appBGFlag && !gattConnectFlag) || (sysSetting.getConnectNotification() == 0))
        /// if (appBGFlag && !gattConnectFlag)
        /// {
        ///     Log.w(TAG, "notificationDialog(), appBGFlag: " + appBGFlag +
        ///             ", sysSetting.getConnectNotification(): " + sysSetting.getConnectNotification() +
        ///             ", sysSetting.getNotification()" + sysSetting.getNotification());
        ///
        ///     return;
        /// }
        ///
        Intent  mainIntent = MainActivity.this.getIntent();
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mainIntent.setAction(Intent.ACTION_MAIN);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        //mainIntent.setClass(this, MainActivity.class);
        //mainIntent.setFlags(  Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP |
        //                      Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Log.w(TAG, "notificationDialog(), mainIntent: " + mainIntent);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                //mainIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Bitmap bmpIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_launcher_foreground);
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentIntent(contentIntent)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(noteMSG)
                .setWhen(System.currentTimeMillis())
                //.setColor(Color.parseColor("#ff0000ff"))
                .setColor(Color.parseColor("#0046ae"))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setLargeIcon(bmpIcon)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setNumber(1)
                .setChannelId(getString(R.string.app_name))
                .setVibrate(new long[]{0, 5000, 60*1000, 5000})
                .setPriority(Notification.PRIORITY_HIGH);
        //.setAutoCancel(true);

        /// Log.w(TAG, "2 Notification title: " + getResources().getString(R.string.app_name) +
        ///         ", " + noteMSG + ", lowBatteryFlag: " + lowBatteryFlag +
        ///         ", gattConnectFlag: " + gattConnectFlag +
        ///         ", lowBatteryAlarmFlag: " + lowBatteryAlarmFlag);

        //if (gattConnectFlag && !lowBatteryAlarmFlag && !lowBatteryFlag)    // High/Low temperature alarm.
        /// if (!lowBatteryAlarmFlag && !lowBatteryFlag)    // High/Low temperature alarm.
        /// {
            //builder.setDefaults(Notification.DEFAULT_SOUND)
            builder.setSound(Uri.parse("android.resource://" + getPackageName() +
                    "/raw/kwahmah_02_alarm1"));
            ///.setVibrate(new long[]{0L}); // Passing null here silently fails)
            Log.w(TAG, "Notification Sound Ruuning !!");
        /// }

        Notification notification = builder.build();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1)  // Android 8.0 Notification Channel
        {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT).build();
            @SuppressLint("WrongConstant")
            NotificationChannel notificationChannel = new NotificationChannel(
                    getString(R.string.app_name),
                    noteMSG,
                    NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setShowBadge(true);
            notificationChannel.setDescription(noteMSG);
            notificationChannel.setVibrationPattern(new long[]{0, 5000, 60*1000, 5000});
            notificationChannel.setSound(Uri.parse("android.resource://" + getPackageName() +
                    "/raw/kwahmah_02_alarm1"), audioAttributes);
            //notificationChannel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), audioAttributes);
            notificationChannel.setLightColor(getColor(R.color.colorPrimary));
            notificationChannel.enableVibration(true);
            //notificationChannel.canShowBadge();
            manager.createNotificationChannel(notificationChannel);
            Log.w(TAG, " Android 8.0, Notification Channel!!");
        }
        else    // Android normal notification action.
        {
            notification.flags |= Notification.FLAG_AUTO_CANCEL;  //sound one shut
            //manager.notify(10, notification);
            Log.w(TAG, " Android, Normal Notification ...");
        }
        manager.notify(10, notification);


        //if (appBGFlag && (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) ) {
        //    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        //        startForegroundService((Intent) getSystemService(Context.NOTIFICATION_SERVICE));
        //    }
        //}
        /// int badgeCount = 1;
        /// ShortcutBadger.applyCount(getBaseContext(), badgeCount); //for 1.1.4+
    }


    //private void initAlarm()
    //{
    //    Intent  alarmIntent = new Intent(ACTION_ALARM_SET);
    //    pi = PendingIntent.getBroadcast(MainActivity.this, 0, alarmIntent,
    //                                        PendingIntent.FLAG_ONE_SHOT);
    //}

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


}

