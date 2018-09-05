package com.example.mcc.supertime.AlarmClock;

import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.example.mcc.supertime.R;

public class AlarmActivity extends AppCompatActivity {

    private MediaPlayer alarmMusic;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏标题栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }

        alarmMusic = MediaPlayer.create(this, R.raw.dang);
        alarmMusic.setLooping(true);

        alarmMusic.start();

        new AlertDialog.Builder(AlarmActivity.this).setTitle("闹钟")
                .setMessage("闹钟响了")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alarmMusic.stop();
                        AlarmActivity.this.finish();
                    }
                }).show();
    }
}
