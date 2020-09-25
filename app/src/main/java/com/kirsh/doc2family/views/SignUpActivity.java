package com.kirsh.doc2family.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kirsh.doc2family.logic.Communicator;
import com.kirsh.doc2family.logic.Constants;
import com.kirsh.doc2family.R;
import com.kirsh.doc2family.logic.DBCallBackTZ;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SignUpActivity extends AppCompatActivity {

    private static final String PASSWORD_DOESNT_MATCH_MESSAGE = "The passwords don't match!\nMake sure to enter the same password twice.";
    private static final String ILLEGAL_EMAIL_MESSAGE = "Enter a valid email!";
    private static final String ILLEGAL_NICKNAME_MESSAGE = "Enter a valid nickname! (Or just leave blank for now...)";
    private static final String ILLEGAL_PASSWORD_MESSAGE = "Enter a valid password!";
    private static final String ILLEGAL_TZ_MESSAGE = "Enter a valid ID number!";


    private TextInputLayout mFirstNameLayout;
    private TextInputLayout mLastNameLayout;
    private TextInputLayout mTzLayout;
    private TextInputLayout mEmailLayout;
    private TextInputLayout mPasswordLayout;
    private TextInputLayout mConfirmPasswordLayout;

    private EditText mFirstNameEditText;
    private EditText mLastNameEditText;
    private EditText mTzEditText;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private EditText mVerifyPasswordEditText;
    private CheckBox mCaregiverCheckbox;
    private Button mSignUpButton;

    private View v;


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
        final boolean isCaregiver = mCaregiverCheckbox.isChecked();
        final String tz = mTzEditText.getText().toString();
        Communicator.cCreateUserWithEmailAndPassword(email, password, SignUpActivity.this, firstName, lastName, isCaregiver,mEmailEditText, mPasswordEditText, mEmailLayout, mPasswordLayout, tz);
    }

    private void initViews() {
        // layouts
        mFirstNameLayout = findViewById(R.id.input_text_first_name_layout);
        mLastNameLayout = findViewById(R.id.input_text_last_name_layout);
        mTzLayout = findViewById(R.id.input_text_tz_layout);
        mEmailLayout = findViewById(R.id.su_input_text_email_layout);
        mPasswordLayout = findViewById(R.id.su_input_text_password_layout);
        mConfirmPasswordLayout = findViewById(R.id.su_input_text_confirm_password_layout);

        // email EditText + info button
        mEmailEditText = findViewById(R.id.su_edit_text_email);
        addPopupOnClick((ImageButton) findViewById(R.id.image_button_email_info), this.getString(R.string.email_info_message));

        // first name EditText + info button
        mFirstNameEditText = findViewById(R.id.edit_text_first_name);
        addPopupOnClick((ImageButton) findViewById(R.id.image_button_first_name_info), this.getString(R.string.first_name_info_message));

        // last name EditText + info button
        mLastNameEditText = findViewById(R.id.edit_text_last_name);
        addPopupOnClick((ImageButton) findViewById(R.id.image_button_last_name_info), this.getString(R.string.last_name_info_message));

        // tz EditText + info button
        mTzEditText = findViewById(R.id.edit_text_tz);
        addPopupOnClick((ImageButton) findViewById(R.id.image_button_tz_info), this.getString(R.string.tz_info_message));


        // password EditText + info button
        mPasswordEditText = findViewById(R.id.su_edit_text_password);
        addPopupOnClick((ImageButton) findViewById(R.id.image_button_password_info), this.getString(R.string.password_info_message));

        // repeat password EditText + info button
        mVerifyPasswordEditText = findViewById(R.id.su_edit_text_confirm_password);
        addPopupOnClick((ImageButton) findViewById(R.id.image_button_verify_password_info), this.getString(R.string.verify_password_info_message));

        // caregiver checkbox
        mCaregiverCheckbox = findViewById(R.id.activity_sign_up_checkbox_caregiver);
        addPopupOnClick((ImageButton) findViewById(R.id.activity_sign_up_image_button_caregiver_info), this.getString(R.string.caregiver_info_message));

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

    private void setErrorLayoutNull(){
        mFirstNameLayout.setError(null);
        mLastNameLayout.setError(null);
        mEmailLayout.setError(null);
        mPasswordLayout.setError(null);
        mConfirmPasswordLayout.setError(null);
        mTzLayout.setError(null);
    }

    private void attemptSignUp(final View v) {
        // collect input
        String tz = mTzEditText.getText().toString();
        final String email = mEmailEditText.getText().toString();
        final String first_name = mFirstNameEditText.getText().toString();
        final String last_name = mLastNameEditText.getText().toString();
        final String password = mPasswordEditText.getText().toString();
        final String repeatPassword = mVerifyPasswordEditText.getText().toString();

        setErrorLayoutNull();

        //check tz
        final boolean flagLen = tz.length() != 9;
        String regex = "[0-9]+";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(tz);
        final boolean containsLetter = !m.matches();
        final boolean[] checkTZ = new boolean[1];
        final boolean[] checkAll = {true};

        Communicator.checkTZ(tz, new DBCallBackTZ() {
            @Override
            public void isTZAlreadyInBD(boolean alreadyIn) {
                checkTZ[0] = alreadyIn;

                // check if first name is legal
                if (first_name.isEmpty() || !Constants.isLegalNickname(first_name)) {
                    Snackbar.make(v, ILLEGAL_NICKNAME_MESSAGE, Snackbar.LENGTH_LONG).show();
                    mFirstNameLayout.setError(" ");
                    mFirstNameLayout.requestFocus();
                    checkAll[0] = false;
                }

                // check if last name is legal
                else if (last_name.isEmpty() || !Constants.isLegalNickname(last_name)) {
                    Snackbar.make(v, ILLEGAL_NICKNAME_MESSAGE, Snackbar.LENGTH_LONG).show();
                    mLastNameLayout.setError(" ");
                    mLastNameLayout.requestFocus();
                    checkAll[0] = false;
                }

                // check tz
                else if (flagLen || containsLetter || checkTZ[0]){
                    Snackbar.make(v, ILLEGAL_TZ_MESSAGE, Snackbar.LENGTH_LONG).show();
                    mTzLayout.setError(" ");
                    mTzLayout.requestFocus();
                    checkAll[0] = false;
                }

                // check if email is legal
                else if (!Constants.isLegalEmail(email)) {
                    Snackbar.make(v, ILLEGAL_EMAIL_MESSAGE, Snackbar.LENGTH_LONG).show();
                    mEmailLayout.setError(" ");
                    mEmailLayout.requestFocus();
                    checkAll[0] = false;
                }

                // check if password is valid
                else if (!Constants.isLegalPassword(password)) {
                    Snackbar.make(v, ILLEGAL_PASSWORD_MESSAGE, Snackbar.LENGTH_LONG).show();
                    mPasswordLayout.setError(" ");
                    mPasswordLayout.requestFocus();
                    checkAll[0] = false;
                }

                // check if passwords match
                else if (!password.equals(repeatPassword)) {
                    Snackbar.make(v, PASSWORD_DOESNT_MATCH_MESSAGE, Snackbar.LENGTH_LONG).show();
                    mPasswordLayout.requestFocus();
                    mConfirmPasswordLayout.requestFocus();
                    mPasswordLayout.setError(" ");
                    mConfirmPasswordLayout.setError(" ");
                    checkAll[0] = false;
                }

                if (checkAll[0]){
                    createUserWithEmailAndPassword();
                }
            }
        });
    }

    public void openActivityLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
