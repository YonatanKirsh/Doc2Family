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

import com.google.gson.Gson;
import com.kirsh.doc2family.R;
import com.kirsh.doc2family.logic.Communicator;
import com.kirsh.doc2family.logic.Constants;
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
        // add friend button
        addFriendButton = findViewById(R.id.button_add_friend);
        if (mPatient.userIsAdmin(communicator.getLocalUser().getId())){
            addFriendButton.setVisibility(View.VISIBLE);
        }
        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddFriendDialog();
            }
        });

        // friends adapter
        RecyclerView friendsAdapter = findViewById(R.id.recycler_friends);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        friendsAdapter.setLayoutManager(layoutManager);
        friendsAdapter.setAdapter(mAdapter);
    }

    private void showAddFriendDialog(){
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
                String newFriend = emailInput.getText().toString();
                communicator.addFriendAndUpdateCollections(newFriend, mPatient, FriendsListActivity.this, mAdapter);
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
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showEditFriendDialog(User friend){
        AlertDialog.Builder builder = new AlertDialog.Builder(FriendsListActivity.this);
        // set view
        View view = getLayoutInflater().inflate(R.layout.friend_dialog, null);
        builder.setView(view);
        // add friend info
//        User user = communicator.getUserById(friend.getId());
        final TextView friendNameTextView = view.findViewById(R.id.friend_dialog_text_view_friend_name);
        friendNameTextView.setText(friend.getFullName());
        final TextView friendEmailtextView = view.findViewById(R.id.friend_dialog_text_view_friend_email);
        friendEmailtextView.setText(friend.getEmail());
        final TextView isAdminTextView = view.findViewById(R.id.friend_dialog_text_view_is_admin);
        if (mPatient.hasAdminWithId(friend.getId())){
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

    private void addAdminPrivilegesToDialog(AlertDialog.Builder builder, View view, final User friend){
        // add admin views
        final CheckBox makeAdminCheckBox = view.findViewById(R.id.friend_dialog_checkbox_make_admin);
        makeAdminCheckBox.setVisibility(View.VISIBLE);
        if (mPatient.hasAdminWithId(friend.getId())){
            makeAdminCheckBox.setChecked(true);
        } else {
            makeAdminCheckBox.setChecked(false);
        }

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
                String friendId = friend.getId();
                boolean makeAdmin = makeAdminCheckBox.isChecked();
                // todo update friend with friendId isAdmin?
                dialog.dismiss();
            }
        });
    }

    private void confirmRemoveFriend(final User friendToRemove, final DialogInterface callingDialog){
//        final User user = friendToRemove;
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked - todo actually remove friend
                        Toast.makeText(FriendsListActivity.this, String.format("%s removed!", friendToRemove.getFullName()), Toast.LENGTH_LONG).show();
                        callingDialog.dismiss();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // No button clicked
                        break;
                }
            }
        };

        ConfirmDialog.show(this, dialogClickListener, String.format("Remove %s?", friendToRemove.getFullName()));
    }

    public void onClickFriend(User friend) {
        //showEditFriendDialog(friend);
    }

    @Override
    public void onRestart() {
        super.onRestart();
        //communicator.createLiveQueryPatientList(mAdapter, mAdapter.getmDataset());
        mAdapter.notifyDataSetChanged();
    }
}
