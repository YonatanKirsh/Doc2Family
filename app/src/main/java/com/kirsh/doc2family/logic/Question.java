package com.kirsh.doc2family.logic;

import java.util.Comparator;

public class Question{
    private String askerID;
    private String question;
    private String answer;
    private long mDateAsked;
    private long mDateEdited;
    private boolean isAnswered = false;

    public Question(){}

    public Question(String question, long dateAsked, long dateEdited, String askerID){
        this.question = question;
        this.answer = null;
        mDateAsked = dateAsked;
        mDateEdited = dateEdited;
        this.askerID = askerID;
        isAnswered = false;
    }

    public Question(String question, String askerID){
        this.question = question;
        this.askerID = askerID;
        mDateAsked = System.currentTimeMillis();
    }

    public Question(String question, String answer, String askerID){
        this(question, askerID);
        this.answer = answer;
        mDateAsked = System.currentTimeMillis();
        isAnswered = true;
    }

    public String getQuestion(){
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public boolean isAnswered() {
        return isAnswered;
    }

    public void setAnswered(boolean answered) {
        isAnswered = answered;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setAnswer(String answer){
        this.answer = answer;
        isAnswered = true;
    }

    public String getAskerID() {
        return askerID;
    }

    public void setAskerID(String askerID) {
        this.askerID = askerID;
    }

    public long getDateAsked(){
        return mDateAsked;
    }

    public long getmDateEdited(){
        if (mDateEdited != 0){
            return mDateEdited;
        }
        return mDateAsked;
    }






    public void setmDateEdited(long mDateEdited) {
        this.mDateEdited = mDateEdited;
    }


    public static class SortByLastEdited implements Comparator<Question>{

        @Override
        public int compare(Question o1, Question o2) {
            if(o1.getmDateEdited() > o2.getmDateEdited()){
                return -1;
            }
            else if (o1.getmDateEdited() < o2.getmDateEdited()){
                return 1;
            }
            return 0;
        }
    }

    public static class SortByUnansweredFirst implements Comparator<Question>{

        @Override
        public int compare(Question o1, Question o2) {
            // un-answered is first
            if (!o1.isAnswered() && o2.isAnswered()){
                return -1;
            }
            if (o1.isAnswered() && !o2.isAnswered()){
                return 1;
            }
            // last-edited otherwise
            return new SortByLastEdited().compare(o1, o2);
        }
    }
}
