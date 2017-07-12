package com.yu.espressotest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.yu.espressotest.adapter.ListAdapter;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017-7-7.
 *
 * RecyclerView 列表测试
 */

public class ListActivity extends AppCompatActivity{

    private RecyclerView mRecyclerView;

    private ArrayList<String> mDataList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        initView();

        initData();

        initRecyclerView();
    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_content);
    }

    private void initData() {
        mDataList = new ArrayList<>();
        for (int i=0; i<30; i++) {
            mDataList.add("item " + i);
        }
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ListAdapter adapter = new ListAdapter(getApplicationContext(), mDataList);
        adapter.setOnItemClickListener(new ListAdapter.OnItemClickedListener() {
            @Override
            public void onItemClick(int pos) {
                Toast.makeText(ListActivity.this, "点击了 " + mDataList.get(pos), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(ListActivity.this, WebActivity.class);
                intent.putExtra("url", "https://www.baidu.com/s?wd=" + pos);
                startActivity(intent);
            }
        });
        mRecyclerView.setAdapter(adapter);
    }
}
