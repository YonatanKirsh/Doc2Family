package com.kirsh.doc2family.logic;

import java.util.ArrayList;

public class Communicator {

    //todo firebase / local db !!
    public static Patient getPatientById(String patientId){
        for (Patient patient: Constants.SAMPLE_PATIENTS) {
            if (patient.getId().equals(patientId)){
                return patient;
            }
        }
        return null;
    }

    //todo firebase! maayann
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
