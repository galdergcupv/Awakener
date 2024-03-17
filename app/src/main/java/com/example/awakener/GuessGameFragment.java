package com.example.awakener;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Random;

public class GuessGameFragment extends Fragment {

    private TextView hintTextView;
    private EditText guessEditText;
    private Button submitButton;
    private int randomNumber;
    private String hintMessage;
    private String userInput;
    private String color = "B";

    public GuessGameFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guess_game, container, false);

        hintTextView = view.findViewById(R.id.hint_text_view);
        guessEditText = view.findViewById(R.id.guess_edit_text);
        submitButton = view.findViewById(R.id.submit_button);

        if (savedInstanceState != null) {
            randomNumber = savedInstanceState.getInt("randomNumber");
            hintMessage = savedInstanceState.getString("hintMessage");
            userInput = savedInstanceState.getString("userInput");
            color = savedInstanceState.getString("color");
            hintTextView.setText(hintMessage);
            switch (color) {
                case "b":
                    // Do nothing
                    break;
                case "g":
                    hintTextView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                    break;
                case "r":
                    hintTextView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    break;
            }

            guessEditText.setText(userInput);
        } else {
            // Generate a random number between 0 and 999
            Random random = new Random();
            randomNumber = random.nextInt(1000);
            // Set default hint message
            hintMessage = getString(R.string.TextMakeAGuess);
        }

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkGuess();
            }
        });

        return view;
    }

    private void checkGuess() {
        String guessString = guessEditText.getText().toString();
        if (guessString.isEmpty()) {
            return;
        }

        int guess = Integer.parseInt(guessString);

        if (guess < randomNumber) {
            hintMessage = getString(R.string.TextHigher);
            hintTextView.setText(hintMessage);
            hintTextView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            color = "g";
        } else if (guess > randomNumber) {
            hintMessage = getString(R.string.TextLower);
            hintTextView.setText(hintMessage);
            hintTextView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            color = "r";
        } else {
            showVictoryDialog();
        }

        guessEditText.getText().clear();
    }

    private void showVictoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.TextCongratulations));
        builder.setMessage(getString(R.string.TextYouGuessedCorrect));
        builder.setPositiveButton(getString(R.string.ButtonOK), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra("turn_off_alarm", true);
                startActivity(intent);
                getActivity().finish();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save state to outState
        outState.putInt("randomNumber", randomNumber);
        outState.putString("hintMessage", hintMessage);
        outState.putString("userInput", userInput);
        outState.putString("color", color);
    }
}
