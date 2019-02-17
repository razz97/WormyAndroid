package com.stucom.abou.game.activities.register;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import alex_bou.stucom.com.alex.R;

import com.stucom.abou.game.model.LoggedUser;
import com.stucom.abou.game.utils.MyVolley;

public class EmailFragment extends Fragment {

    private TextInputEditText emailEdit;
    private TextInputLayout textInputLayout;
    private EmailFragmentListener listener;
    private LinearLayout rootView;

    interface EmailFragmentListener {
        void onVerificationSent(String email);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_email,container,false);
        emailEdit = v.findViewById(R.id.edit_email);
        textInputLayout = v.findViewById(R.id.text_input_layout);
        rootView = v.findViewById(R.id.fragment_email);
        Button submitButton = v.findViewById(R.id.button_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (validateEmail())
                            sendVerificationCode(emailEdit.getText());
                        else
                            textInputLayout.setError("Invalid email.");
                    }
                }
        );
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof EmailFragmentListener)
            listener = (EmailFragmentListener) context;
        else
            throw new RuntimeException(context.toString() + " must implement EmailFragmentListener");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    private boolean validateEmail() {
        return Patterns.EMAIL_ADDRESS.matcher(emailEdit.getText()).matches();
    }

    private void sendVerificationCode(final CharSequence email) {
        final ProgressDialog progress = ProgressDialog.show(getActivity(),null,null);
        progress.setContentView(new ProgressBar(getActivity()));
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
        StringRequest request = new StringRequest(Request.Method.POST, RegisterActivity.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();
                        listener.onVerificationSent(email.toString());
                        LoggedUser.getInstance().setEmail(email.toString());
                        LoggedUser.getInstance().saveToPrefs();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.dismiss();
                        Snackbar.make(EmailFragment.this.getActivity().findViewById(android.R.id.content),"You don't have internet connection.",Snackbar.LENGTH_INDEFINITE);                    }
                })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email.toString());
                return params;
            }
        };
        MyVolley.getInstance().add(request);
    }
}
