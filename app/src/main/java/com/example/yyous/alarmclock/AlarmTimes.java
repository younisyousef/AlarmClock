package com.example.yyous.alarmclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AlarmTimes extends AppCompatActivity {
    ListView myListView;
    ArrayList<String> alarmNum;
    ArrayList<String> alarmTime;
    CustomAdapter customAdapter;
    Gson gson = new Gson();
    BroadcastReceiver RecieveFromService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_screen);
        myListView = (ListView) findViewById(R.id.myListView);
        SharedPreferences prefs = getSharedPreferences("Prefs", 0);
        //fetches alarm arrayLists from the main activity class
        String json1 = prefs.getString("alarmNumJSON", "defaultStringIfNothingFound");
        String json2 = prefs.getString("alarmTimeJSON", "defaultStringIfNothingFound");
         alarmNum = gson.fromJson(json1, new TypeToken<ArrayList<String>>(){}.getType());
        alarmTime = gson.fromJson(json2, new TypeToken<ArrayList<String>>(){}.getType());
        //creates a custom adapter to show all alarms as a list view
        customAdapter = new CustomAdapter(this, alarmNum, alarmTime);
        myListView.setAdapter(customAdapter);
        Toast.makeText(this, "Successfully Set!", Toast.LENGTH_LONG).show();


    }
    //deletes set alarm
    public void onDelete(View v) {
        //finds the position of the selected alarm
        View parentRow = (View) v.getParent();
        ListView listView = (ListView) parentRow.getParent();
        final int position = listView.getPositionForView(parentRow);
        int i = position;
        //the request code is the position of the alarm in the list, used to cancel request
        int requestCode = Integer.parseInt(alarmNum.get(i).substring(6));
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intentAlarm = new Intent(getApplicationContext(), AlarmReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), requestCode + 1, intentAlarm, 0);
        alarmManager.cancel(pendingIntent);
        //removes selected alarm from arrayList
        alarmNum.remove(i);
        alarmTime.remove(i);
        //updates shared preferences to reflect new arrayLists
        SharedPreferences prefs = getSharedPreferences("Prefs", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("alarmNumJSON", gson.toJson(alarmNum));
        editor.putString("alarmTimeJSON", gson.toJson(alarmTime));
        editor.commit();
        //updates the alarm list UI in real time
        customAdapter.notifyDataSetChanged();


    }
    //updates alarm list UI on alarm completion
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String currentTime = sdf.format(new Date());
            for (int i = 0; i<alarmTime.size(); i++){
                if (alarmTime.get(i).equals(currentTime)){
                    alarmTime.remove(i);
                    alarmNum.remove(i);
                }
            }
            SharedPreferences prefs = getSharedPreferences("Prefs", 0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("alarmTimeJSON", gson.toJson(alarmTime));
            editor.putString("alarmNumJSON", gson.toJson(alarmNum));
            editor.commit();
            customAdapter.notifyDataSetChanged();
        }
    };
    protected void onResume(){
        super.onResume();
        this.registerReceiver(mMessageReceiver, new IntentFilter("time"));
    }
    protected  void onPause(){
        super.onPause();
        this.unregisterReceiver(mMessageReceiver);
    }
}