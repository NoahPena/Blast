package com.blast.apis;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.blast.MainActivity;
import com.blast.R;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by khanguy on 10/19/14.
 */
public class twitter {//extends AsyncTask<String, Void, String> {


    //unsafe as f****.
    static final String CONSUMER_KEY = "K1jXyCHvqE7p5HhLpkzMcDQPL";
    static final String CONSUMER_SECRET = "2v2Yy5XdO9GnQbihCOSm1BjROHC7sUW0paD3tf3unyQv4qYDSp";
    static final String ACCESS_TOKEN = "2864537648-Dn4a2iHEjXEXAQJwDqysbu4ijbCPNmrcO99Wq2W\n";
    static final String ACCESS_SECRET = "hI92iJwPGaBkWbfKGP9NGtT1jjwWRDwVbv4P8ie4yUBIj";

    // Preference Constants
    static String PREFERENCE_NAME = "twitter_oauth";
    static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLoggedIn";

    static final String TWITTER_CALLBACK_URL = "oauth://t4jsample";

    // Twitter oauth urls
    static final String URL_TWITTER_AUTH = "auth_url";
    static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";

    // Twitter
    private static Twitter twitter;
    private static RequestToken requestToken;

    // Shared Preferences
    private static SharedPreferences mSharedPreferences;

    // Internet Connection detector
    private ConnectionDetector cd;

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    Activity a;

    public twitter(Activity a) {
        this.a = a;
        initTwitter(a);
        loginToTwitter(a);
    }

    /**
     * This if conditions is tested once is
     * redirected from twitter page. Parse the uri to get oAuth
     * Verifier
     */
    public void initTwitter(Activity a) {

        // Shared Preferences
        mSharedPreferences = a.getApplicationContext().getSharedPreferences(
                "MyPref", 0);
        if (!isTwitterLoggedInAlready()) {
            Uri uri = a.getIntent().getData();
            if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
                // oAuth verifier
                String verifier = uri
                        .getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);

                try {
                    // Get the access token
                    AccessToken accessToken = twitter.getOAuthAccessToken(
                      //      requestToken, verifier);
                            verifier);


                    // Shared Preferences
                    SharedPreferences.Editor e = mSharedPreferences.edit();

                    // After getting access token, access token secret
                    // store them in application preferences
                    e.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
                    e.putString(PREF_KEY_OAUTH_SECRET,
                            accessToken.getTokenSecret());
                    // Store login status - true
                    e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
                    e.commit(); // save changes

                    Log.e("Twitter OAuth Token", "> " + accessToken.getToken());

                    // Getting user details from twitter
                    // For now i am getting his name only
                    long userID = accessToken.getUserId();
                    User user = twitter.showUser(userID);
                    String username = user.getName();

                } catch (Exception e) {
                    // Check log for login errors
                    Log.e("Twitter Login Error", "> " + e.getMessage());
                }
            }
        }
    }

    private void loginToTwitter(Activity a) {
        // Check if already logged in
        if (!isTwitterLoggedInAlready()) {
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(CONSUMER_KEY);
            builder.setOAuthConsumerSecret(CONSUMER_SECRET);
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
    }

    /**
     * Check user already logged in your application using twitter Login flag is
     * fetched from Shared Preferences
     */
    private boolean isTwitterLoggedInAlready() {
        // return twitter login status from Shared Preferences
        return mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
    }

    public void sendTweet(String tweet, Activity a) {
        // Check for blank text
        if (tweet.trim().length() > 0) {
            // update status
            new updateTwitterStatus().execute(tweet);
        } else {
            // EditText is empty
            Toast.makeText(a.getApplicationContext(),
                    "Please enter status message", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    /**
     * Function to logout from twitter
     * It will just clear the application shared preferences
     * */
    public void logoutFromTwitter() {
        // Clear the shared preferences
        SharedPreferences.Editor e = mSharedPreferences.edit();
        e.remove(PREF_KEY_OAUTH_TOKEN);
        e.remove(PREF_KEY_OAUTH_SECRET);
        e.remove(PREF_KEY_TWITTER_LOGIN);
        e.commit();
    }


    /**
     * Function to update status
     */
    class updateTwitterStatus extends AsyncTask<String, String, String> {

        /**
         * getting Places JSON
         */
        protected String doInBackground(String... args) {
            Log.d("Tweet Text", "> " + args[0]);
            String status = args[0];
            try {
                ConfigurationBuilder builder = new ConfigurationBuilder();
                builder.setOAuthConsumerKey(CONSUMER_KEY);
                builder.setOAuthConsumerSecret(CONSUMER_SECRET);

                // Access Token
                String access_token = mSharedPreferences.getString(PREF_KEY_OAUTH_TOKEN, "");
                // Access Token Secret
                String access_token_secret = mSharedPreferences.getString(PREF_KEY_OAUTH_SECRET, "");

                AccessToken accessToken = new AccessToken(access_token, access_token_secret);
                Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);

                // Update status
                twitter4j.Status response = twitter.updateStatus(status);

                Log.d("Status", "> " + response.getText());
            } catch (TwitterException e) {
                // Error in updating status
                Log.d("Twitter Update Error", e.getMessage());
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog and show
         * the data in UI Always use runOnUiThread(new Runnable()) to update UI
         * from background thread, otherwise you will get error
         * *
         */
        protected void onPostExecute(String file_url) {
            Toast.makeText(a.getApplicationContext(),
                    "Status tweeted successfully", Toast.LENGTH_SHORT)
                    .show();
        }

    }
}

class AlertDialogManager {
        /**
         * Function to display simple Alert Dialog
         *
         * @param context - application context
         * @param title   - alert dialog title
         * @param message - alert message
         * @param status  - success/failure (used to set icon)
         *                - pass null if you don't want icon
         */
        public void showAlertDialog(Context context, String title, String message,
                                    Boolean status) {
            AlertDialog alertDialog = new AlertDialog.Builder(context).create();

            // Setting Dialog Title
            alertDialog.setTitle(title);

            // Setting Dialog Message
            alertDialog.setMessage(message);

            if (status != null);

            // Showing Alert Message
            alertDialog.show();
        }
}





