package com.kirsh.doc2family.logic;

import java.util.ArrayList;

public class User {

    private String mEmail;
    private String mFirstName;
    private String mLastName;
    private String mId;
    private boolean mIsCareGiver;
    private ArrayList<String> mPatientIds = new ArrayList<>();

    public User(){}

    public User(String email, String firstName, String lastName, String id, boolean isCareGiver){
        mEmail = email;
        mFirstName = firstName;
        mLastName = lastName;
        mId = id;
        mIsCareGiver = isCareGiver;
    }

    public String getEmail() {
        return mEmail;
    }


    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    public String getLastName(){
        return mLastName;
    }

    public String getId() {
        return mId;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public void setId(String id) {
        mId = id;
    }

    public ArrayList<String> getPatientIds() {
        return mPatientIds;
    }

    public boolean isCareGiver() {
        return mIsCareGiver;
    }

    public void setIsCareGiver(boolean isCareGiver) {
        mIsCareGiver = isCareGiver;
    }

    public String getFullName(){
        return String.format("%s %s", getFirstName(), getLastName());
    }


    public void setmPatientIds(ArrayList<String> mPatientIds) {
        this.mPatientIds = mPatientIds;
    }


}
