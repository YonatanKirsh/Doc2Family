package com.kirsh.doc2family.logic;

import java.util.ArrayList;

public class Constants {

    // messages
    public static final String EMAIL_INFO_MESSAGE = "Please enter a valid email address.";
    public static final String NICKNAME_INFO_MESSAGE = "Others will see this name when you follow a patient.\nNickname should be between 3 and 12 letters long.";
    public static final String PASSWORD_INFO_MESSAGE = "Password should be at least 4 characters long.";
    public static final String VERIFY_PASSWORD_INFO_MESSAGE = "Verify password - enter the same password again.";

    // keys
    public static final String PATIENT_ID_KEY = "patient_id";

    // validation functions
    public static boolean isLegalEmail(String email){
        return email.matches(".+@.+\\.[a-z]+");
    }

    public static boolean isLegalNickname(String username){
        return username.length() >= 3 && username.length() <= 12 && username.matches("[A-Za-z_0-9]+");
    }

    public static boolean isLegalPassword(String password){
        return password.length() >= 4;
    }

    // sample data
    public static final ArrayList<Patient> SAMPLE_PATIENTS = getSamplePatients();

    private static ArrayList<Patient> getSamplePatients(){
        ArrayList<Patient> patients = new ArrayList<>();
        patients.add(new Patient("John", "Snow", "js", new ArrayList<PatientUpdate>()));
        patients.add(new Patient("Deneris", "Targerijan", "dt", new ArrayList<PatientUpdate>()));
        patients.add(new Patient("Clark", "Kent", "ck", new ArrayList<PatientUpdate>()));
        patients.add(new Patient("Captain", "America", "ca", new ArrayList<PatientUpdate>()));
        return patients;
    }
}
