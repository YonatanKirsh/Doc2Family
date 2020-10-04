package com.kirsh.doc2family.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kirsh.doc2family.R;
import com.kirsh.doc2family.logic.Communicator;

public class AddPatientActivity extends AppCompatActivity {

    Button addPatientButton;
    EditText firstNameEdit;
    EditText lastNameEdit;
    EditText tzEdit;
    EditText diagnosisEdit;
    Communicator communicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);
        communicator = Communicator.getSingleton();
        this.firstNameEdit = (EditText) findViewById(R.id.edit_text_patient_first_name);
        this.lastNameEdit = (EditText) findViewById(R.id.edit_text_patients_last_name);
        this.tzEdit =  (EditText) findViewById(R.id.edit_text_tz);
        this.diagnosisEdit = (EditText) findViewById(R.id.edit_text_patients_diagnosis) ;
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
//                Snackbar.make(findViewById(android.R.id.content), "Add patient attempt!", Snackbar.LENGTH_SHORT).show();
                attemptAddPatient();
            }
        });
    }

    private void attemptAddPatient(){

        String firstName = firstNameEdit.getText().toString();
        String lastName = lastNameEdit.getText().toString();
        String diagnosis = diagnosisEdit.getText().toString();
        String tz = tzEdit.getText().toString();

        if (firstName.isEmpty()){
            Toast.makeText(this, "Please enter valid first name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (lastName.isEmpty()){
            Toast.makeText(this, "Please enter valid last name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (tz.isEmpty()){
            Toast.makeText(this, "Please enter valid teudat zeut number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (diagnosis.isEmpty()){
            Toast.makeText(this, "Please enter valid diagnosis", Toast.LENGTH_SHORT).show();
            return;
        }

        firstNameEdit.getText().clear();
        lastNameEdit.getText().clear();
        tzEdit.getText().clear();
        diagnosisEdit.getText().clear();
        communicator.cAddPatient(firstName, lastName, tz, diagnosis, this);
    }

    public void openActivityListPatients(){
        Intent intent = new Intent(this, PatientsListActivity.class);
        this.startActivity(intent);
    }
}
