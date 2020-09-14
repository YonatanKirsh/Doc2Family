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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kirsh.doc2family.R;
import com.kirsh.doc2family.logic.Communicator;
import com.kirsh.doc2family.logic.Friend;
import com.kirsh.doc2family.logic.Patient;

import com.kirsh.doc2family.logic.Constants;
import com.kirsh.doc2family.logic.Update;
import com.kirsh.doc2family.logic.User;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class PatientInfoActivity extends AppCompatActivity {

    private Patient mPatient;
    UpdatesAdapter mAdapter;

    TextView patientNameTextView;
    TextView diagnosisTextView;
    Button questionsButton;
    Button caregiversButton;
    Button friendsButton;
    Button addAdminButton;
    Button addUpdateButton;
    AlertDialog dialogAdmin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_info);
        mPatient = (Patient) getIntent().getSerializableExtra(Constants.PATIENT_ID_KEY);
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
        diagnosisTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDiagnosisDialog();
            }
        });

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
        addAdminButton = findViewById(R.id.add_an_admin_button);


        //add update button
        addUpdateButton = findViewById(R.id.add_an_update_button);

        FirebaseAuth myAuth = FirebaseAuth.getInstance();
        final FirebaseUser myUser = myAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Users").whereEqualTo("id", myUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot myDoc : task.getResult()){
                                User user = myDoc.toObject(User.class);
                                if(user.isCareGiver()){
                                    addAdminButton.setVisibility(View.VISIBLE);
                                    addUpdateButton.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }
                });

        addAdminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAdmin();
            }
        });

        addUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUpdate();
            }
        });


    }

    public void addUpdate(){
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
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String updateMess = updateInput.getText().toString();
                if (updateMess != null){
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    String careGiverID = auth.getCurrentUser().getUid();
                    long time = System.currentTimeMillis();
                    Update newUpdate = new Update(careGiverID, updateMess, time);

                    //update the list of updates of the patient
                    ArrayList<Update> updates = mPatient.getUpdates();
                    updates.add(newUpdate);
                    mPatient.setUpdates(updates);

                    // update the db
                    Communicator.updatePatientInUsersandPatientCollection(mPatient);
                }
                String message = "added update:\n" + updateMess;
                Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
                updateInput.setText("");
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
        builder.setView(adminTzInput);

        // Add the buttons
        builder.setPositiveButton("Define", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String adminTz = adminTzInput.getText().toString();
                if (adminTz != null){
                    mPatient.setAdminTz(adminTz);
                    Communicator.updatePatientInUsersandPatientCollection(mPatient);
                }
                String message = "added admin:\n" + adminTz;
                Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
                adminTzInput.setText("");
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
        builder.show();
    }

    private void showEditUpdateDialog(Update update){
        AlertDialog.Builder builder = new AlertDialog.Builder(PatientInfoActivity.this);
        // set view
        View view = getLayoutInflater().inflate(R.layout.update_dialog, null);
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
        builder.setNegativeButton(R.string.update, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked update button - todo change the update's content
                String newUpdate = updateContent.getText().toString();
                confirmUpdateUpdate(newUpdate, dialog);
//                updateContent.setText("");
            }
        });

        builder.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.cancel();
            }
        });
        // Create the AlertDialog
        AlertDialog updatesDialog = builder.create();
        updatesDialog.show();
    }

    public void confirmUpdateUpdate(final String newUpdate, final DialogInterface callingDialog) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked - todo actually update diagnosis
                        Toast.makeText(PatientInfoActivity.this, String.format("Updated to:\n%s", newUpdate), Toast.LENGTH_LONG).show();
                        callingDialog.dismiss();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // No button clicked
                        break;
                }
            }
        };

        ConfirmDialog.show(this, dialogClickListener, String.format("Update to\n\"%s\"?", newUpdate));
    }

    private void showEditDiagnosisDialog(){
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
                        //Yes button clicked - todo actually update diagnosis
                        Toast.makeText(PatientInfoActivity.this, String.format("Diagnosis updated:\n%s", newDiagnosis), Toast.LENGTH_LONG).show();
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

    public void onClickUpdate(Update update) {
        // todo iff update issued by current user
        showEditUpdateDialog(update);
    }

    public void openActivityQuestions(){
        Intent intent = new Intent(this, QuestionsListActivity.class);
        intent.putExtra(Constants.PATIENT_ID_KEY, mPatient);
        startActivity(intent);
    }

    public void openActivityFriends(){
        Intent intent = new Intent(this, FriendsListActivity.class);
        intent.putExtra(Constants.PATIENT_ID_KEY, mPatient);
        startActivity(intent);
    }

    public void openActivityCaregivers(){
        Intent intent = new Intent(this, CaregiversListActivity.class);
        intent.putExtra(Constants.PATIENT_ID_KEY, mPatient);
        startActivity(intent);
    }

}
