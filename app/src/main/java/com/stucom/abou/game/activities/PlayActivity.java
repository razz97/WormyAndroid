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
import com.stucom.abou.game.rest.AccessApi;

import java.util.regex.Pattern;

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
        boolean valid = false;
        if (input.getEditText() != null)
            valid = Pattern.compile("^\\d{9}$").matcher(input.getEditText().getText()).matches();
        if (valid)
            input.setError("Score must have 1 to 9 digits");
        return valid;
    }

    private boolean validateLevel() {
        boolean valid = false;
        if (input.getEditText() != null)
            valid = Pattern.compile("^\\d{9}$").matcher(input.getEditText().getText()).matches();
        if (valid)
            input.setError("Level must have 1 to 9 digits");
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
