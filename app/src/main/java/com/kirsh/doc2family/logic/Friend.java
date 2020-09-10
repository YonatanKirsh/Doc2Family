package com.kirsh.doc2family.logic;

import java.io.Serializable;

public class Friend implements Serializable {

    private String mUserId;
    private boolean mIsAdmin;

    public Friend(){}

    public Friend(String userId, boolean isAmdin)
    {
        mUserId = userId;
        mIsAdmin = isAmdin;
    }

    public String getUserId(){
        return mUserId;
    }

    public boolean isAdmin(){
        return mIsAdmin;
    }
}
