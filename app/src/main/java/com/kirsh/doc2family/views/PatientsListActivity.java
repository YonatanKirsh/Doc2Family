package com.kirsh.doc2family.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.kirsh.doc2family.R;
import com.kirsh.doc2family.logic.Communicator;
import com.kirsh.doc2family.logic.Constants;
import com.kirsh.doc2family.logic.Patient;
import com.kirsh.doc2family.logic.Update;
import com.kirsh.doc2family.logic.User;

import java.io.Serializable;
import java.util.ArrayList;

public class PatientsListActivity extends AppCompatActivity {

    PatientsAdapter mAdapter;
    Button addPatientButton;
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients_list);
        initPatientAdapter();
        initViews();
    }

    public void initViews() {
        // patients adapter
        RecyclerView patientsRecycler = findViewById(R.id.recycler_patients);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        patientsRecycler.setLayoutManager(layoutManager);
        new ItemTouchHelper(itemTouchHelper).attachToRecyclerView(patientsRecycler);
        patientsRecycler.setAdapter(mAdapter);

        // add-patient button
        addPatientButton = findViewById(R.id.button_goto_add_patient);

        FirebaseAuth myAuth = FirebaseAuth.getInstance();
        final FirebaseUser myUser = myAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Users").whereEqualTo("id", myUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot myDoc : task.getResult()) {
                                User user = myDoc.toObject(User.class);
                                if (user.isCareGiver()) {
                                    addPatientButton.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }
                });

        addPatientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initEnterTzDialog();

            }
        });
    }

    private void initEnterTzDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PatientsListActivity.this);
        builder.setTitle(" ENTER THE TZ'S PATIENT");

        // add edit text
        final EditText updateInput = new EditText(PatientsListActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        updateInput.setLayoutParams(lp);
        builder.setView(updateInput);

        // Add the buttons
        final boolean[] flag = {false};
        final Patient[] patient = {null};

        builder.setPositiveButton("ENTER", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String updateMess = updateInput.getText().toString();
                if (!updateMess.equals("")) {
                    final FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("Patients").whereEqualTo("tz", updateMess).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot myDoc : task.getResult()) {
                                    flag[0] = true;
                                    patient[0] = myDoc.toObject(Patient.class);

                                    FirebaseAuth myAuth = FirebaseAuth.getInstance();
                                    final FirebaseUser myUser = myAuth.getCurrentUser();
                                    db.collection("Users").whereEqualTo("id", myUser.getUid())
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot doc : task.getResult()) {
                                                            User user = doc.toObject(User.class);
                                                            ArrayList<String> careGivers = patient[0].getCaregiverIds();
                                                            if (!careGivers.contains(user.getId())) {
                                                                careGivers.add(user.getId());
                                                                patient[0].setCaregiverIds(careGivers);
                                                                db.collection("Patients").document(patient[0].getId()).set(patient[0]);
                                                                ArrayList<Patient> patients = user.getPatientIds();
                                                                patients.add(patient[0]);
                                                                user.setPatientIds(patients);
                                                                db.collection("Users").document(user.getId()).set(user);
                                                            } else {
                                                                String message = "Already your patient";
                                                                Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
                                                            }


                                                        }
                                                    }
                                                }
                                            });
                                }
                            }
                        }
                    });
                }
                if (!flag[0]) {
                    openActivityAddPatient();
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

    private void initPatientAdapter() {
        //todo userId? here? from where?
        FirebaseAuth myAuth = FirebaseAuth.getInstance();
        final FirebaseUser myUser = myAuth.getCurrentUser();
        ArrayList<Patient> patients = Communicator.getUsersPatients(myUser.getUid());
        mAdapter = new PatientsAdapter(this, patients);
        Communicator.createLiveQueryPatientList(mAdapter, mAdapter.getmDataset());
        mAdapter.notifyDataSetChanged();
    }

    public void onClickPatient(Patient patient) {
        mAdapter.notifyDataSetChanged();
        openActivityPatientInfo(patient);
    }

    public void openActivityAddPatient() {
        Intent intent = new Intent(this, AddPatientActivity.class);
        startActivity(intent);
        mAdapter.notifyDataSetChanged();
    }

    private void openActivityPatientInfo(Patient patient) {
        Intent intent = new Intent(this, PatientInfoActivity.class);

        String patientString = gson.toJson(patient);
        intent.putExtra(Constants.PATIENT_ID_KEY, patientString);
        startActivity(intent);
    }

    ItemTouchHelper.SimpleCallback itemTouchHelper = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            ArrayList<Patient> mDataset = mAdapter.getmDataset();

            final Patient currentPatient = mDataset.get(viewHolder.getAbsoluteAdapterPosition());
            mDataset.remove(currentPatient);
            mAdapter.setmDataset(mDataset);
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser userAuth = auth.getCurrentUser();
            final FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("Users").whereEqualTo("id", userAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            User user = doc.toObject(User.class);
                            ArrayList<Patient> patients = user.getPatientIds();
                            ArrayList<Patient> nLstPatient = new ArrayList<>();
                            for (Patient patient : patients) {
                                if (!patient.getId().equals(currentPatient.getId())) {
                                    nLstPatient.add(patient);
                                }
                            }
                            user.setPatientIds(nLstPatient);
                            db.collection("Users").document(user.getId()).set(user);

                            if (user.isCareGiver()) {
                                ArrayList<String> careGivers = currentPatient.getCaregiverIds();
                                careGivers.remove(user.getId());
                                currentPatient.setCaregiverIds(careGivers);
                                Communicator.updatePatientInUsersandPatientCollection(currentPatient);
                            } else {
                                ArrayList<String> friends = currentPatient.getFriends();
                                friends.remove(user.getId());
                                currentPatient.setFriends(friends);
                                Communicator.updatePatientInUsersandPatientCollection(currentPatient);
                            }
                            mAdapter.notifyDataSetChanged();

                        }
                    }
                }
            });
        }
    };

    @Override
    public void onRestart() {
        super.onRestart();
        Communicator.createLiveQueryPatientList(mAdapter, mAdapter.getmDataset());
        mAdapter.notifyDataSetChanged();
    }

}
