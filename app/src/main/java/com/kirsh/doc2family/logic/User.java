package com.kirsh.doc2family.logic;

import java.util.ArrayList;

public class User {

    private String email;
    private String firstName;
    private String lastName;
    private String id;
    private boolean careGiver;
    private ArrayList<String> patientIds;
    String fullName;
    private String tz;

    public User(){}

    public User(String email, String firstName, String lastName, String id, boolean careGiver, String tz){
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.id = id;
        this.careGiver = careGiver;
        fullName = String.format("%s %s", getFirstName(), getLastName());
        patientIds = new ArrayList<>();
        this.tz = tz;
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

    public ArrayList<String> getPatientIds() {
        return patientIds;
    }

    public void removePatientId(String patientId){
        patientIds.remove(patientId);
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

    public void setPatientIds(ArrayList<String> patientIds){
        this.patientIds = patientIds;
    }

    public void addPatientId(String id){
        if (!patientIds.contains(id)){
            patientIds.add(id);
        }
    }

    public String getTz() {
        return tz;
    }

    public void setTz(String tz) {
        this.tz = tz;
    }

}
