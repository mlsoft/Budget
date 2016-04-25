package net.ddns.mlsoftlaberge.budget.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import net.ddns.mlsoftlaberge.budget.R;

/**
 * Created by mlsoft on 15/04/16.
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }

}
