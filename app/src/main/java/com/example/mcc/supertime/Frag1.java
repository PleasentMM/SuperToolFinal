package com.example.mcc.supertime;


import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.mcc.supertime.BigClock.BigClockActivity;
import com.example.mcc.supertime.CirCleTimerView.CircleTimerActivity;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by mcc on 2018/8/1.
 */

public class Frag1 extends Fragment {

    private View view;

    //加载frag1
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_layout1, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button CircleTimer = view.findViewById(R.id.countdown_timer);
        final Intent CircleTimerActivity = new Intent(getActivity(), CircleTimerActivity.class);
        CircleTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(CircleTimerActivity);
            }
        });

        Button AlarmClock = view.findViewById(R.id.alarm_clock);
        final Intent AlarmActivity = new Intent(getActivity(), com.example.mcc.supertime.AlarmClock.AlarmActivity.class);
        AlarmClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar currentTime = Calendar.getInstance();
                new TimePickerDialog(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_DARK,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {

                                PendingIntent pi = PendingIntent.getActivity(getContext(), 0, AlarmActivity, 0);

                                Calendar c = Calendar.getInstance();
                                c.setTimeInMillis(System.currentTimeMillis());
                                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                c.set(Calendar.MINUTE, minute);

                                AlarmManager aManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);

                                aManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);

                                Toast.makeText(getContext(), "闹钟设置成功", Toast.LENGTH_LONG).show();
                            }
                        }, currentTime.get(Calendar.HOUR_OF_DAY), currentTime.get(Calendar.MINUTE), false).show();
            }
        });


        Button BigClock = getActivity().findViewById(R.id.big_clock);
        final Intent intent = new Intent(getActivity(), BigClockActivity.class);
        BigClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
            }
        });


    }

}
