package com.sbodirectory.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.sbodirectory.CompanyListActivity;
import com.sbodirectory.R;
import com.sbodirectory.controller.UserControler;
import com.sbodirectory.model.MyResponse;
import com.sbodirectory.util.Utils;

public class WelcomeFragment extends Fragment {
	private Button btnLogin;
    private Button btnSignup;
    public WelcomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_welcome, container, false);
        boolean isLogined = Utils.isLogined(getActivity());
        if (isLogined) {
            Intent myIntent = new Intent(getActivity(), CompanyListActivity.class);
            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            getActivity().startActivity(myIntent);
            getActivity().finish();
        }
        btnLogin = (Button) rootView.findViewById(R.id.login_wc);
        btnLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentTransaction ft = getActivity().getFragmentManager()
						.beginTransaction();
				ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_in_right);
				ft.replace(R.id.container, new LoginFragment())
				.commit();
			}
		});

        btnSignup = (Button) rootView.findViewById(R.id.signup_wc);
        btnSignup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getActivity().getFragmentManager()
                        .beginTransaction();
                ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_in_right);
                ft.replace(R.id.container, new SignupFragment())
                        .commit();
            }
        });
        return rootView;
    }
}
