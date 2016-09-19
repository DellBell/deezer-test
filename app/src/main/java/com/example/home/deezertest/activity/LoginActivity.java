package com.example.home.deezertest.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.deezer.sdk.model.Permissions;
import com.deezer.sdk.network.connect.DeezerConnect;
import com.deezer.sdk.network.connect.SessionStore;
import com.deezer.sdk.network.connect.event.DialogListener;
import com.example.home.deezertest.NavigationUtil;
import com.example.home.deezertest.R;


public class LoginActivity extends MainActivity{

    protected static final String[] PERMISSIONS = new String[]{
            Permissions.BASIC_ACCESS, Permissions.OFFLINE_ACCESS
    };


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        findViewById(R.id.buttonLogin).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(final View v) {
                connectToDeezer();
            }
        });

        SessionStore sessionStore = new SessionStore();

        if (sessionStore.restore(mDeezerConnect, this)) {
            Toast.makeText(this, "Already logged in !", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(LoginActivity.this, SearchActivity.class);
            startActivity(intent);
        }

        ((TextView) findViewById(R.id.textVersion)).setText(DeezerConnect.SDK_VERSION);
    }

    /**
     * Asks the SDK to display a log in dialog for the user
     */
    private void connectToDeezer() {
        mDeezerConnect.authorize(this, PERMISSIONS, mDeezerDialogListener);
    }

    /**
     * A listener for the Deezer Login Dialog
     */
    private DialogListener mDeezerDialogListener = new DialogListener() {

        @Override
        public void onComplete(final Bundle values) {
            // store the current authentication info
            SessionStore sessionStore = new SessionStore();
            sessionStore.save(mDeezerConnect, LoginActivity.this);

            // Launch the Search activity
            NavigationUtil.launchSearchActivity(LoginActivity.this);
        }

        @Override
        public void onException(final Exception exception) {
            Toast.makeText(LoginActivity.this, R.string.deezer_error_during_login,
                    Toast.LENGTH_LONG).show();
        }


        @Override
        public void onCancel() {
            Toast.makeText(LoginActivity.this, R.string.login_cancelled, Toast.LENGTH_LONG).show();
        }


    };
}
