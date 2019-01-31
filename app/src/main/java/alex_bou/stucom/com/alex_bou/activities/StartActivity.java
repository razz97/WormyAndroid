package alex_bou.stucom.com.alex_bou.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import alex_bou.stucom.com.alex_bou.model.LoggedUser;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (LoggedUser.getInstance().isUpdated()) {
            startActivity(new Intent(this, MainActivity.class));
        }  else
            startActivity(new Intent(this,SplashActivity.class));

        finish();
    }
}
