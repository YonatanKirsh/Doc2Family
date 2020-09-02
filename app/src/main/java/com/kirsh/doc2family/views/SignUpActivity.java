package com.kirsh.doc2family.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kirsh.doc2family.logic.Constants;
import com.kirsh.doc2family.R;
import com.kirsh.doc2family.logic.User;


public class SignUpActivity extends AppCompatActivity {

    private static final String PASSWORD_DOESNT_MATCH_MESSAGE = "The passwords don't match!\nMake sure to enter the same password twice.";
    private static final String ILLEGAL_EMAIL_MESSAGE = "Enter a valid email!";
    private static final String ILLEGAL_NICKNAME_MESSAGE = "Enter a valid nickname! (Or just leave blank for now...)";
    private static final String ILLEGAL_PASSWORD_MESSAGE = "Enter a valid password!";

    private EditText mEmailEditText;
    private EditText mNicknameEditText;
    private EditText mPasswordEditText;
    private EditText mVerifyPasswordEditText;
    private Button mSignUpButton;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initViews();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    private void createUserWithEmailAndPassword() {
        String email = mEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        final String name = mNicknameEditText.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("SIGN_UP_SUCCESS", "createUserWithEmail:success");
                            Toast.makeText(SignUpActivity.this, "Registration succeed!",
                                    Toast.LENGTH_SHORT).show();
                            mAuth.getCurrentUser().sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                               @Override
                                                               public void onComplete(@NonNull Task<Void> task) {
                                                                   if (task.isSuccessful()) {
                                                                       Toast.makeText(SignUpActivity.this, "Registration succeed!. Please check your email for verification.",
                                                                               Toast.LENGTH_SHORT).show();
                                                                       mEmailEditText.setText("");
                                                                       mPasswordEditText.setText("");
                                                                   } else {
                                                                       Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                                   }
                                                               }
                                                           }
                                    );
                            FirebaseUser user_auth = mAuth.getCurrentUser();
                            User newUser = new User(user_auth.getEmail(), name, user_auth.getUid());
                            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                            DocumentReference document = firestore.collection("Users").document();
                            document.set(newUser);
                            openActivityLogin();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("SIGN_UP_ERR", "createUserWithEmail: failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Registration failed!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void initViews() {
        // email EditText + info button
        mEmailEditText = findViewById(R.id.edit_text_set_email);
        addPopupOnClick((ImageButton) findViewById(R.id.image_button_email_info), Constants.EMAIL_INFO_MESSAGE);

        // nickname EditText + info button
        mNicknameEditText = findViewById(R.id.edit_text_set_nickname);
        addPopupOnClick((ImageButton) findViewById(R.id.image_button_nickname_info), Constants.NICKNAME_INFO_MESSAGE);

        // password EditText + info button
        mPasswordEditText = findViewById(R.id.edit_text_set_password);
        addPopupOnClick((ImageButton) findViewById(R.id.image_button_password_info), Constants.PASSWORD_INFO_MESSAGE);

        // repeat password EditText + info button
        mVerifyPasswordEditText = findViewById(R.id.edit_text_verify_password);
        addPopupOnClick((ImageButton) findViewById(R.id.image_button_verify_password_info), Constants.VERIFY_PASSWORD_INFO_MESSAGE);

        // sign up button
        mSignUpButton = findViewById(R.id.button_sign_up);
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSignUp(v);
            }
        });
    }

    private void addPopupOnClick(final ImageButton button, final String message) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                builder.setMessage(message);
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    private void attemptSignUp(View v) {
        if (isLegalInput(v)) {
            //todo signup
            createUserWithEmailAndPassword();
            //finish();
        }
    }

    private boolean isLegalInput(View v) {
        // collect input
        String email = mEmailEditText.getText().toString();
        String nickname = mNicknameEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        String repeatPassword = mVerifyPasswordEditText.getText().toString();

        // check if email is legal
        if (!Constants.isLegalEmail(email)) {
            Snackbar.make(v, ILLEGAL_EMAIL_MESSAGE, Snackbar.LENGTH_LONG).show();
            return false;
        }

        // check if nickname is legal
        if (!nickname.isEmpty() && !Constants.isLegalNickname(nickname)) {
            Snackbar.make(v, ILLEGAL_NICKNAME_MESSAGE, Snackbar.LENGTH_LONG).show();
            return false;
        }

        // check if password is valid
        if (!Constants.isLegalPassword(password)) {
            Snackbar.make(v, ILLEGAL_PASSWORD_MESSAGE, Snackbar.LENGTH_LONG).show();
            return false;
        }

        // check if passwords match
        if (!password.equals(repeatPassword)) {
            Snackbar.make(v, PASSWORD_DOESNT_MATCH_MESSAGE, Snackbar.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void openActivityLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
