package com.kirsh.doc2family.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kirsh.doc2family.R;
import com.kirsh.doc2family.logic.Patient;

import java.util.ArrayList;

public class ListPatientsActivity extends AppCompatActivity {

    // todo remove
    TextView patientDemo;

    Button addPatientButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_patients);
        initViews();
    }

    //todo remove fake patient
    public void initViews(){
        // todo add patients recycler
        // todo instead of demo - set foreach item in recycler
        patientDemo = findViewById(R.id.text_view_patient_demo);
        patientDemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivityPatientInfo(new Patient());
            }
        });

        // add patient button
        addPatientButton = findViewById(R.id.button_goto_add_patient);
        addPatientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivityAddPatient();
            }
        });
    }

    public void openActivityAddPatient(){
        Intent intent = new Intent(this, AddPatientActivity.class);
        startActivity(intent);
    }

    public void openActivityPatientInfo(Patient patient){
        // todo open specific patient, not demo
        Intent intent = new Intent(this, PatientInfoActivity.class);
        startActivity(intent);
    }

}
