package com.kirsh.doc2family.logic;

import java.io.Serializable;

public class Friend implements Serializable {
    private String userId;
    private boolean admin;

    public Friend(){}

    public Friend(String userId, boolean admin)
    {
        this.userId = userId;
        this.admin = admin;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
