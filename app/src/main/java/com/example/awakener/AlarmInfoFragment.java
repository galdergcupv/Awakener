package com.example.awakener;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AlarmInfoFragment extends Fragment {

    private TextView alarmNameTextView;
    private TextView alarmTimeTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm_info, container, false);

        alarmNameTextView = view.findViewById(R.id.alarm_name_text_view);
        alarmTimeTextView = view.findViewById(R.id.alarm_time_text_view);

        Bundle args = getArguments();
        if (args != null) {
            String name = args.getString("alarm_name");
            String time = args.getString("alarm_time");

            alarmNameTextView.setText(name);
            alarmTimeTextView.setText(time);
        }

        return view;
    }
}

