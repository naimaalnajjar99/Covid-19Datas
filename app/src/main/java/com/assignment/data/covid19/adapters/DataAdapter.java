package com.assignment.data.covid19.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.assignment.data.covid19.R;
import com.assignment.data.covid19.databases.DatabaseHandler;
import com.assignment.data.covid19.models.Data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DataAdapter extends BaseAdapter {
    Context context;
    List<Data> dataList;
    DatabaseHandler databaseHandler;
    boolean isDatabaseValue;

    public DataAdapter(Context context, List<Data> dataList, boolean isDatabaseValue) {
        this.context = context;
        this.dataList = dataList;
        this.isDatabaseValue = isDatabaseValue;
        databaseHandler = new DatabaseHandler(context);
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int i) {
        return dataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_data, viewGroup, false);
        }

        TextView tvDate = view.findViewById(R.id.tv_date);
        TextView tvCases = view.findViewById(R.id.tv_cases);
        ImageView ivSave = view.findViewById(R.id.iv_save);
        ImageView ivDelete = view.findViewById(R.id.iv_delete);

        if (databaseHandler.exists(dataList.get(i).getDate())) {
            ivSave.setVisibility(View.GONE);
            ivDelete.setVisibility(View.VISIBLE);
        } else {
            ivSave.setVisibility(View.VISIBLE);
            ivDelete.setVisibility(View.GONE);
        }
        tvDate.setText(convertDate(dataList.get(i).getDate().split("T")[0]));
        tvCases.setText(String.valueOf(dataList.get(i).getCases()));
        ivSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (databaseHandler.saveData(dataList.get(i))) {
                    ivSave.setVisibility(View.GONE);
                    ivDelete.setVisibility(View.VISIBLE);
                    notifyDataSetChanged();
                    Toast.makeText(context, "Data is saved", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseHandler.deleteData(dataList.get(i));
                if (isDatabaseValue) {
                    dataList.remove(dataList.get(i));
                }
                notifyDataSetChanged();
                Toast.makeText(context, "Data is removed", Toast.LENGTH_SHORT).show();
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog((Activity) context, dataList.get(i));
            }
        });
        return view;
    }

    public String convertDate(String time) {
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "MMM dd yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return time;
        }
    }

    public void showDialog(Activity activity, Data data) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_custom);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        ImageView ivClose = dialog.findViewById(R.id.iv_close);
        TextView tvCountry = dialog.findViewById(R.id.tv_country);
        TextView tvLatLong = dialog.findViewById(R.id.tv_lat_long);
        TextView tvCases = dialog.findViewById(R.id.tv_cases);
        TextView tvDate = dialog.findViewById(R.id.tv_date);

        tvCountry.setText(data.getCountry());
        tvLatLong.setText(data.getLat() + "," + data.getLon());
        tvCases.setText(String.valueOf(data.getCases()));
        tvDate.setText(convertDate(data.getDate().split("T")[0]));

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
