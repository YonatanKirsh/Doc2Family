package com.kirsh.doc2family;

public class Constants {

    public static final String EMAIL_INFO_MESSAGE = "Please enter a valid email address.";
    public static final String NICKNAME_INFO_MESSAGE = "Others will see this name when you follow a patient.\nNickname should be between 3 and 12 letters long.";
    public static final String PASSWORD_INFO_MESSAGE = "Password should be at least 4 characters long.";
    public static final String VERIFY_PASSWORD_INFO_MESSAGE = "Verify password - enter the same password again.";

    public static boolean isLegalEmail(String email){
        return email.matches(".+@.+\\.[a-z]+");
    }

    public static boolean isLegalNickname(String username){
        return username.length() >= 3 && username.length() <= 12 && username.matches("[A-Za-z_0-9]+");
    }

    public static boolean isLegalPassword(String password){
        return password.length() >= 4;
    }
}
