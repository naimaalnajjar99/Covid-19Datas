package com.assignment.data.covid19.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.assignment.data.covid19.R;
import com.assignment.data.covid19.adapters.DataAdapter;
import com.assignment.data.covid19.models.Data;

import java.util.List;

public class ResultFragment extends Fragment {
    View view;
    ListView listView;
    DataAdapter dataAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_result, container, false);

        listView = view.findViewById(R.id.list_view);
        List<Data> dataList = getArguments().getParcelableArrayList("dataList");

        dataAdapter = new DataAdapter(getActivity(), dataList,false);
        listView.setAdapter(dataAdapter);
        return view;
    }

}
