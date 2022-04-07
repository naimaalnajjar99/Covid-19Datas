package com.assignment.data.covid19.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import com.assignment.data.covid19.R;
import com.assignment.data.covid19.adapters.DataAdapter;
import com.assignment.data.covid19.databases.DatabaseHandler;
import com.assignment.data.covid19.models.Data;

import java.util.List;

public class StoredActivity extends AppCompatActivity {

    DatabaseHandler databaseHandler;
    ListView listView;
    DataAdapter dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stored);

        databaseHandler = new DatabaseHandler(StoredActivity.this);

        listView = findViewById(R.id.list_view);
        List<Data> dataList = databaseHandler.getAllData();

        dataAdapter = new DataAdapter(StoredActivity.this, dataList, true);
        listView.setAdapter(dataAdapter);
    }
}