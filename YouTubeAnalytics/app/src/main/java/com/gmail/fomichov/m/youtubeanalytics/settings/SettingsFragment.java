package com.gmail.fomichov.m.youtubeanalytics.settings;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gmail.fomichov.m.youtubeanalytics.MainActivity;
import com.gmail.fomichov.m.youtubeanalytics.R;

public class SettingsFragment extends PreferenceFragment {
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        // задеаем саммари значение настройки для показа
        final EditTextPreference editTextPreference = (EditTextPreference) findPreference("setNameFolder");
        editTextPreference.setSummary(MainActivity.sharedPreferences.getString("setNameFolder", "my_cache"));
        editTextPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                String yourString = o.toString();
                MainActivity.sharedPreferences.edit().putString("setNameFolder", yourString).apply();
                editTextPreference.setSummary(yourString);
                return true;
            }
        });

        final SwitchPreference switchPreference = (SwitchPreference)findPreference("setSaveCache");
        switchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                getPermission(); // запрашиваем разрегения на запись файлов у системы
                return true;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.setBackgroundColor(getResources().getColor(android.R.color.white));
        return view;
    }

    private void getPermission() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }
}
