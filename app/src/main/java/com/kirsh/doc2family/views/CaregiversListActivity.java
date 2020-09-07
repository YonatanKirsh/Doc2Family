package com.kirsh.doc2family.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.kirsh.doc2family.R;
import com.kirsh.doc2family.logic.Communicator;
import com.kirsh.doc2family.logic.Constants;
import com.kirsh.doc2family.logic.Patient;
import com.kirsh.doc2family.logic.User;

import java.util.ArrayList;

public class CaregiversListActivity extends AppCompatActivity {

    private Patient mPatient;
    CaregiversAdapter mAdapter;
    Button addCaregiverButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caregivers_list);
        initPatient();
        initCaregiversAdapter();
        initViews();
    }

    private void initPatient(){
        String patientId = getIntent().getStringExtra(Constants.PATIENT_ID_KEY);
        mPatient = Communicator.getPatientById(patientId);
    }

    private void initCaregiversAdapter() {
        ArrayList<User> caregivers = Communicator.getPatientsCaregivers(mPatient.getId());
        mAdapter = new CaregiversAdapter(this, caregivers);
    }

    private void initViews() {
        // caregivers activity
        RecyclerView caregiversRecycler = findViewById(R.id.recycler_caregivers);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        caregiversRecycler.setLayoutManager(layoutManager);
        caregiversRecycler.setAdapter(mAdapter);

        // add-caregiver button
        addCaregiverButton = findViewById(R.id.caregivers_list_button_goto_add_caregiver);
        addCaregiverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddCaregiverDialog();
            }
        });
    }

    public void showAddCaregiverDialog(){
        Toast.makeText(CaregiversListActivity.this, "clicked add caregiver", Toast.LENGTH_LONG).show();
    }

    public void onLongClickCaregiver(User currentUser) {
        // todo add remove caregiver option
        Toast.makeText(CaregiversListActivity.this, String.format("long-clicked %s", currentUser.getFullName()), Toast.LENGTH_LONG).show();
    }
}
