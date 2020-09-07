package com.kirsh.doc2family.logic;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

public class Update {

    String mContent;
    LocalDateTime mDateCreated;
    String mIssuingCareGiverId;
    private DateTimeFormatter mFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:mm");

    public Update(String careGiverId, String content, LocalDateTime date){
        mIssuingCareGiverId = careGiverId;
        mContent = content;
        mDateCreated = date;
    }

    public String getContent(){
        return mContent;
    }

    public LocalDateTime getDateCreated() {
        return mDateCreated;
    }

    public String getDateString(){
        return getDateCreated().format(mFormatter);
    }

    public String getIssuingCareGiverId() {
        return mIssuingCareGiverId;
    }

    public static class UpdateSorter implements Comparator<Update>{

        @Override
        public int compare(Update o1, Update o2) {
            return o2.getDateCreated().compareTo(o1.getDateCreated());
        }
    }
}
