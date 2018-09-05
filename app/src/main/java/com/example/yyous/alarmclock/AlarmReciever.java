package com.example.yyous.alarmclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
//recieves alarm request at time indicated by user
public class AlarmReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Ringtone r = RingtoneManager.getRingtone(context.getApplicationContext(), alert);
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        //plays the user's default ringtone
        r.play();
        //vibrates the phone for 5 seconds
        v.vibrate(5000);
        //creates and sends an intent to the list of alarm times to update the UI
        Intent time = new Intent("time");
        context.sendBroadcast(time);


    }
}
