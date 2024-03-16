package com.example.awakener;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddAlarmFragment extends Fragment {

    private TextView timeTextView;
    private EditText nameEditText;
    private Button addButton;

    private AlarmDatabase alarmDatabase;

    private static String[] ALARM_TYPES;

    private Button chooseTypeButton;
    private TextView selectedTypeTextView;

    private int selectedTypeIndex = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_alarm, container, false);


        ALARM_TYPES = new String[]{
                getString(R.string.TypeClassic),
                getString(R.string.TypeGuessTheNumber)
        };

        timeTextView = view.findViewById(R.id.time_text_view);
        nameEditText = view.findViewById(R.id.alarm_name_edit_text);
        addButton = view.findViewById(R.id.add_button);

        chooseTypeButton = view.findViewById(R.id.choose_type_button);
        selectedTypeTextView = view.findViewById(R.id.selected_type_text_view);
        selectedTypeTextView.setText(ALARM_TYPES[selectedTypeIndex]);

        alarmDatabase = AlarmDatabase.getInstance(getActivity());

        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAlarm();
            }
        });

        updateTimeTextViewToCurrentTime();

        chooseTypeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showTypeListDialog();
            }
        });

        return view;
    }

    private void showTimePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE) + 1;

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String selectedTime = String.format("%02d:%02d", hourOfDay, minute);
                timeTextView.setText(selectedTime);
            }
        }, hour, minute, true);

        timePickerDialog.show();
    }

    private void addAlarm() {
        final String time = timeTextView.getText().toString();
        final String name = nameEditText.getText().toString();

        // Perform database operation asynchronously using Room's built-in support for async transactions
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Alarm alarm = new Alarm(time, name, selectedTypeIndex);

                if (getActivity() instanceof OnAlarmAddedListener) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((OnAlarmAddedListener) getActivity()).onAlarmAdded(alarm);
                        }
                    });
                }
            }
        });
    }

    private void updateTimeTextViewToCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        // Add 1 minute to the current time
        calendar.add(Calendar.MINUTE, 1);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String currentTime = sdf.format(calendar.getTime());
        timeTextView.setText(currentTime);
    }

    public interface OnAlarmAddedListener {
        void onAlarmAdded(Alarm alarm);
    }

    private void showTypeListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(getString(R.string.TextChooseAlarmType));
        builder.setItems(ALARM_TYPES, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedTypeIndex = which;

                selectedTypeTextView.setText(ALARM_TYPES[which]);
            }
        });
        builder.show();
    }
}
