package com.stucom.abou.game.activities.register;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.RegexValidator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stucom.abou.game.model.LoggedUser;
import com.stucom.abou.game.utils.APIResponse;
import com.stucom.abou.game.utils.MyVolley;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import alex_bou.stucom.com.alex.R;

public class VerifyFragment extends Fragment {

    private TextInputEditText verifyEdit;
    private TextInputLayout textInputLayout;
    private VerifyFragmentListener listener;
    private LinearLayout rootView;
    private String email;

    interface VerifyFragmentListener {
        void onCodeVerified();
        void onChangeEmail();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_verify,container,false);
        verifyEdit = v.findViewById(R.id.edit_code);
        textInputLayout = v.findViewById(R.id.text_input_layout);
        rootView = v.findViewById(R.id.fragment_verify);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyEdit.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        });
        Button submitButton = v.findViewById(R.id.button_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (validateCode())
                            verify();
                        else
                            textInputLayout.setError("Code must have 6 digits");
                    }
                }
        );
        Button changeEmailButton = v.findViewById(R.id.button_change);
        changeEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onChangeEmail();
            }
        });
        return v;
    }

    public static VerifyFragment newInstance(String email) {
        VerifyFragment fragment = new VerifyFragment();
        fragment.email = email;
        fragment.setArguments(new Bundle());
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof VerifyFragmentListener)
            listener = (VerifyFragmentListener) context;
        else
            throw new RuntimeException(context.toString() + " must implement VerifyFragmentListener");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    private void verify() {
        final ProgressDialog progress = new ProgressDialog(getContext());
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
        final CharSequence verify = verifyEdit.getText();
        Log.d("infoDebug","Verification code supplied: " + verify);
        StringRequest request = new StringRequest(Request.Method.POST, RegisterActivity.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        Type typeToken = new TypeToken<APIResponse<String>>() {}.getType();
                        APIResponse<String> apiResponse = gson.fromJson(response, typeToken);
                        if (apiResponse.getErrorCode() != 1) {
                            String token = apiResponse.getData();
                            Log.d("infoDebug", "Successfully registered application with token: " + token);
                            LoggedUser.getInstance().setToken(token);
                            LoggedUser.getInstance().saveToPrefs();
                            listener.onCodeVerified();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("infoDebug","Error sending register request (verification): " + error.toString());
                        progress.dismiss();
                        Snackbar.make(getActivity().findViewById(android.R.id.content),"You don't have internet connection.",Snackbar.LENGTH_INDEFINITE).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("verify", verify.toString());
                return params;
            }
        };
        MyVolley.getInstance().add(request);
    }

    public boolean validateCode() {
        return Pattern.compile("^\\d{6}$").matcher(verifyEdit.getText()).matches();
    }

}
