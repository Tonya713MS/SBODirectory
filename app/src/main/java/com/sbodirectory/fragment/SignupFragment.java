package com.sbodirectory.fragment;

import android.annotation.SuppressLint;
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
import android.widget.EditText;
import android.widget.Toast;

import com.sbodirectory.CompanyListActivity;
import com.sbodirectory.R;
import com.sbodirectory.controller.UserControler;
import com.sbodirectory.model.MyResponse;
import com.sbodirectory.model.User;
import com.sbodirectory.util.Utils;


@SuppressLint("CutPasteId") public class SignupFragment extends Fragment {

	private Button btnLogin;
	private Button btnSignup;
	UserControler userControler = new UserControler();
	private static User user = new User();
	private static MyResponse myResponse = new MyResponse();
    public SignupFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.signup_fragment, container, false);
        btnLogin = (Button) rootView.findViewById(R.id.login_help);
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
        
        final EditText inputUsername = (EditText) rootView.findViewById(R.id.signup_username_input);
        final EditText inputPassword = (EditText) rootView.findViewById(R.id.signup_password_input);
        final EditText inputEmail = (EditText) rootView.findViewById(R.id.signup_email_input);
        final EditText inputRePassword = (EditText) rootView.findViewById(R.id.signup_confirm_password_input);
        final EditText inputName = (EditText) rootView.findViewById(R.id.signup_name_input);
        btnSignup = (Button) rootView.findViewById(R.id.create_account);
        btnSignup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String password = inputPassword.getText().toString();
				String rePassword = inputRePassword.getText().toString();
				
				if(password.equals(rePassword)){
					user.setUserName(inputUsername.getText().toString());
					user.setPassword(inputPassword.getText().toString());
					user.setName(inputName.getText().toString());
					user.setEmail(inputEmail.getText().toString());
					if (!Utils.isUsernameValid(user.getUserName())){
						Toast.makeText(getActivity(), "Username is invalid!", Toast.LENGTH_LONG).show();
					}else if (!Utils.isEmailValid(user.getEmail())){
						Toast.makeText(getActivity(), "Email is invalid!", Toast.LENGTH_LONG).show();
					}else if(password.length()< 6 ){
						Toast.makeText(getActivity(), "Password is too short!", Toast.LENGTH_LONG).show();
					}else
						new SignUpTask().execute(user);
				} else {
					Toast.makeText(getActivity(), "Passwords do not match", Toast.LENGTH_LONG).show();
				}
				
			}
		});
        return rootView;
    }
    class SignUpTask extends AsyncTask<User, Void, Integer> {
		ProgressDialog dialogSave;
		@Override
		protected Integer doInBackground(User... value) {
			myResponse = userControler.signUp(user);;
			return 1;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialogSave = new ProgressDialog(getActivity());
            dialogSave.setCanceledOnTouchOutside(false);
			dialogSave.setMessage("Loading...");
			dialogSave.show();
		}

		@Override
		protected void onPostExecute(Integer id) {
			super.onPostExecute(id);
			
			if (null != dialogSave && dialogSave.isShowing()) {
				dialogSave.setMessage("Succeed!");
				dialogSave.dismiss();
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