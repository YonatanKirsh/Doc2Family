package com.kirsh.doc2family.logic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kirsh.doc2family.views.LoginActivity;
import com.kirsh.doc2family.views.SignUpActivity;

import java.util.ArrayList;

public class Communicator {

    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();

    //todo firebase / local db !!
    public static Patient getPatientById(String patientId){
        for (Patient patient: Constants.SAMPLE_PATIENTS) {
            if (patient.getId().equals(patientId)){
                return patient;
            }
        }
        return null;
    }

    public static void cCreateUserWithEmailAndPassword(String email, String password, final Context context, final String firstName, final String lastName, final boolean  mIsDoctor, final EditText mEmailEditText,
                                                       final EditText mPasswordEditText, final TextInputLayout mEmailLayout, final TextInputLayout mPasswordLayout){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("SIGN_UP_SUCCESS", "createUserWithEmail:success");
                            Toast.makeText(context, "Registration succeed!",
                                    Toast.LENGTH_SHORT).show();
                            mAuth.getCurrentUser().sendEmailVerification()
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
                            FirebaseUser user_auth = mAuth.getCurrentUser();
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

    private static void openActivityLogin(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    //todo firebase!
    public static ArrayList<Patient> getUsersPatients(String userId){
        return Constants.SAMPLE_PATIENTS;
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

}
