package com.kirsh.doc2family.logic;

import java.util.ArrayList;

public class Patient {

    private String mFirstName;
    private String mLastName;
    private String mId;
    private String mDiagnosis;
    private ArrayList<Update> mUpdates;
    private ArrayList<Question> mQuestions;
    private ArrayList<Friend> mFriends;
    private ArrayList<String> mTreaterIds;

    public Patient(String firstName, String lastName, String id, String diagnosis, ArrayList<Update> updates, ArrayList<Question> questions, ArrayList<Friend> friends, ArrayList<String> treaterIds){
        mFirstName = firstName;
        mLastName = lastName;
        mId = id;
        mDiagnosis = diagnosis;
        mUpdates = updates;
        mQuestions = questions;
        mFriends = friends;
        mTreaterIds = treaterIds;
    }

    public String getId() {
        return mId;
    }

    public String getFullName(){
        return mFirstName + " " + mLastName;
    }

    public String getDiagnosis(){
        return mDiagnosis;
    }

    public ArrayList<Update> getUpdates(){
        return mUpdates;
    }

    public ArrayList<Question> getQuestions(){
        return mQuestions;
    }

    public ArrayList<Friend> getFriends(){
        return mFriends;
    }

    public ArrayList<String> getTreaterIds(){
        return mTreaterIds;
    }

}

