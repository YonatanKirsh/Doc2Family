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
import com.google.gson.Gson;
import com.kirsh.doc2family.R;
import com.kirsh.doc2family.logic.Communicator;
import com.kirsh.doc2family.logic.Constants;
import com.kirsh.doc2family.logic.Patient;
import com.kirsh.doc2family.logic.User;

import java.util.ArrayList;

public class CaregiversListActivity extends AppCompatActivity {

    private Patient mPatient;
    CaregiversAdapter mAdapter;
    Button addCaregiverButton;
    Gson gson = new Gson();
    Communicator communicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caregivers_list);
        communicator = Communicator.getSingleton();
        initPatient();
        initCaregiversAdapter();
        initViews();
    }

    private void initPatient(){
        String patientString = getIntent().getStringExtra(Constants.PATIENT_AS_STRING_KEY);
        mPatient = gson.fromJson(patientString, Patient.class);
    }

    private void initCaregiversAdapter() {
        ArrayList<User> careGivers = new ArrayList<>();
        mAdapter = new CaregiversAdapter(this, careGivers);
        communicator.createLiveQueryCaregiversAdapter(mPatient, mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void initViews() {
        // caregivers activity
        RecyclerView caregiversRecycler = findViewById(R.id.recycler_caregivers);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        caregiversRecycler.setLayoutManager(layoutManager);
        caregiversRecycler.setAdapter(mAdapter);

//         add-caregiver button
        addCaregiverButton = findViewById(R.id.caregivers_list_button_goto_add_caregiver);
        if (mPatient.hasCaregiverWithId(communicator.getLocalUser().getId())){
            addCaregiverButton.setVisibility(View.VISIBLE);
        }
        addCaregiverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddCaregiverDialog();
            }
        });
    }

    private void showAddCaregiverDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(CaregiversListActivity.this);
        builder.setTitle(R.string.add_caregiver);

        // add edit text
        final EditText tzInput = new EditText(CaregiversListActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        tzInput.setLayoutParams(lp);
        tzInput.setHint(R.string.caregiver_tz_hint);
        builder.setView(tzInput);

        // Add the buttons
        builder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked add button
                String caregiverTz = tzInput.getText().toString();
                communicator.addCaregiverToPatient(mPatient, caregiverTz, CaregiversListActivity.this);
                tzInput.setText("");
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

    public void onLongClickCaregiver(final User caregiverAsUser) {
        // only allow caregivers to remove other caregivers
        if (!mPatient.hasCaregiverWithId(communicator.getLocalUser().getId())){
            return;
        }
        // init builder, get diagnosis
        AlertDialog.Builder builder = new AlertDialog.Builder(CaregiversListActivity.this);
        String titleToFormat = this.getString(R.string.remove_caregiver_format);
        final String nameToRemove = caregiverAsUser.getFullName();
        builder.setTitle(String.format(titleToFormat, nameToRemove));

        // set cancel button
        builder.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // set remove caregiver button
        builder.setNegativeButton(R.string.remove, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                confirmRemoveCaregiver(caregiverAsUser.getId(), dialog);
            }
        });
        builder.show();
    }

    private void confirmRemoveCaregiver(final String caregiverId, final DialogInterface callingDialog){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        // confirm
                        communicator.removeCaregiverFromPatient(mPatient, caregiverId, CaregiversListActivity.this);
                        callingDialog.dismiss();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // regret
                        break;
                }
            }
        };
        ConfirmDialog.show(this, dialogClickListener);
    }
}
