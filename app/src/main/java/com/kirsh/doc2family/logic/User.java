package com.kirsh.doc2family.logic;

import java.util.ArrayList;

public class User {

    private String email;
    private String firstName;
    private String lastName;
    private String id;
    private boolean careGiver;
    private ArrayList<Patient> patientIds = new ArrayList<>();
    String fullName;

    public User(){}

    public User(String email, String firstName, String lastName, String id, boolean careGiver){
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.id = id;
        this.careGiver = careGiver;
        fullName = String.format("%s %s", getFirstName(), getLastName());
    }

    public String getEmail() {
        return email;
    }

    public boolean isCareGiver() {
        return careGiver;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName(){
        return lastName;
    }

    public String getId() {
        return id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<Patient> getPatientIds() {
        return patientIds;
    }



    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setIsCareGiver(boolean isCareGiver) {
        this.careGiver = isCareGiver;
    }

    public String getFullName(){
        return String.format("%s %s", getFirstName(), getLastName());
    }

    public void setPatientIds(ArrayList<Patient> patientIds){
        this.patientIds = patientIds;
    }
}
