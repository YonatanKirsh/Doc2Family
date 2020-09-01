package com.kirsh.doc2family.logic;

import java.util.ArrayList;

public class User {
    String firstName;
    String lastName;
    String id;
    ArrayList<Patient> patients = new ArrayList<>();

    public User(){}

    public User(String firstName, String lastName){
        this.firstName = firstName;
        this.lastName = lastName;
    }

}
