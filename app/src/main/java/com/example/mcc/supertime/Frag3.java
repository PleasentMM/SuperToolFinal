package com.example.mcc.supertime;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
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
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.mcc.supertime.CashBook.CashDataBaseHelper;
import com.example.mcc.supertime.CashBook.cashEdit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mcc on 2018/8/1.
 */

public class Frag3 extends Fragment implements
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private View view;
    private ListView listview;

    private setClolorAdapter item;

    private List<Map<String, Object>> dataList;
    private Button addNote;
    private TextView tv_cash_content;

    private TextView cost;
    private TextView in;
    private TextView real;

    private String indexcost;
    private String indexin;
    private String indexreal;


    private int finalindexcost;
    private int finalindexin;
    private int finalreal;


    private CashDataBaseHelper DbHelper;
    private SQLiteDatabase DB;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_layout3, container, false);

        InitView();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        RefreshNotesList();
    }

    private void InitView() {

        cost = view.findViewById(R.id.cost);
        in = view.findViewById(R.id.in);
        real = view.findViewById(R.id.real);

        tv_cash_content = view.findViewById(R.id.tv_cash_content);
        listview = view.findViewById(R.id.cash_listview);

        dataList = new ArrayList<Map<String, Object>>();

        addNote = view.findViewById(R.id.btn_editcash);


        DbHelper = new CashDataBaseHelper(getActivity());
        DB = DbHelper.getReadableDatabase();

        listview.setOnItemClickListener(this);
        listview.setOnItemLongClickListener(this);

        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getActivity(), cashEdit.class);
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

        finalindexcost = 0;
        finalindexin = 0;
        finalreal = 0;
        //从数据库读取信息
        Cursor cursor = DB.query("cash", null, null, null, null, null, null);
        //startManagingCursor(cursor);
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("content"));
            String date = cursor.getString(cursor.getColumnIndex("date"));

            int index = cursor.getInt(cursor.getColumnIndex("cost"));

            if (index > 0) {
                finalindexcost += index;
            }

            if (index < 0) {
                finalindexin += index;
            }

            finalreal += index;

            Map<String, Object> map = new HashMap<String, Object>();

            map.put("tv_content", name);
            map.put("tv_date", date);
            map.put("tv_cost", index);

            dataList.add(map);
        }

//        simple_adapter = new SimpleAdapter(getActivity(), dataList, R.layout.cashitem,
//                new String[]{"tv_content", "tv_date","tv_cost"}, new int[]{
//                R.id.tv_cash_content, R.id.tv_cash_date,R.id.tv_cost}
//                );

        item = new setClolorAdapter(getActivity(), dataList, R.layout.cashitem,
                new String[]{"tv_content", "tv_date", "tv_cost"}, new int[]{
                R.id.tv_cash_content, R.id.tv_cash_date, R.id.tv_cost});


        listview.setAdapter(item);

        //显示总体收支情况

        indexcost = String.valueOf(finalindexcost);
        cost.setText(indexcost);

        indexin = String.valueOf(finalindexin);
        in.setText(indexin);

        indexreal = String.valueOf(finalreal);
        real.setText(indexreal);
    }

    // 点击listview中某一项的点击监听事件
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        //获取listview中此个item中的内容
        String content = listview.getItemAtPosition(arg2) + "";
        String content1 = content.substring(content.indexOf("=") + 1,
                content.indexOf(","));

        Intent myIntent = new Intent(getActivity(), cashEdit.class);

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

        //对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("要删除该笔记录吗?");
        builder.setMessage("确认删除吗？");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //获取listview中此个item中的内容
                //删除该行后刷新listview的内容

                String content = listview.getItemAtPosition(arg2) + "";
                String content1 = content.substring(content.indexOf("=") + 1, content.indexOf(","));
                String date1 = content.substring(content.indexOf("tv_date=") + 8, content.indexOf("}"));

                Log.e("onClick: ", content1);
                Log.e("onClick1: ", date1);

                DB.delete("cash", "content = ? and date = ?", new String[]{content1, date1});

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

    public class setClolorAdapter extends SimpleAdapter {

        List<? extends Map<String, Object>> mdata;

        public setClolorAdapter(Context context, List<? extends Map<String, Object>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
            this.mdata = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = LinearLayout.inflate(getContext(), R.layout.cashitem, null);
            }

            TextView content = convertView.findViewById(R.id.tv_cash_content);
            TextView cost = convertView.findViewById(R.id.tv_cost);
            int ss = (int) mdata.get(position).get("tv_cost");

            if (ss > 0) {
                content.setTextColor(Color.parseColor("#FF2cc312"));
                cost.setTextColor(Color.parseColor("#FF2cc312"));
            } else {
                content.setTextColor(Color.RED);
                cost.setTextColor(Color.RED);
            }
            return super.getView(position, convertView, parent);
        }
    }

}
