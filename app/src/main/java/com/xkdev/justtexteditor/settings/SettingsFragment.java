package com.xkdev.justtexteditor.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.xkdev.justtexteditor.R;

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
    }
}
