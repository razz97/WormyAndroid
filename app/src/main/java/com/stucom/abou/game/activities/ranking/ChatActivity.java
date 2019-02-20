package com.stucom.abou.game.activities.ranking;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.stucom.abou.game.activities.bootstrap.StartActivity;
import com.stucom.abou.game.rest.AccessApi;
import com.stucom.abou.game.utils.LoggedUser;
import com.stucom.abou.game.model.Message;

import java.util.List;

import alex_bou.stucom.com.alex.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity  {

    TextView textView ;
    TextInputLayout input;
    Button button;
    CircleImageView imageView;
    RecyclerView recyclerView;
    int userId ;
    String userName;
    String userImage;
    final Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            refreshMessages();
            handler.postDelayed(this, 3000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
    }

    @Override
    protected void onResume() {
        super.onResume();
        userId = getIntent().getIntExtra("id",0);
        userName = getIntent().getStringExtra("name");
        userImage = getIntent().getStringExtra("image");
        bindViews();
        handler.postDelayed(runnable,0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    private void bindViews() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        imageView = findViewById(R.id.imgProfile);
        textView = findViewById(R.id.text);
        if (userImage != null)
            Picasso.get().load(userImage).into(imageView);
        else
            imageView.setImageResource(R.drawable.usr);
        textView.setText(userName);
        input = findViewById(R.id.textInput);
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (input.getEditText() != null && input.getEditText().getText() != null && !input.getEditText().getText().toString().equals(""))
                    sendMessage(input.getEditText().getText().toString());
                else
                    Snackbar.make(findViewById(android.R.id.content),"Invalid message.",Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage(final String message) {
            input.getEditText().setText("");
            AccessApi.getInstance().sendMessage(new AccessApi.ApiListener<Boolean>() {
                @Override
                public void onResult(AccessApi.Result result, @Nullable Boolean data) {
                    switch (result) {
                        case OK:
                            refreshMessages();
                            break;
                        case ERROR_CONNECTION:
                            Snackbar.make(findViewById(android.R.id.content),"Can't connect to the server.",Snackbar.LENGTH_INDEFINITE)
                                    .setAction("Retry", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            sendMessage(message);
                                        }
                                    }).show();
                            break;
                        case ERROR_TOKEN:
                            Intent intent = new Intent(ChatActivity.this, StartActivity.class);
                            startActivity(intent);
                            finish();
                    }
                }
            }, userId, message);
    }

    private void refreshMessages() {
        AccessApi.getInstance().getMessages(new AccessApi.ApiListener<List<Message>>() {
            @Override
            public void onResult(AccessApi.Result result, @Nullable List<Message> data) {
                switch (result) {
                    case OK:
                        Log.d("infoDebug", "refreshed");
                        MessagesAdapter messagesAdapter = new MessagesAdapter(data);
                        recyclerView.setAdapter(messagesAdapter);
                        recyclerView.scrollToPosition(data.size() -1);
                        break;
                }
            }
        }, userId);
    }

    class MessagesViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        TextView textDate;
        ImageView imageView;

        MessagesViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text);
            textDate = itemView.findViewById(R.id.time);
            imageView = itemView.findViewById(R.id.status);
        }
    }


    class MessagesAdapter extends RecyclerView.Adapter<MessagesViewHolder> {

        private List<Message> messages;

        MessagesAdapter(List<Message> messages) {
            super();
            this.messages = messages;
        }

        @NonNull
        @Override
        public MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
            View view = LayoutInflater.from(parent.getContext()).
                    inflate(
                            messages.get(position).getFromId() != LoggedUser.getInstance().getId() ? R.layout.message_received : R.layout.message_sent,
                            parent,
                            false
                    );
            return new MessagesViewHolder(view);
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(@NonNull MessagesViewHolder viewHolder, int position) {
            final Message message = messages.get(position);
            viewHolder.textView.setText(message.getText());
            if (message.getFromId() == LoggedUser.getInstance().getId()) {
                boolean seen = message.getReceivedAt() != null;
                viewHolder.imageView.setImageResource(seen ? R.drawable.received : R.drawable.sent);
                viewHolder.textDate.setText(seen ? message.getReceivedAt() : message.getSentAt());
            } else {
                viewHolder.textDate.setText(message.getSentAt());
            }

        }
        @Override
        public int getItemCount() {
            return messages.size();
        }
    }
}
