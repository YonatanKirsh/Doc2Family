package com.kirsh.doc2family.logic;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Constants {

    // error messages
//    public static final String NULL_USER_ERROR_FORMAT_MESSAGE = "Could not find user with id: %s.";

    // keys
    public static final String PATIENT_AS_STRING_KEY = "patient_id";
    public static final String TZ_KEY = "tz";

    // fields
    public static final String PATIENTS_COLLECTION_FIELD = "Patients";
    public static final String USERS_COLLECTION_FIELD = "Users";
    public static final String PATIENT_IDS_FIELD = "patientIds";
    public static final String TZ_FIELD = "tz";

    // tags
    public static final String NULL_USER_TAG = "Null User";
    public static final String UNEXPECTED_TAG = "Unexpected";
    public static final String UPDATE_FIREBASE_TAG = "Updated firebase";

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

    private static ArrayList<Patient> getSamplePatients(){
        ArrayList<Patient> patients = new ArrayList<>();
//        patients.add(new Patient("John", "Snow", "js", "can come back to life", getSampleUpdates(), getSampleQuestions(), getSampleFriends(), getSampleCareGiverIds()));
//        patients.add(new Patient("Deneris", "Targerijan", "dt", "Power-Hungry", getSampleUpdates(), getSampleQuestions(), getSampleFriends(), getSampleCareGiverIds()));
//        patients.add(new Patient("Clark", "Kent", "ck", "boring, too powerful", getSampleUpdates(), getSampleQuestions(), getSampleFriends(), getSampleCareGiverIds()));
//        patients.add(new Patient("Captain", "America", "ca", "goody-good", getSampleUpdates(), getSampleQuestions(), getSampleFriends(), getSampleCareGiverIds()));
        return patients;
    }

    private static ArrayList<Update> getSampleUpdates(){
        //LocalDateTime firstTime = LocalDateTime.now().minusDays(10);
        ArrayList<Update> updates = new ArrayList<>();
        //updates.add(new Update(getSampleCareGiverIds().get(0), "patient just admitted. has severe fomo.", firstTime));
        //updates.add(new Update(getSampleCareGiverIds().get(0), "second update!!", firstTime.plusMinutes(1)));
        //updates.add(new Update(getSampleCareGiverIds().get(0), "started treating patient with hourly whiskey shots.", updates.get(updates.size()-1).getDateCreated().plusHours(3)));
        //updates.add(new Update(getSampleCareGiverIds().get(0), "patient is being a lil' bish- says he doesn't like whiskey.. wtf?", updates.get(updates.size()-1).getDateCreated().plusMinutes(15)));
        //updates.add(new Update(getSampleCareGiverIds().get(0), "patient stopped complaining, now loves whiskey", updates.get(updates.size()-1).getDateCreated().plusHours(3)));
        //updates.add(new Update(getSampleCareGiverIds().get(0), "patient is drunk.", updates.get(updates.size()-1).getDateCreated().plusHours(1)));
        return updates;
    }

    private static ArrayList<Question> getSampleQuestions(){
        ArrayList<Question> questions = new ArrayList<>();
        LocalDateTime oldTime = LocalDateTime.now().minusDays(10);
        LocalDateTime now = LocalDateTime.now();
//        questions.add(new Question("How did the operation go??", null, oldTime, null));
//        questions.add(new Question("did you start the new treatment?", "yes, just this morning.", oldTime, now.minusDays(1)));

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

    public static ArrayList<User> getSampleUsers(){
        ArrayList<User> users = new ArrayList<>();
        // add regular users
        //users.add(new User("user1@email.com", "Jake", "Peralta", "JP93", false));
        //users.add(new User("user2@email.com", "Elizabeth", "Doubtfire", "ED45", false));
        //users.add(new User("user3@email.com", "Raymond", "Holt", "RH52", false));

        // add doctors
        //users.add(new User("doctor1@email.com", "Dr.", "McDreamy", "DM78", true));
        //users.add(new User("doctor2@email.com", "Dr.", "Dre", "AD65", true));

        return users;
    }

    private static final ArrayList<String> getSampleCareGiverIds(){
        return new ArrayList<String>() {{
            add("AD65");
        }};
    }
}
