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
import android.widget.TextView;

import com.google.gson.Gson;
import com.kirsh.doc2family.R;
import com.kirsh.doc2family.logic.Communicator;
import com.kirsh.doc2family.logic.Constants;
import com.kirsh.doc2family.logic.Friend;
import com.kirsh.doc2family.logic.Patient;
import com.kirsh.doc2family.logic.User;

import java.util.ArrayList;

public class FriendsListActivity extends AppCompatActivity {

    private Patient mPatient;
    FriendsAdapter mAdapter;

    Button addFriendButton;
    Gson gson = new Gson();
    Communicator communicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        communicator = Communicator.getSingleton();
        initPatient();
        initFriendsadapter();
        initViews();
    }

    private void initPatient(){
        String patientString = getIntent().getStringExtra(Constants.PATIENT_AS_STRING_KEY);
        mPatient = gson.fromJson(patientString, Patient.class);
    }

    private void initFriendsadapter(){
        ArrayList<User> friendsList = new ArrayList<>();
        mAdapter = new FriendsAdapter(this, friendsList);
        communicator.createLiveQueryFriendsAdapter(mPatient, mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void initViews(){
        // set title
        TextView title = findViewById(R.id.activity_friends_list_text_view_title);
        title.setText(String.format(getString(R.string.friends_list_title_format), mPatient.getFirstName()));

        // add friend button
        addFriendButton = findViewById(R.id.activity_friends_list_button_add_friend);
        if (mPatient.userHasAdminPrivilege(communicator.getLocalUser().getId())){
            addFriendButton.setVisibility(View.VISIBLE);
        }
        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddFriendDialog();
            }
        });

        // friends adapter
        RecyclerView friendsAdapter = findViewById(R.id.activity_friends_list_recycler_friends);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        friendsAdapter.setLayoutManager(layoutManager);
        friendsAdapter.setAdapter(mAdapter);
    }

    private void showAddFriendDialog(){
        // init builder
        AlertDialog.Builder builder = new AlertDialog.Builder(FriendsListActivity.this);
        builder.setTitle(R.string.add_friend);
        View view = getLayoutInflater().inflate(R.layout.add_friend_dialog, null);
        builder.setView(view);
        final EditText friendTzEditText = view.findViewById(R.id.add_friend_dialog_edit_text_friend_id);
        final CheckBox adminCheckbox = view.findViewById(R.id.add_friend_dialog_checkbox_make_admin);

        // Add the buttons
        builder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String friendTz = friendTzEditText.getText().toString();
                communicator.addFriendToPatient(friendTz, mPatient, adminCheckbox.isChecked(), FriendsListActivity.this);
                friendTzEditText.setText("");
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
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showEditFriendDialog(User friend){
        AlertDialog.Builder builder = new AlertDialog.Builder(FriendsListActivity.this);
        // set view
        View view = getLayoutInflater().inflate(R.layout.edit_friend_dialog, null);
        builder.setView(view);
        // add friend info
        final TextView friendNameTextView = view.findViewById(R.id.edit_friend_dialog_text_view_friend_name);
        friendNameTextView.setText(friend.getFullName());
        final TextView friendEmailtextView = view.findViewById(R.id.edit_friend_dialog_text_view_friend_email);
        friendEmailtextView.setText(friend.getEmail());
        final TextView isAdminTextView = view.findViewById(R.id.edit_friend_dialog_text_view_is_admin);
        if (mPatient.hasAdminWithId(friend.getId())){
            isAdminTextView.setText(R.string.admin);
        }else {
            isAdminTextView.setText(R.string.not_admin);
        }
        if (mPatient.userHasAdminPrivilege(communicator.getLocalUser().getId())){
            addAdminPrivilegesToDialog(builder, view, friend);
        }
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

    private void addAdminPrivilegesToDialog(AlertDialog.Builder builder, View view, final User friendAsUser){
        // add admin views
        final CheckBox makeAdminCheckBox = view.findViewById(R.id.edit_friend_dialog_checkbox_make_admin);
        makeAdminCheckBox.setVisibility(View.VISIBLE);
        if (mPatient.hasAdminWithId(friendAsUser.getId())){
            makeAdminCheckBox.setChecked(true);
        } else {
            makeAdminCheckBox.setChecked(false);
        }

        builder.setNeutralButton(R.string.remove_friend, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                confirmRemoveFriend(friendAsUser, dialog);
            }
        });

        // set update button
        builder.setNegativeButton(R.string.update, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // get friend, update admin status
                boolean makeAdmin = makeAdminCheckBox.isChecked();
                Friend friend = mPatient.getFriendWithId(friendAsUser.getId());
                friend.setAdmin(makeAdmin);
                // send to comm
                communicator.updateFriendInPatient(mPatient, friend, FriendsListActivity.this);
                dialog.dismiss();
            }
        });
    }

    private void confirmRemoveFriend(final User friendToRemove, final DialogInterface callingDialog){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        // confirm
                        communicator.removeFriendFromPatient(mPatient, friendToRemove.getId(), FriendsListActivity.this);
                        callingDialog.dismiss();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // regret
                        break;
                }
            }
        };
        ConfirmDialog.show(this, dialogClickListener);
//        ConfirmDialog.show(this, dialogClickListener, String.format("Remove %s?", friendToRemove.getFullName()));
    }

    public void onClickFriend(User friend) {
        showEditFriendDialog(friend);
    }

    @Override
    public void onRestart() {
        super.onRestart();
        //communicator.createLiveQueryPatientList(mAdapter, mAdapter.getmDataset());
        mAdapter.notifyDataSetChanged();
    }
}
