package com.stucom.abou.game.model;

import android.support.annotation.Nullable;
import android.support.v4.util.Consumer;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stucom.abou.game.activities.RankingActivity;
import com.stucom.abou.game.utils.APIResponse;
import com.stucom.abou.game.utils.MyVolley;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccessApi {

    private static final String  API_URL = "https://api.flx.cat/dam2game/";
    private static AccessApi instance;

    public enum Result {
        OK,ERROR_TOKEN ,ERROR_CONNECTION,ERROR_VERIFY
    }

    public interface ApiListener<T> {
        void onResult(Result result, @Nullable T data);
    }

    private AccessApi() {}

    public static AccessApi getInstance() {
        if (instance == null)
            instance = new AccessApi();
        return instance;
    }

    public void updateLocalUser(final ApiListener<String> listener) {
        StringRequest request = new StringRequest(Request.Method.GET, API_URL + "user?token=" + LoggedUser.getInstance().getToken(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("infoDebug","User data successfully downloaded: " + response);
                        Type typeToken = new TypeToken<APIResponse<LoggedUser>>() {}.getType();
                        APIResponse<LoggedUser> apiResponse = new Gson().fromJson(response, typeToken);
                        if (apiResponse.getErrorCode() != 0) {
                            listener.onResult(Result.ERROR_TOKEN, null);
                        } else {
                            LoggedUser user = apiResponse.getData();
                            user.setToken(LoggedUser.getInstance().getToken());
                            user.setEmail(LoggedUser.getInstance().getEmail());
                            user.setUpdated(true);
                            LoggedUser.setInstance(user);
                            LoggedUser.getInstance().saveToPrefs();
                            listener.onResult(Result.OK, null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onResult(Result.ERROR_CONNECTION,null);
                    }
                });
        MyVolley.getInstance().add(request);
    }

    public void registerEmail(final ApiListener<Integer> listener, final String email) {
        StringRequest request = new StringRequest(Request.Method.POST, API_URL + "register",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Type typeToken = new TypeToken<APIResponse<Integer>>() {}.getType();
                        APIResponse<Integer> apiResponse = new Gson().fromJson(response, typeToken);
                        if (apiResponse.getErrorCode() != 0) {
                            listener.onResult(Result.ERROR_TOKEN, null);
                        } else {
                            LoggedUser.getInstance().setEmail(email);
                            LoggedUser.getInstance().saveToPrefs();
                            listener.onResult(Result.OK, apiResponse.getData());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onResult(Result.ERROR_CONNECTION, null);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                return params;
            }
        };
        MyVolley.getInstance().add(request);
    }

    public void verifyEmail(final ApiListener<String> listener, final String email, final String verify) {
        StringRequest request = new StringRequest(Request.Method.POST, API_URL + "register",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        Type typeToken = new TypeToken<APIResponse<String>>() {}.getType();
                        APIResponse<String> apiResponse = gson.fromJson(response, typeToken);
                        if (apiResponse.getErrorCode() != 0) {
                            listener.onResult(Result.ERROR_VERIFY,null);
                        } else {
                            String token = apiResponse.getData();
                            LoggedUser.getInstance().setToken(token);
                            LoggedUser.getInstance().saveToPrefs();
                            listener.onResult(Result.OK, null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onResult(Result.ERROR_CONNECTION, null);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("verify", verify);
                return params;
            }
        };
        MyVolley.getInstance().add(request);
    }

    public void updateServerUser(final ApiListener<String> listener, @Nullable final String base64Image, @Nullable final String name) {
        StringRequest request = new StringRequest(Request.Method.PUT, API_URL + "user",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Type typeToken = new TypeToken<APIResponse<Boolean>>() {}.getType();
                        APIResponse<Boolean> apiResponse = new Gson().fromJson(response, typeToken);
                        if (apiResponse.getErrorCode() != 0)
                            listener.onResult(Result.ERROR_TOKEN, null);
                        else
                            AccessApi.getInstance().updateLocalUser(listener);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onResult(Result.ERROR_CONNECTION, null);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token",LoggedUser.getInstance().getToken());
                if (base64Image != null)
                    params.put("image",base64Image);
                if (name != null)
                    params.put("name",name);
                return params;
            }
        };
        MyVolley.getInstance().add(request);
    }

    public void getRanking(final ApiListener<List<User>> listener) {
        StringRequest request = new StringRequest(Request.Method.GET, API_URL + "ranking" + "?token=" + LoggedUser.getInstance().getToken(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        Type typeToken = new TypeToken<APIResponse<List<User>>>() {}.getType();
                        APIResponse<List<User>> apiResponse = gson.fromJson(response, typeToken);
                        if (apiResponse.getErrorCode() != 0)
                            listener.onResult(Result.ERROR_TOKEN, null);
                        else {
                            List<User> users = apiResponse.getData();
                            Collections.sort(users);
                            listener.onResult(Result.OK, users);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onResult(Result.ERROR_CONNECTION, null);
                    }
                });
        MyVolley.getInstance().add(request);

    }

}
