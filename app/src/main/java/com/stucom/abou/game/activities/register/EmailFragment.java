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
import android.widget.ProgressBar;


import alex_bou.stucom.com.alex.R;

import com.stucom.abou.game.model.AccessApi;

public class EmailFragment extends Fragment {

    private TextInputEditText emailEdit;
    private TextInputLayout textInputLayout;
    private EmailFragmentListener listener;

    interface EmailFragmentListener {
        void onVerificationSent(String email);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_email,container,false);
        emailEdit = v.findViewById(R.id.edit_email);
        textInputLayout = v.findViewById(R.id.text_input_layout);
        Button submitButton = v.findViewById(R.id.button_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (validateEmail() && emailEdit.getText() != null)
                            sendVerificationCode(emailEdit.getText().toString());
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

    private void sendVerificationCode(final String email) {
        final ProgressDialog progress = ProgressDialog.show(getActivity(),null,null);
        progress.setContentView(new ProgressBar(getActivity()));
        progress.setCancelable(false);
        progress.show();
        AccessApi.getInstance().registerEmail(new AccessApi.ApiListener<Integer>() {
            @Override
            public void onResult(AccessApi.Result result, @Nullable Integer data) {
                progress.dismiss();
                switch (result) {
                    case OK:
                        listener.onVerificationSent(email);
                        break;
                    case ERROR_CONNECTION:
                        if (getActivity() != null)
                            Snackbar.make(getActivity().findViewById(android.R.id.content),"Could not connect to the server.",Snackbar.LENGTH_INDEFINITE)
                                    .setAction("Retry", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) { sendVerificationCode(email);
                                        }
                                    });
                        break;
                    case GENERIC_ERROR:
                        if (getActivity() != null)
                            Snackbar.make(getActivity().findViewById(android.R.id.content),"There was an error.",Snackbar.LENGTH_LONG)
                                    .setAction("Retry", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) { sendVerificationCode(email);
                                    }
                                });
                        break;
                }
            }
        }, email);
    }

}
