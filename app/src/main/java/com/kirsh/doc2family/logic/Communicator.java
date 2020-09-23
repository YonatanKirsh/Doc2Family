package com.kirsh.doc2family.logic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firestore.v1.ArrayValue;
import com.kirsh.doc2family.views.CaregiversAdapter;
import com.kirsh.doc2family.views.FriendsAdapter;
import com.kirsh.doc2family.views.LoginActivity;
import com.kirsh.doc2family.views.PatientsAdapter;
import com.kirsh.doc2family.views.PatientsListActivity;
import com.kirsh.doc2family.views.QuestionsAdapter;

import java.io.Serializable;
import java.util.ArrayList;

public class Communicator {

    private static FirebaseAuth Auth = FirebaseAuth.getInstance();
    static final FirebaseFirestore[] fireStore = {FirebaseFirestore.getInstance()};
    static FirebaseFirestore db = FirebaseFirestore.getInstance();



    public static void cCreateUserWithEmailAndPassword(String email, String password, final Context context, final String firstName, final String lastName, final boolean  mIsDoctor, final EditText mEmailEditText,
                                                       final EditText mPasswordEditText, final TextInputLayout mEmailLayout, final TextInputLayout mPasswordLayout, final String tz){
        Auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("SIGN_UP_SUCCESS", "createUserWithEmail:success");
                            Toast.makeText(context, "Registration succeed!",
                                    Toast.LENGTH_SHORT).show();
                            Auth.getCurrentUser().sendEmailVerification()
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
                            final FirebaseUser user_auth = Auth.getCurrentUser();
                            final User newUser = new User(user_auth.getEmail(), firstName, lastName, user_auth.getUid(), mIsDoctor, tz);
                            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                            firestore.collection("Users").document(user_auth.getUid()).set(newUser);
                            //document.set(newUser);
                            db.collection("Patients").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()){
                                        for (QueryDocumentSnapshot doc : task.getResult()){
                                            Patient patient = doc.toObject(Patient.class);
                                            if (patient.getAdminTz().equals(newUser.getTz())){
                                                ArrayList<String> friends = patient.getFriends();
                                                friends.add(newUser.getId());
                                                patient.setFriends(friends);
                                                ArrayList<Patient> userPatients = newUser.getPatientIds();
                                                userPatients.add(patient);
                                                newUser.setPatientIds(userPatients);
                                                db.collection("Users").document(user_auth.getUid()).set(newUser);
                                                Communicator.updatePatientInUsersandPatientCollection(patient);
                                            }
                                        }
                                    }
                                }
                            });

                            openActivityLogin(context);
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

    public static void cSignInWithEmailAndPassword(String email, String password, final Context context){
        Auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            if (Auth.getCurrentUser().isEmailVerified()){
                                Log.d("SIGN_IN_SUCCESS", "signInWithEmail: success");
                                openActivityListPatients(context);
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

    public static void cSendPasswordResetEmail(String email, final Context context) {
        Auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Reset password sent to your email.", Toast.LENGTH_LONG).show();
                    openActivityLogin(context);
                } else {
                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public static void cAddPatient(String firstName, String lastName, String tz, String diagnosis, final Context context){
        //TODO What if the patient is already in the db ( need to add tz ??)
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference myDocPatient = db.collection("Patients").document();
        final Patient myPatient = new Patient(firstName, lastName, myDocPatient.getId(), diagnosis, tz);
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
                                ArrayList<Patient> myPatients = user.getPatientIds();

//                                if (user.isCareGiver()){
//                                    db.collection("Users").document(user.getId()).set(user, SetOptions.merge());
//                                }
//                                else {
//                                }
                                if (user.isCareGiver()){
                                    ArrayList<String> careGivers = myPatient.getCaregiverIds();
                                    careGivers.add(myUser.getUid());
                                    myPatient.setCaregiverIds(careGivers);
                                    db.collection("Patients").document(myPatient.getId()).set(myPatient);
                                }
                                else{
                                    //TODO check admin
                                    // todo
                                    ArrayList<String> friends = myPatient.getFriends();
                                    friends.add(myUser.getUid());
                                    myPatient.setFriends(friends);
                                    db.collection("Patients").document(myPatient.getId()).set(myPatient);
                                }
                                myPatients.add(myPatient);
                                user.setPatientIds(myPatients);
                                db.collection("Users").document(user.getId()).set(user);
                                openActivityListPatients(context);
                            }
                        }
                    }
                });
    }

    public static void cAddQuestionForPatient(Context context, final Patient patient, String questions, Friend asker){

        // update the list of questions of given patient in the Patient collection
        //todo test remove answer
        final Question question = new Question(questions, asker);
        final ArrayList<Question> oldQuestions = patient.getQuestions();
        oldQuestions.add(question);
        patient.setQuestions(oldQuestions);

        // update the Patient in the User collection and in the Patient collection
        updatePatientInUsersandPatientCollection(patient);
    }

    public static void updatePatientInUsersandPatientCollection(final Patient patient){
        db.collection("Patients").document(patient.getId()).set(patient);
        db.collection("Users").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot myDoc : task.getResult()){
                                User user = myDoc.toObject(User.class);
                                ArrayList<Patient> patientList = user.getPatientIds();
                                for (Patient userPatient : patientList){
                                    if (userPatient.getId().equals(patient.getId())){
                                        patientList.remove(userPatient);
                                        patientList.add(patient);
                                        user.setPatientIds(patientList);
                                        db.collection("Users").document(user.getId()).set(user);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                });
    }

    public static void createLiveQueryPatientList(final PatientsAdapter adapter, final ArrayList<Patient> patientsList){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        final FirebaseUser myUser  = Auth.getCurrentUser();
        final String userID = myUser.getUid();
        final ArrayList<Patient>[] patientsIds = new ArrayList[]{new ArrayList<Patient>()};
        final User[] user = new User[1];
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
                patientsList.clear();
                //adapter.setmDataset(getUsersPatients(myUser.getUid()));
                adapter.notifyDataSetChanged();
            }
        });

        referenceToCollection.whereEqualTo("id", userID).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                //adapter.setmDataset(getUsersPatients(myUser.getUid()));
                adapter.notifyDataSetChanged();
            }
        });

        referenceToCollection.whereEqualTo("id", userID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot myDoc : task.getResult()) {
                        user[0] = myDoc.toObject(User.class);
                        patientsIds[0] = user[0].getPatientIds();
                        adapter.setmDataset(patientsIds[0]);
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

    public static void createLiveQueryQuestionsList(final QuestionsAdapter adapter, final ArrayList<Question> questionsList, Patient patient){
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

    public  static void getUsersByIds(final CaregiversAdapter adapter, final ArrayList<User> userList, final ArrayList<String> idsList){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        final FirebaseUser myUser  = Auth.getCurrentUser();
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

    public  static void getFriendsByIds(final FriendsAdapter adapter, final ArrayList<User> userList, final ArrayList<String> idsList){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        final FirebaseUser myUser  = Auth.getCurrentUser();
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

    //todo firebase / local db !! TO TEST
    public static Patient getPatientById(Serializable patientId){
        final Patient[] patient = new Patient[1];
        fireStore[0].collection("Patients")
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

//    //todo firebase! TO TEST
    public static ArrayList<Patient> getUsersPatients(String userId){
        //return Constants.SAMPLE_PATIENTS;
        final ArrayList<Patient>[] patientsIds = new ArrayList[]{new ArrayList<Patient>()};
        final User[] user = new User[1];

        fireStore[0].collection("Users")
                .whereEqualTo("id", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot myDoc : task.getResult()) {
                                user[0] = myDoc.toObject(User.class);
                                patientsIds[0] = user[0].getPatientIds();
                            }
                        }
                        else {
                            Log.d("ErrorDoc", "Error getting documents: ", task.getException());
                            return;
                        }
                    }
                });
        return patientsIds[0];
    }

    //todo firebase!
    public static ArrayList<String> getPatientsCaregivers(String patientId){
        //ArrayList<User> caregivers = new ArrayList<>();
        //Patient patient = getPatientById(patientId);
        //final ArrayList<String> caregiverIds = patient.getCaregiverIds();
        //for (String userId : caregiverIds) {
        //    caregivers.add(getUserById(userId));
        //}
        //return caregivers;

        final ArrayList<String>[] careGivers = new ArrayList[]{new ArrayList<>()};

        fireStore[0].collection("Users")
                .whereEqualTo("id", patientId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot myDoc : task.getResult()) {
                                Patient patient = myDoc.toObject(Patient.class);
                                careGivers[0] = patient.getCaregiverIds();
                            }
                        }
                        else {
                            Log.d("ErrorDoc", "Error getting documents: ", task.getException());
                            return;
                        }
                    }
                });
        return null;
    }

    // todo firebase!
    public static User getUserById(String userId){
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

    public static Friend getFriendById(String friendId){
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

    private static void openActivityListPatients(Context context){
        // todo
//        User thisUser = new User();
        Intent intent = new Intent(context, PatientsListActivity.class);
        context.startActivity(intent);
    }

    private static void openActivityLogin(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

}
