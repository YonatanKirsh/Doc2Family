package com.kirsh.doc2family.views;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.google.android.material.textfield.TextInputLayout;
import com.kirsh.doc2family.R;
import com.kirsh.doc2family.logic.Communicator;
import com.kirsh.doc2family.logic.Constants;

import static com.kirsh.doc2family.logic.Communicator.getUsersPatients;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout email_layout;
    private TextInputLayout password_layout;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button signUpButton;
    private Button forgotP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();

        //todo remove openActivity!!
//        openActivityListPatients();
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
        Communicator.cSignInWithEmailAndPassword(email, password, this);
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
