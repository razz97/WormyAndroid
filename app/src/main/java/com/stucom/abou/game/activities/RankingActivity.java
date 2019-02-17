package com.stucom.abou.game.activities;

import android.app.ProgressDialog;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.stucom.abou.game.model.LoggedUser;
import com.stucom.abou.game.model.User;
import com.stucom.abou.game.utils.APIResponse;
import com.stucom.abou.game.utils.MyVolley;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import alex_bou.stucom.com.alex.R;

public class RankingActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    String URL = "https://api.flx.cat/dam2game/ranking";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                downloadUsers();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        downloadUsers();
    }

    private void downloadUsers() {
        StringRequest request = new StringRequest(Request.Method.GET, URL + "?token=" + LoggedUser.getInstance().getToken(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("infoDebug","response: " + response);
                        Gson gson = new Gson();
                        Type typeToken = new TypeToken<APIResponse<List<User>>>() {}.getType();
                        APIResponse<List<User>> apiResponse = gson.fromJson(response, typeToken);
                        List<User> users = apiResponse.getData();
                        Collections.sort(users);
                        UsersAdapter adapter = new UsersAdapter(users);
                        recyclerView.setAdapter(adapter);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        MyVolley.getInstance().add(request);

    }

    class UsersViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView textView;
        TextView textViewScore;
        ImageView imageView;

        UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView.findViewById(R.id.container);
            textView = itemView.findViewById(R.id.textView);
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
                    inflate(R.layout.list_item, parent, false);
            return new UsersViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull UsersViewHolder viewHolder, int position) {
            User user = users.get(position);
            viewHolder.textView.setText(user.getName());
            viewHolder.textViewScore.setText(user.getTotalScore());
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
