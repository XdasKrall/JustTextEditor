package com.xkdev.editor.settings;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by user on 03.04.2016.
 */
public class SettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }
}
