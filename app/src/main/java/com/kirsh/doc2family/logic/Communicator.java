package com.kirsh.doc2family.logic;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.material.snackbar.Snackbar;
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
import com.kirsh.doc2family.views.AddPatientActivity;
import com.kirsh.doc2family.views.CaregiversAdapter;
import com.kirsh.doc2family.views.ForgotPasswordActivity;
import com.kirsh.doc2family.views.FriendsAdapter;
import com.kirsh.doc2family.views.LoginActivity;
import com.kirsh.doc2family.views.PatientInfoActivity;
import com.kirsh.doc2family.views.PatientsAdapter;
import com.kirsh.doc2family.views.PatientsListActivity;
import com.kirsh.doc2family.views.QuestionsAdapter;
import com.kirsh.doc2family.views.QuestionsListActivity;
import com.kirsh.doc2family.views.SignUpActivity;
import com.kirsh.doc2family.views.UpdatesAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class Communicator {

    final TaskCompletionSource<List<Objects>> tcs = new TaskCompletionSource<>();

    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    User localUser;
    ArrayList<Patient> localPatients = new ArrayList<>();
    HashSet<String> localPatientIds = new HashSet<>();
    final User[] userBucket = new User[1];
    final ArrayList<Patient>[] patientsBucket = new ArrayList[]{new ArrayList<Patient>()};

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
        return singleton;
    }

    private void updateUserFromBucket(){
        localUser = userBucket[0];
    }

    private void initLocalUser(){
        db.collection("Users").document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userBucket[0] = documentSnapshot.toObject(User.class);
                updateUserFromBucket();
                updateLocalPatients();
            }
        });
        createLiveQueryLocalUser();
    }

    private void createLiveQueryLocalUser(){
        DocumentReference userReference = db.collection("Users").document(firebaseUser.getUid());

        userReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("firebase error", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    userBucket[0] = snapshot.toObject(User.class);
                    updateUserFromBucket();
                    updateLocalPatients();
                } else {
                    Log.d("ErrorDoc", "Error getting document with id: " + firebaseUser.getUid());
                }
            }
        });
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
                            db.collection(Constants.PATIENTS_COLLECTION_FIELD).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()){
                                        for (QueryDocumentSnapshot doc : task.getResult()){
                                            Patient patient = doc.toObject(Patient.class);
                                            if (patient.getAdminTz().equals(newUser.getTz())){
                                                ArrayList<String> friends = patient.getFriends();
                                                friends.add(newUser.getId());
                                                patient.setFriends(friends);
                                                ArrayList<String> userPatients = newUser.getPatientIds();
                                                userPatients.add(patient.getId());
                                                newUser.setPatientIds(userPatients);
                                                db.collection("Users").document(firebaseUser.getUid()).set(newUser);
                                                updatePatientInCollection(patient);
                                            }
                                        }
                                    }
                                }
                            });

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
                                Log.d("SIGN_IN_SUCCESS", "signInWithEmail: success");
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
        //TODO What if the patient is already in the db ( need to add tz ??)
        // create new patient in db
        final DocumentReference myDocPatient = db.collection(Constants.PATIENTS_COLLECTION_FIELD).document();
        final Patient patientToAdd = new Patient(firstName, lastName, myDocPatient.getId(), diagnosis, tz);
        myDocPatient.set(patientToAdd);
        localUser.addPatientId(patientToAdd.getId());
        // add to user's patients on db
        db.collection(Constants.USERS_COLLECTION_FIELD)
                .document(localUser.getId())
                .update(Constants.PATIENT_IDS_FIELD, localUser.getPatientIds())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(Constants.UPDATE_FIREBASE_TAG, "DocumentSnapshot User: " + localUser.getId()  + " patientIds successfully updated!");
                        ((AddPatientActivity)context).openActivityPatientsList();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(Constants.UPDATE_FIREBASE_TAG, "Error updating document: " + localUser.getId(), e);
                        ((AddPatientActivity)context).openActivityPatientsList();
                    }
                });
    }

    public void cAddQuestionForPatient(Context context, final Patient patient, final String questions, final QuestionsAdapter adpater){

        // update the list of questions of given patient in the Patient collection
        //todo test remove answer

        db.collection(Constants.PATIENTS_COLLECTION_FIELD).document(patient.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    Patient patient = documentSnapshot.toObject(Patient.class);
                    Question question = new Question(questions, System.currentTimeMillis(), System.currentTimeMillis(), localUser.getId());

                    ArrayList<Question> oldQuestions = patient.getQuestions();
                    oldQuestions.add(question);
                    patient.setQuestions(oldQuestions);
                    //todo just added now
                    //todo move to currentPatient event in QuestionsLiveQuery?
                    adpater.setmDataset(oldQuestions);
                    adpater.notifyDataSetChanged();
                    // update the Patient in the User collection and in the Patient collection
                    updatePatientInCollection(patient);
                }
            }
        });

    }

    public void updatePatientInCollection(final Patient patient){
        db.collection(Constants.PATIENTS_COLLECTION_FIELD).document(patient.getId()).set(patient);
    }

    private void updateLocalPatients(){
        // no patients for null user
        if (localUser == null){
            Log.d(Constants.NULL_USER_TAG, "updateLocalPatients: local User is null");
            return;
        }
        // get each patient _once_
        localPatientIds.clear();
        CollectionReference patientsCollection = db.collection("Patients");
        for (final String patientId : localUser.getPatientIds()) {
            patientsCollection.document(patientId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Patient patient = documentSnapshot.toObject(Patient.class);
                    if (!localPatientIds.contains(patient.getId())){
                        localPatientIds.add(patient.getId());
                        patientsBucket[0].add(patient);
                        updatePatientsFromBucket();
                    }
                }
            });
        }
    }

    private void updatePatientsFromBucket(){
        localPatients = patientsBucket[0];
    }

    public void createLiveQueryPatientsAdapter(final PatientsAdapter adapter){
        createLiveQueryLocalUser();

        DocumentReference userReference = db.collection("Users").document(firebaseUser.getUid());

        userReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("firebase error", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    adapter.setmDataset(getPatientsListForLocalUser());
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d("ErrorDoc", "Error getting document with id: " + firebaseUser.getUid());
                }
            }
        });
    }

    public void createLiveQueryQuestionsList(final QuestionsAdapter adapter, final ArrayList<Question> questionsList, Patient patient){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        final Patient[] dbPatient = new Patient[1];

        CollectionReference referenceToCollection = firestore.collection("Patients");
        referenceToCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null){
                    Log.d("FirestoreManager", "exception in snapshot :(" + e.getMessage());
                    return;
                }
                if (queryDocumentSnapshots == null){
                    Log.d("FirestoreManager", "value is null");
                    return;
                }
                // let's refresh the local array list
                //patientsList.clear();
                //adapter.setmDataset(getUsersPatients(myUser.getUid()));
                //adapter.notifyDataSetChanged();
            }
        });

        referenceToCollection.whereEqualTo("id", patient.getId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                //adapter.setmDataset(getUsersPatients(myUser.getUid()));
                adapter.notifyDataSetChanged();
            }
        });

        referenceToCollection.whereEqualTo("id", patient.getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot myDoc : task.getResult()) {
                        dbPatient[0] = myDoc.toObject(Patient.class);
                        adapter.setmDataset(dbPatient[0].getQuestions());
                        adapter.notifyDataSetChanged();
                    }
                }
                else {
                    Log.d("ErrorDoc", "Error getting documents: ", task.getException());
                    return;
                }
                //adapter.setmDataset(getUsersPatients(myUser.getUid()));
                //adapter.notifyDataSetChanged();
            }
        });
    }

    public  void getUsersByIds(final CaregiversAdapter adapter, final ArrayList<User> userList, final ArrayList<String> idsList){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        final FirebaseUser myUser  = firebaseAuth.getCurrentUser();
        final ArrayList<User>[] careGivers = new ArrayList[]{new ArrayList<User>()};
        CollectionReference referenceToCollection = firestore.collection("Users");
        referenceToCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null){
                    Log.d("FirestoreManager", "exception in snapshot :(" + e.getMessage());
                    return;
                }
                if (queryDocumentSnapshots == null){
                    Log.d("FirestoreManager", "value is null");
                    return;
                }
                // let's refresh the local array list
                userList.clear();
                //adapter.setmDataset(getUsersPatients(myUser.getUid()));
                adapter.notifyDataSetChanged();
            }
        });

        referenceToCollection.whereIn("id", idsList).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                //adapter.setmDataset(getUsersPatients(myUser.getUid()));
                adapter.notifyDataSetChanged();
            }
        });

        referenceToCollection.whereIn("id", idsList).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot myDoc : task.getResult()) {
                        careGivers[0].add(myDoc.toObject(User.class));

                    }
                    adapter.setmDataset(careGivers[0]);
                    adapter.notifyDataSetChanged();
                }
                else {
                    Log.d("ErrorDoc", "Error getting documents: ", task.getException());
                    return;
                }
                //adapter.setmDataset(getUsersPatients(myUser.getUid()));
                //adapter.notifyDataSetChanged();
            }
        });
    }

    public  void getFriendsByIds(final FriendsAdapter adapter, final ArrayList<User> userList, final ArrayList<String> idsList){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        final FirebaseUser myUser  = firebaseAuth.getCurrentUser();
        final ArrayList<User>[] friends = new ArrayList[]{new ArrayList<User>()};
        CollectionReference referenceToCollection = firestore.collection("Users");
        referenceToCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null){
                    Log.d("FirestoreManager", "exception in snapshot :(" + e.getMessage());
                    return;
                }
                if (queryDocumentSnapshots == null){
                    Log.d("FirestoreManager", "value is null");
                    return;
                }
                // let's refresh the local array list
                userList.clear();
                //adapter.setmDataset(getUsersPatients(myUser.getUid()));
                adapter.notifyDataSetChanged();
            }
        });

        referenceToCollection.whereIn("id", idsList).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                //adapter.setmDataset(getUsersPatients(myUser.getUid()));
                adapter.notifyDataSetChanged();
            }
        });

        referenceToCollection.whereIn("id", idsList).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot myDoc : task.getResult()) {
                        User user = myDoc.toObject(User.class);
                        friends[0].add(user);

                    }
                    adapter.setmDataset(friends[0]);
                    adapter.notifyDataSetChanged();
                }
                else {
                    Log.d("ErrorDoc", "Error getting documents: ", task.getException());
                    return;
                }
                //adapter.setmDataset(getUsersPatients(myUser.getUid()));
                //adapter.notifyDataSetChanged();
            }
        });
    }

    public boolean localUserIsCaregiver(){
        return localUser.isCareGiver();
    }

    public void appearAddPatientIfCaregiver(final Button addPatientButton){
        db.collection("Users").whereEqualTo("id", firebaseUser.getUid())
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
    }

    public void appearAddAdminAndUpdate(final Button addAdminButton, final Button addUpdateButton, final Patient mPatient){

        if (mPatient.getCaregiverIds().contains(firebaseUser.getUid())){
            addUpdateButton.setVisibility(View.VISIBLE);
        }
        if (mPatient.getAdminTz().equals("")){
            addAdminButton.setVisibility(View.VISIBLE); //todo to test
        }

//        db.collection("Users").whereEqualTo("id", myUser.getUid())
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if(task.isSuccessful()){
//                            for (QueryDocumentSnapshot myDoc : task.getResult()){
//                                User user = myDoc.toObject(User.class);
//                                if(user.isCareGiver()){
//                                    if (mPatient.getAdminTz().equals("")){
//                                        addAdminButton.setVisibility(View.VISIBLE);
//                                    }
//                                    addUpdateButton.setVisibility(View.VISIBLE);
//                                }
//                            }
//                        }
//                    }
//                });
    }

    public void removePatientFromUserAndUpdate(final Patient currentPatient){
        db.collection("Users").whereEqualTo("id", firebaseUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        User user = doc.toObject(User.class);
                        ArrayList<String> patientIds = user.getPatientIds();
                        ArrayList<String> nLstPatient = new ArrayList<>();
                        for (String patientId : patientIds) {
                            if (!patientId.equals(currentPatient.getId())) {
                                nLstPatient.add(patientId);
                            }
                        }
                        user.setPatientIds(nLstPatient);
                        db.collection("Users").document(user.getId()).set(user);

                        if (user.isCareGiver()) {
                            ArrayList<String> careGivers = currentPatient.getCaregiverIds();
                            careGivers.remove(user.getId());
                            currentPatient.setCaregiverIds(careGivers);
                            updatePatientInCollection(currentPatient);
                        } else {
                            ArrayList<String> friends = currentPatient.getFriends();
                            friends.remove(user.getId());
                            currentPatient.setFriends(friends);
                            updatePatientInCollection(currentPatient);
                        }
                    }
                }
            }
        });


    }

    public void updateAdminInUsersAndPatientCollection(final Patient mPatient, final String adminTz){
        updatePatientInCollection(mPatient);
        db.collection("Users").whereEqualTo("tz", adminTz).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot doc : task.getResult()){
                        User user = doc.toObject(User.class);
                        ArrayList<String> friends = mPatient.getFriends();
                        friends.add(user.getId());
                        mPatient.setFriends(friends);
                        ArrayList<String> patients = user.getPatientIds();
                        patients.add(mPatient.getId());
                        user.setPatientIds(patients);
                        db.collection("Users").document(user.getId()).set(user);
                        updatePatientInCollection(mPatient);
                    }
                }
            }
        });

    }

    public void checkNewPatientExistence(final String updateMess, final boolean[] flag, final Patient[] patient, final Context context, final View contextView){
        // todo there is a pb when I delete a patient and try to add it with the same tz.
        db.collection("Patients").whereEqualTo("tz", updateMess).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot myDoc : task.getResult()) {
                        flag[0] = true;
                        patient[0] = myDoc.toObject(Patient.class);
                        db.collection("Users").whereEqualTo("id", firebaseUser.getUid())
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
                                                    ArrayList<String> patients = user.getPatientIds();
                                                    patients.add(patient[0].getId());
                                                    user.setPatientIds(patients);
                                                    db.collection("Users").document(user.getId()).set(user);
                                                } else {
                                                    String message = "Already your patient";
                                                    Snackbar.make(contextView, message, Snackbar.LENGTH_LONG).show();
                                                }
                                            }
                                        }
                                    }
                                });
                    }
                }
                if (!flag[0]) {
                    ((PatientsListActivity)context).openActivityAddPatient();
                }
            }
        });
    }

    public void editUpdateIfCurrentUser(final Update update, final Context context){
        db.collection("Users").whereEqualTo("id", firebaseUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot doc: task.getResult()){
                        User user = doc.toObject(User.class);
                        if (update.getIssuingCareGiverId().equals(user.getId())) {
                            ((PatientInfoActivity)context).showEditUpdateDialog(update, user);
                        }
                    }
                }
            }
        });
    }

    public void appearNewQuestionIfFriend(final Button submitQuestionButton, final Context context, Patient patient){
        String userID = firebaseUser.getUid();
//        final User[] user = new User[1];

        if (patient.getFriends().contains(userID)){
            submitQuestionButton.setVisibility(View.VISIBLE);
            ((QuestionsListActivity)context).setAddQuestionButton();
        }
//        db.collection("Users")
//                .whereEqualTo("id", userID)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot myDoc : task.getResult()) {
//                                user[0] = myDoc.toObject(User.class);
//                                if (!user[0].isCareGiver()){
//                                    submitQuestionButton.setVisibility(View.VISIBLE);
//                                    ((QuestionsListActivity)context).setAddQuestionButton();
//                                }
//                            }
//                        }
//                        else {
//                            Log.d("ErrorDoc", "Error getting documents: ", task.getException());
//                            return;
//                        }
//                    }
//                });
    }

    public void appearAddFriendIfAdmin(final Button addFriendButton, final Patient mPatient){
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Users").whereEqualTo("id", auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot doc : task.getResult()){
                        User user = doc.toObject(User.class);
                        if (user.getTz().equals(mPatient.getAdminTz())){
                            addFriendButton.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });
    }

    public void addFriendAndUpdateCollections(final String newFriend, final Patient mPatient, final Context context, final  FriendsAdapter mAdapter){
        final boolean[] flag = {false};

        db.collection("Users").whereEqualTo("tz", newFriend).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot doc : task.getResult()){


                        User user = doc.toObject(User.class);

                        if (mPatient.getFriends().contains(user.getId())){
                            Toast.makeText(context, "Already his friend !",
                                    Toast.LENGTH_SHORT).show();
                            break;
                        }
                        flag[0] = true;
                        ArrayList<String> patients = user.getPatientIds();
                        patients.add(mPatient.getId());
                        user.setPatientIds(patients);
                        db.collection("Users").document(user.getId()).set(user);
                        ArrayList<String> friends = mPatient.getFriends();
                        friends.add(user.getId());
                        mPatient.setFriends(friends);
                        updatePatientInCollection(mPatient);
                        Toast.makeText(context, "Friend added !",
                                Toast.LENGTH_SHORT).show();
                        ArrayList<User> users = mAdapter.getmDataset();
                        users.add(user);
                        mAdapter.setmDataset(users);
                        mAdapter.notifyDataSetChanged();
                    }
                }
                if (!flag[0]){
                    Toast.makeText(context, "Not a valid User !",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void updateUpdateAdapterFullname(final String userID, final UpdatesAdapter.UpdateHolder holder ){
        db.collection("Users").whereEqualTo("id", userID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot doc: task.getResult()){
                        User user = doc.toObject(User.class);
                        String fullName = "by " + user.getFullName();
                        holder.texViewIssuer.setText(fullName);
                    }
                }
            }
        });
    }


    public ArrayList<Patient> getPatient (String patientID){
        final ArrayList<Patient> updatedPatient = new ArrayList<>();
        db.collection("Patients").whereEqualTo("id", patientID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot doc: task.getResult()){
                        Patient patient = doc.toObject(Patient.class);
                        updatedPatient.add(patient);
                    }
                }
            }
        });
        return updatedPatient;
    }

    //todo firebase / local db !! TO TEST
    public Patient getPatientById(Serializable patientId){
        final Patient[] patient = new Patient[1];
        db.collection("Patients")
                .whereEqualTo("id", patientId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot myDoc : task.getResult()) {
                                patient[0] = myDoc.toObject(Patient.class);
                            }
                        }
                        else {
                            Log.d("ErrorDoc", "Error getting documents: ", task.getException());
                            return;
                        }
                    }
                });
        return patient[0];
        //for (Patient patient: Constants.SAMPLE_PATIENTS) {
        //    if (patient.getId().equals(patientId)){
        //        return patient;
        //    }
        //}
    }

    public ArrayList<Patient> getPatientsListForLocalUser(){
        ArrayList<Patient> patients = new ArrayList<>(localPatients);
        return patients;
    }

    //todo firebase!
//    public ArrayList<String> getPatientsCaregivers(String patientId){
//        //ArrayList<User> caregivers = new ArrayList<>();
//        //Patient patient = getPatientById(patientId);
//        //final ArrayList<String> caregiverIds = patient.getCaregiverIds();
//        //for (String userId : caregiverIds) {
//        //    caregivers.add(getUserById(userId));
//        //}
//        //return caregivers;
//
//        final ArrayList<String>[] careGivers = new ArrayList[]{new ArrayList<>()};
//
//        fireStore[0].collection("Users")
//                .whereEqualTo("id", patientId)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot myDoc : task.getResult()) {
//                                Patient patient = myDoc.toObject(Patient.class);
//                                careGivers[0] = patient.getCaregiverIds();
//                            }
//                        }
//                        else {
//                            Log.d("ErrorDoc", "Error getting documents: ", task.getException());
//                            return;
//                        }
//                    }
//                });
//        return null;
//    }

    // todo firebase!
    public User getUserById(String userId){
        final User[] user = new User[1];
        db.collection("Users")
                .whereEqualTo("id", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot myDoc : task.getResult()) {
                                user[0] = myDoc.toObject(User.class);
                            }
                        }
                        else {
                            Log.d("ErrorDoc", "Error getting documents: ", task.getException());
                            return;
                        }
                    }
                });
        return user[0];
    }

    public Friend getFriendById(String friendId){
        final Friend[] friend = new Friend[1];
        db.collection("Users")
                .whereEqualTo("id", friendId)
                .whereEqualTo("careGiver", false)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot myDoc : task.getResult()) {
                                friend[0] = myDoc.toObject(Friend.class);
                            }
                        }
                        else {
                            Log.d("ErrorDoc", "Error getting documents: ", task.getException());
                            return;
                        }
                    }
                });
        return friend[0];
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

                    if(flag){
                        dbCallBackTZ.isTZAlreadyInBD(true);
                    }
                    else{
                        dbCallBackTZ.isTZAlreadyInBD(false);
                    }


                }

            }
        });

    }

    public void updateanswerToQuestion(String answer, long edited, Question question, Patient patient) {

        ArrayList<Question> questionsPatient = patient.getQuestions();
        for(Question q : questionsPatient){
            //TODO how to check in a better way

            if( q.getQuestion().equals(question.getQuestion()) && q.getAskerID().equals(question.getAskerID()) &&
                    q.getDateAsked() == question.getDateAsked()){
                questionsPatient.remove(q);
                q.setAnswer(answer);
                q.setmDateEdited(edited);
                q.setAnswered(true);
                questionsPatient.add(q);
                patient.setQuestions(questionsPatient);
                updatePatientInCollection(patient);
                break;
            }
        }

    }

    public void updateAskerFullname(String askerID, final QuestionsAdapter.QuestionHolder holder) {
        db.collection("Users").whereEqualTo("id", askerID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot doc: task.getResult()){
                        User user = doc.toObject(User.class);
                        String fullName = "questioned by " + user.getFullName();
                        holder.textViewIssuerQuestion.setText(fullName);
                    }
                }
            }
        });
    }

    public void createLiveQueryUpdatesList(final Patient mPatient, final UpdatesAdapter mAdapter){
        db.collection("Patients").whereEqualTo("id", mPatient.getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot doc: task.getResult()){
                        Patient patient = doc.toObject(Patient.class);
                        //mPatient = patient; //todo maybe have to update the patient in PatientInfoActivity
                        mAdapter.setmDataset(patient.getUpdates());
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }


    public boolean isCareGiverOfPatient(final Patient patient) {
        final boolean[] flag = {false};
        db.collection("Users").whereEqualTo("id", firebaseUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    if (patient.getCaregiverIds().contains(firebaseUser.getUid())){
                        flag[0] = true;
                    }

                }
            }
        });
        return flag[0];
    }

    public void updateQuestionChange(String newUpdate, long edited, Question question, Patient mPatient) {

        ArrayList<Question> questionsPatient = mPatient.getQuestions();
        for(Question q : questionsPatient){
            //TODO how to check in a better way

            if( q.getQuestion().equals(question.getQuestion()) && q.getAskerID().equals(question.getAskerID()) &&
                    q.getDateAsked() == question.getDateAsked()){
                questionsPatient.remove(q);
                q.setQuestion(newUpdate);
                q.setmDateEdited(edited);
                q.setAnswered(true);
                questionsPatient.add(q);
                mPatient.setQuestions(questionsPatient);
                updatePatientInCollection(mPatient);
                break;
            }
        }
    }
}