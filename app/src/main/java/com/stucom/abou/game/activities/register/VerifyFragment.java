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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.stucom.abou.game.rest.AccessApi;

import java.util.regex.Pattern;

import alex_bou.stucom.com.alex.R;

public class VerifyFragment extends Fragment {

    private TextInputEditText verifyEdit;
    private TextInputLayout textInputLayout;
    private VerifyFragmentListener listener;
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
        LinearLayout rootView = v.findViewById(R.id.fragment_verify);
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
                        if (verifyEdit.getText() != null && validateCode())
                            verify(verifyEdit.getText().toString());
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

    private void verify(final String verifyCode) {
        final ProgressDialog progress = ProgressDialog.show(getActivity(),null,null);
        progress.setContentView(new ProgressBar(getActivity()));
        progress.setCancelable(false);
        progress.show();
        AccessApi.getInstance().verifyEmail(new AccessApi.ApiListener<String>() {
            @Override
            public void onResult(AccessApi.Result result, @Nullable String data) {
                boolean generic = false;
                switch (result) {
                    case OK:
                        listener.onCodeVerified();
                        break;
                    case GENERIC_ERROR:
                        generic = true;
                    case ERROR_CONNECTION:
                        if (getActivity() != null)
                            Snackbar.make(getActivity().findViewById(android.R.id.content), generic ?  "There was an error" :"Can't connect to the server.",Snackbar.LENGTH_INDEFINITE)
                                    .setAction("Retry", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {  verify(verifyCode);  }
                                    }).show();
                        break;
                    case ERROR_VERIFY:
                        if (getActivity() != null)
                            Snackbar.make(getActivity().findViewById(android.R.id.content),"Invalid code, please try again.",Snackbar.LENGTH_LONG).show();
                        break;
                }
                progress.dismiss();
            }
        }, email, verifyCode);
    }

    public boolean validateCode() {
        return Pattern.compile("^\\d{6}$").matcher(verifyEdit.getText()).matches();
    }

}
