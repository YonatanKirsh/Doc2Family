package com.kirsh.doc2family.logic;

public class Doctor extends User {

    public Doctor(String email, String nickname, String id) {
        super(email, nickname, id, true);
    }

    public void addPatient(Patient patient){
        mPatients.add(patient);
    }
}
