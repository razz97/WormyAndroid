package com.stucom.abou.game.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stucom.abou.game.activities.bootstrap.StartActivity;
import com.stucom.abou.game.model.AccessApi;
import com.stucom.abou.game.model.LoggedUser;
import com.stucom.abou.game.model.Score;
import com.stucom.abou.game.model.User;
import com.stucom.abou.game.utils.App;

import java.util.List;

import alex_bou.stucom.com.alex.R;

import static com.stucom.abou.game.model.AccessApi.Result.ERROR_TOKEN;

public class HistoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
    }

    @Override
    protected void onResume() {
        super.onResume();
         recyclerView = findViewById(R.id.recyclerView);
         recyclerView.setLayoutManager(new LinearLayoutManager(this));
         recyclerView.setItemAnimator(new DefaultItemAnimator());
         setActivityState();
    }

    private void setActivityState() {
        if (App.isOnline()) {
            refreshRecycler();
        } else {
            Snackbar.make(findViewById(android.R.id.content),"Internet connection is missing.",Snackbar.LENGTH_INDEFINITE)
                    .setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) { setActivityState();
                        }
                    }).show();
        }
    }

    private void refreshRecycler() {
        AccessApi.getInstance().updateLocalUser(new AccessApi.ApiListener<String>() {
            @Override
            public void onResult(AccessApi.Result result, @Nullable String data) {
                switch (result) {
                    case OK:
                        recyclerView.setAdapter(new ScoresAdapter(LoggedUser.getInstance().getScores()));
                        if (LoggedUser.getInstance().getScores().isEmpty())
                            Snackbar.make(findViewById(android.R.id.content),"No games played yet.",Snackbar.LENGTH_INDEFINITE).show();
                        break;
                    case ERROR_TOKEN:
                        Intent intent = new Intent(HistoryActivity.this, StartActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case ERROR_CONNECTION:
                        Snackbar.make(findViewById(android.R.id.content),"Error connecting to the server.",Snackbar.LENGTH_INDEFINITE)
                                .setAction("Retry", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        refreshRecycler();
                                    }
                                }).show();
                }
            }
        });
    }

    class ScoreViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        TextView textViewScore;
        TextView textViewPlayed;

        ScoreViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            textViewScore = itemView.findViewById(R.id.textViewScore);
            textViewPlayed = itemView.findViewById(R.id.textViewPlayed);
        }
    }

    class ScoresAdapter extends RecyclerView.Adapter<ScoreViewHolder> {

        private List<Score> scores;

        ScoresAdapter(List<Score> scores) {
            super();
            this.scores = scores;
        }

        @NonNull
        @Override
        public ScoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
            View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.score, parent, false);
            return new ScoreViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull ScoreViewHolder viewHolder, int position) {
            final Score score = scores.get(position);
            viewHolder.textView.setText(String.valueOf(score.getScore()));
            viewHolder.textViewScore.setText(String.valueOf(score.getScore()));
            viewHolder.textViewPlayed.setText(String.valueOf(score.getPlayedAt()));
        }
        @Override
        public int getItemCount() {
            return scores.size();
        }
    }
}
