package com.kirsh.doc2family.views;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.kirsh.doc2family.R;
import com.kirsh.doc2family.logic.Communicator;
import com.kirsh.doc2family.logic.Patient;

import com.kirsh.doc2family.logic.Constants;
import com.kirsh.doc2family.logic.Update;
import com.kirsh.doc2family.logic.User;

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

    private void showUpdateDialog(Update update){
        AlertDialog.Builder builder = new AlertDialog.Builder(PatientInfoActivity.this);
        // set view
        View view = getLayoutInflater().inflate(R.layout.view_update_dialog, null);
        builder.setView(view);
        // add update info
        final TextView updateDate = view.findViewById(R.id.text_view_update_popup_date);
        updateDate.setText(update.getDateString());
        final EditText updateContent = view.findViewById(R.id.edit_text_update_popup_content);
        updateContent.setText(update.getContent());
        final TextView updateIssuer = view.findViewById(R.id.text_view_update_popup_issuer);
        User issuer = Communicator.getUserById(update.getIssuingCareGiverId());
        updateIssuer.setText(issuer.getFullName());
        // Add the buttons
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked update button - todo change the update's content
                String newUpdate = updateContent.getText().toString();
                String message = "updated to:\n" + newUpdate;
                Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
                updateContent.setText("");
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.cancel();
            }
        });
        // Create the AlertDialog
        AlertDialog updatesDialog = builder.create();
        updatesDialog.show();
    }


    public void onClickUpdate(Update update) {
        showUpdateDialog(update);
    }

    public void openActivityQuestions(){
        // todo this patient's questions page
        Intent intent = new Intent(this, QuestionsListActivity.class);
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
