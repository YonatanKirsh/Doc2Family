package com.kirsh.doc2family.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kirsh.doc2family.R;
import com.kirsh.doc2family.logic.Communicator;
import com.kirsh.doc2family.logic.Patient;

import com.kirsh.doc2family.logic.Constants;

public class PatientInfoActivity extends AppCompatActivity {

    private Patient mPatient;
    TextView patientNameTextView;
    Button questionsButton;
    Button friendsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_info);
        String patientId = getIntent().getStringExtra(Constants.PATIENT_ID_KEY);
        mPatient = Communicator.getPatientById(patientId);
        initViews();
    }


    public void initViews(){
        // patient name view
        patientNameTextView = findViewById(R.id.text_view_name_in_adapter);
        if (mPatient != null){
            patientNameTextView.setText(mPatient.getFullName());
        }
        else {
            patientNameTextView.setText(R.string.no_patient);
        }

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

    public void openActivityQuestions(){
        // todo this patient's questions page
        Intent intent = new Intent(this, QuestionsActivity.class);
        startActivity(intent);
    }

    public void openActivityFriends(){
        // todo this patient's friends page
        Intent intent = new Intent(this, FriendsActivity.class);
        startActivity(intent);
    }
}
