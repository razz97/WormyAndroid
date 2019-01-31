package alex_bou.stucom.com.alex_bou.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import alex_bou.stucom.com.alex_bou.utils.APIResponse;
import alex_bou.stucom.com.alex_bou.utils.MyVolley;
import alex_bou.stucom.com.alex_bou.R;
import alex_bou.stucom.com.alex_bou.model.LoggedUser;
import alex_bou.stucom.com.alex_bou.model.User;

public class SplashActivity extends AppCompatActivity {

    private static final String USER_DATA_URL = "https://api.flx.cat/dam2game/user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d("infoDebug","resumeSplash");
        update();
    }

    protected void update() {
        LoggedUser.getInstance().loadFromPrefs(this);
        if (!isRegistered()) {
            Log.d("infoDebug","Token not found, starting registration intent");
            Intent getCodeIntent = new Intent(this, RegisterActivity.class);
            startActivityForResult(getCodeIntent, 1);
        } else {
            Log.d("infoDebug", "Application token was found.");
            downloadData();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            switch(resultCode) {
                case 1: update(); break;
                default: finish(); break;
            }
        }
    }

    private void downloadData() {
        Log.d("infoDebug","Downloading user data...");
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
                            LoggedUser.getInstance().saveToPrefs(SplashActivity.this);
                            Intent proceedIntent= new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(proceedIntent);
                            finish();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("infoDebug","Error fetching user data: " + error.toString());
                    }
                });
        MyVolley.getInstance(this).add(request);
    }

    private boolean isRegistered() {
        Log.d("infoDebug","user:"+ LoggedUser.getInstance().getToken());
        return LoggedUser.getInstance().getToken() != null;
    }
}
