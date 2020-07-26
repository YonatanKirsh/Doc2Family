package com.kirsh.doc2family.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;
import com.kirsh.doc2family.Constants;
import com.kirsh.doc2family.R;


public class SignUpActivity extends AppCompatActivity {

    private static final String PASSWORD_DOESNT_MATCH_MESSAGE = "The passwords don't match!\nMake sure to enter the same password twice.";
    private static final String ILLEGAL_EMAIL_MESSAGE = "Enter a valid email!";
    private static final String ILLEGAL_NICKNAME_MESSAGE = "Enter a valid nickname! (Or just leave blank for now...)";
    private static final String ILLEGAL_PASSWORD_MESSAGE = "Enter a valid password!";

    private EditText mEmailEditText;
    private EditText mNicknameEditText;
    private EditText mPasswordEditText;
    private EditText mRepeatPasswordEditText;
    private Button mSignUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initViews();
    }

    private void initViews(){
        // email EditText
        mEmailEditText = findViewById(R.id.edit_text_set_email);

        // nickname EditText
        mNicknameEditText = findViewById(R.id.edit_text_set_nickname);

        // password EditText
        mPasswordEditText = findViewById(R.id.edit_text_set_password);

        // repeat password EditText
        mRepeatPasswordEditText = findViewById(R.id.edit_text_repeat_password);

        // sign up button
        mSignUpButton = findViewById(R.id.button_sign_up);
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSignUp(v);
            }
        });
    }

    private void attemptSignUp(View v){
        if (isLegalInput(v)){
            //todo signup
            finish();
        }
    }

    private boolean isLegalInput(View v){
        // collect input
        String email = mEmailEditText.getText().toString();
        String nickname = mNicknameEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        String repeatPassword = mRepeatPasswordEditText.getText().toString();

        // check if email is legal
        if (!Constants.isLegalEmail(email)){
            Snackbar.make(v, ILLEGAL_EMAIL_MESSAGE, Snackbar.LENGTH_LONG).show();
            return false;
        }

        // check if nickname is legal
        if (!nickname.isEmpty() && !Constants.isLegalNickname(nickname)){
            Snackbar.make(v, ILLEGAL_NICKNAME_MESSAGE, Snackbar.LENGTH_LONG).show();
            return false;
        }

        // check if password is valid
        if (!Constants.isLegalPassword(password)){
            Snackbar.make(v, ILLEGAL_PASSWORD_MESSAGE, Snackbar.LENGTH_LONG).show();
            return false;
        }

        // check if passwords match
        if (!password.equals(repeatPassword)){
            Snackbar.make(v, PASSWORD_DOESNT_MATCH_MESSAGE, Snackbar.LENGTH_LONG).show();
            return false;
        }

        return true;
    }



}
