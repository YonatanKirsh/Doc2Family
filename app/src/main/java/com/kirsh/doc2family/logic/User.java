package com.kirsh.doc2family.logic;

import java.util.ArrayList;

public class User {
    String mFirstName;
    String mLastName;
    String mNickname;
    String mId;
    private String email;
    private boolean isDoctor;
    ArrayList<Patient> mPatients = new ArrayList<>();

//    public User(){}

    public User(String email, String nickname, String id, boolean isDoctor){
        this.mNickname = nickname;
        this.mId = id;
        this.email = email;
        this.isDoctor = isDoctor;
    }

    public String getEmail() {
        return email;
    }

    public String getNickname() {
        return mNickname;
    }

    public String getId() {
        return mId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNickname(String name) {
        this.mNickname = name;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public ArrayList<Patient> getPatients() {
        return mPatients;
    }

    public void setPatients(ArrayList<Patient> patients) {
        this.mPatients = patients;
    }

    public boolean isDoctor() {
        return isDoctor;
    }

    public void setDoctor(boolean doctor) {
        isDoctor = doctor;
    }


}
