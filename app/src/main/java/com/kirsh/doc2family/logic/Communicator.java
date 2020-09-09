package com.kirsh.doc2family.logic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kirsh.doc2family.views.ForgotPasswordActivity;
import com.kirsh.doc2family.views.LoginActivity;
import com.kirsh.doc2family.views.PatientsListActivity;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Communicator {

    private static FirebaseAuth Auth = FirebaseAuth.getInstance();
    static final FirebaseFirestore[] fireStore = {FirebaseFirestore.getInstance()};


    public static void cCreateUserWithEmailAndPassword(String email, String password, final Context context, final String firstName, final String lastName, final boolean  mIsDoctor, final EditText mEmailEditText,
                                                       final EditText mPasswordEditText, final TextInputLayout mEmailLayout, final TextInputLayout mPasswordLayout){
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
                            FirebaseUser user_auth = Auth.getCurrentUser();
                            User newUser = new User(user_auth.getEmail(), firstName, lastName, user_auth.getUid(), mIsDoctor);
                            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                            DocumentReference document = firestore.collection("Users").document();
                            document.set(newUser);
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

    //todo firebase / local db !! TO TEST
    static final FirebaseFirestore[] fireStore = {FirebaseFirestore.getInstance()};

//    //todo firebase / local db !!
//    public static Patient getPatientById(String patientId){
//        for (Patient patient: Constants.SAMPLE_PATIENTS) {
//            if (patient.getId().equals(patientId)){
//                return patient;
//            }
//        }
//        return null;
//    }


    public static Patient getPatientById(String patientId){
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
    public static ArrayList<String> getUsersPatients(String userId) {
        //return Constants.SAMPLE_PATIENTS;
        final ArrayList<String>[] patientsIds = new ArrayList[]{new ArrayList<>()};

        fireStore[0].collection("Users")
                .whereEqualTo("id", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot myDoc : task.getResult()) {
                                User user = myDoc.toObject(User.class);
                                patientsIds[0] = user.getPatientIds();
                            }
                        } else {
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
        for (User user: Constants.SAMPLE_USERS) {
            if (user.getId().equals(userId)){
                return user;
            }
        }
        return null;
    }

    private void createLiveQuery(){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
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
                //my_todo_list.clear();
                for(QueryDocumentSnapshot document : queryDocumentSnapshots){
                  //  TodoItem todoItem = document.toObject(TodoItem.class);
                    //my_todo_list.add(todoItem);
                }
                //adapter.setList_of_todo_items(my_todo_list);
                //adapter.notifyDataSetChanged();
            }
        });

        referenceToCollection.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                //adapter.setList_of_todo_items(my_todo_list);
                //adapter.notifyDataSetChanged();
            }
        });

        referenceToCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                //adapter.setList_of_todo_items(my_todo_list);
                //adapter.notifyDataSetChanged();
            }
        });
    }

}
