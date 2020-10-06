package com.kirsh.doc2family.logic;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.kirsh.doc2family.views.AddPatientActivity;
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
        final Patient patientToAdd = new Patient(firstName, lastName, myDocPatient.getId(), diagnosis, tz, localUser.getId());
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

    public void cAddQuestionForPatient(final Patient patient, final String questions, final QuestionsAdapter adpater){
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
                    updatePatientInCollection(patient);
                }
            }
        });

    }

    public void updatePatientInCollection(final Patient patient){
        db.collection(Constants.PATIENTS_COLLECTION_FIELD).document(patient.getId()).set(patient);
    }

    public void updateUserInCollection(final User user){
        db.collection(Constants.USERS_COLLECTION_FIELD).document(user.getId()).set(user);
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
                    userBucket[0] = snapshot.toObject(User.class);
                    updateUserFromBucket();
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
                    // if patient updated - update it's caregivers
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

    public boolean localUserIsCaregiver(){
        if (localUser == null){
            return false;
        }
        return localUser.isCareGiver();
    }

    public void removePatientFromUserAndUpdate(final Patient currentPatient){
        db.collection(Constants.USERS_COLLECTION_FIELD).document(localUser.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                // remove patient from user's patients
                User user = documentSnapshot.toObject(User.class);
                if (user == null){
                    Log.d(Constants.NULL_USER_TAG, "onSuccess: got null user with id: " + localUser.getId());
                    return;
                }
                user.removePatientId(currentPatient.getId());
                db.collection(Constants.USERS_COLLECTION_FIELD).document(user.getId()).set(user);
                // remove this user as caregiver if necessary
                if (currentPatient.getCaregiverIds().contains(user.getId())){
                    currentPatient.removeCaregiver(user.getId());
                }
                // remove this user as friend if necessary
                if (currentPatient.getFriends().contains(user.getId())){
                    currentPatient.removeFriend(user.getId());
                }
                updatePatientInCollection(currentPatient);
            }
        });
    }

    public void updateAdminInUsersAndPatientCollection(final Patient patient, final String adminTz){
//        updatePatientInCollection(patient);
        db.collection(Constants.USERS_COLLECTION_FIELD).whereEqualTo("tz", adminTz).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot doc : task.getResult()){
                        User user = doc.toObject(User.class);
                        user.addPatientId(patient.getId());
                        updateUserInCollection(user);
                        patient.addFriend(user.getId(), true);
                        updatePatientInCollection(patient);
                    }
                }
            }
        });

    }

    public void attemptAddPatient(final String patientTz, final Context context){
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
                            updateUserInCollection(localUser);
                        }
                        // add as friend iff user is patient's friend
                        if (patient.hasFriendWithId(localUser.getId()) && !localUser.getPatientIds().contains(patient.getId())){
                            Toast.makeText(context, context.getString(R.string.added_patient_as_friend_message), Toast.LENGTH_LONG).show();
                            localUser.addPatientId(patient.getId());
                            updateUserInCollection(localUser);
                        }
                        // display error message if user isn't treating/following this patient
                        else {
                            String message = context.getString(R.string.friend_unable_to_add_patient_message);
                            if (localUser.isCareGiver()){
                                message = context.getString(R.string.caregiver_unable_to_add_patient_message);
                            }
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                        }
//
//                        db.collection(Constants.USERS_COLLECTION_FIELD).whereEqualTo("id", firebaseUser.getUid())
//                                .get()
//                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                        if (task.isSuccessful()) {
//                                            for (QueryDocumentSnapshot doc : task.getResult()) {
//                                                User user = doc.toObject(User.class);
//                                                ArrayList<String> careGivers = patient.getCaregiverIds();
//                                                if (!careGivers.contains(user.getId())) {
//                                                    String message = "Another caregiver must add you\nin order to access this patient";
//                                                    Snackbar.make(contextView, message, Snackbar.LENGTH_LONG).show();
////                                                    careGivers.add(user.getId());
////                                                    patient[0].setCaregiverIds(careGivers);
////                                                    db.collection("Patients").document(patient[0].getId()).set(patient[0]);
////                                                    ArrayList<String> patients = user.getPatientIds();
////                                                    patients.add(patient[0].getId());
////                                                    user.setPatientIds(patients);
////                                                    db.collection("Users").document(user.getId()).set(user);
//                                                } else {
//                                                    String message = "Already your patient";
//                                                    Snackbar.make(contextView, message, Snackbar.LENGTH_LONG).show();
//                                                }
//                                            }
//                                        }
//                                    }
//                                });
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

    public void addFriendAndUpdateCollections(final String newFriendTz, final Patient patient, final Context context, final  FriendsAdapter mAdapter){
        final boolean[] flag = {false};

        db.collection(Constants.USERS_COLLECTION_FIELD).whereEqualTo(Constants.TZ_FIELD, newFriendTz).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot doc : task.getResult()){
                        User friendAsUser = doc.toObject(User.class);
                        if (patient.hasFriendWithId(friendAsUser.getId())){
                            Toast.makeText(context, "Already his friend!",
                                    Toast.LENGTH_SHORT).show();
                            break;
                        }
                        flag[0] = true;
                        patient.addFriend(friendAsUser.getId());
                        updatePatientInCollection(patient);
                        Toast.makeText(context, "Friend added!",
                                Toast.LENGTH_SHORT).show();
//                        ArrayList<User> users = mAdapter.getmDataset();
//                        users.add(friendAsUser);
//                        mAdapter.setmDataset(users);
//                        mAdapter.notifyDataSetChanged();
                    }
                }
                if (!flag[0]){
                    Toast.makeText(context, "Not a valid User!",
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