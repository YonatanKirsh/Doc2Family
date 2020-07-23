package com.kirsh.doc2family.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.kirsh.doc2family.R;

public class LoginActivity extends AppCompatActivity {

    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private Button mLoginButton;
    private boolean wasUsernameEdited = false;
    private boolean wasPasswordEdited = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
    }

    private void initViews(){
        // username EditText
        mUsernameEditText = findViewById(R.id.edit_text_username);

        // password EditText
        mPasswordEditText = findViewById(R.id.edit_text_password);

        // login button
        mLoginButton = findViewById(R.id.button_login);

    }

    private boolean isLegalUsername(){
        // check if was edited, then if legal
        return false;
    }

    private boolean isLegalPassword(){
        // check if was edited, then if legal
        return false;
    }
}
