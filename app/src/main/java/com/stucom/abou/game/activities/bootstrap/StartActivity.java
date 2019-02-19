package com.stucom.abou.game.activities.bootstrap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.stucom.abou.game.activities.MainActivity;
import com.stucom.abou.game.model.LoggedUser;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (LoggedUser.getInstance().isUpdated())
            startActivity(new Intent(this, MainActivity.class));
        else
            startActivity(new Intent(this, SplashActivity.class));

        finish();
    }
}
