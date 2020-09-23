package com.kirsh.doc2family.views;

import androidx.appcompat.app.AppCompatActivity;
import com.kirsh.doc2family.R;
import com.kirsh.doc2family.logic.Communicator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailEditText;
    private Button resetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        initViews();
    }

    private void initViews() {
        // email EditText
        emailEditText = findViewById(R.id.edit_text_email_for_reset);

        // reset password button
        resetPassword = findViewById(R.id.reset_password);
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = emailEditText.getText().toString();
                Communicator.cSendPasswordResetEmail(email, ForgotPasswordActivity.this);
            }
        });
    }

    public void openActivityLogin(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
