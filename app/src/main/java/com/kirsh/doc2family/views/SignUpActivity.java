package com.kirsh.doc2family.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.zzu;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kirsh.doc2family.logic.Communicator;
import com.kirsh.doc2family.logic.Constants;
import com.kirsh.doc2family.R;
import com.kirsh.doc2family.logic.User;


public class SignUpActivity extends AppCompatActivity {

    private static final String PASSWORD_DOESNT_MATCH_MESSAGE = "The passwords don't match!\nMake sure to enter the same password twice.";
    private static final String ILLEGAL_EMAIL_MESSAGE = "Enter a valid email!";
    private static final String ILLEGAL_NICKNAME_MESSAGE = "Enter a valid nickname! (Or just leave blank for now...)";
    private static final String ILLEGAL_PASSWORD_MESSAGE = "Enter a valid password!";

    private TextInputLayout mFirstNameLayout;
    private TextInputLayout mLastNameLayout;
    private TextInputLayout mEmailLayout;
    private TextInputLayout mPasswordLayout;
    private TextInputLayout mConfirmPasswordLayout;
    private EditText mFirstNameEditText;
    private EditText mLastNameEditText;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private EditText mVerifyPasswordEditText;
    private boolean mIsDoctor;
    private Button mSignUpButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initViews();
    }

    private void createUserWithEmailAndPassword() {
        String email = mEmailEditText.getText().toString().trim();
        String password = mPasswordEditText.getText().toString();
        final String firstName = mFirstNameEditText.getText().toString();
        final String lastName = mLastNameEditText.getText().toString();
        Communicator.cCreateUserWithEmailAndPassword(email, password, SignUpActivity.this, firstName, lastName, mIsDoctor,mEmailEditText, mPasswordEditText, mEmailLayout, mPasswordLayout);
    }

    private void initViews() {
        // layouts
        mFirstNameLayout = findViewById(R.id.input_text_first_name_layout);
        mLastNameLayout = findViewById(R.id.input_text_last_name_layout);
        mEmailLayout = findViewById(R.id.su_input_text_email_layout);
        mPasswordLayout = findViewById(R.id.su_input_text_password_layout);
        mConfirmPasswordLayout = findViewById(R.id.su_input_text_confirm_password_layout);

        // email EditText + info button
        mEmailEditText = findViewById(R.id.su_edit_text_email);
        addPopupOnClick((ImageButton) findViewById(R.id.image_button_email_info), Constants.EMAIL_INFO_MESSAGE);

        // first name EditText + info button
        mFirstNameEditText = findViewById(R.id.edit_text_first_name);
        addPopupOnClick((ImageButton) findViewById(R.id.image_button_first_name_info), Constants.FIRSTNAME_INFO_MESSAGE);

        // last name EditText + info button
        mLastNameEditText = findViewById(R.id.edit_text_last_name);
        addPopupOnClick((ImageButton) findViewById(R.id.image_button_last_name_info), Constants.LASTNAME_INFO_MESSAGE);

        // password EditText + info button
        mPasswordEditText = findViewById(R.id.su_edit_text_password);
        addPopupOnClick((ImageButton) findViewById(R.id.image_button_password_info), Constants.PASSWORD_INFO_MESSAGE);

        // repeat password EditText + info button
        mVerifyPasswordEditText = findViewById(R.id.su_edit_text_confirm_password);
        addPopupOnClick((ImageButton) findViewById(R.id.image_button_verify_password_info), Constants.VERIFY_PASSWORD_INFO_MESSAGE);

        // sign up button
        mSignUpButton = findViewById(R.id.button_sign_up);
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSignUp(v);
            }
        });

        // checkBox doctor and friend
        RadioButton checkDoctor = (RadioButton) findViewById(R.id.checkDoc);
        RadioButton checkFriend = (RadioButton) findViewById(R.id.checkFriend);

        checkDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsDoctor = ((RadioButton) v).isChecked();
            }
        });

        checkFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsDoctor = !((RadioButton) v).isChecked();
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

    private void setErrorLayoutNull(){
        mFirstNameLayout.setError(null);
        mLastNameLayout.setError(null);
        mEmailLayout.setError(null);
        mPasswordLayout.setError(null);
        mConfirmPasswordLayout.setError(null);
    }

    private boolean isLegalInput(View v) {
        // collect input
        String email = mEmailEditText.getText().toString();
        String first_name = mFirstNameEditText.getText().toString();
        String last_name = mLastNameEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        String repeatPassword = mVerifyPasswordEditText.getText().toString();

        setErrorLayoutNull();

        // check if first name is legal
        if (first_name.isEmpty() || !Constants.isLegalNickname(first_name)) {
            Snackbar.make(v, ILLEGAL_NICKNAME_MESSAGE, Snackbar.LENGTH_LONG).show();
            mFirstNameLayout.setError(" ");
            mFirstNameLayout.requestFocus();
            return false;
        }

        // check if last name is legal
        if (last_name.isEmpty() || !Constants.isLegalNickname(last_name)) {
            Snackbar.make(v, ILLEGAL_NICKNAME_MESSAGE, Snackbar.LENGTH_LONG).show();
            mLastNameLayout.setError(" ");
            mLastNameLayout.requestFocus();
            return false;
        }

        // check if email is legal
        if (!Constants.isLegalEmail(email)) {
            Snackbar.make(v, ILLEGAL_EMAIL_MESSAGE, Snackbar.LENGTH_LONG).show();
            mEmailLayout.setError(" ");
            mEmailLayout.requestFocus();
            return false;
        }

        // check if password is valid
        if (!Constants.isLegalPassword(password)) {
            Snackbar.make(v, ILLEGAL_PASSWORD_MESSAGE, Snackbar.LENGTH_LONG).show();
            mPasswordLayout.setError(" ");
            mPasswordLayout.requestFocus();
            return false;
        }

        // check if passwords match
        if (!password.equals(repeatPassword)) {
            Snackbar.make(v, PASSWORD_DOESNT_MATCH_MESSAGE, Snackbar.LENGTH_LONG).show();
            mPasswordLayout.requestFocus();
            mConfirmPasswordLayout.requestFocus();
            mPasswordLayout.setError(" ");
            mConfirmPasswordLayout.setError(" ");
            return false;
        }
        return true;
    }

    private void openActivityLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
