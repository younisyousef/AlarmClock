package com.example.yyous.alarmclock;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {
    LayoutInflater inflater;
    ArrayList<String> alarmNum;
    ArrayList<String> alarmTime;
    public CustomAdapter(Context context, ArrayList alarmNum, ArrayList alarmTime){
        this.alarmNum = alarmNum;
        this.alarmTime = alarmTime;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return alarmNum.size();
    }

    @Override
    public Object getItem(int i) {
        return alarmNum.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = inflater.inflate(R.layout.activity_alarm_times, null);
        TextView mainTextView = (TextView) v.findViewById(R.id.mainTextView);
        TextView alarmTimeTextView = (TextView) v.findViewById(R.id.alarmTime);
        String alarmNumber = alarmNum.get(i);
        String alarmTimeHolder = alarmTime.get(i);
        mainTextView.setText(alarmNumber);
        alarmTimeTextView.setText(alarmTimeHolder);

        return v;
    }
}
