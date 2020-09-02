package com.kirsh.doc2family.logic;

import java.util.ArrayList;

public class User {
    String mFirstName;
    String mLastName;
    String mId;
    ArrayList<Patient> mPatients = new ArrayList<>();

//    public User(){}

    public User(String firstName, String lastName, String id){
        this.mFirstName = firstName;
        this.mLastName = lastName;
        this.mId = id;
    }
}
