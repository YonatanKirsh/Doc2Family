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
import android.widget.Toast;

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
        String patientString = getIntent().getStringExtra(Constants.PATIENT_ID_KEY);
        mPatient = gson.fromJson(patientString, Patient.class);
    }

    private void initCaregiversAdapter() {
        ArrayList<String> careGiverIds = mPatient.getCaregiverIds();
        ArrayList<User> careGivers = new ArrayList<User>();
        mAdapter = new CaregiversAdapter(this, careGivers);
        communicator.getUsersByIds(mAdapter, mAdapter.getmDataset(), careGiverIds);
        mAdapter.notifyDataSetChanged();
    }

    private void initViews() {
        // caregivers activity
        RecyclerView caregiversRecycler = findViewById(R.id.recycler_caregivers);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        caregiversRecycler.setLayoutManager(layoutManager);
        caregiversRecycler.setAdapter(mAdapter);

        // add-caregiver button
        //addCaregiverButton = findViewById(R.id.caregivers_list_button_goto_add_caregiver);
        //addCaregiverButton.setOnClickListener(new View.OnClickListener() {
          //  @Override
           // public void onClick(View v) {
            //    showAddCaregiverDialog();
            //}
        //});
    }

    private void showAddCaregiverDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(CaregiversListActivity.this);
        builder.setTitle(R.string.add_caregiver);

        // add edit text
        final EditText emailInput = new EditText(CaregiversListActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        emailInput.setLayoutParams(lp);
        emailInput.setHint(R.string.caregiver_email_hint);
        builder.setView(emailInput);

        // Add the buttons
        builder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked add button - todo add caregiver
                String newCaregiver = emailInput.getText().toString();
                String message = "added caregiver:\n" + newCaregiver;
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
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void onLongClickCaregiver(final User currentUser) {
        // init builder, get diagnosis
        //AlertDialog.Builder builder = new AlertDialog.Builder(CaregiversListActivity.this);
        //String titleToFormat = this.getString(R.string.remove_caregiver_format);
        //final String nameToRemove = currentUser.getFullName();
        //builder.setTitle(String.format(titleToFormat, nameToRemove));

        // set cancel button
        //builder.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
            //@Override
          //  public void onClick(DialogInterface dialog, int which) {
              //  dialog.dismiss();
            //}
        //});

        // set update button
        //builder.setNegativeButton(R.string.remove, new DialogInterface.OnClickListener() {
          //  @Override
            //public void onClick(DialogInterface dialog, int which) {
                // todo actually remove caregiver. leave at least one caregiver!
              //  Toast.makeText(CaregiversListActivity.this, String.format("removed %s", nameToRemove), Toast.LENGTH_LONG).show();
            //}
        //});

        //builder.show();
    }
}
