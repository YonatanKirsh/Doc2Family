package com.kirsh.doc2family.logic;

import android.util.ArrayMap;

import java.util.ArrayList;
import java.util.HashMap;

public class Patient{

    private String firstName;
    private String lastName;
    private String id;
    private String diagnosis;
    private ArrayList<Update> updates;
    private ArrayList<Question> questions;
//    private ArrayList<Friend> friends;
    private HashMap<String, Friend> friends;
    private ArrayList<String> caregiverIds;
    private String tz;

    public Patient(){ }

    public Patient(String firstName, String lastName, String id, String diagnosis, String tz, String caregiverId){
        this.firstName = firstName;
        this.lastName = lastName;
        this.id = id;
        this.diagnosis = diagnosis;
        updates = new ArrayList<Update>();
        questions = new ArrayList<Question>();
        friends = new HashMap<>();
        caregiverIds = new ArrayList<String>();
        caregiverIds.add(caregiverId);
        this.tz = tz;
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
        return questions;
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

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
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

    public void removeFriend(String friendId){
        friends.remove(friendId);
    }

    public void addFriend(String userId, boolean isAdmin){
        Friend friend = new Friend(userId, isAdmin);
        friends.put(friend.getUserId(), friend);
    }

    public void addFriend(String userId){
        addFriend(userId, false);
    }

    private Friend getFriendWithId(String id){
        return friends.get(id);
    }

    public boolean userIsAdmin(String userId){
        Friend friend = getFriendWithId(userId);
        if (friend != null){
            return friend.isAdmin();
        }
        return false;
    }

    public boolean userIsCaregiver(String userId){
        return caregiverIds.contains(userId);
    }

    public void addAdmin(String userId){
        addFriend(userId, true);
    }

    public ArrayList<String> getAdminIds(){
        ArrayList<String> admins = new ArrayList<>();
        for (Friend friend : friends.values()){
            if (friend.isAdmin()){
                admins.add(friend.getUserId());
            }
        }
        return admins;
    }
}

