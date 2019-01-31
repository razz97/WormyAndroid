package alex_bou.stucom.com.alex_bou.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import alex_bou.stucom.com.alex_bou.model.LoggedUser;
import alex_bou.stucom.com.alex_bou.model.User;
import alex_bou.stucom.com.alex_bou.utils.APIResponse;
import alex_bou.stucom.com.alex_bou.utils.MyVolley;
import alex_bou.stucom.com.alex_bou.R;

public class RegisterActivity extends AppCompatActivity {

    final String REGISTER_URL = "https://api.flx.cat/dam2game/register";
    EditText emailInput;
    Button submitButton;
    TextView register;
    String email;
    String verify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        emailInput = findViewById(R.id.emailInput);
        submitButton = findViewById(R.id.submitButtton);
        register = findViewById(R.id.registerLabel);
    }

    @Override
    protected void onResume() {
        super.onResume();
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailInput.getText().toString();
                sendVerificationCode();
                showVerifyDialog();
            }
        });
    }


    private void showVerifyDialog() {



        // Create a builder for generating the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Set title for the dialog
        builder.setTitle("Enter verification code.");
        final EditText input = new EditText(this);
        builder.setView(input);
        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                verify = input.getText().toString();
                verify();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {dialog.cancel();}
        });
        builder.create().show();
    }

    private void sendVerificationCode() {
        StringRequest request = new StringRequest(Request.Method.POST, REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("infoDebug","Verification code sent for: " + email);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("infoDebug","Error sending verification code for: " + email);
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
        MyVolley.getInstance(this).add(request);
    }

    private void verify() {
        Log.d("infoDebug","Verification code supplied: " + verify);
        StringRequest request = new StringRequest(Request.Method.POST, REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        Type typeToken = new TypeToken<APIResponse<String>>() {}.getType();
                        APIResponse<String> apiResponse = gson.fromJson(response, typeToken);
                        String token = apiResponse.getData();
                        Log.d("infoDebug","Successfully registered application with token: " + token);
                        LoggedUser.getInstance().setToken(token);
                        LoggedUser.getInstance().saveToPrefs(RegisterActivity.this);
                        Intent intent = new Intent();
                        setResult(1,intent);
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("infoDebug","Error sending register request (verification): " + error.toString());
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
        MyVolley.getInstance(this).add(request);
    }


}
