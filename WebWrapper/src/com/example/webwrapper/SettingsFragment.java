package com.example.webwrapper;

import android.os.Bundle;
import android.preference.PreferenceFragment;


public class SettingsFragment extends PreferenceFragment
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getActivity().setTitle(R.string.settings);
		addPreferencesFromResource(R.xml.settings);
	}
}
