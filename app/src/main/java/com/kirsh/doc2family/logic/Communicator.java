package com.kirsh.doc2family.logic;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kirsh.doc2family.R;
import com.kirsh.doc2family.views.CaregiversAdapter;
import com.kirsh.doc2family.views.ForgotPasswordActivity;
import com.kirsh.doc2family.views.FriendsAdapter;
import com.kirsh.doc2family.views.LoginActivity;
import com.kirsh.doc2family.views.PatientInfoActivity;
import com.kirsh.doc2family.views.PatientsAdapter;
import com.kirsh.doc2family.views.PatientsListActivity;
import com.kirsh.doc2family.views.QuestionsAdapter;
import com.kirsh.doc2family.views.SignUpActivity;
import com.kirsh.doc2family.views.UpdatesAdapter;
import com.kirsh.doc2family.views.UsersAdapter;

import java.util.ArrayList;
import java.util.HashSet;

public class Communicator {

    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    User localUser;
    ArrayList<Patient> localPatients = new ArrayList<>();

    private static Communicator singleton;

    private Communicator(){
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        initLocalUser();
    }

    public static Communicator getSingleton(){
        if (singleton == null){
            singleton = new Communicator();
        }
        if (singleton.localUser == null){
            singleton.initLocalUser();
        }
        return singleton;
    }

    private void initLocalUser(){
        if (firebaseUser == null){
            return;
        }
        db.collection(Constants.USERS_COLLECTION_FIELD).document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                localUser = documentSnapshot.toObject(User.class);
                updateLocalPatients();
            }
        });
        createLiveQueryLocalUser();
    }

    private void createLiveQueryLocalUser(){
        db.collection(Constants.USERS_COLLECTION_FIELD).document(firebaseUser.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("firebase error", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    localUser = snapshot.toObject(User.class);
                    updateLocalPatients();
                } else {
                    Log.d("ErrorDoc", "Error getting document with id: " + firebaseUser.getUid());
                }
            }
        });
    }

    public User getLocalUser(){
        return localUser;
    }

    public void cCreateUserWithEmailAndPassword(String email, String password, final Context context, final String firstName, final String lastName, final boolean  mIsDoctor, final EditText mEmailEditText,
                                                       final EditText mPasswordEditText, final TextInputLayout mEmailLayout, final TextInputLayout mPasswordLayout, final String tz){
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // update UI with the signed-in user's information
                            Log.d("Sign up success", "createUserWithEmail: success");
                            Toast.makeText(context, "Registration succeed!", Toast.LENGTH_SHORT).show();
                            // send email verification
                            firebaseAuth.getCurrentUser().sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task) {
                                               if (task.isSuccessful()) {
                                                   Toast.makeText(context, "Registration succeed!. Please check your email for verification.",
                                                           Toast.LENGTH_SHORT).show();
                                                   mEmailEditText.setText("");
                                                   mPasswordEditText.setText("");
                                               } else {
                                                   Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                               }
                                           }
                                       }
                                    );
                            // add new User object to db
                            final User newUser = new User(firebaseUser.getEmail(), firstName, lastName, firebaseUser.getUid(), mIsDoctor, tz);
                            db.collection(Constants.USERS_COLLECTION_FIELD).document(firebaseUser.getUid()).set(newUser);
                            ((SignUpActivity)context).openActivityLogin();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("SIGN_UP_ERR", "createUserWithEmail: failure", task.getException());
                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(context, "Registration failed! " + errorMessage,
                                    Toast.LENGTH_LONG).show();
                            if (errorMessage.contains("email")){
                                mEmailLayout.setError(" ");
                                mEmailLayout.requestFocus();
                            }
                            else if (errorMessage.contains("password")){
                                mPasswordLayout.setError(" ");
                                mPasswordLayout.requestFocus();
                            }
                        }
                    }
                });
    }

    public void cSignInWithEmailAndPassword(String email, String password, final Context context){
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            if (firebaseAuth.getCurrentUser().isEmailVerified()){
                                Log.d("Sign in success", "signInWithEmail: success");
                                initLocalUser();
                                ((LoginActivity)context).openActivityListPatients();
                            }
                            else{
                                Toast.makeText(context, "Please verify your email address.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("SIGN_IN_ERR", "signInWithEmail:failure", task.getException());
                            Toast.makeText(context, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void cSendPasswordResetEmail(String email, final Context context) {
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Reset password sent to your email.", Toast.LENGTH_LONG).show();
                    ((ForgotPasswordActivity)context).openActivityLogin();
                } else {
                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void createNewPatient(String firstName, String lastName, String tz, String diagnosis, final Context context){
        // create new patient in db
        final DocumentReference myDocPatient = db.collection(Constants.PATIENTS_COLLECTION_FIELD).document();
        final Patient patientToAdd = new Patient(firstName, lastName, myDocPatient.getId(), diagnosis, tz, localUser.getId());
        myDocPatient.set(patientToAdd);
        localUser.addPatientId(patientToAdd.getId());
        updateUserInDatabase(localUser);
    }

    public void removeFriendFromPatient(Patient patient, final String friendUserId, final Context context){
        db.collection(Constants.PATIENTS_COLLECTION_FIELD).document(patient.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Patient dbPatient = documentSnapshot.toObject(Patient.class);
                if (dbPatient != null){
                    dbPatient.removeFriend(friendUserId);
                    updatePatientInDatabase(dbPatient);
                    Toast.makeText(context, context.getString(R.string.removed_friend_message), Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(context, context.getString(R.string.unable_to_remove_friend_message), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void updateFriendInPatient(Patient patient, final Friend friend, final Context context){
        db.collection(Constants.PATIENTS_COLLECTION_FIELD).document(patient.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Patient dbPatient = documentSnapshot.toObject(Patient.class);
                if (dbPatient != null){
                    dbPatient.updateFriend(friend);
                    updatePatientInDatabase(dbPatient);
                    Toast.makeText(context, context.getString(R.string.updated_friend_message), Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(context, context.getString(R.string.unable_to_remove_friend_message), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void addQuestionForPatient(Patient patient, final String questionInput){
        db.collection(Constants.PATIENTS_COLLECTION_FIELD).document(patient.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    Patient patient = documentSnapshot.toObject(Patient.class);
                    Question question = new Question(questionInput, System.currentTimeMillis(), System.currentTimeMillis(), localUser.getId());
                    patient.addQuestion(question);
                    updatePatientInDatabase(patient);
                }
            }
        });
    }

    //todo make private?
    public void updatePatientInDatabase(final Patient patient){
        db.collection(Constants.PATIENTS_COLLECTION_FIELD).document(patient.getId()).set(patient);
    }

    private void updateUserInDatabase(final User user){
        db.collection(Constants.USERS_COLLECTION_FIELD).document(user.getId()).set(user);
    }

    private void updateLocalPatients(){
        // no patients for null user
        if (localUser == null){
            Log.d(Constants.NULL_USER_TAG, "updateLocalPatients: local User is null");
            return;
        }
        localPatients = new ArrayList<>();
        CollectionReference patientsCollection = db.collection(Constants.PATIENTS_COLLECTION_FIELD);
        for (final String patientId : localUser.getPatientIds()) {
            patientsCollection.document(patientId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    // attempt to get patient
                    Patient patient = documentSnapshot.toObject(Patient.class);
                    if (patient == null){
                        return;
                    }
                    // assert this patient allows local user to follow
                    if (patient.hasCaregiverWithId(localUser.getId()) || patient.hasFriendWithId(localUser.getId())){
                        localPatients.add(patient);
                    }
                }
            });
        }
    }

    public void createLiveQueryPatientsAdapter(final PatientsAdapter adapter){
        // init adapter
        updatePatientsAdapter(adapter);
        // listen for this users updates
        db.collection(Constants.USERS_COLLECTION_FIELD).document(localUser.getId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d("FirestoreManager", "exception in snapshot :(" + e.getMessage());
                    return;
                }
                if (snapshot != null && snapshot.exists() && snapshot.toObject(User.class) != null) {
                    // if user updated - update it's patients
                    updatePatientsAdapter(adapter);
                } else {
                    Log.d("ErrorDoc", "Error getting document with id:" + localUser.getId());
                }
            }
        });
    }

    private void updatePatientsAdapter(final PatientsAdapter adapter){
        final ArrayList<Patient>[] updatedPatients = new ArrayList[]{new ArrayList<Patient>()};
        updatedPatients[0].clear();
        for (String patientId : localUser.getPatientIds()) {
            db.collection(Constants.PATIENTS_COLLECTION_FIELD).document(patientId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Patient patient = documentSnapshot.toObject(Patient.class);
                    updatedPatients[0].add(patient);
                    adapter.setmDataset(updatedPatients[0]);
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    public void createLiveQueryQuestionsAdapter(final QuestionsAdapter adapter, final Patient patient){
        final Patient[] dbPatient = new Patient[1];

        DocumentReference patientReference = db.collection(Constants.PATIENTS_COLLECTION_FIELD).document(patient.getId());
        patientReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null){
                    Log.d("FirestoreManager", "exception in snapshot :(" + e.getMessage());
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    dbPatient[0] = snapshot.toObject(Patient.class);
                    adapter.setmDataset(dbPatient[0].getQuestions());
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d("ErrorDoc", "Error getting document with id: " + patient.getId());
                }
            }
        });
    }

    private void updateUsersAdapter(final UsersAdapter adapter, final ArrayList<String> userIds){
        final ArrayList<User>[] updatedUsers = new ArrayList[]{new ArrayList<User>()};
        updatedUsers[0].clear();
        for (String userId : userIds) {
            db.collection(Constants.USERS_COLLECTION_FIELD).document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User user = documentSnapshot.toObject(User.class);
                    updatedUsers[0].add(user);
                    adapter.setmDataset(updatedUsers[0]);
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    public void createLiveQueryFriendsAdapter(final Patient patient, final FriendsAdapter adapter){
        // init adapter
        updateUsersAdapter(adapter, patient.getFriendIds());
        final Patient[] patientHolder = new Patient[1];
        // listen for this patients updates
        db.collection(Constants.PATIENTS_COLLECTION_FIELD).document(patient.getId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d("FirestoreManager", "exception in snapshot :(" + e.getMessage());
                    return;
                }
                if (snapshot != null && snapshot.exists() && snapshot.toObject(Patient.class) != null) {
                    // if patient updated - update it's friends
                    patientHolder[0] = snapshot.toObject(Patient.class);
                    updateUsersAdapter(adapter, patientHolder[0].getFriendIds());
                } else {
                    Log.d("ErrorDoc", "Error getting document with id:" + patient.getId());
                }
            }
        });
    }

    public void createLiveQueryCaregiversAdapter(final Patient patient, final CaregiversAdapter adapter){
        if (patient.getCaregiverIds() == null){
            return;
        }
        // init adapter
        updateUsersAdapter(adapter, patient.getCaregiverIds());
        final Patient[] patientHolderTemp = new Patient[1];
        // listen for this patients updates
        db.collection(Constants.PATIENTS_COLLECTION_FIELD).document(patient.getId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d("FirestoreManager", "exception in snapshot :(" + e.getMessage());
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    // if patient updated - update it's caregivers
                    patientHolderTemp[0] = snapshot.toObject(Patient.class);
                    updateUsersAdapter(adapter, patientHolderTemp[0].getCaregiverIds());
                } else {
                    Log.d("ErrorDoc", "Error getting document with id: " + patient.getId());
                }
            }
        });
    }

    public void removePatientFromUser(final Patient currentPatient){
        localUser.removePatientId(currentPatient.getId());
        updateUserInDatabase(localUser);
        // remove this user as caregiver if necessary
        if (currentPatient.getCaregiverIds().contains(localUser.getId())){
            currentPatient.removeCaregiver(localUser.getId());
        }
        // remove this user as friend if necessary
        if (currentPatient.hasFriendWithId(localUser.getId())){
            currentPatient.removeFriend(localUser.getId());
        }
        updatePatientInDatabase(currentPatient);
    }

    public void addPatientForLocalUser(final String patientTz, final Context context){
        // todo there is a pb when I delete a patient and try to add it with the same tz.
        // find patient with this tz
        db.collection(Constants.PATIENTS_COLLECTION_FIELD).whereEqualTo(Constants.TZ_FIELD, patientTz).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    boolean patientExists = false;
                    for (QueryDocumentSnapshot myDoc : task.getResult()) {
                        patientExists = true;
                        final Patient patient = myDoc.toObject(Patient.class);
                        // return if user already treats/follows this patient
                        if (localUser.getPatientIds().contains(patient.getId())){
                            Toast.makeText(context, context.getString(R.string.already_following_patient_message), Toast.LENGTH_LONG).show();
                            return;
                        }
                        // add as caregiver iff user is patient's caregiver
                        if (patient.hasCaregiverWithId(localUser.getId()) && !localUser.getPatientIds().contains(patient.getId())){
                            Toast.makeText(context, context.getString(R.string.added_patient_as_caregiver_message), Toast.LENGTH_LONG).show();
                            localUser.addPatientId(patient.getId());
                            updateUserInDatabase(localUser);
                        }
                        // add as friend iff user is patient's friend
                        if (patient.hasFriendWithId(localUser.getId()) && !localUser.getPatientIds().contains(patient.getId())){
                            Toast.makeText(context, context.getString(R.string.added_patient_as_friend_message), Toast.LENGTH_LONG).show();
                            localUser.addPatientId(patient.getId());
                            updateUserInDatabase(localUser);
                        }
                        // display error message if user isn't treating/following this patient
                        else {
                            String message = context.getString(R.string.friend_unable_to_add_patient_message);
                            if (localUser.isCareGiver()){
                                message = context.getString(R.string.caregiver_unable_to_add_patient_message);
                            }
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                        }
                    }
                    if (!patientExists){
                        // caregivers can create new patients
                        if (localUser.isCareGiver()){
                            ((PatientsListActivity)context).openActivityAddPatient(patientTz);
                        }
                    }
                }
            }
        });
    }

    public void addFriendToPatient(final String newFriendTz, final Patient patient, final  boolean makeAdmin, final Context context){
        // get new friend as user
        db.collection(Constants.USERS_COLLECTION_FIELD).whereEqualTo(Constants.TZ_FIELD, newFriendTz).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                boolean userExists = false;
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot doc : task.getResult()){
                        final User friendAsUser = doc.toObject(User.class);
                        userExists = true;
                        // update patient from db
                        db.collection(Constants.PATIENTS_COLLECTION_FIELD).document(patient.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Patient dbPatient = documentSnapshot.toObject(Patient.class);
                                if (dbPatient == null){
                                    Toast.makeText(context, "DB Error :(", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if (dbPatient.hasFriendWithId(friendAsUser.getId())){
                                    Toast.makeText(context, "Already his friend!", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                dbPatient.addFriend(friendAsUser.getId(), makeAdmin);
                                updatePatientInDatabase(dbPatient);
                                Toast.makeText(context, "Friend added!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                if (!userExists){
                    Toast.makeText(context, "Not a valid User!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void updateUpdateAdapterFullname(final String userID, final UpdatesAdapter.UpdateHolder holder){
        db.collection("Users").whereEqualTo("id", userID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot doc: task.getResult()){
                        User user = doc.toObject(User.class);
                        holder.texViewIssuer.setText(user.getFullName());
                    }
                }
            }
        });
    }

    public ArrayList<Patient> getPatientsListForLocalUser(){
        ArrayList<Patient> patients = new ArrayList<>(localPatients);
        return patients;
    }

    public void checkTZ(String tz, final DBCallBackTZ dbCallBackTZ) {
        db.collection("Users").whereEqualTo("tz", tz).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                boolean flag = false;
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()){
                        User user = doc.toObject(User.class);
                        flag = true;
                    }

                    if(flag) {
                        dbCallBackTZ.isTZAlreadyInBD(true);
                    }
                    else{
                        dbCallBackTZ.isTZAlreadyInBD(false);
                    }
                }
            }
        });
    }

    public void updateQuestionForPatient(Patient patient, final Question question, final Context context){
        db.collection(Constants.PATIENTS_COLLECTION_FIELD).document(patient.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    Patient patient = documentSnapshot.toObject(Patient.class);
                    patient.updateQuestion(question);
                    updatePatientInDatabase(patient);
                    Toast.makeText(context, context.getString(R.string.updated_question_message), Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(context, context.getString(R.string.unable_to_update_question_message), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void updateUserFullname(String userID, final TextView textView) {
        db.collection(Constants.USERS_COLLECTION_FIELD).document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    User user = doc.toObject(User.class);
                    textView.setText(user.getFullName());
                }
            }
        });
    }

    public void createLiveQueryUpdatesAdapter(final Patient mPatient, final UpdatesAdapter mAdapter){
        db.collection(Constants.PATIENTS_COLLECTION_FIELD).document(mPatient.getId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d("FirestoreManager", "exception in snapshot :(" + e.getMessage());
                    return;
                }
                if (snapshot != null && snapshot.exists() && snapshot.toObject(Patient.class) != null) {
                    // if user updated - update it's patients
                    Patient patient = snapshot.toObject(Patient.class);
                    mAdapter.setmDataset(patient.getUpdates());
                    mAdapter.notifyDataSetChanged();
                } else {
                    Log.d("ErrorDoc", "Error getting document with id:" + localUser.getId());
                }
            }
        });
    }

    public void addCaregiverToPatient(final Patient patient, String tz, final Context context){
        db.collection(Constants.USERS_COLLECTION_FIELD).whereEqualTo(Constants.TZ_FIELD, tz).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        final User caregiver = doc.toObject(User.class);
                        db.collection(Constants.PATIENTS_COLLECTION_FIELD).document(patient.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Patient dbPatient = documentSnapshot.toObject(Patient.class);
                                if (dbPatient != null){
                                    dbPatient.addCaregiver(caregiver.getId());
                                    updatePatientInDatabase(dbPatient);
                                    Toast.makeText(context, context.getString(R.string.added_caregiver_message), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(context, context.getString(R.string.unable_to_add_caregiver_message), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void removeCaregiverFromPatient(final Patient patient, final String caregiverId, final Context context){
        // remove caregiver from patient's caregivers
        db.collection(Constants.PATIENTS_COLLECTION_FIELD).document(patient.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Patient dbPatient = documentSnapshot.toObject(Patient.class);
                if (dbPatient != null){
                    dbPatient.removeCaregiver(caregiverId);
                    updatePatientInDatabase(dbPatient);
                    Toast.makeText(context, context.getString(R.string.removed_caregiver_message), Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(context, context.getString(R.string.unable_to_remove_caregiver_message), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void addUpdateForPatient(Patient patient, final Update update, final Context context){
        db.collection(Constants.PATIENTS_COLLECTION_FIELD).document(patient.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    Patient patient = documentSnapshot.toObject(Patient.class);
                    patient.addUpdate(update);
                    updatePatientInDatabase(patient);
                    Toast.makeText(context, context.getString(R.string.added_update_message), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void removeUpdateForPatient(Patient patient, final Update update, final Context context){
        db.collection(Constants.PATIENTS_COLLECTION_FIELD).document(patient.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    Patient patient = documentSnapshot.toObject(Patient.class);
                    patient.removeUpdate(update);
                    updatePatientInDatabase(patient);
                    Toast.makeText(context, context.getString(R.string.removed_update_message), Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(context, context.getString(R.string.unable_to_remove_update_message), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}