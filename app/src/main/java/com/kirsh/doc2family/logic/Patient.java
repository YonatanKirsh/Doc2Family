package com.kirsh.doc2family.logic;

import java.util.ArrayList;

public class Patient {

    private String mFirstName;
    private String mLastName;
    private String mId;
    private ArrayList<Update> mUpdates;
    private ArrayList<Question> mQuestions;

    public Patient(String firstName, String lastName, String id, ArrayList<Update> updates, ArrayList<Question> questions){
        mFirstName = firstName;
        mLastName = lastName;
        mId = id;
        mUpdates = updates;
        mQuestions = questions;
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

    public void addUpdate(Update update){
        mUpdates.add(update);
    }

    public void addQuestion(Question question){
        mQuestions.add(question);
    }
}

