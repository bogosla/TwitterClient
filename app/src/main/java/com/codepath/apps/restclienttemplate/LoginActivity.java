package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;

import android.view.Menu;
import android.view.View;

import com.codepath.oauth.OAuthLoginActionBarActivity;
public class LoginActivity extends OAuthLoginActionBarActivity<RestClient> {

	String rTitle, rUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setSupportActionBar(findViewById(R.id.toolbar0));

		Intent i = getIntent();
		String action = i.getAction();
		String type = i.getType();

		if (Intent.ACTION_SEND.equals(action) && type != null) {
			if ("text/plain".equals(type)) {
				rTitle = i.getStringExtra(Intent.EXTRA_SUBJECT);
				rUrl = i.getStringExtra(Intent.EXTRA_TEXT);
			}
		}
	}


	// Inflate the menu; this adds items to the action bar if it is present.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	// OAuth authenticated successfully, launch primary authenticated activity
	// i.e Display application "homepage"
	@Override
	public void onLoginSuccess() {
		 Intent i = new Intent(this, TimelineActivity.class);
		 if (rTitle != null && rUrl != null) {
			 i.putExtra("rTitle", rTitle);
			 i.putExtra("rUrl", rUrl);
		 }
		 startActivity(i);
		 finish();
	}

	// OAuth authentication flow failed, handle the error
	// i.e Display an error dialog or toast
	@Override
	public void onLoginFailure(Exception e) {
		// Toast.makeText(LoginActivity.this, "Login failed!!!", Toast.LENGTH_LONG).show();
	}

	// Click handler method for the button used to start OAuth flow
	// Uses the client to initiate OAuth authorization
	public void loginToRest(View view) {
		getClient().connect();
	}

}
