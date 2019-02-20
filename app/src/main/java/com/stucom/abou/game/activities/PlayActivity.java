package com.stucom.abou.game.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.stucom.abou.game.activities.bootstrap.StartActivity;
import com.stucom.abou.game.model.AccessApi;

import alex_bou.stucom.com.alex.R;

public class PlayActivity extends AppCompatActivity {

    TextInputLayout input ;
    TextInputLayout inputLevel;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
    }

    @Override
    protected void onResume() {
        super.onResume();
        input = findViewById(R.id.inputScore);
        inputLevel = findViewById(R.id.inputLevel);
        button = findViewById(R.id.buttonSubmit);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateScore() && validateLevel())
                    submitScore();
            }
        });
    }

    private boolean validateScore() {
        if (input.getEditText() != null) {
            String text = input.getEditText().getText().toString();
            if (text.equals("") ) {
                input.setError("Score is required.");
                return false;
            } else if (text.length() > 10) {
                input.setError("Score must have less than 10 digits");
                return false;
            }
            return true;
        }
        input.setError("Score is required.");
        return false;
    }

    private boolean validateLevel() {
        if (inputLevel.getEditText() != null) {
            String text = inputLevel.getEditText().getText().toString();
            if (text.equals("") ) {
                inputLevel.setError("Level is required.");
                return false;
            } else if (text.length() > 10) {
                inputLevel.setError("Level must have less than 10 digits");
                return false;
            }
            return true;
        }
        inputLevel.setError("Level is required.");
        return false;
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
                switch (result) {
                    case OK:
                        Snackbar.make(findViewById(android.R.id.content),"Score sent.",Snackbar.LENGTH_SHORT).show();
                        break;
                    case ERROR_TOKEN:
                        Intent intent = new Intent(PlayActivity.this, StartActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case ERROR_CONNECTION:
                        Snackbar.make(findViewById(android.R.id.content),"Can't connect to the server.",Snackbar.LENGTH_INDEFINITE)
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
