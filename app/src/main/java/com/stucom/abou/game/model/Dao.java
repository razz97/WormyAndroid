package com.stucom.abou.game.model;

import android.content.Intent;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stucom.abou.game.activities.MainActivity;
import com.stucom.abou.game.activities.bootstrap.SplashActivity;
import com.stucom.abou.game.utils.APIResponse;
import com.stucom.abou.game.utils.MyVolley;

import java.lang.reflect.Type;

public class Dao {

    private static final String USER_DATA_URL = "https://api.flx.cat/dam2game/user";


    public void downloadUserData(final Runnable runnable) {
        StringRequest request = new StringRequest(Request.Method.GET, USER_DATA_URL + "?token=" + LoggedUser.getInstance().getToken(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("infoDebug","User data successfully downloaded: " + response);
                        Type typeToken = new TypeToken<APIResponse<LoggedUser>>() {}.getType();
                        APIResponse<LoggedUser> apiResponse = new Gson().fromJson(response, typeToken);
                        if (apiResponse.getErrorCode() != 0) {
                            // Mostrar error de red
                        } else {
                            LoggedUser user = apiResponse.getData();
                            user.setToken(LoggedUser.getInstance().getToken());
                            user.setEmail(LoggedUser.getInstance().getEmail());
                            user.setUpdated(true);
                            LoggedUser.setInstance(user);
                            LoggedUser.getInstance().saveToPrefs();
                            runnable.run();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("infoDebug","Error fetching user data: " + error.toString());
                    }
                });
        MyVolley.getInstance().add(request);
    }

}
