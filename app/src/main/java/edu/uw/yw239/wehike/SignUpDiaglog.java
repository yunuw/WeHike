package edu.uw.yw239.wehike;


import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


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
                registerNewUser(v);
            }
        });

        return view;
    }

    public void registerNewUser(View view) {
        if (!this.isValidInput()) {
            return;
        }

        // TODO: call API and see if register succeded

        // TODO: save userId and authentication token to internal storage

        // Update the user name in sign in activity
        Dialog diag = this.getDialog();
        EditText signupUserName = (EditText) diag.findViewById(R.id.sign_up_user_name);

        // Hard code the type of the containing activity
        SignInActivity act = (SignInActivity) getActivity();
        EditText signinUserName = (EditText) act.findViewById(R.id.input_user_name);
        signinUserName.setText(signupUserName.getText());

        this.dismiss();
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
