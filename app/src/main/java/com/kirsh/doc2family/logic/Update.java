package com.kirsh.doc2family.logic;

import java.time.LocalDateTime;
import java.util.Comparator;

public class Update {
    String mContent;
    LocalDateTime mDate;
    String mIssuingTreaterId;

    public Update(String content, LocalDateTime date, String treaterId){
        mContent = content;
        mDate = date;
        mIssuingTreaterId = treaterId;
    }

    public String getContent(){
        return mContent;
    }

    public LocalDateTime getDate() {
        return mDate;
    }

    public String getIssuingTreaterId() {
        return mIssuingTreaterId;
    }

    public static class UpdateSorter implements Comparator<Update>{

        @Override
        public int compare(Update o1, Update o2) {
            return o2.getDate().compareTo(o1.getDate());
        }
    }
}
