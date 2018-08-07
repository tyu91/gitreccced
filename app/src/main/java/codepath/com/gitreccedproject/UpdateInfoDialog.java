package codepath.com.gitreccedproject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UpdateInfoDialog extends DialogFragment{
    EditText etEmail;
    EditText etPassword;

    String email;
    String password;

    Button btnEnter;
    Activity mActivity;

    public UpdateInfoDialog () {
        //empty constructor
    }

    @SuppressLint("ValidFragment")
    public UpdateInfoDialog(Activity activity) {
        mActivity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mydialog, null);

        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);

        btnEnter = (Button) view.findViewById(R.id.btnEnter);
        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("MyDialogFragment", "Enter Selected");

                final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                email = etEmail.getText().toString();
                password = etPassword.getText().toString();

                if (email == null || email.equals("") || password == null || password.equals("")) {
                    //if email or password is empty, don't authenticate and toast error
                    Toast.makeText(getActivity(), "Incorrect! Please try again.", Toast.LENGTH_SHORT).show();
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

                                        //TODO: get username to welcome specific user to account
                                        Toast.makeText(getActivity(), "Welcome!", Toast.LENGTH_SHORT).show();
                                        Log.i("ReAuth", "Welcome, bitch!");
                                    } else {
                                        Toast.makeText(getActivity(), "Incorrect! Please try again.", Toast.LENGTH_SHORT).show();
                                        Log.i("ReAuth", "Denied, bitch!");
                                    }
                                }
                            });
                }
            }
        });

        return view;
    }
}
