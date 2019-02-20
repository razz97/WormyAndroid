package com.stucom.abou.game.rest;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.stucom.abou.game.utils.App;

public class MyVolley {

    private static MyVolley instance;

    public static MyVolley getInstance() {
        if (instance == null) {
            instance = new MyVolley(App.getAppContext());
        }
        return instance;
    }

    private RequestQueue queue;

    private MyVolley(Context context) {
        queue = Volley.newRequestQueue(context);
    }

    public <T> void add(Request<T> request) {
        queue.add(request);
    }
}
