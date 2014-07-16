package com.example.webwrapper;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class BrowserFragment extends Fragment
{
	private static final String TAG       = BrowserFragment.class.getSimpleName();
	private static final String TAG_ABOUT = "tagAbout";
	private static final String ARG_URL   = "argUrl";
	
	private RetainedViewFragment mRetainedFragment;
	private View     mInternalView;
	private WebView  mWebView;
	
	private String   mUrl;
	
	
	public static BrowserFragment newInstance(String url)
	{
		BrowserFragment frag = new BrowserFragment();
		Bundle args = new Bundle();
		args.putString(ARG_URL, url);
		frag.setArguments(args);
		return frag;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		
		FragmentManager fm = getFragmentManager();
		mRetainedFragment = (RetainedViewFragment)fm.findFragmentByTag("webview");
		if(mRetainedFragment == null){
			mRetainedFragment = new RetainedViewFragment();
			fm.beginTransaction().add(mRetainedFragment, "webview").commit();
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		mInternalView = mRetainedFragment.getRetainedView(getActivity());
		if(mInternalView == null){
			Log.e(TAG, "Creating retainable view");
			mInternalView = RetainedViewFragment.inflateRetainableView(getActivity(), 
				R.layout.activity_main, container);
			mRetainedFragment.setRetainedView(mInternalView);
			mUrl = getArguments().getString(ARG_URL);
		}
		else{
			mUrl = null;
		}
		return mInternalView;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		mWebView = (WebView)view.findViewById(R.id.web_view);
		updateWebSettings(mWebView.getSettings());
		mWebView.setWebViewClient(mWebViewClient);
		mWebView.setInitialScale(1);
		if(mUrl != null){
			mWebView.loadUrl(mUrl);
		}else{
			getActivity().setTitle(mWebView.getTitle());
		}
	}
	
	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		mRetainedFragment.setRetainedView(mInternalView);
	}
	
	public boolean loadUrl(String url)
	{
		if(isResumed()){
			mWebView.loadUrl(url);
			return true;
		}
		return false;
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		mWebView.onResume();
	}
	
	public boolean canGoBack()
	{
		if(isResumed()){
			return mWebView.canGoBack();
		}
		return false;
	}
	
	public void goBack()
	{
		if(isResumed()){
			mWebView.goBack();
		}
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		mWebView.onPause();
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	private void updateWebSettings(WebSettings webSettings)
	{
		webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		webSettings.setLoadsImagesAutomatically(true);
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setUseWideViewPort(true);
		webSettings.setSupportZoom(true);
	}
	
	private WebViewClient mWebViewClient = new WebViewClient()
	{
		public boolean shouldOverrideUrlLoading(WebView view, String url)
		{
			final Uri uri = Uri.parse(url);
			if(uri.getHost().length() == 0 || uri.getHost().endsWith(getString(R.string.base_domain))){
				return false;
			}
			
			startActivity(new Intent(Intent.ACTION_VIEW, uri));
			
			return true;
		}
		
		public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon)
		{
			getActivity().setProgressBarIndeterminateVisibility(true);
		}
		
		public void onPageFinished(WebView view, String url)
		{
			getActivity().setProgressBarIndeterminateVisibility(false);
			getActivity().setTitle(view.getTitle());
		}
		
	};
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.browser, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()){
			case R.id.action_refresh:
				mWebView.reload();
				return true;
				
			case R.id.action_about:
				new AboutDialogFragment()
					.show(getFragmentManager(), TAG_ABOUT);
				return true;
				
			case R.id.action_settings:
				getFragmentManager().beginTransaction()
					.replace(Window.ID_ANDROID_CONTENT, new SettingsFragment())
					.addToBackStack(null)
					.commit();
				return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
}
