package com.kirsh.doc2family.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;
import com.kirsh.doc2family.R;

public class AddPatientActivity extends AppCompatActivity {

    Button addPatientButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);
        initViews();
    }

    private void initViews(){
        addPatientButton = findViewById(R.id.button_add_patient);
        setAddPatientButton();
    }

    private void setAddPatientButton(){
        addPatientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(findViewById(android.R.id.content), "Add patient attempt!", Snackbar.LENGTH_SHORT).show();
                attemptAddPatient();
            }
        });
    }

    private void attemptAddPatient(){

    }
}
