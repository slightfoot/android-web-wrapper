package com.example.webwrapper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.MutableContextWrapper;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;


public class BrowserActivity extends FragmentActivity
{
	private View          mContent;
	private WebView       mWebView;
	private TextView      mConnectionView;
	private TitleSwitcher mTitleSwitcher;
	
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		getWindow().setBackgroundDrawable(null);
		
		// Retains part of the View hierarchy without leaking the previous Context
		mContent = (View)getLastCustomNonConfigurationInstance();
		if(mContent == null){
			LayoutInflater inflater = getLayoutInflater()
				.cloneInContext(new MutableContextWrapper(this));
			ViewGroup parent = (ViewGroup)getWindow().getDecorView()
				.findViewById(Window.ID_ANDROID_CONTENT);
			mContent = inflater.inflate(R.layout.activity_main, parent, false);
			setContentView(mContent);
		}
		else{
			MutableContextWrapper context = (MutableContextWrapper)mContent.getContext();
			context.setBaseContext(this);
			setContentView(mContent);
		}
		
		mWebView = (WebView)findViewById(R.id.web_view);
		mConnectionView = (TextView)findViewById(R.id.connection_warning);
		
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		webSettings.setLoadsImagesAutomatically(true);
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setUseWideViewPort(true);
		webSettings.setSupportZoom(true);
		
		mWebView.setInitialScale(1);
		mWebView.setWebViewClient(mWebViewClient);
		
		mTitleSwitcher = (TitleSwitcher)getActionBar().getCustomView();
		getActionBar().setHomeButtonEnabled(true);
		
		if(savedInstanceState == null){
			mWebView.loadUrl(getString(R.string.base_url));
		}
		else{
			setTitle(mWebView.getTitle());
		}
		
		updateUIState();
	}
	
	@Override
	public Object onRetainCustomNonConfigurationInstance()
	{
		// We detach our content and return it to be retained
		((ViewGroup)findViewById(Window.ID_ANDROID_CONTENT))
			.removeView(mContent);
		return mContent;
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
		getMenuInflater().inflate(R.menu.overflow, menu);
		return true;
	}
	
	private WebViewClient mWebViewClient = new WebViewClient()
	{
		public boolean shouldOverrideUrlLoading(WebView view, String url)
		{
			final Uri uri = Uri.parse(url);
			if(uri.getHost().length() == 0 || uri.getHost().endsWith(getString(R.string.base_domain))) {
				return false;
			}
			
			startActivity(new Intent(Intent.ACTION_VIEW, uri));
			
			return true;
		}
		
		public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon)
		{
			setProgressBarIndeterminateVisibility(true);
		}
		
		public void onPageFinished(WebView view, String url)
		{
			setProgressBarIndeterminateVisibility(false);
			setTitle(view.getTitle());
		}
		
	};
	
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
	protected void onResume()
	{
		super.onResume();
		mWebView.onResume();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()){
			case android.R.id.home:
				mWebView.loadUrl(getString(R.string.base_url));
				return true;
				
			case R.id.action_refresh:
				mWebView.reload();
				return true;
				
			case R.id.action_about:
				new AboutDialogFragment()
					.show(getSupportFragmentManager(), "about");
				return true;
				
			case R.id.action_settings:
				startActivity(new Intent(this, SettingsActivity.class));
				return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed()
	{
		if(mWebView.canGoBack()){
			mWebView.goBack();
		}
		else{
			super.onBackPressed();
		}
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		mWebView.onPause();
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
