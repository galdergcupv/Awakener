package com.example.awakener;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ButtonsFragment extends Fragment {

    public ButtonsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buttons, container, false);

        Button button1 = view.findViewById(R.id.button1);
        Button button2 = view.findViewById(R.id.button2);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the ActivityAlarm for type 0 (Classic)
                Intent intent = new Intent(getActivity(), AlarmActivity.class);
                intent.putExtra("type", 0);
                startActivity(intent);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the ActivityAlarm for type 1 (Guess the number)
                Intent intent = new Intent(getActivity(), AlarmActivity.class);
                intent.putExtra("type", 1);

                startActivity(intent);
            }
        });

        return view;
    }
}
