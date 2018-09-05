package com.example.mcc.supertime.CirCleTimerView;

import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.mcc.supertime.R;

public class CircleTimerActivity extends AppCompatActivity implements CircleTimerView.CircleTimerListener {

    private static final String TAG = CircleTimerActivity.class.getSimpleName();

    private CircleTimerView mTimer;
    private EditText mTimerSet;
    private EditText mHintSet;
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mediaPlayer = MediaPlayer.create(this, R.raw.dang);

        vibrator = (Vibrator) CircleTimerActivity.this.getSystemService(VIBRATOR_SERVICE);

        setTitle("计时器");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }

        setContentView(R.layout.activity_circletimer);

        mTimer = findViewById(R.id.ctv);
        mTimer.setCircleTimerListener(this);
        Button CircleBack = findViewById(R.id.circletimer_back);
        CircleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    //结束程序时停止音乐播放和震动
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        vibrator.cancel();
    }

    public void setTime(View v) {
        try {
            mTimer.setCurrentTime(Integer.parseInt(mTimerSet.getText().toString()));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void setHint(View v) {
        mTimer.setHintText(mHintSet.getText().toString());
    }

    public void start(View v) {
        mTimer.startTimer();
        Snackbar.make(v, "计时开始", Snackbar.LENGTH_LONG).show();
    }

    public void pause(View v) {
        mTimer.pauseTimer();
        Snackbar.make(v, "计时暂停", Snackbar.LENGTH_LONG).show();
    }


    @Override
    public void onTimerStop() {
        mediaPlayer.setLooping(true);
        mediaPlayer.start();


        long[] patter = {0, 3000, 500};
        vibrator.vibrate(patter, 0);

        new AlertDialog.Builder(CircleTimerActivity.this)
                .setMessage("时间到")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mediaPlayer.stop();
                        vibrator.cancel();
                    }
                }).show();
    }

    @Override
    public void onTimerStart(int currentTime) {

    }

    @Override
    public void onTimerPause(int currentTime) {

    }

    @Override
    public void onTimerTimingValueChanged(int time) {
        Log.d(TAG, "onTimerTimingValueChanged");
    }

    @Override
    public void onTimerSetValueChanged(int time) {
        Log.d(TAG, "onTimerSetValueChanged");
    }

    @Override
    public void onTimerSetValueChange(int time) {
        Log.d(TAG, "onTimerSetValueChange");
    }
}
