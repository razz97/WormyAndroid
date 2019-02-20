package com.stucom.abou.game.activities.ranking;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.stucom.abou.game.activities.bootstrap.StartActivity;
import com.stucom.abou.game.rest.AccessApi;
import com.stucom.abou.game.utils.LoggedUser;
import com.stucom.abou.game.model.User;
import com.stucom.abou.game.utils.App;

import java.util.List;

import alex_bou.stucom.com.alex.R;

public class RankingActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setActivityState();
    }

    private void setActivityState() {
        if (App.isOnline()) {
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {refreshRecycler();
                }
            });
            refreshRecycler();
        } else {
            Snackbar.make(findViewById(android.R.id.content),"Internet connection is missing.",Snackbar.LENGTH_INDEFINITE)
                    .setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {setActivityState();
                        }
                    }).show();
        }
    }

    private void refreshRecycler() {
        swipeRefreshLayout.setRefreshing(true);
        AccessApi.getInstance().getRanking(new AccessApi.ApiListener<List<User>>() {
            @Override
            public void onResult(AccessApi.Result result, @Nullable List<User> data) {
                boolean generic = false;
                switch (result) {
                    case OK:
                        UsersAdapter adapter = new UsersAdapter(data);
                        recyclerView.setAdapter(adapter);
                        swipeRefreshLayout.setRefreshing(false);
                        break;
                    case ERROR_TOKEN:
                        Intent intent = new Intent(RankingActivity.this, StartActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case GENERIC_ERROR:
                        generic = true;
                    case ERROR_CONNECTION:
                        Snackbar.make(findViewById(android.R.id.content), generic ?  "There was an error" :"Can't connect to the server.",Snackbar.LENGTH_INDEFINITE)
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

    class UsersViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView textView;
        TextView textViewScore;
        ImageView imageView;

        UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView.findViewById(R.id.container);
            textView = itemView.findViewById(R.id.text);
            imageView = itemView.findViewById(R.id.imageView);
            textViewScore = itemView.findViewById(R.id.textViewScore);
        }
    }

    class UsersAdapter extends RecyclerView.Adapter<UsersViewHolder> {

        private List<User> users;

        UsersAdapter(List<User> users) {
            super();
            this.users = users;
        }

        @NonNull
        @Override
        public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
            View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.item_ranking, parent, false);
            return new UsersViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull UsersViewHolder viewHolder, int position) {
            final User user = users.get(position);
            viewHolder.textView.setText(user.getName());
            viewHolder.textViewScore.setText(user.getTotalScore());
            viewHolder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (user.getId() != LoggedUser.getInstance().getId()) {
                        Intent intent = new Intent(RankingActivity.this, ChatActivity.class);
                        intent.putExtra("id", user.getId());
                        intent.putExtra("image", user.getImage());
                        intent.putExtra("name", user.getName());
                        startActivity(intent);
                    } else {
                        startActivity(new Intent(RankingActivity.this, HistoryActivity.class));
                    }
                }
            });
            if (user.getId() == LoggedUser.getInstance().getId())
                viewHolder.view.setBackgroundColor(getResources().getColor(R.color.blue));
            else
                viewHolder.view.setBackground(null);
            if (user.getImage() != null)
                Picasso.get().load(user.getImage()).into(viewHolder.imageView);
            else
                viewHolder.imageView.setImageResource(R.drawable.usr);
        }
        @Override
        public int getItemCount() {
            return users.size();
        }
    }
}
