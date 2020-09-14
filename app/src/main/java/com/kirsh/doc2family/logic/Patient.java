package com.kirsh.doc2family.logic;

import java.io.Serializable;
import java.util.ArrayList;

public class Patient implements Serializable {

    private String firstName;
    private String lastName;
    private String id;
    private String diagnosis;
    private ArrayList<Update> updates;
    private ArrayList<Question> questions;
    private ArrayList<String> friends;
    private ArrayList<String> caregiverIds;
    private String adminTz;
    private String tz;

    public Patient(){}

    public Patient(String firstName, String lastName, String id, String diagnosis, String tz){
        this.firstName = firstName;
        this.lastName = lastName;
        this.id = id;
        this.diagnosis = diagnosis;
        updates = new ArrayList<Update>();
        questions = new ArrayList<Question>();
        friends = new ArrayList<String>();
        caregiverIds = new ArrayList<String>();
        adminTz = "";
        this.tz = tz;
    }

    public String getId() {
        return id;
    }

    public String getFullName(){
        return firstName + " " + lastName;
    }

    public String getDiagnosis(){
        return diagnosis;
    }

    public ArrayList<Update> getUpdates(){
        return updates;
    }

    public ArrayList<Question> getQuestions(){
        return questions;
    }

    public ArrayList<String> getFriends(){
        return friends;
    }

    public ArrayList<String> getCaregiverIds(){
        return caregiverIds;
    }

    public void setCaregiverIds(ArrayList<String> caregiverIds) {
        this.caregiverIds = caregiverIds;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public void setFriends(ArrayList<String> friends) {
        this.friends = friends;
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }

    public void setUpdates(ArrayList<Update> updates) {
        this.updates = updates;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAdminTz() {
        return adminTz;
    }

    public void setAdminTz(String adminTz) {
        this.adminTz = adminTz;
    }

    public String getTz() {
        return tz;
    }

    public void setTz(String tz) {
        this.tz = tz;
    }


}

