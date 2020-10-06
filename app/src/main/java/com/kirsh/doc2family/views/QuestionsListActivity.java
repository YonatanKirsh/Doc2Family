package com.kirsh.doc2family.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.kirsh.doc2family.R;
import com.kirsh.doc2family.logic.Communicator;
import com.kirsh.doc2family.logic.Constants;
import com.kirsh.doc2family.logic.Patient;
import com.kirsh.doc2family.logic.Question;

public class QuestionsListActivity extends AppCompatActivity {

    private Patient mPatient;
    QuestionsAdapter mAdapter;

    Button submitQuestionButton;
    AlertDialog dialog;
    Gson gson = new Gson();
    Communicator communicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Init patient before deciding if we need tp show add question button
        communicator = Communicator.getSingleton();
        initPatient();
        setContentView(R.layout.activity_questions_list);
        initQuestionsAdapter();
        initViews();
    }

    private void initPatient(){
        String patientString = getIntent().getStringExtra(Constants.PATIENT_AS_STRING_KEY);
        mPatient = gson.fromJson(patientString, Patient.class);
    }

    private void initQuestionsAdapter(){
        mAdapter = new QuestionsAdapter(this, mPatient.getQuestions());
        communicator.createLiveQueryQuestionsAdapter(mAdapter, mPatient);
        mAdapter.notifyDataSetChanged();
    }

    private void initViews(){
        // submit new question button
        submitQuestionButton = findViewById(R.id.button_submit_question);
        setAddQuestionButton();
        if (mPatient.getFriendIds().contains(communicator.getLocalUser().getId())){
            submitQuestionButton.setVisibility(View.VISIBLE);
        }

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
                    communicator.addQuestionForPatient(mPatient, newQuestion);
                    String message = "added question:\n" + newQuestion;
                    Toast.makeText(QuestionsListActivity.this, message, Toast.LENGTH_LONG).show();
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
        questionEditText.setText(question.getQuestionContent());
        final TextView answerTexView = view.findViewById(R.id.question_dialog_text_view_answer_content);
        if (question.isAnswered()){
            answerTexView.setText(question.getAnswerContent());
            return;
        }
        // Add the buttons
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked update button - todo change the update's content
                String newQuestionContent = questionEditText.getText().toString();
                String message = "updated to:\n" + newQuestionContent;
                question.setQuestionContent(newQuestionContent);
                communicator.updateQuestionForPatient(mPatient, question, QuestionsListActivity.this);
                questionEditText.setText("");
                dialog.dismiss();
                long edited = System.currentTimeMillis();
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
        questionTexView.setText(question.getQuestionContent());
        final EditText answerEditText = view.findViewById(R.id.question_dialog_edit_text_answer_content);
        if (question.isAnswered()){
            answerEditText.setText(question.getAnswerContent());
        }
        // Add the buttons
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String newAnswer = answerEditText.getText().toString();
                long edited = System.currentTimeMillis();
                question.answerQuestion(newAnswer, communicator.getLocalUser().getId());
                communicator.updateQuestionForPatient(mPatient, question, QuestionsListActivity.this);
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

    public void onClickQuestion(final Question question) {
        if (mPatient.hasCaregiverWithId(communicator.getLocalUser().getId())){
            showCaregiverEditQuestionDialog(question);
        }
        else{
            showFriendEditQuestionDialog(question);
        }
    }
}
