package com.kirsh.doc2family.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.kirsh.doc2family.R;
import com.kirsh.doc2family.logic.Communicator;
import com.kirsh.doc2family.logic.Constants;
import com.kirsh.doc2family.logic.Friend;
import com.kirsh.doc2family.logic.Patient;
import com.kirsh.doc2family.logic.Question;
import com.kirsh.doc2family.logic.User;

public class QuestionsListActivity extends AppCompatActivity {

    private Patient mPatient;
    QuestionsAdapter mAdapter;

    Button submitQuestionButton;
    AlertDialog dialog;
    Gson gson = new Gson();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Init patient before deciding if we need tp show add question button
        initPatient();
        setContentView(R.layout.activity_questions_list);
        initQuestionsAdapter();
        initViews();
    }

    private void initPatient(){
        String patientString = getIntent().getStringExtra(Constants.PATIENT_ID_KEY);
        mPatient = gson.fromJson(patientString, Patient.class);
    }

    private void initQuestionsAdapter(){
        mAdapter = new QuestionsAdapter(this, mPatient.getQuestions());
        Communicator.createLiveQueryQuestionsList(mAdapter, mAdapter.getmDataset(), mPatient);
        mAdapter.notifyDataSetChanged();
    }

    private void initViews(){
        // submit new question button
        submitQuestionButton = findViewById(R.id.button_submit_question);

        Communicator.appearNewQuestionIfFriend(submitQuestionButton, this, mPatient);

        // questions adapter
        RecyclerView questionsAdapter = findViewById(R.id.recycler_questions);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        questionsAdapter.setLayoutManager(layoutManager);
        questionsAdapter.setAdapter(mAdapter);

        // questions dialog
        initNewQuestionDialog();

    }

    public void setAddQuestionButton(){
        submitQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
    }

    private void initNewQuestionDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(QuestionsListActivity.this);
        builder.setTitle("ADD QUESTION");

        // add edit text
        final EditText questionInput = new EditText(QuestionsListActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        questionInput.setLayoutParams(lp);
        builder.setView(questionInput);

        // Add the buttons
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked submit button - todo submit question
                String newQuestion = questionInput.getText().toString();
                if (!newQuestion.equals("")){

                    //todo add to list of questions of the patients  ?

                    Communicator.cAddQuestionForPatient(QuestionsListActivity.this, mPatient, newQuestion, mAdapter);
                    String message = "added question:\n" + newQuestion;
                    Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
                    questionInput.setText("");
                    dialog.dismiss();
                    //mAdapter.notifyDataSetChanged();

                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.cancel();
            }
        });
        // Create the AlertDialog
        dialog = builder.create();
    }

    private void showFriendEditQuestionDialog(final Question question){
        AlertDialog.Builder builder = new AlertDialog.Builder(QuestionsListActivity.this);
        // set view
        View view = getLayoutInflater().inflate(R.layout.question_friend_dialog, null);
        builder.setView(view);
        // add question info
        final EditText questionEditText = view.findViewById(R.id.question_dialog_edit_text_question_content);
        questionEditText.setText(question.getQuestion());
        final TextView answerTexView = view.findViewById(R.id.question_dialog_text_view_answer_content);
        if (question.isAnswered()){
            answerTexView.setText(question.getAnswer());
        }
        // Add the buttons
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked update button - todo change the update's content
                String newUpdate = questionEditText.getText().toString();
                String message = "updated to:\n" + newUpdate;
                Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
                questionEditText.setText("");
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.cancel();
            }
        });
        // Create the AlertDialog
        AlertDialog questionsDialog = builder.create();
        questionsDialog.show();
    }

    private void showCaregiverEditQuestionDialog(final Question question){
        AlertDialog.Builder builder = new AlertDialog.Builder(QuestionsListActivity.this);
        // set view
        View view = getLayoutInflater().inflate(R.layout.question_caregiver_dialog, null);
        builder.setView(view);
        // add question info
        final TextView questionTexView = view.findViewById(R.id.question_dialog_text_view_question_content);
        questionTexView.setText(question.getQuestion());
        final EditText answerEditText = view.findViewById(R.id.question_dialog_edit_text_answer_content);
        if (question.isAnswered()){
            answerEditText.setText(question.getAnswer());
        }
        // Add the buttons
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String newAnswer = answerEditText.getText().toString();
                String message = "updated to:\n" + newAnswer;
                long edited = System.currentTimeMillis();

                // update the question locally
                question.setAnswered(true);
                question.setAnswer(newAnswer);
                question.setmDateEdited(edited);
                mAdapter.notifyDataSetChanged();

                // update the db
                Communicator.updateanswerToQuestion(newAnswer, edited, question, mPatient);

                Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
                answerEditText.setText("");
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.cancel();
            }
        });
        // Create the AlertDialog
        AlertDialog questionsDialog = builder.create();
        questionsDialog.show();
    }

    public void onClickQuestion(Question question) {
        // todo show caregiver or friend??
        showCaregiverEditQuestionDialog(question);
    }
}
