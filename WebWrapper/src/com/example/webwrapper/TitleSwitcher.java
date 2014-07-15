package com.example.webwrapper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;


public class TitleSwitcher extends FrameLayout
{
	private TextView[] mTitle   = new TextView[2];
	private int        mCurrent = 0;
	private int        mTitleTextStyle;
	private int        mShortAnimTime;
	
	
	public TitleSwitcher(Context context)
	{
		this(context, null, 0);
	}
	
	public TitleSwitcher(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}
	
	public TitleSwitcher(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		
		TypedArray a = context.obtainStyledAttributes(null, new int[] { 
			android.R.attr.titleTextStyle }, android.R.attr.actionBarStyle, 0);
		mTitleTextStyle = a.getResourceId(0, 0);
		a.recycle();
		
		mShortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
	}
	
	@Override
	protected void onFinishInflate()
	{
		super.onFinishInflate();
		mTitle[0] = (TextView)findViewById(R.id.title1);
		mTitle[1] = (TextView)findViewById(R.id.title2);
		mTitle[0].setTextAppearance(getContext(), mTitleTextStyle);
		mTitle[1].setTextAppearance(getContext(), mTitleTextStyle);
	}
	
	public void setTitle(CharSequence title)
	{
		final TextView oldTitle = mTitle[mCurrent];
		
		if(oldTitle.getText().equals(title)){
			// Same title.. don't bother animating
			return;
		}
		
		mCurrent = (mCurrent + 1) % 2;
		final TextView newTitle = mTitle[mCurrent];
		
		newTitle.setText(title);
		newTitle.setAlpha(0f);
		newTitle.setVisibility(View.VISIBLE);
		newTitle.animate()
			.alpha(1.0f)
			.setDuration(mShortAnimTime)
			.setListener(null);
		
		oldTitle.animate()
			.alpha(0f)
			.setDuration(mShortAnimTime)
			.setListener(new AnimatorListenerAdapter()
			{
				@Override
				public void onAnimationEnd(Animator animation)
				{
					oldTitle.setVisibility(View.GONE);
				}
			});
	}

	public void setTitleAppearance(int textAppearanceResId)
	{
		mTitle[0].setTextAppearance(getContext(), textAppearanceResId);
		mTitle[1].setTextAppearance(getContext(), textAppearanceResId);
	}
}