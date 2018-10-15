package alex_bou.stucom.com.alex_bou;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnPlay = findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToPlay = new Intent(MainActivity.this,PlayActivity.class);
                startActivity(goToPlay);
            }
        });

        Button btnRanking = findViewById(R.id.btnRanking);
        btnRanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToRanking = new Intent(MainActivity.this,RankingActivity.class);
                startActivity(goToRanking);
            }
        });

        Button btnSetting = findViewById(R.id.btnSetting);
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToSettings = new Intent(MainActivity.this,SettingActivity.class);
                startActivity(goToSettings);
            }
        });

        Button btnAbout = findViewById(R.id.btnAbout);
        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToAbout = new Intent(MainActivity.this,AboutActivity.class);
                startActivity(goToAbout);
            }
        });

    }
}
