package com.kirsh.doc2family.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;
import com.kirsh.doc2family.R;
import com.kirsh.doc2family.logic.User;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
    }

    private void initViews(){
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
    }

    private void attemptLogin(){
        Snackbar.make(findViewById(android.R.id.content), "Login attempt!", Snackbar.LENGTH_SHORT).show();
        // todo login
        openActivityListPatients();
    }

    private void openActivitySignUp(){
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    private void openActivityListPatients(){
        // todo
//        User thisUser = new User();
        Intent intent = new Intent(this, ListPatientsActivity.class);
        startActivity(intent);
    }
}
