package com.jade.speeddial;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;
    private ArrayList<Picture> mDiaryList;
    private Button mBtn;
    private EditText mEdt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDiaryList = new ArrayList<>();

        mDiaryList.add(new Picture(1));

        mListView = (ListView) findViewById(R.id.listview);
        mBtn = (Button) findViewById(R.id.btn);


        DemoListAdapter listAdapter = new DemoListAdapter(mDiaryList);
        mListView.setAdapter(listAdapter);

    }


}
