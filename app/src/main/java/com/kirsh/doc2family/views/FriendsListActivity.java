package com.kirsh.doc2family.views;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.kirsh.doc2family.R;
import com.kirsh.doc2family.logic.Communicator;
import com.kirsh.doc2family.logic.Constants;
import com.kirsh.doc2family.logic.Friend;
import com.kirsh.doc2family.logic.Patient;
import com.kirsh.doc2family.logic.User;

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
        builder.setTitle(R.string.add_friend);

        // add edit text
        final EditText emailInput = new EditText(FriendsListActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        emailInput.setLayoutParams(lp);
        emailInput.setHint(R.string.friends_email_hint);
        builder.setView(emailInput);

        // Add the buttons
        builder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked add button - todo add friend
                String newFriend = emailInput.getText().toString();
                String message = "added friend:\n" + newFriend;
                Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
                emailInput.setText("");
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.cancel();
            }
        });
        // Create the AlertDialog
        dialog = builder.create();
    }

    private void showEditFriendDialog(Friend friend){
        AlertDialog.Builder builder = new AlertDialog.Builder(FriendsListActivity.this);
        // set view
        View view = getLayoutInflater().inflate(R.layout.view_friend_dialog, null);
        builder.setView(view);
        // add friend info
        User user = Communicator.getUserById(friend.getUserId());
        final TextView friendNameTextView = view.findViewById(R.id.friend_dialog_text_view_friend_name);
        friendNameTextView.setText(user.getFullName());
        final TextView friendEmailtextView = view.findViewById(R.id.friend_dialog_text_view_friend_email);
        friendEmailtextView.setText(user.getEmail());
        final TextView isAdminTextView = view.findViewById(R.id.friend_dialog_text_view_is_admin);
        if (friend.isAdmin()){
            isAdminTextView.setText(R.string.admin);
        }else {
            isAdminTextView.setText(R.string.not_admin);
        }
        // todo if user is admin- add admin privileges
        addAdminPrivilegesToDialog(builder, view, friend);
        // Add the buttons
        builder.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.cancel();
            }
        });
        // Create the AlertDialog
        AlertDialog questionsDialog = builder.create();
        questionsDialog.show();
    }

    private void addAdminPrivilegesToDialog(AlertDialog.Builder builder, View view, final Friend friend){
        // add admin views
        final CheckBox makeAdminCheckBox = view.findViewById(R.id.friend_dialog_checkbox_make_admin);
//        final Button removeFriendButton = view.findViewById(R.id.friend_dialog_button_remove_friend);
        makeAdminCheckBox.setVisibility(View.VISIBLE);
//        removeFriendButton.setVisibility(View.VISIBLE);
        if (friend.isAdmin()){
            makeAdminCheckBox.setChecked(true);
        } else {
            makeAdminCheckBox.setChecked(false);
        }

        // set remove friend button
//        removeFriendButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                confirmRemoveFriend(friend);
//            }
//        });

        builder.setNeutralButton(R.string.remove_friend, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                confirmRemoveFriend(friend, dialog);
            }
        });

        // set update button
        builder.setNegativeButton(R.string.update, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked submit button - todo update friend
                String friendId = friend.getUserId();
                boolean makeAdmin = makeAdminCheckBox.isChecked();
                // todo update friend with friendId isAdmin?
                dialog.dismiss();
            }
        });
    }

    private void confirmRemoveFriend(Friend friendToRemove, final DialogInterface callingDialog){
        final User user = Communicator.getUserById(friendToRemove.getUserId());
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked - todo actually remove friend
                        Toast.makeText(FriendsListActivity.this, String.format("%s removed!", user.getFullName()), Toast.LENGTH_LONG).show();
                        callingDialog.dismiss();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // No button clicked
                        break;
                }
            }
        };

        ConfirmDialog.show(this, dialogClickListener, String.format("Remove %s?", user.getFullName()));
    }

    public void onClickFriend(Friend friend) {
        showEditFriendDialog(friend);
    }
}
