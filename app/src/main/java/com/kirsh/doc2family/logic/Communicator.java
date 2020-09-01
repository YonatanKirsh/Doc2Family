package com.kirsh.doc2family.logic;

import java.util.ArrayList;

public class Communicator {

    //todo firebase!
    public static Patient getPatientById(String id){
        for (Patient patient: Constants.SAMPLE_PATIENTS) {
            if (patient.getId().equals(id)){
                return patient;
            }
        }
        return null;
    }

    //todo firebase!
    public static ArrayList<Patient> getUsersPatients(String id){
        return Constants.SAMPLE_PATIENTS;
    }
}
