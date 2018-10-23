package alex_bou.stucom.com.alex_bou;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set layout to display.
        setContentView(R.layout.activity_about);
        // Add onClick listener for Where we work button
        findViewById(R.id.btnMap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a new intent for starting MapsActivity
                Intent goToPlay = new Intent(AboutActivity.this,MapsActivity.class);
                // Start it
                startActivity(goToPlay);
            }
        });

    }
}
