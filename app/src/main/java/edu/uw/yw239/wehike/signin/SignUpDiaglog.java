package edu.uw.yw239.wehike.signin;


import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.yw239.wehike.R;
import edu.uw.yw239.wehike.common.RequestSingleton;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignUpDiaglog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignUpDiaglog extends DialogFragment {
    public SignUpDiaglog() {
        // Required empty public constructor
    }

    public static SignUpDiaglog newInstance() {
        SignUpDiaglog fragment = new SignUpDiaglog();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up_diaglog, container, false);
        Button btn = view.findViewById(R.id.sign_up);
        btn.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                InputMethodManager imm =
                        (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }

                registerNewUser(v);
            }
        });

        return view;
    }

    public void registerNewUser(View view) {
        if (!this.isValidInput()) {
            return;
        }

        Dialog diag = this.getDialog();
        EditText signupUserName = (EditText) diag.findViewById(R.id.sign_up_user_name);
        EditText signupPassword = (EditText) diag.findViewById(R.id.sign_up_pwd);
        EditText signupEmail = (EditText) diag.findViewById(R.id.sign_up_email);
        final String userName = signupUserName.getText().toString();
        final String password = signupPassword.getText().toString();
        final String email = signupEmail.getText().toString();
        final Resources resources = this.getActivity().getResources();
        final String backendPrefix = resources.getString(R.string.backend_prefix);

        // call API and register new user
        String urlString = String.format("%s/users/create?userName=%s&password=%s&email=%s", backendPrefix, userName, password, email);
        Request request = new JsonObjectRequest(Request.Method.POST, urlString, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        boolean success = response.getBoolean("success");

                        if (success) {
                            Toast.makeText(SignUpDiaglog.this.getActivity(), resources.getString(R.string.signup_succeed), Toast.LENGTH_SHORT).show();

                            // Update the user name and password in sign in activity
                            SignInActivity act = (SignInActivity) getActivity();
                            EditText signinUserName = (EditText) act.findViewById(R.id.input_user_name);
                            signinUserName.setText(userName);
                            EditText signinPassword = (EditText) act.findViewById(R.id.input_pwd);
                            signinPassword.setText(password);

                            dismiss();
                        } else {
                            String msg = response.getString("message");
                            Toast.makeText(SignUpDiaglog.this.getActivity(), msg, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(SignUpDiaglog.this.getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String errMsg = "Sign up failed!";
                    if (error.networkResponse != null) {
                        errMsg = "Status code: " + error.networkResponse.statusCode + "\n" + new String(error.networkResponse.data);
                    }
                    Toast.makeText(SignUpDiaglog.this.getActivity(), errMsg, Toast.LENGTH_LONG).show();
                }
            }
        );

        RequestSingleton.getInstance(this.getActivity()).add(request);
    }

    private boolean isValidInput() {
        Dialog diag = this.getDialog();
        EditText signupUserName = (EditText) diag.findViewById(R.id.sign_up_user_name);
        EditText signupEmail = (EditText) diag.findViewById(R.id.sign_up_email);
        EditText signupPwd = (EditText) diag.findViewById(R.id.sign_up_pwd);
        EditText signupConfirmPwd = (EditText) diag.findViewById(R.id.sign_up_confirm_pwd);

        String userName = signupUserName.getText().toString();
        String email = signupEmail.getText().toString();
        String pwd = signupPwd.getText().toString();
        String confirmPwd = signupConfirmPwd.getText().toString();

        if (userName.equals("")) {
            Toast.makeText(getActivity(), R.string.user_name_null, Toast.LENGTH_SHORT).show();
            return false;
        } else if (email.equals("")) {
            Toast.makeText(getActivity(), R.string.email_null, Toast.LENGTH_SHORT).show();
            return false;
        } else if (pwd.equals("")) {
            Toast.makeText(getActivity(), R.string.password_null, Toast.LENGTH_SHORT).show();
            return false;
        } else if (confirmPwd.equals("")) {
            Toast.makeText(getActivity(), R.string.confirm_password_null, Toast.LENGTH_SHORT).show();
            return false;
        } else if (!pwd.equals(confirmPwd)) {
            Toast.makeText(getActivity(), R.string.pwd_not_match, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
