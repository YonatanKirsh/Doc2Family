package com.kirsh.doc2family.logic;

import java.time.LocalDateTime;
import java.util.Comparator;

public class Update {
    String mContent;
    LocalDateTime mDate;
    Doctor mIssuingDoctor;

    public Update(String content, LocalDateTime date, Doctor doctor){
        mContent = content;
        mDate = date;
        mIssuingDoctor = doctor;
    }

    public String getContent(){
        return mContent;
    }

    public LocalDateTime getDate() {
        return mDate;
    }

    public Doctor getIssuingDoctor() {
        return mIssuingDoctor;
    }

    public static class UpdateSorter implements Comparator<Update>{

        @Override
        public int compare(Update o1, Update o2) {
            return o2.getDate().compareTo(o1.getDate());
        }
    }
}
