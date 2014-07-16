package com.example.webwrapper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;


public class AboutDialogFragment extends DialogFragment
{
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		return new AlertDialog.Builder(getActivity())
			.setTitle(R.string.about_title)
			.setMessage(R.string.about_message)
			.setPositiveButton(android.R.string.ok, null)
			.create();
	}
}
