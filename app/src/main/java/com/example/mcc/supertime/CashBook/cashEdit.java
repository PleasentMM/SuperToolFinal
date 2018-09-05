package com.example.mcc.supertime.CashBook;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mcc.supertime.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class cashEdit extends AppCompatActivity implements OnClickListener {

    public int enter_state = 0;//用来区分是新建一个cash还是更改原来的cash
    public String last_content;//用来获取edittext内容
    public String last_cash;//用来获取cash
    private TextView tv_date;
    private EditText et_content;
    private EditText et_cost;
    private Button btn_ok;
    private Button btn_cancel;
    private CashDataBaseHelper DBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("记一笔");
        setContentView(R.layout.cashedit);
        InitView();
    }

    private void InitView() {

        et_content = findViewById(R.id.et_cash_content);
        tv_date = findViewById(R.id.tv_cash_date);
        et_cost = findViewById(R.id.et_cash_cost);

        btn_ok = findViewById(R.id.btn_cash_ok);
        btn_cancel = findViewById(R.id.btn_cash_cancel);

        DBHelper = new CashDataBaseHelper(this);

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
        et_cost.setText(last_cash);

        btn_cancel.setOnClickListener(this);
        btn_ok.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cash_ok:
                SQLiteDatabase db = DBHelper.getReadableDatabase();
                // 获取edittext内容
                String content = et_content.getText().toString();
                String cost = et_cost.getText().toString();
                // 添加一个新的日志
                if (enter_state == 0) {
                    if (!content.equals("") && !cost.equals("")) {
                        //获取此时时刻时间
                        Date date = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String dateString = sdf.format(date);
                        //向数据库添加信息
                        ContentValues values = new ContentValues();
                        values.put("content", content);
                        values.put("date", dateString);
                        values.put("cost", cost);

                        db.insert("cash", null, values);
                        finish();
                    } else {
                        Toast.makeText(cashEdit.this, "请完善信息！", Toast.LENGTH_SHORT).show();
                    }
                }

                // 查看并修改一个已有的日志
                else {
                    if (!content.equals("") && !cost.equals("")) {
                        ContentValues values = new ContentValues();
                        values.put("content", content);
                        values.put("cost", cost);
                        db.update("cash", values, "content = ?", new String[]{last_content});
                        finish();
                    } else {
                        Toast.makeText(cashEdit.this, "请完善信息！", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case R.id.btn_cash_cancel:
                finish();
                break;
        }
    }
}
