package com.kirsh.doc2family.logic;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class Update{

    String content;
    long dateCreated;
    String issuingCareGiverId;


    public Update(){}

    public Update(String careGiverId, String content, long date){
        issuingCareGiverId = careGiverId;
        this.content = content;
        dateCreated = date;
    }

    public String getContent(){
        return content;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public String getIssuingCareGiverId() {
        return issuingCareGiverId;
    }


    public void setContent(String content) {
        this.content = content;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setIssuingCareGiverId(String issuingCareGiverId) {
        this.issuingCareGiverId = issuingCareGiverId;
    }

    public static class UpdateSorter implements Comparator<Update>{

        @Override
        public int compare(Update o1, Update o2) {
            if(o1.getDateCreated() > o2.getDateCreated()){
                return -1;
            }
            else if (o1.getDateCreated() < o2.getDateCreated()){
                return 1;
            }
            return 0;
        }
    }
}
