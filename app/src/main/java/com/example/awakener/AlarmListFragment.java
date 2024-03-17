package com.example.awakener;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AlarmListFragment extends Fragment {

    private RecyclerView recyclerView;
    private AlarmAdapter alarmAdapter;
    private AlarmDatabase alarmDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm_list, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        alarmDatabase = AlarmDatabase.getInstance(getContext());

        alarmAdapter = new AlarmAdapter(getContext(), new ArrayList<>(), (MainActivity) getActivity());
        recyclerView.setAdapter(alarmAdapter);

        loadAlarmsFromDatabase();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadAlarmsFromDatabase();
    }

    private void loadAlarmsFromDatabase() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                List<Alarm> alarmList = alarmDatabase.alarmDao().getAll();
                // Update the UI on the main thread
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Update the dataset of the adapter with the loaded alarms
                            alarmAdapter.setAlarmList(alarmList);
                        }
                    });
                }
            }
        });
    }
}

