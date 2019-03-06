package com.stucom.abou.game.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.stucom.abou.game.activities.bootstrap.StartActivity;
import com.stucom.abou.game.rest.AccessApi;
import com.stucom.abou.game.views.WormyView;

import java.util.regex.Pattern;

import alex_bou.stucom.com.alex.R;

public class PlayActivity extends AppCompatActivity implements WormyView.WormyListener {

    TextInputLayout input ;
    TextInputLayout inputLevel;
    Button button;
    private WormyView wormyView;
    private TextView tvScore;

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*
        input = findViewById(R.id.inputScore);
        inputLevel = findViewById(R.id.inputLevel);
        button = findViewById(R.id.buttonSubmit);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateLevel() && validateScore())
                    submitScore();
            }
        }); */
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
        tvScore.setText(String.valueOf(score));
    }

    @Override
    public void gameLost(View view) {
        Toast.makeText(this, getString(R.string.you_lost), Toast.LENGTH_LONG).show();
    }

    private boolean validateScore() {
        boolean valid = false;
        if (input.getEditText() != null)
            valid = Pattern.compile("^\\s*-?[0-9]{1,9}\\s*$").matcher(input.getEditText().getText()).matches();
        if (!valid)
            input.setError("Score must have 1 to 9 digits");
        return valid;
    }

    private boolean validateLevel() {
        boolean valid = false;
        if (inputLevel.getEditText() != null)
            valid = Pattern.compile("^\\s*-?[0-9]{1,9}\\s*$").matcher(inputLevel.getEditText().getText()).matches();
        if (!valid)
            inputLevel.setError("Level must have 1 to 9 digits");
        return valid;
    }

    private void submitScore() {
        final ProgressDialog progress = ProgressDialog.show(this,null,null);
        progress.setContentView(new ProgressBar(this));
        progress.setCancelable(false);
        progress.show();
        long score = Long.parseLong(input.getEditText().getText().toString());
        int level = Integer.parseInt(inputLevel.getEditText().getText().toString());
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
}
