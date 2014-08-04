package com.michaelcarrano.sup;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AuthenticationActivity extends FragmentActivity {

    private AuthenticationFragmentAdapter mAdapter;

    private ViewPager mPager;

    private PageIndicator mIndicator;

    private EditText mUsernameEtxt;

    private EditText mPasswordEtxt;

    private Button mSignupBtn;

    private Button mSigninBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        // Get view references
        mPasswordEtxt = (EditText) findViewById(R.id.password);
        mUsernameEtxt = (EditText) findViewById(R.id.username);
        mSigninBtn = (Button) findViewById(R.id.btn_sign_in);
        mSignupBtn = (Button) findViewById(R.id.btn_sign_up);

        //Set the pager with an adapter
        mAdapter = new AuthenticationFragmentAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);

        // Setup button click listeners
        mSigninBtn.setOnClickListener(new SignInOnClickListener());
        mSignupBtn.setOnClickListener(new SignUpOnClickListener());
    }

    // Input validation
    private boolean isFormInputValid(String username, String password) {
        mUsernameEtxt.setError(null);
        mPasswordEtxt.setError(null);

        // validate the username
        if (username == null || username.isEmpty()) {
            mUsernameEtxt.setError(getString(R.string.error_username));
            mUsernameEtxt.requestFocus();
            return false;
        }

        // validate the password
        if (password == null || password.isEmpty()) {
            mPasswordEtxt.setError(getString(R.string.error_password));
            mPasswordEtxt.requestFocus();
            return false;
        }

        return true;
    }

    // OnClickListener Implementations
    private class SignUpOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // Get the username and password from the view
            String username = mUsernameEtxt.getText().toString().toUpperCase();
            String password = mPasswordEtxt.getText().toString();
            if (isFormInputValid(username, password)) {
                ParseUser user = new ParseUser();
                user.setUsername(username);
                user.setPassword(password);

                user.signUpInBackground(new SignUpCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            // Successful sign up, take user to the MainActivity
                            Intent intent = new Intent(getBaseContext(), MainActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            // Sign up didn't succeed, most likely the username is taken
                            mUsernameEtxt.setError(getString(R.string.error_username_taken));
                            mUsernameEtxt.requestFocus();
                        }
                    }
                });
            }
        }
    }

    private class SignInOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // Get the username and password from the view
            String username = mUsernameEtxt.getText().toString().toUpperCase();
            String password = mPasswordEtxt.getText().toString();
            if (isFormInputValid(username, password)) {
                ParseUser.logInInBackground(username, password, new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            // Hooray! The user is logged in.
                            Intent intent = new Intent(getBaseContext(), MainActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            findViewById(R.id.sign_in_error).setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        }
    }

}
