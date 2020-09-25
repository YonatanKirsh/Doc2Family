package com.kirsh.doc2family.logic;

import java.util.Comparator;

public class Question{

    private String asker;
    private String question;
    private String answer;
    private long mDateAsked;
    private long mDateEdited;
    private boolean mIsAnswered = false;

    public Question(){}

    public Question(String question, long dateAsked, long dateEdited, String asker){
        this.question = question;
        this.answer = null;
        mDateAsked = dateAsked;
        mDateEdited = dateEdited;
        this.asker = asker;
        mIsAnswered = false;

    }

    public Question(String question, String asker){
        this.question = question;
        this.asker = asker;
        mDateAsked = System.currentTimeMillis();
    }

    public Question(String question, String answer, String asker){
        this(question, asker);
        this.answer = answer;
        mDateAsked = System.currentTimeMillis();
        mIsAnswered = true;
    }

    public String getQuestion(){
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setAnswer(String answer){
        this.answer = answer;
//        mDateEdited = System.currentTimeMillis();
        mIsAnswered = true;
    }

    public boolean isAnswered(){
        return mIsAnswered;
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

    public boolean ismIsAnswered() {
        return mIsAnswered;
    }

    public void setmIsAnswered(boolean mIsAnswered) {
        this.mIsAnswered = mIsAnswered;
    }

    public String getAsker() {
        return asker;
    }

    public void setAsker(String asker) {
        this.asker = asker;
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
