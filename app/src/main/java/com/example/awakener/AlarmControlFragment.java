package com.example.awakener;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class AlarmControlFragment extends Fragment {

    private Button turnOffButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm_control, container, false);


        turnOffButton = view.findViewById(R.id.turn_off_button);

        turnOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnOffAlarmAndReturnToMainActivity();
            }
        });

        return view;
    }

    private void turnOffAlarmAndReturnToMainActivity() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra("turn_off_alarm", true);
        startActivity(intent);
        getActivity().finish();
    }
}

