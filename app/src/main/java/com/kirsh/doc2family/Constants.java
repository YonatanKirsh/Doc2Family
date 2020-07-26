package com.kirsh.doc2family;

public class Constants {
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
