package com.stucom.abou.game.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.stucom.abou.game.model.User;

import alex_bou.stucom.com.alex.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserDetailActivity extends AppCompatActivity {

    TextView textView ;
    CircleImageView imageView;
    int userId ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        //imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);

        userId = getIntent().getIntExtra("user",0);
    }


}
