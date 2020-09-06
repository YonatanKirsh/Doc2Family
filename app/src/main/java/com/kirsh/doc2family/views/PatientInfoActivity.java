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
import com.kirsh.doc2family.logic.Communicator;
import com.kirsh.doc2family.logic.Patient;

import com.kirsh.doc2family.logic.Constants;
import com.kirsh.doc2family.logic.Update;

public class PatientInfoActivity extends AppCompatActivity {

    private Patient mPatient;
    UpdatesAdapter mAdapter;

    TextView patientNameTextView;
    TextView diagnosisTextView;
    Button questionsButton;
    Button friendsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_info);
        String patientId = getIntent().getStringExtra(Constants.PATIENT_ID_KEY);
        mPatient = Communicator.getPatientById(patientId);
        initUpdatesAdapter();
        initViews();
    }

    private void initUpdatesAdapter(){
        mAdapter = new UpdatesAdapter(this, mPatient.getUpdates());
    }

    public void initViews(){
        // patient name view
        patientNameTextView = findViewById(R.id.text_view_patient_info_title);
        if (mPatient != null){
            patientNameTextView.setText(mPatient.getFullName());
        }
        else {
            patientNameTextView.setText(R.string.no_patient);
        }

        // diagnosis text view
        diagnosisTextView = findViewById(R.id.text_view_patient_diagnosis);
        if (mPatient != null){
            diagnosisTextView.setText(mPatient.getDiagnosis());
        }
        else {
            diagnosisTextView.setText(R.string.no_patient);
        }

        // updates adapter
        RecyclerView updatesRecycler = findViewById(R.id.recycler_updates);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        updatesRecycler.setLayoutManager(layoutManager);
        updatesRecycler.setAdapter(mAdapter);

        // questions button
        questionsButton = findViewById(R.id.button_goto_questions);
        questionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivityQuestions();
            }
        });

        // friends button
        friendsButton = findViewById(R.id.button_goto_friends);
        friendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivityFriends();
            }
        });
    }

    public void onClickUpdate(Update update) {
        //todo do something with update? show in popup?
    }

    public void openActivityQuestions(){
        // todo this patient's questions page
        Intent intent = new Intent(this, QuestionsActivity.class);
        intent.putExtra(Constants.PATIENT_ID_KEY, mPatient.getId());
        startActivity(intent);
    }

    public void openActivityFriends(){
        // todo this patient's friends page
        Intent intent = new Intent(this, FriendsListActivity.class);
        intent.putExtra(Constants.PATIENT_ID_KEY, mPatient.getId());
        startActivity(intent);
    }

}
