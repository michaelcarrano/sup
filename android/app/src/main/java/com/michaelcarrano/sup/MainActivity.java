package com.michaelcarrano.sup;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

// TODO: Turn Toast messages into an error view
// TODO: See if we need to make all the queries to Parse
public class MainActivity extends Activity {

    private ParseUser mCurrentUser;

    private List<ParseUser> mUserFriends;

    private FriendListViewAdapter mFriendAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ParseAnalytics.trackAppOpened(getIntent());

        isUserAuthenticated();
        if (mCurrentUser != null) {
            registerPushNotification();
            loadFriendList();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.add_user:
                addFriend();
                return true;
            case R.id.settings:
                ParseUser.logOut();
                isUserAuthenticated();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // If user is not authenticated, take them to the AuthenticationActivity
    private void isUserAuthenticated() {
        // Check if user is authenticated
        mCurrentUser = ParseUser.getCurrentUser();
        if (mCurrentUser == null) {
            startActivity(new Intent(this, AuthenticationActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        }
    }

    private void addFriend() {
        final RelativeLayout container = (RelativeLayout) findViewById(R.id.add_user_container);
        final EditText input = (EditText) findViewById(R.id.add_user_input);

        if (container.isShown()) {
            container.setVisibility(View.GONE);
        } else {
            container.setVisibility(View.VISIBLE);
        }

        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    container.setVisibility(View.GONE);
                    final String friend = v.getText().toString().toUpperCase();
                    // Query to see if user exists
                    // TODO: Is this really needed?
                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    query.whereEqualTo("username", friend);
                    query.getFirstInBackground(new GetCallback<ParseUser>() {
                        @Override
                        public void done(final ParseUser parseUser, ParseException e) {
                            if (parseUser == null) {
                                Toast.makeText(getBaseContext(),
                                        getString(R.string.error_does_not_exist),
                                        Toast.LENGTH_LONG).show();
                            } else {
                                // Save the friend relationship to Parse
                                ParseObject query = new ParseObject("Friend");
                                query.put("user", mCurrentUser);
                                query.put("friend", friend);
                                query.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            mUserFriends.add(parseUser);
                                            mFriendAdapter.notifyDataSetChanged();
                                        } else {
                                            Toast.makeText(getBaseContext(),
                                                    getString(R.string.error_oops),
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
                return false;
            }
        });
    }

    private void loadFriendList() {
        final ListView listview = (ListView) findViewById(R.id.listView);
        listview.setOnItemClickListener(new FriendClickListener());
        mUserFriends = new ArrayList<ParseUser>();
        mFriendAdapter = new FriendListViewAdapter(getBaseContext(), mUserFriends);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Friend");
        query.whereEqualTo("user", mCurrentUser);
        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.whereMatchesKeyInQuery("username", "friend", query);
        userQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                if (parseUsers == null) {
                    Toast.makeText(getBaseContext(), getString(R.string.error_oops),
                            Toast.LENGTH_LONG).show();
                } else {
                    mUserFriends = parseUsers;
                    mFriendAdapter = new FriendListViewAdapter(getBaseContext(), mUserFriends);
                    listview.setAdapter(mFriendAdapter);
                    mFriendAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    // Associate the device with a user
    public void registerPushNotification() {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("user", mCurrentUser);
        installation.saveInBackground();
    }

    // Listener for ListView item clicks
    private class FriendClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // Create our Installation query
            ParseQuery pushQuery = ParseInstallation.getQuery();
            pushQuery.whereEqualTo("user", mUserFriends.get(position));

            // Send push notification to query
            ParsePush push = new ParsePush();
            push.setQuery(pushQuery); // Set our Installation query
            push.setMessage(mCurrentUser.getUsername());
            push.sendInBackground();
        }
    }

}
