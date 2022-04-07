package com.assignment.data.covid19.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.assignment.data.covid19.R;
import com.assignment.data.covid19.fragments.ResultFragment;
import com.assignment.data.covid19.models.Data;
import com.assignment.data.covid19.preferences.MySharedPreferences;
import com.assignment.data.covid19.restApi.RetrofitClient;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    EditText etName;
    TextView tvFrom, tvTo;
    Button btnSearch;
    ProgressBar progressBar;

    String name, from, to;
    Calendar c = Calendar.getInstance();
    int mYear = c.get(Calendar.YEAR);
    int mMonth = c.get(Calendar.MONTH);
    int mDay = c.get(Calendar.DAY_OF_MONTH);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = findViewById(R.id.et_name);
        tvFrom = findViewById(R.id.tv_from);
        tvTo = findViewById(R.id.tv_to);
        btnSearch = findViewById(R.id.btn_search);
        progressBar = findViewById(R.id.progress_bar);

        drawerLayout = findViewById(R.id.my_drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {

                    case R.id.nav_home: {
                        Intent i = new Intent(MainActivity.this, MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        break;
                    }
                    case R.id.nav_save: {
                        Intent s = new Intent(MainActivity.this, StoredActivity.class);
                        startActivity(s);
                        break;
                    }
                }
                //close navigation drawer
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        if (!TextUtils.isEmpty(MySharedPreferences.getToDate(MainActivity.this))) {
            to = MySharedPreferences.getToDate(MainActivity.this) + "T00:00:00Z";
            Log.d("theS", "onCreate: " + to);
        }
        if (!TextUtils.isEmpty(MySharedPreferences.getFromDate(MainActivity.this))) {
            from = MySharedPreferences.getFromDate(MainActivity.this) + "T00:00:00Z";
            Log.d("theS", "onCreate: " + from);
        }

        etName.setText(MySharedPreferences.getName(MainActivity.this));
        tvFrom.setText(MySharedPreferences.getFromDate(MainActivity.this));
        tvTo.setText(MySharedPreferences.getToDate(MainActivity.this));

        tvFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                from = year + "-" + String.format("%02d", monthOfYear + 1) + "-" + String.format("%02d", dayOfMonth) + "T00:00:00Z";
                                tvFrom.setText(year + "-" + String.format("%02d", monthOfYear + 1) + "-" + String.format("%02d", dayOfMonth));
                            }
                        }, mYear, mMonth, mDay);


                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        tvTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                to = year + "-" + String.format("%02d", monthOfYear + 1) + "-" + String.format("%02d", dayOfMonth) + "T00:00:00Z";
                                tvTo.setText(year + "-" + String.format("%02d", monthOfYear + 1) + "-" + String.format("%02d", dayOfMonth));
                            }
                        }, mYear, mMonth, mDay);


                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCallApi()) {
                    MySharedPreferences.save(MainActivity.this, name, tvFrom.getText().toString(), tvTo.getText().toString());
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    getData();
                }
            }
        });
    }

    public boolean isCallApi() {
        name = etName.getText().toString().trim().toUpperCase();
        if (TextUtils.isEmpty(name)) {
            Snackbar.make(findViewById(android.R.id.content), "Please Enter Country Name", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(tvFrom.getText().toString())) {
            Snackbar.make(findViewById(android.R.id.content), "Please Select From Date", Snackbar.LENGTH_SHORT).show();
            Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(tvTo.getText().toString())) {
            Snackbar.make(findViewById(android.R.id.content), "Please Select To Date", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void getData() {
        Log.d("theS", "getData: " + name + " " + from + " " + to);
        progressBar.setVisibility(View.VISIBLE);
        Call<List<Data>> call = RetrofitClient.getInstance().getMyApi().getData(name, from, to);
        call.enqueue(new Callback<List<Data>>() {
            @Override
            public void onResponse(Call<List<Data>> call, Response<List<Data>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    List<Data> dataList = response.body();
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("dataList", (ArrayList<? extends Parcelable>) dataList);
                    ResultFragment fragment = new ResultFragment();
                    fragment.setArguments(bundle);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame_layout, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                    Toast.makeText(MainActivity.this, "Data Loaded", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Not Found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Data>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}