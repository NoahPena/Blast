package com.blast.apis;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.blast.MainActivity;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by khanguy on 10/19/14.
 */
public class twitterTest {


    //unsafe as f****.
    static final String TWITTER_CONSUMER_KEY = "K1jXyCHvqE7p5HhLpkzMcDQPL";
    static final String TWITTER_CONSUMER_SECRET = "2v2Yy5XdO9GnQbihCOSm1BjROHC7sUW0paD3tf3unyQv4qYDSp";
    static final String ACCESS_TOKEN = "2864537648-Dn4a2iHEjXEXAQJwDqysbu4ijbCPNmrcO99Wq2W\n";
    static final String ACCESS_SECRET = "hI92iJwPGaBkWbfKGP9NGtT1jjwWRDwVbv4P8ie4yUBIj";

    // Preference Constants
    static String PREFERENCE_NAME = "twitter_oauth";
    static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLoggedIn";
    static final String PREF_KEY_USER_NAME = "username";
    static final String TWITTER_CALLBACK_URL = "oauth://t4jsample";

    // Twitter oauth urls
    static final String AUTHENTICATION_URL_KEY = "auth_url";
    static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";

    // Twitter
    private static Twitter twitter;
    private static RequestToken requestToken;
    private static AccessToken accessToken;

    // Shared Preferences
    private static SharedPreferences mSharedPreferences;

    // Internet Connection detector
    private ConnectionDetector cd;

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    Activity a;


    public twitterTest(Activity a) {
        this.a = a;
        //initTwitter(a);
        loginToTwitter(a);
    }

    private void loginToTwitter(Activity a) {
        // Check if already logged in
        if (!isTwitterLoggedInAlready()) {
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
            builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
            Configuration configuration = builder.build();

            TwitterFactory factory = new TwitterFactory(configuration);
            twitter = factory.getInstance();

            new initTwitterStatus().execute("");
        }
        else{
            // user already logged into twitter
            Toast.makeText(a.getApplicationContext(),
                    "Already Logged into twitter", Toast.LENGTH_LONG).show();
        }
    }
    class initTwitterStatus extends AsyncTask<String, String, String> {
        /**
         * Function to login twitter
         */
        protected String doInBackground(String... args) {
            try {
                requestToken = twitter
                        .getOAuthRequestToken(TWITTER_CALLBACK_URL);
                a.startActivity(new Intent(Intent.ACTION_VIEW, Uri
                        .parse(requestToken.getAuthenticationURL())));
            } catch (TwitterException e) {
                e.printStackTrace();
            }
            return "";
        }

        protected void onPostExecute(String arg) {
            // TODO: check this.exception
            // TODO: do something with the feed
        }
    }

    private class LoginToTwitterThread extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            loginToTwitter();
            return null;
        }
    }

    private boolean isTwitterLoggedInAlready() {
        // return twitter login status from Shared Preferences
        return mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
    }

    protected void loginToTwitter() {
        if (!isTwitterLoggedInAlready())
        {
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
            builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
            builder.setDebugEnabled(true);
            Configuration confrigation = builder.build();
            TwitterFactory factory = new TwitterFactory(confrigation);
            twitter = factory.getInstance();
            try
            {
                requestToken = twitter.getOAuthRequestToken(TWITTER_CALLBACK_URL);
                launchLoginWebView(requestToken);
                requestToken = null;
            } catch (TwitterException e)
            {
                System.out.println(e.getMessage());
            }

        } else{
            System.out.println("Already Logged into twitter");
        }

    }


    private void launchLoginWebView(RequestToken requestToken)
    {
        Intent intent = new Intent(a, LoginToTwitter.class);
        Log.d("Authenticate URL", requestToken.getAuthenticationURL());
        intent.putExtra(AUTHENTICATION_URL_KEY, requestToken.getAuthenticationURL());
        a.startActivityForResult(intent, LOGIN_TO_TWITTER_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == Activity.RESULT_OK)
        {
            switch (requestCode)
            {
                case LOGIN_TO_TWITTER_REQUEST:
                    try
                    {
                        if (!isTwitterLoggedInAlready())
                        {
                            Uri uri = Uri.parse(data.getStringExtra(LoginToTwitter.CALLBACK_URL_KEY));
                            System.out.println("Uri -- " + uri.toString());
                            if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL))
                            {
                        // oAuth verifier
                                String verifier = uri.getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);
                                System.out.println("verifier" + verifier);
                                new GetAccessToken().execute(verifier);
                            }
                        }
                    }catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                break;
                default:
                    break;
            }

        }

    }

    class GetAccessToken extends AsyncTask<String, Void, Long> {
        String username;

        @Override
        protected Long doInBackground(String... params)
        {
            String verifier = params[0];
            long userID = 0;
            try
            {
// Get the access token
                accessToken = twitter.getOAuthAccessToken(verifier);
                try
                {
                    username = twitter.getScreenName();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
// Shared Preferences
                SharedPreferences.Editor e = mSharedPreferences.edit();
// After getting access token, access token secret
// store them in application preferences
                e.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
                e.putString(PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());

// Store login status - true
                e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
                e.putString(PREF_KEY_USER_NAME, username);
                e.commit(); // save changes
                userID = accessToken.getUserId();
                Log.v("Twitter accessToken ", "> " + accessToken.getToken());
                Log.v("Twitter Secret Token ", "> " + accessToken.getTokenSecret());
                Log.v("UserName ", "> " + username);
                Log.v("UserID ", "> " + userID);

            } catch (Exception e)
            {
                Log.e("Twitter Login Error", "> " + e.getMessage());
            }
            return userID;
        }
    }

}