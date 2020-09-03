package com.kirsh.doc2family.logic;

public class Doctor extends User {

    public Doctor(String firstName, String lastName, String id) {
        super(firstName, lastName, id);
    }

    public void addPatient(Patient patient){
        mPatients.add(patient);
        getPatients().add(patient);
    }
}
