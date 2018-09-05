package com.example.mcc.supertime;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.mcc.supertime.NoteBook.NoteDataBaseHelper;
import com.example.mcc.supertime.NoteBook.noteEdit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mcc on 2018/8/1.
 */

public class Frag2 extends Fragment implements
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    //View
    private View view;
    private ListView listview;
    private setProgressAdapter item;

    private List<Map<String, Object>> dataList;

    //item
    private Button addNote;
    private TextView tv_content;
    private TextView time_mow;
    //SQL
    private NoteDataBaseHelper DbHelper;
    private SQLiteDatabase DB;
    private Handler listHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 123:
                    RefreshNotesList();
                    break;
            }
            return false;
        }
    });
    private Handler timrHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    time_mow.setText(simpleDateFormat.format(new Date(System.currentTimeMillis())));
                    break;
            }
            return false;
        }
    });

    public static Date stringToDate(String strTime, String formatType) {
        Date date = null;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(formatType);
            date = formatter.parse(strTime);

        } catch (ParseException e) {
            Log.e("stringToDate: ", "e");
        }
        return date;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_layout2, container, false);
        //初始化组件
        InitView();
        //动态显示时间
        new TimeThread().start();
        //定时刷进度条
        new refreshThread().start();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        RefreshNotesList();
    }

    //初始化第二页的信息
    private void InitView() {
        tv_content = view.findViewById(R.id.tv_content);
        time_mow = view.findViewById(R.id.time_now);

        listview = view.findViewById(R.id.listview);
        dataList = new ArrayList<Map<String, Object>>();
        addNote = view.findViewById(R.id.btn_editnote);

        DbHelper = new NoteDataBaseHelper(getActivity());
        DB = DbHelper.getReadableDatabase();

        listview.setOnItemClickListener(this);
        listview.setOnItemLongClickListener(this);

        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getActivity(), noteEdit.class);
                Bundle bundle = new Bundle();
                bundle.putString("info", "");
                bundle.putInt("enter_state", 0);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    //刷新listview
    public void RefreshNotesList() {
        //如果dataList已经有的内容，全部删掉
        //并且更新simp_adapter
        int size = dataList.size();
        if (size > 0) {
            dataList.removeAll(dataList);
            item.notifyDataSetChanged();
        }


        //从数据库读取信息
        Cursor cursor = DB.query("note", null, null, null, null, null, null);
        //startManagingCursor(cursor);
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("content"));
            String date = cursor.getString(cursor.getColumnIndex("date"));
            String finish = cursor.getString(cursor.getColumnIndex("finish"));

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("tv_content", name);
            map.put("tv_date", date);
            map.put("tv_finish", finish);
            dataList.add(map);
        }

        item = new setProgressAdapter(getActivity(), dataList, R.layout.item,
                new String[]{"tv_content", "tv_date", "tv_finish"}, new int[]{
                R.id.tv_content, R.id.tv_date, R.id.tv_date_end});

        listview.setAdapter(item);

    }

    // 点击listview中某一项的点击监听事件
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        //获取listview中此个item中的内容
        String content = listview.getItemAtPosition(arg2) + "";
        String content1 = content.substring(content.indexOf("=") + 1,
                content.indexOf(","));

        Intent myIntent = new Intent(getActivity(), noteEdit.class);
        Bundle bundle = new Bundle();
        bundle.putString("info", content1);
        bundle.putInt("enter_state", 1);
        myIntent.putExtras(bundle);
        startActivity(myIntent);
    }

    // 点击listview中某一项长时间的点击事件
    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int arg2,
                                   long arg3) {

        //设置长按震动
        Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(50);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("要删除该计划吗?");
        builder.setMessage("确认删除吗？");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //获取listview中此个item中的内容
                //删除该行后刷新listview的内容
                String content = listview.getItemAtPosition(arg2) + "";
                String content1 = content.substring(content.indexOf("=") + 1,
                        content.indexOf(","));

                String time = content.substring(content.indexOf("tv_date=") + 8,
                        content.indexOf("}"));
                Log.e("onClick: ", time);

                DB.delete("note", "content = ? and date = ?", new String[]{content1, time});
                RefreshNotesList();
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.create();
        builder.show();
        return true;
    }

    public class TimeThread extends Thread {
        @Override
        public void run() {
            super.run();
            do {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = 1;
                    timrHandler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (true);
        }
    }

    public class refreshThread extends Thread {
        @Override
        public void run() {
            super.run();
            do {
                try {
                    Thread.sleep(60000);
                    Message msg = new Message();
                    msg.what = 123;
                    listHandler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (true);
        }
    }

    //为每一个item设置进度条的适配器
    public class setProgressAdapter extends SimpleAdapter {

        int temp;

        List<? extends Map<String, Object>> mdata;

        public setProgressAdapter(Context context, List<? extends Map<String, Object>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
            this.mdata = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = LinearLayout.inflate(getContext(), R.layout.item, null);
            }

            String formatType = "yyyy-MM-dd HH:mm:ss";

            //获取item的percent数字显示
            TextView percent = convertView.findViewById(R.id.percent);
            //获取item的percent的Progressbar
            ProgressBar progressBar = convertView.findViewById(R.id.progressbar);
            //获取item的时间信息
            String starttime = (String) mdata.get(position).get("tv_date");
            String finishtime = (String) mdata.get(position).get("tv_finish");
            Date now = new Date();
            Date finishdate = stringToDate(finishtime, formatType);
            Date startdate = stringToDate(starttime, formatType);

            //计算时间信息
            long differ = finishdate.getTime() - startdate.getTime();
            long nows = now.getTime() - startdate.getTime();

            TextView textView = convertView.findViewById(R.id.tv_content);
            TextView textView1 = convertView.findViewById(R.id.tv_date);
            TextView textView2 = convertView.findViewById(R.id.tv_date_end);
            TextView textView3 = convertView.findViewById(R.id.percent_logo);
            TextView textView4 = convertView.findViewById(R.id.to);
            //计算进度
            temp = (int) (nows * 100 / differ);

            if (nows >= differ) {
                temp = 100;
                textView.setTextColor(Color.RED);
                textView1.setTextColor(Color.RED);
                textView2.setTextColor(Color.RED);
                textView3.setTextColor(Color.RED);
                textView4.setTextColor(Color.RED);
                textView.setTextColor(Color.RED);
                percent.setTextColor(Color.RED);
            } else if (temp > 90) {
                textView.setTextColor(Color.RED);
                textView1.setTextColor(Color.RED);
                textView2.setTextColor(Color.RED);
                textView3.setTextColor(Color.RED);
                textView4.setTextColor(Color.RED);
                percent.setTextColor(Color.RED);
            } else if (temp > 40) {
                textView.setTextColor(Color.parseColor("#FF00a00b"));
                textView1.setTextColor(Color.parseColor("#FF00a00b"));
                textView2.setTextColor(Color.parseColor("#FF00a00b"));
                textView3.setTextColor(Color.parseColor("#FF00a00b"));
                textView4.setTextColor(Color.parseColor("#FF00a00b"));
                percent.setTextColor(Color.parseColor("#FF00a00b"));
            } else if (temp > 50) {
                textView.setTextColor(Color.parseColor("#FF1d9d00"));
                textView1.setTextColor(Color.parseColor("#FF1d9d00"));
                textView2.setTextColor(Color.parseColor("#FF1d9d00"));
                textView3.setTextColor(Color.parseColor("#FF1d9d00"));
                textView4.setTextColor(Color.parseColor("#FF1d9d00"));
                percent.setTextColor(Color.parseColor("#FF1d9d00"));
            } else if (temp > 70) {
                textView.setTextColor(Color.parseColor("#FFde4a00"));
                textView1.setTextColor(Color.parseColor("#FFde4a00"));
                textView2.setTextColor(Color.parseColor("#FFde4a00"));
                textView3.setTextColor(Color.parseColor("#FFde4a00"));
                textView4.setTextColor(Color.parseColor("#FFde4a00"));
                percent.setTextColor(Color.parseColor("#FFde4a00"));
            } else {
                textView.setTextColor(Color.parseColor("#FF2cc312"));
                textView1.setTextColor(Color.parseColor("#FF2cc312"));
                textView2.setTextColor(Color.parseColor("#FF2cc312"));
                textView3.setTextColor(Color.parseColor("#FF2cc312"));
                textView4.setTextColor(Color.parseColor("#FF2cc312"));
                percent.setTextColor(Color.parseColor("#FF2cc312"));
            }
            progressBar.setProgress(temp);

            Log.e("calc: ", "" + temp);
            Log.e("asddiffer: ", "" + differ);
            Log.e("asdnows: ", "" + nows);
            String d = String.valueOf(temp);

            percent.setText(d);
            return super.getView(position, convertView, parent);
        }
    }
}
