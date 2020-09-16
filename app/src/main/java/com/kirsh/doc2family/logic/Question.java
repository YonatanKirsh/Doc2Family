package com.kirsh.doc2family.logic;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Comparator;

public class Question{

    private Friend asker;
    private String question;
    private String answer;
    private LocalDateTime mDateAsked;
    private LocalDateTime mDateEdited;
    private boolean mIsAnswered = false;

    public Question(){}

    public Question(String question, String answer, LocalDateTime dateAsked, LocalDateTime dateEdited){
        this.question = question;
        this.answer = answer;
        mDateAsked = dateAsked;
        mDateEdited = dateEdited;
        if (answer != null && !answer.isEmpty()){
            mIsAnswered = true;
        }
    }

    public Question(String question, Friend asker){
        this.question = question;
        this.asker = asker;
        mDateAsked = LocalDateTime.now();
    }

    public Question(String question, String answer, Friend asker){
        this(question, asker);
        this.answer = answer;
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

    public Friend getAsker() {
        return asker;
    }

    public void setAsker(Friend asker) {
        this.asker = asker;
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
