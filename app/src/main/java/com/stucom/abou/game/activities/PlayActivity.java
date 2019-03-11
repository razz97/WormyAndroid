package com.stucom.abou.game.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.stucom.abou.game.activities.bootstrap.StartActivity;
import com.stucom.abou.game.rest.AccessApi;
import com.stucom.abou.game.views.WormyView;


import alex_bou.stucom.com.alex.R;

public class PlayActivity extends AppCompatActivity implements WormyView.WormyListener, SensorEventListener {

    private WormyView wormyView;
    private TextView tvScore;
    private SensorManager sensorManager;
    private MediaPlayer mediaPlayer;
    private SoundPool soundPool;
    private int coin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        wormyView = findViewById(R.id.wormyView);
        Button btnNewGame = findViewById(R.id.btnNewGame);
        tvScore = findViewById(R.id.tvScore);
        btnNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvScore.setText("0");
                wormyView.newGame();
            }
        });
        wormyView.setWormyListener(this);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // FIXME: Set proper resource for media player.
        mediaPlayer = MediaPlayer.create(this, R.raw.a);
        soundPool = new SoundPool.Builder().build();
        coin = soundPool.load(this, R.raw.a,1);
    }

    @Override
    public void onResume() {
        super.onResume();
        mediaPlayer.start();
        // Connect the sensor's listener to the view
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (sensor != null) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
        }
    }
    @Override
    public void onPause() {
        // Nicely disconnect the sensor's listener from the view
        mediaPlayer.stop();
        sensorManager.unregisterListener(this);
        super.onPause();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch(event.getKeyCode()) {
            case KeyEvent.KEYCODE_A: wormyView.update(0, +10); break;
            case KeyEvent.KEYCODE_Q: wormyView.update(0, -10); break;
            case KeyEvent.KEYCODE_O: wormyView.update(-10, 0); break;
            case KeyEvent.KEYCODE_P: wormyView.update(+10, 0); break;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void scoreUpdated(View view, int score) {
        soundPool.play(coin,100,100,1,0,1);
        tvScore.setText(String.valueOf(score));
    }

    @Override
    public void gameLost(View view) {
        Toast.makeText(this, getString(R.string.you_lost), Toast.LENGTH_LONG).show();
        submitScore();
    }

    private void submitScore() {
        final ProgressDialog progress = ProgressDialog.show(this,null,null);
        progress.setContentView(new ProgressBar(this));
        progress.setCancelable(false);
        progress.show();
        long score = Long.parseLong(tvScore.getText().toString());
        int level = 0;
        AccessApi.getInstance().submitScore(new AccessApi.ApiListener<Boolean>() {
            @Override
            public void onResult(AccessApi.Result result, @Nullable Boolean data) {
                boolean generic = false;
                switch (result) {
                    case OK:
                        Snackbar.make(findViewById(android.R.id.content),"Score sent.",Snackbar.LENGTH_SHORT).show();
                        break;
                    case ERROR_TOKEN:
                        Intent intent = new Intent(PlayActivity.this, StartActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case GENERIC_ERROR:
                        generic = true;
                    case ERROR_CONNECTION:
                        Snackbar.make(findViewById(android.R.id.content),generic ? "There was an error." : "Error connecting to the server.",Snackbar.LENGTH_INDEFINITE)
                                .setAction("Retry", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        submitScore();
                                    }
                                }).show();
                }
                progress.dismiss();
            }
        }, score, level);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d("infoDebug","sensor changed");
        wormyView.update(-event.values[0], event.values[1]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

}
