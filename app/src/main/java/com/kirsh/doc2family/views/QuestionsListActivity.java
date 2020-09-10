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

    static FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions_list);
        initPatient();
        initQuestionsAdapter();
        initViews();
    }

    private void initPatient(){
        mPatient = (Patient) getIntent().getSerializableExtra(Constants.PATIENT_ID_KEY);
//        mPatient = Communicator.getPatientById(patientId);
    }

    private void initQuestionsAdapter(){
        mAdapter = new QuestionsAdapter(this, mPatient.getQuestions());
        Communicator.createLiveQueryQuestionsList(mAdapter, mAdapter.getmDataset(), mPatient);
        mAdapter.notifyDataSetChanged();
    }

    private void initViews(){
        // submit new question button
        submitQuestionButton = findViewById(R.id.button_submit_question);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userID = auth.getCurrentUser().getUid();
        final User[] user = new User[1];
        db.collection("Users")
                .whereEqualTo("id", userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot myDoc : task.getResult()) {
                                user[0] = myDoc.toObject(User.class);
                                if (!user[0].isCareGiver()){
                                    submitQuestionButton.setVisibility(View.VISIBLE);
                                    setAddQuestionButton();
                                }
                            }
                        }
                        else {
                            Log.d("ErrorDoc", "Error getting documents: ", task.getException());
                            return;
                        }
                    }
                });

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
                if (newQuestion != null){
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    String askerID = auth.getCurrentUser().getUid();
                    Friend asker = Communicator.getFriendById(askerID);

                    Communicator.cAddQuestionForPatient(QuestionsListActivity.this, mPatient, newQuestion, asker);

                }
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
