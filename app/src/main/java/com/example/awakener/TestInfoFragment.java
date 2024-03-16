package com.example.awakener;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class TestInfoFragment extends Fragment {

    private ImageButton backButton;
    private TextView gameNameTextView;
    private int type;

    public TestInfoFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_info, container, false);

        backButton = view.findViewById(R.id.back_button);
        gameNameTextView = view.findViewById(R.id.game_name_text_view);

        // "Back" button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        Bundle args = getArguments();
        if (args != null) {
            type = args.getInt("alarm_type");


        }

        switch (type) {
            case 0:
                gameNameTextView.setText(getString(R.string.TextTesting) + getString(R.string.TypeClassic));
                break;
            case 1:
                gameNameTextView.setText(getString(R.string.TextTesting) + " " + getString(R.string.TypeGuessTheNumber));
                break;
        }


        return view;
    }
}
