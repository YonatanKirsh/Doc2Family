package com.kirsh.doc2family.logic;

import java.util.Comparator;

public class Question{
    private String id;
    private String askerID;
    private String answererId;
    private String questionContent;
    private String answerContent;
    private long mDateAsked;
    private long mDateEdited;
    private boolean isAnswered = false;

    public Question(){}

    public Question(String questionContent, long dateAsked, long dateEdited, String askerID){
        this.questionContent = questionContent;
        this.answerContent = null;
        mDateAsked = dateAsked;
        mDateEdited = dateEdited;
        this.askerID = askerID;
        isAnswered = false;
    }

    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getQuestionContent(){
        return questionContent;
    }

    public String getAnswerContent() {
        return answerContent;
    }

    public boolean isAnswered() {
        return isAnswered;
    }

    public void setAnswered(boolean answered) {
        isAnswered = answered;
    }

    public void setQuestionContent(String questionContent) {
        this.questionContent = questionContent;
    }

    public void setAnswerContent(String answerContent){
        this.answerContent = answerContent;
    }

    public String getAskerID() {
        return askerID;
    }

    public void setAskerID(String askerID) {
        this.askerID = askerID;
    }

    public String getAnswererId(){
        return answererId;
    }

    public void setAnswererId(String userId){
        answererId = userId;
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

    public void answerQuestion(String answer, String answererId){
        answerContent = answer;
        this.answererId = answererId;
        isAnswered = true;
//        setmDateEdited(System.currentTimeMillis());
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
