package com.example.webwrapper;

import android.app.Fragment;
import android.content.Context;
import android.content.MutableContextWrapper;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class RetainedViewFragment extends Fragment
{
	private View mRetainedView;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}
	
	public static View inflateRetainableView(Context context, int layoutId, ViewGroup container)
	{
		LayoutInflater inflater = LayoutInflater.from(context)
			.cloneInContext(new MutableContextWrapper(context));
		return inflater.inflate(layoutId, container, false);
	}
	
	// You must detach the view from its parent before calling this method
	public void setRetainedView(View view)
	{
		if(!(view.getContext() instanceof MutableContextWrapper)){
			throw new IllegalStateException("View must be created with inflateRetainableView");
		}
		mRetainedView = view;
	}
	
	public View getRetainedView(Context context)
	{
		if(mRetainedView != null){
			MutableContextWrapper mutable = (MutableContextWrapper)mRetainedView.getContext();
			mutable.setBaseContext(context);
		}
		return mRetainedView;
	}
}
