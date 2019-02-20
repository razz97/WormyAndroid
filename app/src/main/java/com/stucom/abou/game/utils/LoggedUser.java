package com.stucom.abou.game.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.stucom.abou.game.model.User;

import java.io.File;

import static android.content.Context.MODE_PRIVATE;

public class LoggedUser extends User {

    private static LoggedUser instance;
    private String token;
    private String email;
    private transient boolean updated = false;

    private LoggedUser() {}

    public static LoggedUser getInstance() {
        if (instance == null)
            instance = new LoggedUser();
        return instance;
    }

    public static void setInstance(LoggedUser instance) {
        LoggedUser.instance = instance;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        Log.d("infoDebug","token set as: " + token);
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    public void saveToPrefs() {
        SharedPreferences.Editor editorPrefs = App.getAppContext().
                getSharedPreferences(App.getAppContext().getPackageName(),MODE_PRIVATE).edit();
        Gson gson = new Gson();
        editorPrefs.putString("user",gson.toJson(this));
        editorPrefs.apply();
    }

    public void loadFromPrefs() {
        String user = App.getAppContext()
                .getSharedPreferences(App.getAppContext().getPackageName(),MODE_PRIVATE)
                .getString("user",null);
        instance = new Gson().fromJson(user, LoggedUser.class);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void logout() {
        setUpdated(false);
        PreferenceManager.getDefaultSharedPreferences(App.getAppContext()).edit().remove("user").apply();
        new File(App.getAppContext().getFilesDir(), "imgProfile.jpg").delete();
    }
}
