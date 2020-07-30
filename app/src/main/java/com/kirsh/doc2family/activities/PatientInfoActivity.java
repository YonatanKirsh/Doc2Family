package com.kirsh.doc2family.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.kirsh.doc2family.R;

public class PatientInfoActivity extends AppCompatActivity {

    Button questionsButton;
    Button friendsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_info);
        initViews();
    }


    public void initViews(){
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
