package com.kirsh.doc2family.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kirsh.doc2family.R;
import com.kirsh.doc2family.logic.Question;

import java.util.ArrayList;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.QuestionHolder> {

    private ArrayList<Question> mDataset;
    private Context mContext;

    public QuestionsAdapter(Context context, ArrayList<Question> dataset){
        //todo sort by unanswered first for doctor, answered first for friend?
        dataset.sort(new Question.SortByLastEdited());
        mDataset = dataset;
        mContext = context;
    }

    @NonNull
    @Override
    public QuestionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View questionView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_question, parent, false);
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
        holder.textViewQuestion.setText(question.getQuestion());
        if (question.isAnswered()){
            holder.textViewAnswer.setText(question.getAnswer());
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    static class QuestionHolder extends RecyclerView.ViewHolder{

        TextView textViewQuestion;
        TextView textViewAnswer;

        public QuestionHolder(@NonNull View itemView) {
            super(itemView);
            textViewQuestion = itemView.findViewById(R.id.text_view_question_content);
            textViewAnswer = itemView.findViewById(R.id.text_view_answer_content);
        }
    }
}
