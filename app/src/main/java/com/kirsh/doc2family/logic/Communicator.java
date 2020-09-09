package com.kirsh.doc2family.logic;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Communicator {

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
//
//    //todo firebase! maayann
//    public static ArrayList<Patient> getUsersPatients(String userId){
//        return Constants.SAMPLE_PATIENTS;
//    }

    //    //todo firebase! TO TEST
    public static ArrayList<String> getUsersPatients(String userId){
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
    public static ArrayList<User> getPatientsCaregivers(String patientId){
        ArrayList<User> caregivers = new ArrayList<>();
        Patient patient = getPatientById(patientId);
        ArrayList<String> caregiverIds = patient.getCaregiverIds();
        for (String userId : caregiverIds) {
            caregivers.add(getUserById(userId));
        }
        return caregivers;
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
