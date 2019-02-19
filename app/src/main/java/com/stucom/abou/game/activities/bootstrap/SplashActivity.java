package com.stucom.abou.game.activities.bootstrap;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.stucom.abou.game.activities.MainActivity;
import com.stucom.abou.game.activities.register.RegisterActivity;
import com.stucom.abou.game.model.AccessApi;

import alex_bou.stucom.com.alex.R;
import com.stucom.abou.game.model.LoggedUser;
import com.stucom.abou.game.utils.App;

public class SplashActivity extends AppCompatActivity implements AccessApi.ApiListener<String> {

    final int REGISTER_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LoggedUser.getInstance().loadFromPrefs();
        update();
    }

    protected void update() {
        if (LoggedUser.getInstance().getToken() == null)
            navigateToRegister();
        else if (App.isOnline())
            AccessApi.getInstance().updateLocalUser(this);
        else
            navigateToMain();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REGISTER_REQUEST && resultCode == 1)
            update();
        else
            finish();
    }

    private void navigateToRegister() {
        Intent getCodeIntent = new Intent(this, RegisterActivity.class);
        startActivityForResult(getCodeIntent, 1);
    }

    private void navigateToMain() {
        Intent proceedIntent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(proceedIntent);
        finish();
    }

    @Override
    public void onResult(AccessApi.Result result, @Nullable String data) {
        if (result == AccessApi.Result.OK || result == AccessApi.Result.ERROR_CONNECTION) {
            navigateToMain();
        } else if (result == AccessApi.Result.ERROR_TOKEN) {
            navigateToRegister();
        }
    }
}
