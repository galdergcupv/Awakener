package com.example.awakener;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

    public GuessGameFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guess_game, container, false);

        hintTextView = view.findViewById(R.id.hint_text_view);
        guessEditText = view.findViewById(R.id.guess_edit_text);
        submitButton = view.findViewById(R.id.submit_button);

        // Generate a random number between 0 and 999
        Random random = new Random();
        randomNumber = random.nextInt(1000);

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
            hintTextView.setText(getString(R.string.TextHigher));
            hintTextView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else if (guess > randomNumber) {
            hintTextView.setText(getString(R.string.TextLower));
            hintTextView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
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
                getActivity().finish();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }
}
