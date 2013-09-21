package com.example.twittersample;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {
	// Constants

	Twitter twitter;
	RequestToken requestToken;
	AccessToken accessToken;
	SharedPreferences sharedPreferences;
	Uri uri;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.d("TAG", "After Set Content View");
		sharedPreferences = getApplicationContext().getSharedPreferences(
				"my_prefs", 0);
		Log.d("TAG", "After Shared Prefs");
		Editor editor = sharedPreferences.edit();
		Log.d("OnCreateCount",
				String.valueOf(sharedPreferences.getInt("onCreateCount", 0)));
		if (sharedPreferences.getInt("onCreateCount", 0) == 0) {
			Log.d("TAG", "Inside If Block");
			editor.putInt("onCreateCount", 1);
			editor.commit();
			Thread loginThread = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					loginToTwitter();
				}
			});
			loginThread.start();
		} else {
			Editor onDestroyEditor = sharedPreferences.edit();
			onDestroyEditor.putInt("OnCreateCount", 0);
			onDestroyEditor.commit();
			Log.d("TAG", "In Else block");

			Thread fetchTimeLineThread = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					newTwitterRequest();
				}
			});
			fetchTimeLineThread.start();

		}
	}

	private void loginToTwitter() {
		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.setOAuthConsumerKey("ENTER_CONSUMER_KEY");
		builder.setOAuthConsumerSecret("ENTER_CONSUMER_SECRET");
		Configuration configuration = builder.build();
		TwitterFactory factory = new TwitterFactory(configuration);
		twitter = factory.getInstance();
		try {
			requestToken = twitter
					.getOAuthRequestToken("x-oauthflow-twitter://privlyT4JCallback");
			Log.d("RequestTokenCreated", requestToken.toString());
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String authUrl = requestToken.getAuthenticationURL();
		this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl)));
	}


	private void newTwitterRequest() {
		try {
			uri = getIntent().getData();
			Log.d("TAG", uri.toString());
			if (uri != null) {
				Thread newTwitterRequest = new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						String oAuthVerifier = uri
								.getQueryParameter("oauth_verifier");
						String oAuthToken = uri
								.getQueryParameter("oauth_token");
						ConfigurationBuilder builder = new ConfigurationBuilder();
						builder.setOAuthConsumerKey("9C0j9rlHFXvnNVeoNnbA");
						builder.setOAuthConsumerSecret("mHw3TWz63Eemq9Jk8SoIBLeYyGWGWmsgHs7KIkA");
						Configuration configuration = builder.build();
						TwitterFactory factory = new TwitterFactory(
								configuration);
						twitter = factory.getInstance();
						try {
							requestToken = twitter
									.getOAuthRequestToken("x-oauthflow-twitter://privlyT4JCallback");
							Log.d("requestToken inside Try",
									requestToken.toString());
							AccessToken accessToken = twitter
									.getOAuthAccessToken(requestToken,
											oAuthVerifier);
							Log.d("Get Token", accessToken.getToken());
							Log.d("Token Secret", accessToken.getTokenSecret());
							long userId = accessToken.getUserId();
							User user = twitter.showUser(userId);
							String username = user.getName();
						} catch (TwitterException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						Log.d("requestToken", requestToken.toString());
						try {
							accessToken = twitter.getOAuthAccessToken(
									requestToken, oAuthVerifier);
							Log.d("Get Token", accessToken.getToken());
							Log.d("Token Secreat", accessToken.getTokenSecret());
							long userId = accessToken.getUserId();
							User user = twitter.showUser(userId);
							String username = user.getName();
						} catch (TwitterException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});

				newTwitterRequest.start();

			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d("OnDestroy", "OnDestroy");
		Editor onDestroyEditor = sharedPreferences.edit();
		onDestroyEditor.putInt("OnCreateCount", 0);
		onDestroyEditor.commit();
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d("OnStop", "OnStop");
		Editor onDestroyEditor = sharedPreferences.edit();
		onDestroyEditor.putInt("OnCreateCount", 1);
		onDestroyEditor.commit();
	}

	@Override
	protected void onStart() {
		super.onStart();

	}


}