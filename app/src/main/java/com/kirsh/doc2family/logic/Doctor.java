package com.kirsh.doc2family.logic;

public class Doctor extends User {

    public void addPatient(Patient patient){
        getPatients().add(patient);
    }
}
