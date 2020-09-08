package com.kirsh.doc2family.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.kirsh.doc2family.R;
import com.kirsh.doc2family.logic.Friend;
import com.kirsh.doc2family.logic.Patient;
import com.kirsh.doc2family.logic.User;

import java.util.ArrayList;

public class AddPatientActivity extends AppCompatActivity {

    Button addPatientButton;
    EditText firstNameEdit;
    EditText lastNameEdit;
    EditText diagnosisEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);
        this.firstNameEdit = (EditText) findViewById(R.id.edit_text_patient_first_name);
        this.lastNameEdit = (EditText) findViewById(R.id.edit_text_patients_last_name);
        this.diagnosisEdit = (EditText) findViewById(R.id.edit_text_patients_diagnosis) ;
        initViews();
    }

    private void initViews(){
        addPatientButton = findViewById(R.id.button_add_patient);
        setAddPatientButton();
    }

    private void setAddPatientButton(){
        addPatientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Snackbar.make(findViewById(android.R.id.content), "Add patient attempt!", Snackbar.LENGTH_SHORT).show();
                attemptAddPatient();
            }
        });
    }

    private void attemptAddPatient(){

        String firstName = firstNameEdit.getText().toString();
        String lastName = lastNameEdit.getText().toString();
        String diagnosis = diagnosisEdit.getText().toString();

        if (firstName.isEmpty()){
            Toast.makeText(this, "Please enter valid first name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (lastName.isEmpty()){
            Toast.makeText(this, "Please enter valid last name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (diagnosis.isEmpty()){
            Toast.makeText(this, "Please enter valid diagnosis", Toast.LENGTH_SHORT).show();
            return;
        }

        firstNameEdit.getText().clear();
        lastNameEdit.getText().clear();
        diagnosisEdit.getText().clear();

        //TODO What if the patient is already in the db ( need to add tz ??)
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference myDocPatient = db.collection("Patients").document();
        final Patient myPatient = new Patient(firstName, lastName, myDocPatient.getId(), diagnosis);
        myDocPatient.set(myPatient);

        FirebaseAuth myAuth = FirebaseAuth.getInstance();
        final FirebaseUser myUser = myAuth.getCurrentUser();
        db.collection("Users")
                .whereEqualTo("id", myUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot myDoc : task.getResult()){
                                User user = myDoc.toObject(User.class);
                                ArrayList<String> myPatients = user.getPatientIds();
                                myPatients.add(myDocPatient.getId());
                                user.setPatientIds(myPatients);
//                                if (user.isCareGiver()){
//                                    db.collection("Users").document(user.getId()).set(user, SetOptions.merge());
//                                }
//                                else {
                                    db.collection("Users").document(user.getId()).set(user);
//                                }

                                if (user.isCareGiver()){
                                    ArrayList<String> careGivers = myPatient.getCaregiverIds();
                                    careGivers.add(myUser.getUid());
                                    myPatient.setCaregiverIds(careGivers);
                                    db.collection("Patients").document(myPatient.getId()).set(myPatient);
                                }
                                else{
                                    //TODO check admin
                                    Friend myFriend = new Friend(myUser.getUid(), false);
                                    ArrayList<Friend> friends = myPatient.getFriends();
                                    friends.add(myFriend);
                                    myPatient.setFriends(friends);
                                    db.collection("Patients").document(myPatient.getId()).set(myPatient);
                                }
                            }
                        }
                    }
                });

    }
}
