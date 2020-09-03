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

import com.google.android.material.snackbar.Snackbar;
import com.kirsh.doc2family.R;
import com.kirsh.doc2family.logic.Communicator;
import com.kirsh.doc2family.logic.Constants;
import com.kirsh.doc2family.logic.Friend;
import com.kirsh.doc2family.logic.Patient;

public class FriendsListActivity extends AppCompatActivity {

    private Patient mPatient;
    FriendsAdapter mAdapter;

    Button addFriendButton;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        initPatient();
        initFriendsadapter();
        initViews();
    }

    private void initPatient(){
        //todo handle no-key exception
        //todo unite with QuestionsActivity? move to Constants?
        String patientId = getIntent().getStringExtra(Constants.PATIENT_ID_KEY);
        mPatient = Communicator.getPatientById(patientId);
    }

    private void initFriendsadapter(){
        mAdapter = new FriendsAdapter(this, mPatient.getFriends());
    }

    private void initViews(){
        // add friend button
        addFriendButton = findViewById(R.id.button_add_friend);
        setAddFriendButton();

        // friends adapter
        RecyclerView friendsAdapter = findViewById(R.id.recycler_friends);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        friendsAdapter.setLayoutManager(layoutManager);
        friendsAdapter.setAdapter(mAdapter);

        // add friend dialog
        initAddFriendDialog();
    }

    private void setAddFriendButton(){
        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
    }

    private void initAddFriendDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(FriendsListActivity.this);
        builder.setTitle("Add Friend");

        // add edit text
        final EditText questionInput = new EditText(FriendsListActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        questionInput.setLayoutParams(lp);
        questionInput.setHint("Enter friend's email:");
        builder.setView(questionInput);

        // Add the buttons
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked add button - todo add friend
                String newQuestion = questionInput.getText().toString();
                String message = "added friend:\n" + newQuestion;
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

    public void onClickFriend(Friend friend) {
        //todo implement
    }
}
