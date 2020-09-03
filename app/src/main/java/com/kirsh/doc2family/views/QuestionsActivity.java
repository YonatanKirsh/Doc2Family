package com.kirsh.doc2family.views;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.solver.widgets.ConstraintAnchor;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.material.snackbar.Snackbar;
import com.kirsh.doc2family.R;
import com.kirsh.doc2family.logic.Communicator;
import com.kirsh.doc2family.logic.Constants;
import com.kirsh.doc2family.logic.Patient;
import com.kirsh.doc2family.logic.Question;

public class QuestionsActivity extends AppCompatActivity {

    private Patient mPatient;
    QuestionsAdapter mAdapter;

    Button submitQuestionButton;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);
        String patientId = getIntent().getStringExtra(Constants.PATIENT_ID_KEY);
        mPatient = Communicator.getPatientById(patientId);
        initQuestionsAdapter();
        initViews();
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
        initQuestionDialog();

    }

    private void setAddQuestionButton(){
        submitQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
    }

    private void initQuestionDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(QuestionsActivity.this);
        builder.setTitle("Add Question");

        // add edit text
        final EditText questionInput = new EditText(QuestionsActivity.this);
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

    public void onClickQuestion(Question question) {
        // todo something with question? treating-doctor can edit answer, asking-friend can edit question
    }
}
