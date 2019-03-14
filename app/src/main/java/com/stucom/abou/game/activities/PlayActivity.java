package com.stucom.abou.game.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.stucom.abou.game.activities.bootstrap.StartActivity;
import com.stucom.abou.game.rest.AccessApi;
import com.stucom.abou.game.utils.Media;
import com.stucom.abou.game.views.WormyView;


import alex_bou.stucom.com.alex.R;

public class PlayActivity extends AppCompatActivity implements WormyView.WormyListener, SensorEventListener {

    private WormyView wormyView;
    private SensorManager sensorManager;
    Button btnNewGame;
    private int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_play);
        wormyView = findViewById(R.id.wormyView);
        btnNewGame = findViewById(R.id.btnNewGame);
        ConstraintLayout.LayoutParams newLayoutParams = (ConstraintLayout.LayoutParams) btnNewGame.getLayoutParams();
        newLayoutParams.topMargin = wormyView.getTopMargin();
        btnNewGame.setLayoutParams(newLayoutParams);
        btnNewGame.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {newGame();}});
        wormyView.setWormyListener(this);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (wormyView.isPlaying())
            Media.getInstance().startGame();
        else
            Media.getInstance().startMenu();
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (sensor != null) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
        }
    }
    @Override
    public void onPause() {
        Media.getInstance().stopAll();
        sensorManager.unregisterListener(this);
        super.onPause();
    }

    @Override
    public void scoreUpdated(View view, int score) {
        Media.getInstance().coin();
        this.score = score;
    }

    @Override
    public void lifeLost(View view) {
        Media.getInstance().life();
    }

    @Override
    public void gameLost(View view) {
        Media.getInstance().gameOver();
        submitScore();
        showdialogPlayAgain();
    }

    private void newGame() {
        Media.getInstance().startGame();
        wormyView.newGame();
    }

    private void submitScore() {
        final ProgressDialog progress = ProgressDialog.show(this,null,null);
        progress.setContentView(new ProgressBar(this));
        progress.setCancelable(false);
        progress.show();
        long score = this.score;
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
        wormyView.update(-event.values[0], event.values[1]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private void showdialogPlayAgain() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Do you want to play again? ");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                newGame();
            }
        });
        builder.setNegativeButton("Maybe later", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { dialog.dismiss(); startActivity(new Intent(PlayActivity.this,MainActivity.class)); }
        });
        builder.create().show();
    }
}
