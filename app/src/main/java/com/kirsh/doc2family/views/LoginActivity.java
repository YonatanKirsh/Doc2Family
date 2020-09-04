package com.kirsh.doc2family.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.kirsh.doc2family.R;
import com.kirsh.doc2family.logic.Constants;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout email_layout;
    private TextInputLayout password_layout;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button signUpButton;
    private Button forgotP;

    private FirebaseAuth Auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //todo remove openActivity!!
        openActivityListPatients();

        initViews();

        // Initialize Firebase Auth
        Auth = FirebaseAuth.getInstance();
    }

    private void initViews(){
        // email layout
        email_layout = findViewById(R.id.input_text_email_layout);

        // password layout
        password_layout = findViewById(R.id.input_text_password_layout);

        // email EditText
        emailEditText = findViewById(R.id.edit_text_email);

        // password EditText
        passwordEditText = findViewById(R.id.edit_text_password);

        // login button
        loginButton = findViewById(R.id.button_login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        // sign up button
        signUpButton = findViewById(R.id.button_goto_sign_up);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivitySignUp();
            }
        });

        //forgot pass button
        forgotP = findViewById(R.id.forgotP);
        forgotP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivityForgotPassword();
            }
        });
    }

    private void attemptLogin(){
        //todo remove openActivity and attempt-message when signup&login work
        //openActivityListPatients();
        if (checkEmailAndPasswordValidity()){
            Snackbar.make(findViewById(android.R.id.content), "Login attempt!", Snackbar.LENGTH_SHORT).show();
            signInWithEmailAndPassword();
        };
    }

    private boolean checkEmailAndPasswordValidity(){
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        email_layout.setError(null);
        password_layout.setError(null);

        if (TextUtils.isEmpty(email)){
            email_layout.setError("Email is required.");
            emailEditText.requestFocus();
            return false;
        }
        if (!Constants.isLegalEmail(email)){
            email_layout.setError("Email is not valid.");
            emailEditText.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(password)){
            password_layout.setError("Password is required.");
            passwordEditText.requestFocus();
            return false;
        }
        return true;
    }

    private void signInWithEmailAndPassword() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        Auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            if (Auth.getCurrentUser().isEmailVerified()){
                                Log.d("SIGN_IN_SUCCESS", "signInWithEmail: success");
                                openActivityListPatients();
                            }
                            else{
                                Toast.makeText(LoginActivity.this, "Please verify your email address.", Toast.LENGTH_LONG).show();
                            }
                            //FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("SIGN_IN_ERR", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                            // ...
                        }
                    }
                });
    }

    private void openActivitySignUp(){
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    private void openActivityListPatients(){
        // todo
//        User thisUser = new User();
        Intent intent = new Intent(this, PatientsListActivity.class);
        startActivity(intent);
    }

    private void openActivityForgotPassword(){
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(intent);
    }
}
