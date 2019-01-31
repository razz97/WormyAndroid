package alex_bou.stucom.com.alex_bou.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import alex_bou.stucom.com.alex_bou.R;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set layout to display
        setContentView(R.layout.activity_main);

        // Get buttons from its id and set onClick listener for starting its corresponding activity.
        findViewById(R.id.btnPlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToPlay = new Intent(MainActivity.this, PlayActivity.class);
                startActivity(goToPlay);
            }
        });
        findViewById(R.id.btnRanking).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToRanking = new Intent(MainActivity.this, RankingActivity.class);
                startActivity(goToRanking);
            }
        });
        findViewById(R.id.btnSetting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToSettings = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(goToSettings);
            }
        });
        findViewById(R.id.btnAbout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToAbout = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(goToAbout);
            }
        });

    }

}