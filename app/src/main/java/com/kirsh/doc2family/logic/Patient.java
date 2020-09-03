package com.kirsh.doc2family.logic;

import java.util.ArrayList;

public class Patient {

    private String mFirstName;
    private String mLastName;
    private String mId;
    private ArrayList<Update> mUpdates;
    private ArrayList<Question> mQuestions;
    private ArrayList<Friend> mFriends;

    public Patient(String firstName, String lastName, String id, ArrayList<Update> updates, ArrayList<Question> questions, ArrayList<Friend> friends){
        mFirstName = firstName;
        mLastName = lastName;
        mId = id;
        mUpdates = updates;
        mQuestions = questions;
        mFriends = friends;
    }

    public String getId() {
        return mId;
    }

    public String getFullName(){
        return mFirstName + " " + mLastName;
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

}

