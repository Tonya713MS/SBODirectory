package com.sbodirectory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.sbodirectory.fragment.LoginFragment;
import com.sbodirectory.fragment.WelcomeFragment;
import com.sbodirectory.util.Utils;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        boolean isLogined = Utils.isLogined(getApplicationContext());
        if (isLogined) {
            Intent myIntent = new Intent(getApplicationContext(), CompanyListActivity.class);
            startActivity(myIntent);
            finish();
        }
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new LoginFragment())
                    .commit();
        }
    }
}
