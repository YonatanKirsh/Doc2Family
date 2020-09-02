package com.kirsh.doc2family.logic;

import java.util.ArrayList;

public class User {
    String email;
    String name;
    String id;
    ArrayList<Patient> patients = new ArrayList<>();

    public User(){}

    public User(String email, String name, String id) {
        this.email = email;
        this.name = name;
        this.id = id;
    }
}
