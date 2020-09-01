package com.kirsh.doc2family.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kirsh.doc2family.R;
import com.kirsh.doc2family.logic.Patient;

import static com.kirsh.doc2family.logic.Constants.PATIENT_ID_KEY;

public class ListPatientsActivity extends AppCompatActivity {

    // todo remove
    TextView patientDemo;

    PatientsAdapter mAdapter;
    Button addPatientButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_patients);
        initPatientAdapter();
        initViews();
    }

    public void initViews(){
        // patients adapter
        RecyclerView patientsRecycler = findViewById(R.id.recycler_patients);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        patientsRecycler.setLayoutManager(layoutManager);
        patientsRecycler.setAdapter(mAdapter);

        // add-patient button
        addPatientButton = findViewById(R.id.button_goto_add_patient);
        addPatientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivityAddPatient();
            }
        });
    }

    private void initPatientAdapter(){
        mAdapter = new PatientsAdapter(this);
    }

    public void onClickPatient(Patient patient){
        openActivityPatientInfo(patient);
    }

    public void openActivityAddPatient(){
        Intent intent = new Intent(this, AddPatientActivity.class);
        startActivity(intent);
    }

    private void openActivityPatientInfo(Patient patient){
        Intent intent = new Intent(this, PatientInfoActivity.class);
        intent.putExtra(PATIENT_ID_KEY, patient.getId());
        startActivity(intent);
    }

}
