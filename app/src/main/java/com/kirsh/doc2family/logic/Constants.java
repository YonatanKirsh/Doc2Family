package com.kirsh.doc2family.logic;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Constants {

    // messages
    public static final String EMAIL_INFO_MESSAGE = "Please enter a valid email address.";
    public static final String FIRSTNAME_INFO_MESSAGE = "Others will see this name when you follow a patient.\nFirst name should be between 3 and 20 characters long.";
    public static final String LASTNAME_INFO_MESSAGE = "Others will see this name when you follow a patient.\nLast name should be between 3 and 20 characters long.";

    public static final String PASSWORD_INFO_MESSAGE = "Password should be at least 6 characters long.";
    public static final String VERIFY_PASSWORD_INFO_MESSAGE = "Verify password - enter the same password again.";

    // keys
    public static final String PATIENT_ID_KEY = "patient_id";

    // validation functions
    public static boolean isLegalEmail(String email){
        return email.matches(".+@.+\\.[a-z]+");
    }

    public static boolean isLegalNickname(String username){
        return username.length() >= 3 && username.length() <= 20 && username.matches("[A-Za-z_0-9. ]+");
    }

    public static boolean isLegalPassword(String password){
        return password.length() >= 6;
    }

    // sample data
    public static final ArrayList<Patient> SAMPLE_PATIENTS = getSamplePatients();

    private static ArrayList<Patient> getSamplePatients(){
        ArrayList<Patient> patients = new ArrayList<>();
        patients.add(new Patient("John", "Snow", "js", getSampleUpdates(), getSampleQuestions(), getSampleFriends()));
        patients.add(new Patient("Deneris", "Targerijan", "dt", getSampleUpdates(), getSampleQuestions(), getSampleFriends()));
        patients.add(new Patient("Clark", "Kent", "ck", getSampleUpdates(), getSampleQuestions(), getSampleFriends()));
        patients.add(new Patient("Captain", "America", "ca", getSampleUpdates(), getSampleQuestions(), getSampleFriends()));
        return patients;
    }

    private static ArrayList<Update> getSampleUpdates(){
        Doctor doctor = new Doctor("myemail", "Dr. McDreamy","DM93");;
        LocalDateTime firstTime = LocalDateTime.now().minusDays(10);
        ArrayList<Update> updates = new ArrayList<>();
        updates.add(new Update("patient just admitted. has severe fomo.", firstTime, doctor));
        updates.add(new Update("second update!!", firstTime.plusMinutes(1), doctor));
        updates.add(new Update("started treating patient with hourly whiskey shots.", updates.get(updates.size()-1).getDate().plusHours(3), doctor));
        updates.add(new Update("patient is being a lil' bish- says he doesn't like whiskey.. wtf?", updates.get(updates.size()-1).getDate().plusMinutes(15), doctor));
        updates.add(new Update("patient stopped complaining, now loves whiskey", updates.get(updates.size()-1).getDate().plusHours(3), doctor));
        updates.add(new Update("patient is drunk.", updates.get(updates.size()-1).getDate().plusHours(1), doctor));
        return updates;
    }

    private static ArrayList<Question> getSampleQuestions(){
        ArrayList<Question> questions = new ArrayList<>();
        LocalDateTime oldTime = LocalDateTime.now().minusDays(10);
        LocalDateTime now = LocalDateTime.now();
        questions.add(new Question("How did the operation go??", null, oldTime, null));
        questions.add(new Question("did you start the new treatment?", "yes, just this morning.", oldTime, now.minusDays(1)));

        return questions;
    }

    private static ArrayList<Friend> getSampleFriends(){
        ArrayList<Friend> friends = new ArrayList<>();
        friends.add(new Friend("bestFriend@mail.com", "best friend", "bestfriendId"));
        friends.add(new Friend("mom@mail.com", "mom", "momId"));
        friends.add(new Friend("daughter@mail.com", "Johnny English", "JEId"));
        return friends;
    }
}
