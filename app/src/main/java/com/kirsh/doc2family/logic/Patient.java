package com.kirsh.doc2family.logic;

import java.util.ArrayList;
import java.util.Date;

public class Patient {
    private String mFirstName;
    private String mLastName;
    private String mId;
    private ArrayList<PatientUpdate> mUpdates;

    public Patient(String firstName, String lastName, String id, ArrayList<PatientUpdate> updates){
        this.mFirstName = firstName;
        this.mLastName = lastName;
        this.mId = id;
        this.mUpdates = updates;
    }

    public String getId() {
        return mId;
    }

    public String getFullName(){
        return mFirstName + " " + mLastName;
    }
}

