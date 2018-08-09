package codepath.com.gitreccedproject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UpdateInfoDialog extends DialogFragment{
    EditText etEmail;
    EditText etPassword;
    TextView tvPrompt;

    String email;
    String password;
    String username;

    Button btnEnter;
    Activity mActivity;
    int mcode;

    DatabaseReference dbUsers;

    public UpdateInfoDialog () {
        //empty constructor
    }

    @SuppressLint("ValidFragment")
    public UpdateInfoDialog(Activity activity, int code) {
        mActivity = activity;
        mcode = code;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.update_info_dialog, null);

        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        dbUsers = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());

        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        tvPrompt = view.findViewById(R.id.tvPrompt);

        btnEnter = (Button) view.findViewById(R.id.btnEnter);

        if (mcode == 0) {
            btnEnter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("MyDialogFragment", "Enter Selected");

                    email = etEmail.getText().toString();
                    password = etPassword.getText().toString();

                    if (email == null || email.equals("") || password == null || password.equals("")) {
                        //if email or password is empty, don't authenticate and toast error
                        Toast.makeText(getActivity(), "Incorrect! Please try again.", Toast.LENGTH_LONG).show();
                    } else {
                        //if email and password are both not empty, reauthenticate

                        // Get auth credentials from the user for re-authentication.
                        AuthCredential credential = EmailAuthProvider.getCredential(email,
                                password);

                        // Prompt the user to re-provide their sign-in credentials
                        currentUser.reauthenticate(credential)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            dismiss();

                                            Intent intent = new Intent(mActivity, UpdateInfoActivity.class);
                                            startActivity(intent);
                                            mActivity.finish();

                                            //TODO: get username to welcome specific user to account
                                            Toast.makeText(getActivity(), "Welcome!", Toast.LENGTH_LONG).show();
                                            Log.i("ReAuth", "Welcome, bitch!");
                                        } else {
                                            Toast.makeText(getActivity(), "Incorrect! Please try again.", Toast.LENGTH_LONG).show();
                                            Log.i("ReAuth", "Denied, bitch!");
                                        }
                                    }
                                });
                    }
                }
            });
        } else if (mcode == 1) {
            etPassword.setVisibility(View.GONE);
            tvPrompt.setText("Enter a new username");

            etEmail.setHint("Username");
            btnEnter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    username = etEmail.getText().toString();

                    if (TextUtils.getTrimmedLength(username) > 0) {
                        dismiss();
                        dbUsers.child("username").setValue(username);
                        LoginActivity.currentuser.setUsername(username);
                        mDialogResult.finish(String.valueOf(username));
                        Log.d("update", "User profile updated.");
                        Toast.makeText(mActivity, "Successfully updated username!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mActivity, "Please enter a username!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else if (mcode == 2) {
            etPassword.setVisibility(View.GONE);
            tvPrompt.setText("Enter a new email");

            etEmail.setHint("Email");
            btnEnter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    email = etEmail.getText().toString();

                    if (TextUtils.getTrimmedLength(email) > 0) {
                        currentUser.updateEmail(email)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            dbUsers.child("email").setValue(email);
                                            LoginActivity.currentuser.setEmail(email);
                                            mDialogResult.finish(String.valueOf(email));
                                            dismiss();
                                            Log.d("update", "User email address updated.");
                                            Toast.makeText(getContext(), "Successfully updated email!", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(getContext(), "failed", Toast.LENGTH_LONG).show();
                                            Log.d("update", "failed.");
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(getContext(), "Please enter a email!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else if (mcode == 3) {
            etEmail.setHint("Password");
            etPassword.setHint("Confirm password");
            etEmail.setTransformationMethod(PasswordTransformationMethod.getInstance());
            tvPrompt.setText("Enter a new password");

            btnEnter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    password = etEmail.getText().toString();
                    String confirmpassword = etPassword.getText().toString();

                    if (!password.equals(confirmpassword)) {
                        Toast.makeText(getContext(), "Passwords don't match!", Toast.LENGTH_LONG).show();
                        Log.i("password",password);
                        Log.i("password1", confirmpassword);
                    } else if (TextUtils.getTrimmedLength(password) > 0) {
                        currentUser.updatePassword(password)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            dbUsers.child("password").setValue(password);
                                            LoginActivity.currentuser.setPassword(password);
                                            dismiss();
                                            Log.d("update", "User password updated.");
                                            Toast.makeText(getContext(), "Successfully updated password!", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(getContext(), "failed", Toast.LENGTH_LONG).show();
                                            Log.d("update", "failed.");
                                        }
                                    }
                                });
                    } else if (TextUtils.getTrimmedLength(password) == 0) {
                        Toast.makeText(getContext(), "Please enter a password!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        return view;
    }

    OnMyDialogResult mDialogResult;
    public void setDialogResult(OnMyDialogResult dialogResult){
        mDialogResult = dialogResult;
    }

    public interface OnMyDialogResult{
        void finish(String result);
    }
}
