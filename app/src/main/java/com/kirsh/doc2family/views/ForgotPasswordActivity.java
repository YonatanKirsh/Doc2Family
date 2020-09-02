package com.kirsh.doc2family.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.kirsh.doc2family.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailEditText;
    private Button resetPassword;

    private FirebaseAuth Auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        initViews();


        // Initialize Firebase Auth
        Auth = FirebaseAuth.getInstance();
    }

    private void initViews() {
        // email EditText
        emailEditText = findViewById(R.id.edit_text_email_for_reset);

        // reset password button
        resetPassword = findViewById(R.id.reset_password);
        final String email = emailEditText.getText().toString();
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(ForgotPasswordActivity.this, "Reset password sent to your email.", Toast.LENGTH_LONG).show();
                            openActivityLogin();
                        }
                        else{
                            Toast.makeText(ForgotPasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    private void openActivityLogin(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
