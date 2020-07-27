package com.kirsh.doc2family.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

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
    private EditText mVerifyPasswordEditText;
    private Button mSignUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initViews();
    }

    private void initViews(){
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

    private void addPopupOnClick(final ImageButton button, final String message){
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
        String repeatPassword = mVerifyPasswordEditText.getText().toString();

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
