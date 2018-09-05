package com.example.yyous.alarmclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Spinner timeHours = (Spinner) findViewById(R.id.selectableHours);
        final Spinner timeMinutes = (Spinner) findViewById(R.id.selectableMinutes);
        //lists all possible times in millitary time
        String [] hours = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};
        String [] minutes = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59"};
        //updates the UI to add the two spinners
        timeHours.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, hours));
        timeMinutes.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, minutes));
        Button submit = (Button) findViewById(R.id.submitTimeBtn);
        submit.setOnClickListener(new View.OnClickListener() {
            //creates variable to store the number of alarms created, used as the request code for tracking each alarm created
            int numAlarms = 0;
            //creates two arrayList objects to store the alarm number when created and time selected
            ArrayList<String> alarmNum, alarmTime = new ArrayList<String>();
            String json1, json2;
            Gson gson = new Gson();
            SharedPreferences.Editor editor;
            SharedPreferences prefs;
            @Override
            public void onClick(View view) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                String str = sdf.format(new Date());
                //feteches current system time to the nearest minute to perform operations on
                long currentTime = System.currentTimeMillis() - System.currentTimeMillis() % 60000;
                Intent transferData = new Intent(getApplicationContext(), AlarmTimes.class);
                //stores user selected time
                String time = timeHours.getSelectedItem().toString() + ":" + timeMinutes.getSelectedItem().toString();
                //retrieves prior selected times, if any, through shared preferences in the event of app closure
                prefs = getSharedPreferences("Prefs", Context.MODE_PRIVATE);
                editor = prefs.edit();
                alarmNum = gson.fromJson(prefs.getString("alarmNumJSON", ""), new TypeToken<ArrayList<String>>() {}.getType());
                alarmTime = gson.fromJson(prefs.getString("alarmTimeJSON", ""), new TypeToken<ArrayList<String>>() {}.getType());

                //stores user alarm
                alarmNum.add("Alarm " + String.valueOf(numAlarms));
                alarmTime.add(time);
                //reorganizes the alarm number in the case one is completed or removed (if alarm 3 and 4 are the only two alarms, they'll be rewritten as alarm 1 and 2)
                for (int i = 0; i < alarmNum.size(); i++) {
                    alarmNum.set(i, "Alarm " + (i + 1));
                    numAlarms = alarmNum.size() + 1;
                }
                    //stores the arrayLists jsons to use in sharedPreferences
                    json1 = gson.toJson(alarmNum);
                    json2 = gson.toJson(alarmTime);
                    editor.putString("alarmNumJSON", json1);
                    editor.putString("alarmTimeJSON", json2);
                    editor.commit();
                    int actualHours, hours = 0;
                    //removes 0 from military time to perform calculations and stores user selected hour and current hour
                    if (time.substring(0, 1).equals("0")) {
                        hours = Integer.parseInt(time.substring(1, 2));
                    } else {
                        hours = Integer.parseInt(time.substring(0, 2));

                    }
                    if (str.substring(0, 1).equals("0")) {
                        actualHours = Integer.parseInt(str.substring(1, 2));
                    } else {
                        actualHours = Integer.parseInt(str.substring(0, 2));
                    }
                    int minutes = 0;
                    int actualMinutes = 0;
                    //removes 0 from military time and stores user selected minutes and current minutes
                    if (time.substring(3, 4).equals("0")) {
                        minutes = Integer.parseInt(time.substring(4));
                    } else {
                        minutes = Integer.parseInt(time.substring(3));
                    }
                    if (str.substring(3, 4).equals("0")) {
                        actualMinutes = Integer.parseInt(str.substring(4, 5));
                    } else {
                        actualMinutes = Integer.parseInt(str.substring(3, 5));
                    }
                    int differenceBtwnHours = 0;
                    int differenceBtwnMinutes = 0;
                    //accounts for hours between the current time and selected time
                    if (hours < actualHours) {
                        differenceBtwnHours = hours + actualHours;
                    } else {
                        differenceBtwnHours = hours - actualHours;
                    }
                    //accounts for minutes between the current time and selected time
                    if (hours == actualHours && minutes < actualMinutes) {
                        differenceBtwnHours = 24;
                        differenceBtwnMinutes = minutes - actualMinutes;
                    } else {
                        differenceBtwnMinutes = minutes - actualMinutes;
                    }
                    //calculates how many milliseconds to wait before sending an alarm request to the alarm reciever
                    long timeToWait = currentTime + (differenceBtwnHours * 60 * 60 * 1000) + (differenceBtwnMinutes * 60 * 1000);
                    Intent intentAlarm = new Intent(getApplicationContext(), AlarmReciever.class);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, timeToWait, PendingIntent.getBroadcast(getApplicationContext(), numAlarms, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
                    //sends user to list of all alarms they have created
                    startActivity(transferData);


                }

        });
    }
}
