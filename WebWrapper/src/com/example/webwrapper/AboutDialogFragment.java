package com.example.webwrapper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;


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
