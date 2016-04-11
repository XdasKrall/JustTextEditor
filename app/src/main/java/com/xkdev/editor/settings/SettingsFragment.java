package com.xkdev.editor.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.xkdev.editor.R;

/**
 * Created by user on 03.04.2016.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
    }
}
