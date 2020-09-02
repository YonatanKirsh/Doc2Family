package com.kirsh.doc2family.logic;

import java.util.ArrayList;

public class User {

    private String email;
    private String name;
    private String id;
    private boolean isDoctor;
    private ArrayList<Patient> patients = new ArrayList<>();

    public User(){}

    public User(String email, String name, String id, boolean isDoctor) {
        this.email = email;
        this.name = name;
        this.id = id;
        this.isDoctor = isDoctor;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<Patient> getPatients() {
        return patients;
    }

    public void setPatients(ArrayList<Patient> patients) {
        this.patients = patients;
    }

    public boolean isDoctor() {
        return isDoctor;
    }

    public void setDoctor(boolean doctor) {
        isDoctor = doctor;
    }


}
