package com.sbodirectory.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sbodirectory.CompanyListActivity;
import com.sbodirectory.R;
import com.sbodirectory.controller.UserControler;
import com.sbodirectory.model.MyResponse;
import com.sbodirectory.model.User;
import com.sbodirectory.util.Utils;


public class LoginFragment extends Fragment {
	private Button btnLogin;
	private Button btnSignup;
	private TextView tvUsername;
	private TextView tvPassword;
	UserControler userControler = new UserControler();
	private static User user = new User();
	private static MyResponse myResponse = new MyResponse();
    public LoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.login_fragment, container, false);
        tvUsername = (TextView)rootView.findViewById(R.id.login_username_input);
        tvPassword = (TextView)rootView.findViewById(R.id.login_password_input);
        btnSignup = (Button) rootView.findViewById(R.id.signup_button);
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
        
        btnLogin = (Button) rootView.findViewById(R.id.login_button);
        btnLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				user.setUserName(tvUsername.getText().toString());
				user.setPassword(tvPassword.getText().toString());
				new LoginTask().execute(user);
			}
		});
        return rootView;
    }
    
    class LoginTask extends AsyncTask<User, Void, Integer> {
		ProgressDialog dialogLoad;
		@Override
		protected Integer doInBackground(User... value) {
			myResponse = userControler.logIn(user);;
			return 1;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialogLoad = new ProgressDialog(getActivity());
			dialogLoad.setMessage("Loading...");
            dialogLoad.setCanceledOnTouchOutside(false);
			dialogLoad.show();
		}

		@Override
		protected void onPostExecute(Integer id) {
			super.onPostExecute(id);
			
			if (null != dialogLoad && dialogLoad.isShowing()) {
				dialogLoad.setMessage("Succeed!");
				dialogLoad.dismiss();
			}
			if(myResponse.isError()){
				Toast.makeText(getActivity(), myResponse.getMessage(), Toast.LENGTH_LONG).show();
			}else {
				Toast.makeText(getActivity(), "Login succeed", Toast.LENGTH_LONG).show();
                Utils.setLogin(getActivity(), true);
                Intent myIntent = new Intent(getActivity(), CompanyListActivity.class);
                getActivity().startActivity(myIntent);
                getActivity().finish();
			}
			
		}
	}
}
