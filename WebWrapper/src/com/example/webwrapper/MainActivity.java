package com.example.webwrapper;

import java.lang.reflect.Field;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;


public class MainActivity extends Activity
{
	private static final String TAG = MainActivity.class.getSimpleName();
	
	private static final String TAG_BROWSER = "tagBrowser";
	
	private BrowserFragment mBrowserFragment;
	private TitleSwitcher   mTitleSwitcher;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		getWindow().setBackgroundDrawable(null);
		
		mTitleSwitcher = (TitleSwitcher)getActionBar().getCustomView();
		getActionBar().setHomeButtonEnabled(true);
		
		if(savedInstanceState == null){
			mBrowserFragment = BrowserFragment
				.newInstance(getString(R.string.base_url));
			getFragmentManager().beginTransaction()
				.add(Window.ID_ANDROID_CONTENT, mBrowserFragment, TAG_BROWSER)
				.commit();
		}
		else{
			mBrowserFragment = (BrowserFragment)getFragmentManager()
				.findFragmentByTag(TAG_BROWSER);
		}
	}
	
	// FIXME: HACK: Find better way to do this
	private void enableActionBarTransitions()
	{
		int action_bar = getResources().getIdentifier("android:id/action_bar", null, null);
		ViewGroup actionBar = (ViewGroup)getWindow().getDecorView().findViewById(action_bar);
		Field menuViewField;
		try{
			Class<?> clazz = actionBar.getClass().getSuperclass();
			menuViewField = clazz.getDeclaredField("mMenuView");
			menuViewField.setAccessible(true);
			ViewGroup mMenuView = (ViewGroup)menuViewField.get(actionBar);
			if(mMenuView != null && mMenuView.getLayoutTransition() == null){
				mMenuView.setLayoutTransition(new LayoutTransition());
			}
		}
		catch(NoSuchFieldException e){
			Log.e(TAG, "enableActionBarTransitions", e);
		}
		catch(IllegalAccessException e){
			Log.e(TAG, "enableActionBarTransitions", e);
		}
		catch(IllegalArgumentException e){
			Log.e(TAG, "enableActionBarTransitions", e);
		}
	}
	
	@Override
	public void onBackPressed()
	{
		if(mBrowserFragment.canGoBack()){
			mBrowserFragment.goBack();
		}
		else{
			super.onBackPressed();
		}
	}
	
	@Override
	protected void onTitleChanged(CharSequence title, int color)
	{
		super.onTitleChanged(title, color);
		if(mTitleSwitcher != null){
			mTitleSwitcher.setTitle(title);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
			return super.onCreateOptionsMenu(menu) | true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		enableActionBarTransitions();
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()){
			case android.R.id.home:
				mBrowserFragment.loadUrl(getString(R.string.base_url));
				return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
}
