package com.example.webwrapper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;


/**
 * TODO: Old class.. code waiting to be migrated over
 * 
 * @deprecated
 */
public class BrowserActivity extends Activity
{
	private WebView       mWebView;
	private TextView      mConnectionView;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		mConnectionView = (TextView)findViewById(R.id.connection_warning);
		
		updateUIState();
	}
	
	private void updateUIState()
	{
		ConnectivityManager cm = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
		
		mWebView.setEnabled(isConnected);
		mConnectionView.animate()
			.alpha(isConnected ? 0.0f : 1.0f)
			.setListener(isConnected ? mHideAnimationListener : mShowAnimationListener)
			.start();
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		registerReceiver(mConnectivityReceiver, 
			new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
	}
	
	@Override
	protected void onStop()
	{
		super.onStop();
		unregisterReceiver(mConnectivityReceiver);
	}
	
	private AnimatorListenerAdapter mShowAnimationListener = new AnimatorListenerAdapter()
	{
		public void onAnimationStart(Animator animation)
		{
			mConnectionView.setVisibility(View.VISIBLE);
		}
		@Override
		public void onAnimationEnd(Animator animation)
		{
			animation.removeAllListeners();
		}
	};
	
	private AnimatorListenerAdapter mHideAnimationListener = new AnimatorListenerAdapter()
	{
		@Override
		public void onAnimationEnd(Animator animation)
		{
			animation.removeAllListeners();
			mConnectionView.setVisibility(View.INVISIBLE);
		}
	};
	
	private BroadcastReceiver mConnectivityReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			runOnUiThread(new Runnable(){
				@Override
				public void run()
				{
					updateUIState();
				}
			});
		}
	};
}
