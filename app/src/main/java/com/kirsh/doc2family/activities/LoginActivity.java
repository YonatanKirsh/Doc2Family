package com.kirsh.doc2family.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;
import com.kirsh.doc2family.R;

public class LoginActivity extends AppCompatActivity {

    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private Button mLoginButton;
    private Button mSignUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
    }

    private void initViews(){
        // email EditText
        mEmailEditText = findViewById(R.id.edit_text_email);

        // password EditText
        mPasswordEditText = findViewById(R.id.edit_text_password);

        // login button
        mLoginButton = findViewById(R.id.button_login);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        // sign up button
        mSignUpButton = findViewById(R.id.button_goto_sign_up);
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivitySignUp();
            }
        });
    }

    private void attemptLogin(){
        Snackbar.make(findViewById(android.R.id.content), "Login attempt!", Snackbar.LENGTH_SHORT).show();
    }

    private void openActivitySignUp(){
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }
}
