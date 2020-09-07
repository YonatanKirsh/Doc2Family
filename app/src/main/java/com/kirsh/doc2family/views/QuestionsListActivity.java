package com.kirsh.doc2family.views;

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

import com.google.android.material.snackbar.Snackbar;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions_list);
        initPatient();
        initQuestionsAdapter();
        initViews();
    }

    private void initPatient(){
        String patientId = getIntent().getStringExtra(Constants.PATIENT_ID_KEY);
        mPatient = Communicator.getPatientById(patientId);
    }

    private void initQuestionsAdapter(){
        mAdapter = new QuestionsAdapter(this, mPatient.getQuestions());
    }

    private void initViews(){
        // submit new question button
        submitQuestionButton = findViewById(R.id.button_submit_question);
        setAddQuestionButton();

        // questions adapter
        RecyclerView questionsAdapter = findViewById(R.id.recycler_questions);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        questionsAdapter.setLayoutManager(layoutManager);
        questionsAdapter.setAdapter(mAdapter);

        // questions dialog
        initNewQuestionDialog();

    }

    private void setAddQuestionButton(){
        submitQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
    }

    private void initNewQuestionDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(QuestionsListActivity.this);
        builder.setTitle("Add Question");

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
                String message = "added question:\n" + newQuestion;
                Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
                questionInput.setText("");
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
        dialog = builder.create();
    }

    private void showFriendEditQuestionDialog(Question question){
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

    private void showCaregiverEditQuestionDialog(Question question){
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
                // User clicked update button - todo change the update's content
                String newUpdate = answerEditText.getText().toString();
                String message = "updated to:\n" + newUpdate;
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
