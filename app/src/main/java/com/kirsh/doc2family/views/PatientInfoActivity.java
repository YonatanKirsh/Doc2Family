package com.kirsh.doc2family.views;

import androidx.annotation.NonNull;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.kirsh.doc2family.R;
import com.kirsh.doc2family.logic.Communicator;
import com.kirsh.doc2family.logic.Patient;

import com.kirsh.doc2family.logic.Constants;
import com.kirsh.doc2family.logic.Update;
import com.kirsh.doc2family.logic.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PatientInfoActivity extends AppCompatActivity {

    private Patient mPatient;
    UpdatesAdapter mAdapter;
    TextView patientNameTextView;
    TextView patientTzTextView;
    TextView diagnosisTextView;
    Button questionsButton;
    Button caregiversButton;
    Button friendsButton;
    Button addAdminButton;
    Button addUpdateButton;
    Gson gson = new Gson();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Communicator communicator;

    private SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_info);
        communicator = Communicator.getSingleton();
        String patientString = getIntent().getStringExtra(Constants.PATIENT_AS_STRING_KEY);
        mPatient = gson.fromJson(patientString, Patient.class);
        initUpdatesAdapter();
        communicator.createLiveQueryUpdatesAdapter(mPatient, mAdapter);
        initViews();
    }

    private void initUpdatesAdapter(){
        mAdapter = new UpdatesAdapter(this, mPatient.getUpdates());
    }

    public void initViews(){
        String localUserId = communicator.getLocalUser().getId();
        // patient name view
        patientNameTextView = findViewById(R.id.activity_patient_info_text_view_title);
        if (mPatient != null){
            patientNameTextView.setText(mPatient.getFullName());
        }
        else {
            patientNameTextView.setText(R.string.no_patient);
        }

        // tz text view
        patientTzTextView = findViewById(R.id.activity_patient_info_text_view_tz_content);
        patientTzTextView.setText(mPatient.getTz());

        // diagnosis text view
        diagnosisTextView = findViewById(R.id.activity_patient_info_text_view_patient_diagnosis);
        if (mPatient != null){
            diagnosisTextView.setText(mPatient.getDiagnosis());
        }
        else {
            diagnosisTextView.setText(R.string.no_patient);
        }
        if (mPatient.hasCaregiverWithId(localUserId)){
            diagnosisTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showEditDiagnosisDialog();
                }
            });
        }

        // updates adapter
        RecyclerView updatesRecycler = findViewById(R.id.recycler_updates);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        updatesRecycler.setLayoutManager(layoutManager);
        updatesRecycler.setAdapter(mAdapter);

        // questions button
        questionsButton = findViewById(R.id.activity_patient_info_button_goto_questions);
        questionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivityQuestions();
            }
        });

        // caregivers button
        caregiversButton = findViewById(R.id.activity_patient_info_button_goto_caregivers);
        caregiversButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivityCaregivers();
            }
        });

        // friends button
        friendsButton = findViewById(R.id.activity_patient_info_button_goto_friends);
        friendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivityFriends();
            }
        });


        // add admin button
        addAdminButton = findViewById(R.id.activity_patient_info_button_add_admin);
        addAdminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAdmin();
            }
        });
        if (mPatient.hasCaregiverWithId(localUserId) && !mPatient.hasAdmin()){
            addAdminButton.setVisibility(View.VISIBLE);
        }

        //add update button
        addUpdateButton = findViewById(R.id.activity_patient_info_button_add_update);
        addUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptAddUpdate();
            }
        });
        if (mPatient.hasCaregiverWithId(localUserId)){
            addUpdateButton.setVisibility(View.VISIBLE);
        }
    }

    public void attemptAddUpdate(){
        AlertDialog.Builder builder = new AlertDialog.Builder(PatientInfoActivity.this);
        builder.setTitle("Add an update");

        // add edit text
        final EditText updateInput = new EditText(PatientInfoActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        updateInput.setLayoutParams(lp);
        builder.setView(updateInput);

        // Add the buttons
        builder.setPositiveButton(getString(R.string.add), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String updateMess = updateInput.getText().toString();
                if (!updateMess.equals("")){
                    long time = System.currentTimeMillis();
                    Update update = new Update(communicator.getLocalUser().getId(), updateMess, time);
                    // update the db
                    communicator.addUpdateForPatient(mPatient, update, PatientInfoActivity.this);
                    dialog.dismiss();
                }
                else {
                    Toast.makeText(PatientInfoActivity.this, getString(R.string.unable_to_add_update_message), Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.cancel();
            }
        });
        // Create the AlertDialog
        builder.show();
    }

    public void addAdmin(){
        AlertDialog.Builder builder = new AlertDialog.Builder(PatientInfoActivity.this);
        builder.setTitle("Add an admin");

        // add edit text
        final EditText adminTzInput = new EditText(PatientInfoActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        adminTzInput.setLayoutParams(lp);
        adminTzInput.setHint("Enter admin's tz");
        builder.setView(adminTzInput);

        // Add the buttons
        builder.setPositiveButton("Define", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String adminTz = adminTzInput.getText().toString();
                if (!adminTz.equals("")){
                    mPatient.addAdmin(adminTz);
                    communicator.addFriendToPatient(adminTz, mPatient, true, PatientInfoActivity.this);
                    addAdminButton.setVisibility(View.INVISIBLE);
                    dialog.dismiss();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.cancel();
            }
        });
        // Create the AlertDialog
        builder.show();
    }

    public void confirmDeleteUpdate(final Update update, final DialogInterface callingDialog) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // confirmed delete
                        communicator.removeUpdateForPatient(mPatient, update, PatientInfoActivity.this);
                        callingDialog.dismiss();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        // No button clicked
                        break;
                }
            }
        };

        ConfirmDialog.show(this, dialogClickListener);
    }

    public void showEditDiagnosisDialog(){
        // init builder, get diagnosis
        AlertDialog.Builder builder = new AlertDialog.Builder(PatientInfoActivity.this);
        final EditText editText = new EditText(PatientInfoActivity.this);
        editText.setText(diagnosisTextView.getText().toString());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        editText.setLayoutParams(lp);
        builder.setView(editText);
        builder.setTitle(R.string.diagnosis);

        // set cancel button
        builder.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // set update button
        builder.setNegativeButton(R.string.update, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newDiagnosis = editText.getText().toString();
                confirmUpdateDiagnosis(newDiagnosis, dialog);
            }
        });

        builder.show();
    }

    public void confirmUpdateDiagnosis(final String newDiagnosis, final DialogInterface callingDialog){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        mPatient.setDiagnosis(newDiagnosis);
                        communicator.updatePatientInDatabase(mPatient);
                        diagnosisTextView.setText(newDiagnosis);
                        callingDialog.dismiss();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // No button clicked
                        break;
                }
            }
        };

        ConfirmDialog.show(this, dialogClickListener, String.format("Update diagnosis to\n\"%s\"?", newDiagnosis));
    }

    public void onLongClickUpdate(final Update update){
        // only allow caregivers to remove other caregivers
        if (!mPatient.hasCaregiverWithId(communicator.getLocalUser().getId())){
            return;
        }
        // init builder, get diagnosis
        AlertDialog.Builder builder = new AlertDialog.Builder(PatientInfoActivity.this);
        builder.setTitle(getString(R.string.delete_update_title));

        // set cancel button
        builder.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // set remove caregiver button
        builder.setNegativeButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                confirmDeleteUpdate(update, dialog);
            }
        });
        builder.show();
    }

//    public void onClickUpdate(final Update update) {
//        User user = communicator.getLocalUser();
//        if (update.getIssuingCareGiverId().equals(user.getId())){
//            showEditUpdateDialog(update, user);
//        }
////        communicator.editUpdateIfCurrentUser(update, this);
//    }

    public void openActivityQuestions(){
        Intent intent = new Intent(this, QuestionsListActivity.class);
        String patientString = gson.toJson(mPatient);
        intent.putExtra(Constants.PATIENT_AS_STRING_KEY, patientString);
        startActivity(intent);
    }

    public void openActivityFriends(){
        Intent intent = new Intent(this, FriendsListActivity.class);
        String patientString = gson.toJson(mPatient);
        intent.putExtra(Constants.PATIENT_AS_STRING_KEY, patientString);
        startActivity(intent);
    }

    public void openActivityCaregivers(){
        Intent intent = new Intent(this, CaregiversListActivity.class);
        String patientString = gson.toJson(mPatient);
        intent.putExtra(Constants.PATIENT_AS_STRING_KEY, patientString);
        startActivity(intent);
    }

    @Override
    public void onRestart() {
        super.onRestart();
        //communicator.updatePatient(mPatient);
        db.collection("Patients").whereEqualTo("id", mPatient.getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot doc: task.getResult()){
                        Patient patient = doc.toObject(Patient.class);
                        mPatient = patient;
                    }
                }
            }
        });
        mAdapter.notifyDataSetChanged();
    }

}
