package com.stucom.abou.game.activities.register;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.stucom.abou.game.utils.App;

import alex_bou.stucom.com.alex.R;

public class RegisterActivity extends AppCompatActivity implements VerifyFragment.VerifyFragmentListener, EmailFragment.EmailFragmentListener {

    protected final static String REGISTER_URL = "https://api.flx.cat/dam2game/register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showSnackbarIfConnectedElseFragment();
    }

    @Override
    public void onVerificationSent(String email) {
        changeFragment(VerifyFragment.newInstance(email),true);
    }

    @Override
    public void onCodeVerified() {
        setResult(1,new Intent());
        finish();
    }

    @Override
    public void onChangeEmail() {
        changeFragment(new EmailFragment(),false);
    }


    private void changeFragment(Fragment fragment, boolean addBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.fragment_container, fragment);
        if  (addBackStack)
            transaction.addToBackStack(null);
        transaction.commit();
    }


    private void showSnackbarIfConnectedElseFragment() {
        if (App.isOnline()) {
            changeFragment(new EmailFragment(),false);
        } else
            getSnackbar().show();
    }


    private Snackbar getSnackbar() {
        return Snackbar.make(findViewById(android.R.id.content), "You don't have internet connection.", Snackbar.LENGTH_INDEFINITE)
                .setAction("Try Again", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showSnackbarIfConnectedElseFragment();
                            }
                        });
    }
}
