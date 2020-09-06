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

    public static final ArrayList<User> SAMPLE_USERS = getSampleUsers();

    private static final ArrayList<String> SAMPLE_TREATER_IDS = new ArrayList<String>() {{
        add("AD65");
    }};

    private static ArrayList<Patient> getSamplePatients(){
        ArrayList<Patient> patients = new ArrayList<>();
        patients.add(new Patient("John", "Snow", "js", getSampleUpdates(), getSampleQuestions(), getSampleFriends(), SAMPLE_TREATER_IDS));
        patients.add(new Patient("Deneris", "Targerijan", "dt", getSampleUpdates(), getSampleQuestions(), getSampleFriends(), SAMPLE_TREATER_IDS));
        patients.add(new Patient("Clark", "Kent", "ck", getSampleUpdates(), getSampleQuestions(), getSampleFriends(), SAMPLE_TREATER_IDS));
        patients.add(new Patient("Captain", "America", "ca", getSampleUpdates(), getSampleQuestions(), getSampleFriends(), SAMPLE_TREATER_IDS));
        return patients;
    }

    private static ArrayList<Update> getSampleUpdates(){
        LocalDateTime firstTime = LocalDateTime.now().minusDays(10);
        ArrayList<Update> updates = new ArrayList<>();
        updates.add(new Update("patient just admitted. has severe fomo.", firstTime, SAMPLE_TREATER_IDS.get(0)));
        updates.add(new Update("second update!!", firstTime.plusMinutes(1), SAMPLE_TREATER_IDS.get(0)));
        updates.add(new Update("started treating patient with hourly whiskey shots.", updates.get(updates.size()-1).getDate().plusHours(3), SAMPLE_TREATER_IDS.get(0)));
        updates.add(new Update("patient is being a lil' bish- says he doesn't like whiskey.. wtf?", updates.get(updates.size()-1).getDate().plusMinutes(15), SAMPLE_TREATER_IDS.get(0)));
        updates.add(new Update("patient stopped complaining, now loves whiskey", updates.get(updates.size()-1).getDate().plusHours(3), SAMPLE_TREATER_IDS.get(0)));
        updates.add(new Update("patient is drunk.", updates.get(updates.size()-1).getDate().plusHours(1), SAMPLE_TREATER_IDS.get(0)));
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
        for (User user : getSampleUsers()) {
            String id = user.getId();
            // use this user as treating doctor, not friend
            if (id.equals("DM78")){
                continue;
            }
            // add mom as admin
            if (id.equals("ED45")){
                friends.add(new Friend(id, true));
                continue;
            }
            // add everyone else as regular friends
            friends.add(new Friend(id, false));
        }

        return friends;
    }

    private static ArrayList<User> getSampleUsers(){
        ArrayList<User> users = new ArrayList<>();
        // add regular users
        users.add(new User("user1@email.com", "Jake", "Peralta", "JP93", false));
        users.add(new User("user2@email.com", "Elizabeth", "Doubtfire", "ED45", false));
        users.add(new User("user3@email.com", "Raymond", "Holt", "RH52", false));

        // add doctors
        users.add(new User("doctor1@email.com", "Derek", "McDreamy", "DM78", true));
        users.add(new User("doctor2@email.com", "Andre", "Dre", "AD65", true));

        return users;
    }

}
