package com.example.mcc.supertime.NoteBook;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.mcc.supertime.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class noteEdit extends AppCompatActivity implements OnClickListener {

    public int enter_state = 0;//用来区分是新建一个note还是更改原来的note
    public String last_content;//用来获取edittext内容
    private TextView tv_date;
    private TextView tv_date_finish;
    private int years, months, days, hours, minutes;
    private String datefinishString;
    private EditText et_content;
    private Button btn_ok;//取消
    private Button btn_cancel;//保存
    private Button btn_datepicker;//设置时间
    private NoteDataBaseHelper DBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.edit);
        setTitle("编辑计划");

        InitView();
    }

    private void InitView() {
        tv_date = findViewById(R.id.tv_date);
        tv_date_finish = findViewById(R.id.tv_finish_date);
        datefinishString = "";
        et_content = findViewById(R.id.et_content);

        btn_ok = findViewById(R.id.btn_ok);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_datepicker = findViewById(R.id.date_picker);

        DBHelper = new NoteDataBaseHelper(this);

        //获取此时时刻时间
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = sdf.format(date);
        tv_date.setText(dateString);

        //接收内容和id
        Bundle myBundle = this.getIntent().getExtras();
        last_content = myBundle.getString("info");
        enter_state = myBundle.getInt("enter_state");
        et_content.setText(last_content);

        btn_cancel.setOnClickListener(this);
        btn_ok.setOnClickListener(this);
        btn_datepicker.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_ok:
                SQLiteDatabase db = DBHelper.getReadableDatabase();
                // 获取edittext内容
                String content = et_content.getText().toString();
                // 添加一个新的日志
                if (enter_state == 0) {
                    if (!content.equals("") && !datefinishString.equals("")) {
                        //获取此时时刻时间
                        Date date = new Date();

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        String dateString = sdf.format(date);
                        //向数据库添加信息
                        ContentValues values = new ContentValues();

                        values.put("content", content);
                        values.put("date", dateString);
                        values.put("finish", datefinishString);

                        db.insert("note", null, values);
                        finish();
                    } else {
                        Toast.makeText(noteEdit.this, "请完善计划信息!", Toast.LENGTH_SHORT).show();
                    }
                }

                // 查看并修改一个已有的日志
                else {
                    ContentValues values = new ContentValues();
                    if (!content.equals("") && !datefinishString.equals("")) {
                        values.put("content", content);
                        values.put("finish", datefinishString);
                        db.update("note", values, "content = ?", new String[]{last_content});
                        finish();
                    } else {
                        Toast.makeText(noteEdit.this, "请完善计划信息!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case R.id.date_picker:

                Calendar currentDate = Calendar.getInstance();
                new TimePickerDialog(noteEdit.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                                hours = hour;
                                minutes = minute;

                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                                Date date = new Date(years - 1900, months, days, hours, minutes);

                                datefinishString = simpleDateFormat.format(date);
                                tv_date_finish.setText(datefinishString);
                            }
                        }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();

                new DatePickerDialog(noteEdit.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                years = year;
                                months = month;
                                days = day;
                            }
                        }, currentDate.get(Calendar.YEAR),
                        currentDate.get(Calendar.MONTH),
                        currentDate.get(Calendar.DAY_OF_MONTH)).show();

                break;
            case R.id.btn_cancel:
                finish();
                break;
        }
    }
}
