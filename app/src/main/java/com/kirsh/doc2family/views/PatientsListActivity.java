package com.kirsh.doc2family.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kirsh.doc2family.R;
import com.kirsh.doc2family.logic.Communicator;
import com.kirsh.doc2family.logic.Constants;
import com.kirsh.doc2family.logic.Patient;

import java.util.ArrayList;

public class PatientsListActivity extends AppCompatActivity {

    PatientsAdapter mAdapter;
    Button addPatientButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients_list);
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
                mAdapter.notifyDataSetChanged(); //todo
            }
        });
    }

    private void initPatientAdapter(){
        //todo userId? here? from where?
       FirebaseAuth myAuth = FirebaseAuth.getInstance();
       final FirebaseUser myUser  = myAuth.getCurrentUser();
       ArrayList<Patient> patients = Communicator.getUsersPatients(myUser.getUid());
       mAdapter = new PatientsAdapter(this, patients);
       Communicator.createLiveQueryPatientList(mAdapter, mAdapter.getmDataset());
       mAdapter.notifyDataSetChanged();
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
        intent.putExtra(Constants.PATIENT_ID_KEY, patient.getId());
        startActivity(intent);
    }

}
