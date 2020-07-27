package com.kirsh.doc2family.logic;

import java.util.ArrayList;
import java.util.Date;

public class Patient {
    String firstName;
    String lastName;
    String id;
    ArrayList<Update> updates;


    public class Update {
        Date date;
        String message;
        Doctor givenByDoctor;
    }
}

