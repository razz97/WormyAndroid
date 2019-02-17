package com.stucom.abou.game.activities.bootstrap;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.stucom.abou.game.activities.MainActivity;
import com.stucom.abou.game.activities.register.RegisterActivity;
import com.stucom.abou.game.model.Dao;

import alex_bou.stucom.com.alex.R;
import com.stucom.abou.game.model.LoggedUser;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("infoDebug","resumeSplash");
        update();
    }

    protected void update() {
        LoggedUser.getInstance().loadFromPrefs();
        if (!isRegistered()) {
            Log.d("infoDebug","Token not found, starting registration intent");
            Intent getCodeIntent = new Intent(this, RegisterActivity.class);
            startActivityForResult(getCodeIntent, 1);
        } else {
            Log.d("infoDebug", "Application token was found.");
            downloadData();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            switch(resultCode) {
                case 1: update(); break;
                default: finish(); break;
            }
        }
    }

    private void downloadData() {
        Log.d("infoDebug","Downloading user data...");
        new Dao().downloadUserData(new Runnable() {
            @Override
            public void run() {
                Intent proceedIntent= new Intent(SplashActivity.this, MainActivity.class);
                startActivity(proceedIntent);
                finish();
            }
        });
    }

    private boolean isRegistered() {
        Log.d("infoDebug","user:"+ LoggedUser.getInstance().getToken());
        return LoggedUser.getInstance().getToken() != null;
    }

}
