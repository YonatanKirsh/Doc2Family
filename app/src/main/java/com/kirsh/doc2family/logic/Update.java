package com.kirsh.doc2family.logic;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

public class Update {

    String mContent;
    LocalDateTime mDate;
    String mIssuingCareGiverId;
    private DateTimeFormatter mFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:mm");

    public Update(String content, LocalDateTime date, String careGiverId){
        mContent = content;
        mDate = date;
        mIssuingCareGiverId = careGiverId;
    }

    public String getContent(){
        return mContent;
    }

    public LocalDateTime getDate() {
        return mDate;
    }

    public String getDateString(){
        return getDate().format(mFormatter);
    }

    public String getIssuingCareGiverId() {
        return mIssuingCareGiverId;
    }

    public static class UpdateSorter implements Comparator<Update>{

        @Override
        public int compare(Update o1, Update o2) {
            return o2.getDate().compareTo(o1.getDate());
        }
    }
}
