package com.kirsh.doc2family.logic;

import java.time.LocalDateTime;
import java.util.Comparator;

public class Question {

    private Friend mAsker;
    private String mQuestion;
    private String mAnswer;
    private LocalDateTime mDateAsked;
    private LocalDateTime mDateEdited;
    private boolean mIsAnswered = false;

    public Question(String question, String answer, LocalDateTime dateAsked, LocalDateTime dateEdited){
        mQuestion = question;
        mAnswer = answer;
        mDateAsked = dateAsked;
        mDateEdited = dateEdited;
        if (answer != null && !answer.isEmpty()){
            mIsAnswered = true;
        }
    }

    public Question(String question){
        mQuestion = question;
        mDateAsked = LocalDateTime.now();
    }

    public Question(String question, String answer){
        this(question);
        mAnswer = answer;
        mIsAnswered = true;
    }

    public String getQuestion(){
        return mQuestion;
    }

    public String getAnswer() {
        return mAnswer;
    }

    public void setAnswer(String answer){
        mAnswer = answer;
        mDateEdited = LocalDateTime.now();
        mIsAnswered = true;
    }

    public boolean isAnswered(){
        return mIsAnswered;
    }

    public LocalDateTime getDateAsked(){
        return mDateAsked;
    }

    public LocalDateTime getDateEdited(){
        if (mDateEdited != null){
            return mDateEdited;
        }
        return mDateAsked;
    }

    public static class SortByLastEdited implements Comparator<Question>{

        @Override
        public int compare(Question o1, Question o2) {
            return o2.getDateEdited().compareTo(o1.getDateEdited());
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
