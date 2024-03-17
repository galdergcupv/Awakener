package com.example.awakener;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.Locale;

public class SettingsFragment extends Fragment {

    private Spinner languageSpinner;
    private String[] languages = {"en", "es", "eu"};
    private String selectedLanguage;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        languageSpinner = root.findViewById(R.id.language_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.languages_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedLanguage = languages[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // "Confirm settings" Button
        Button confirmButton = root.findViewById(R.id.confirm_button);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveLanguagePreference(selectedLanguage);
                setLocale(selectedLanguage);

                if (getActivity() instanceof OnSettingsConfirmedListener) {
                    ((OnSettingsConfirmedListener) getActivity()).onSettingsConfirmed();
                }
            }
        });

        return root;
    }

    // Save preferences
    private void saveLanguagePreference(String language) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("language", language);
        editor.apply();
    }

    private void setLocale(String language) {
        Locale locale;
        switch (language) {
            case "en":
                locale = new Locale("en");
                break;
            case "es":
                locale = new Locale("es", "ES");
                break;
            case "eu":
                locale = new Locale("eu", "ES"); // Basque language code
                break;
            default:
                locale = new Locale("en");
                break;
        }
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }

    public interface OnSettingsConfirmedListener {
        void onSettingsConfirmed();
    }

}
