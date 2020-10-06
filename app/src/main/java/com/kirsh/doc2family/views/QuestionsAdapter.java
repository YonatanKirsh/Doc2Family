package com.kirsh.doc2family.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kirsh.doc2family.R;
import com.kirsh.doc2family.logic.Communicator;
import com.kirsh.doc2family.logic.Question;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.QuestionHolder> {

    private ArrayList<Question> mDataset;
    private Context mContext;
    private SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
    private Communicator communicator;

    public QuestionsAdapter(Context context, ArrayList<Question> dataset){
        //todo sort by unanswered first for doctor, answered first for friend?
        communicator = Communicator.getSingleton();
//        dataset.sort(new Question.SortByLastEdited());
        mDataset = dataset;
        mContext = context;
    }

    public ArrayList<Question> getmDataset() {
        return mDataset;
    }

    public void setmDataset(ArrayList<Question> mDataset) {
        this.mDataset = mDataset;
    }

    @NonNull
    @Override
    public QuestionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View questionView = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_item, parent, false);
        final QuestionHolder questionHolder = new QuestionHolder(questionView);
        questionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Question currentQuestion = mDataset.get(questionHolder.getAbsoluteAdapterPosition());
                if (mContext instanceof QuestionsListActivity){
                    ((QuestionsListActivity)mContext).onClickQuestion(currentQuestion);
                }
            }
        });
        return questionHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionHolder holder, int position) {
        Question question = mDataset.get(position);
        holder.textViewQuestion.setText(question.getQuestionContent());
        communicator.updateUserFullname(question.getAskerID(), holder.textViewQuestionIssuer);
        if (question.isAnswered()){
            holder.textViewAnswer.setText(question.getAnswerContent());
            communicator.updateUserFullname(question.getAnswererId(), holder.textViewAnswerIssuer);
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    public static class QuestionHolder extends RecyclerView.ViewHolder{

        TextView textViewQuestion;
        TextView textViewAnswer;
        public TextView textViewQuestionIssuer;
        public TextView textViewAnswerIssuer;


        public QuestionHolder(@NonNull View itemView) {
            super(itemView);
            textViewQuestion = itemView.findViewById(R.id.text_view_question_content);
            textViewAnswer = itemView.findViewById(R.id.text_view_answer_content);
            textViewQuestionIssuer = itemView.findViewById(R.id.text_view_question_issuer);
            textViewAnswerIssuer = itemView.findViewById(R.id.text_view_answer_issuer);
        }
    }
}
