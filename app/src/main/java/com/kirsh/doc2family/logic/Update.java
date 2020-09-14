package com.kirsh.doc2family.logic;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Date;

public class Update  implements  Serializable{

    String mContent;
    long mDateCreated;
    String mIssuingCareGiverId;
    private SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");

    public Update(){}

    public Update(String careGiverId, String content, long date){
        mIssuingCareGiverId = careGiverId;
        mContent = content;
        mDateCreated = date;
    }

    public String getContent(){
        return mContent;
    }

    public long getDateCreated() {
        return mDateCreated;
    }

    public String getDateString(){

        Date resultdate = new Date(getDateCreated());
        return sdf.format(resultdate);
    }

    public String getIssuingCareGiverId() {
        return mIssuingCareGiverId;
    }


    public void setmContent(String mContent) {
        this.mContent = mContent;
    }

    public void setmDateCreated(long mDateCreated) {
        this.mDateCreated = mDateCreated;
    }

    public void setmIssuingCareGiverId(String mIssuingCareGiverId) {
        this.mIssuingCareGiverId = mIssuingCareGiverId;
    }

    public static class UpdateSorter implements Comparator<Update>{

        @Override
        public int compare(Update o1, Update o2) {
            return o2.getDateString().compareTo(o1.getDateString());
        }
    }
}
