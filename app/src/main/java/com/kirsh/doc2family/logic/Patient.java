package com.kirsh.doc2family.logic;

import android.util.ArrayMap;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class Patient{

    private String firstName;
    private String lastName;
    private String id;
    private String diagnosis;
    private ArrayList<Update> updates;
//    private ArrayList<Question> questions;
    private HashMap<String, Question> questions;
    private HashMap<String, Friend> friends;
    private ArrayList<String> caregiverIds;
    private String tz;
    private int questionCounter;

    public Patient(){ }

    public Patient(String firstName, String lastName, String id, String diagnosis, String tz, String caregiverId){
        this.firstName = firstName;
        this.lastName = lastName;
        this.id = id;
        this.diagnosis = diagnosis;
        updates = new ArrayList<Update>();
        questions = new HashMap<>();
        friends = new HashMap<>();
        caregiverIds = new ArrayList<String>();
        caregiverIds.add(caregiverId);
        this.tz = tz;
        questionCounter = 0;
    }

    public int getQuestionCounter(){
        return questionCounter;
    }

    public String getId() {
        return id;
    }

    public String getFullName(){
        return firstName + " " + lastName;
    }

    public String getDiagnosis(){
        return diagnosis;
    }

    public ArrayList<Update> getUpdates(){
        return updates;
    }

    public ArrayList<Question> getQuestions(){
        ArrayList<Question> toReturn = new ArrayList<>();
        if (questions != null){
            toReturn.addAll(questions.values());
        }
        return toReturn;
    }

    public ArrayList<Friend> getFriends(){
        ArrayList<Friend> toReturn = new ArrayList<>();
        if (friends != null){
            toReturn.addAll(friends.values());
        }
        return toReturn;
    }

    public ArrayList<String> getFriendIds(){
        ArrayList<String> ids = new ArrayList<>();
        for (Friend friend : getFriends()) {
            ids.add(friend.getUserId());
        }
        return ids;
    }

    public ArrayList<String> getCaregiverIds(){
        return caregiverIds;
    }

    public void setQuestionCounter(int count){
        questionCounter = count;
    }

    public void setCaregiverIds(ArrayList<String> caregiverIds) {
        this.caregiverIds = caregiverIds;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public void setFriends(ArrayList<Friend> friendsList) {
        HashMap<String, Friend> updatedFriends = new HashMap<>();
        for (Friend friend : friendsList){
            updatedFriends.put(friend.getUserId(), friend);
        }
        this.friends = updatedFriends;
    }

    public void setQuestions(ArrayList<Question> questionsList) {
        HashMap<String, Question> updatedQuestions = new HashMap<>();
        for (Question question : questionsList){
            updatedQuestions.put(question.getId(), question);
        }
        this.questions = updatedQuestions;
    }

    public void setUpdates(ArrayList<Update> updates) {
        this.updates = updates;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getTz() {
        return tz;
    }

    public void setTz(String tz) {
        this.tz = tz;
    }

    public void removeCaregiver(String caregiverId){
        caregiverIds.remove(caregiverId);
    }

    public void removeFriend(String userId){
        friends.remove(userId);
    }

    public void updateFriend(Friend friend){
        if (friends.containsKey(friend.getUserId())){
            friends.put(friend.getUserId(), friend);
        }
        else {
            Log.w(Constants.UNEXPECTED_TAG, "updateFriend: patient does not have friend with id " + friend.getUserId());
        }
    }

    public void makeFriendAdmin(String userId){
        if (friends.containsKey(userId)){
            friends.get(userId).setAdmin(true);
        }
        else {
            Log.w(Constants.UNEXPECTED_TAG, "updateFriend: patient does not have friend with id " + userId);
        }
    }

    public void addFriend(String userId, boolean isAdmin){
        Friend friend = new Friend(userId, isAdmin);
        friends.put(friend.getUserId(), friend);
    }

    public void addFriend(String userId){
        addFriend(userId, false);
    }

    public void addCaregiver(String userId){
        caregiverIds.add(userId);
    }

    public Friend getFriendWithId(String id){
        return friends.get(id);
    }

    public boolean hasAdminWithId(String userId){
        Friend friend = getFriendWithId(userId);
        if (friend != null){
            return friend.isAdmin();
        }
        return false;
    }

    public boolean hasCaregiverWithId(String userId){
        return caregiverIds.contains(userId);
    }

    public boolean hasFriendWithId(String userId){
        return friends.containsKey(userId);
    }

    public void addAdmin(String userId){
        addFriend(userId, true);
    }

    public boolean hasAdmin(){
        for (Friend friend : friends.values()){
            if (friend.isAdmin()){
                return true;
            }
        }
        return false;
    }

    public boolean userHasAdminPrivilege(String userId){
        // give admin rights to caregivers
        return hasAdminWithId(userId) || hasCaregiverWithId(userId);
    }

    public void addQuestion(Question question){
        question.setId(String.valueOf(questionCounter++));
        questions.put(question.getId(), question);
    }

    public void deleteQuestion(Question question){
        questions.remove(question.getId());
    }

    public void updateQuestion(Question question){
        if (questions.containsKey(question.getId())){
            questions.put(question.getId(), question);
        }
        Log.w(Constants.UNEXPECTED_TAG, "updateQuestion: question does not exist for this patient");
    }

    public void addUpdate(Update update){
        updates.add(update);
    }

    public void removeUpdate(Update toRemove){
        // could probably do this better
        for (Update update : updates){
            if (update.getIssuingCareGiverId().equals(toRemove.getIssuingCareGiverId()) &&
                    update.getContent().equals(toRemove.getContent()) &&
                    update.getDateCreated() == toRemove.getDateCreated()){
                updates.remove(update);
            }
        }
    }
}

