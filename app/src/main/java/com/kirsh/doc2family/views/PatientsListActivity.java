package com.kirsh.doc2family.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.kirsh.doc2family.R;
import com.kirsh.doc2family.logic.Communicator;
import com.kirsh.doc2family.logic.Constants;
import com.kirsh.doc2family.logic.Patient;

import java.util.ArrayList;


public class PatientsListActivity extends AppCompatActivity {

    PatientsAdapter mAdapter;
    Button addPatientButton;

    Gson gson = new Gson();
    ItemTouchHelper.SimpleCallback itemTouchHelper;
    private Paint p = new Paint();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients_list);
        initPatientAdapter();
        initViews();
    }

    public void initViews() {
        // patients adapter
        RecyclerView patientsRecycler = findViewById(R.id.recycler_patients);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        patientsRecycler.setLayoutManager(layoutManager);

        // init swipe
        initSwipe();
        new ItemTouchHelper(itemTouchHelper).attachToRecyclerView(patientsRecycler);
        patientsRecycler.setAdapter(mAdapter);

        // add-patient button if the user is CareGiver
        addPatientButton = findViewById(R.id.button_goto_add_patient);
        Communicator.appearAddPatientIfCaregiver(addPatientButton);
        addPatientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initEnterTzDialog();
            }
        });
    }

    public void initSwipe(){
        itemTouchHelper = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // remove the current patient from adapter
                ArrayList<Patient> mDataset = mAdapter.getmDataset();
                final Patient currentPatient = mDataset.get(viewHolder.getAbsoluteAdapterPosition());
                mDataset.remove(currentPatient);
                mAdapter.setmDataset(mDataset);

                // remove the current patient from the db
                Communicator.removePatientFromUserAndUpdate(currentPatient);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if(dX < 0){
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.delete_final);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    }
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            };
        };
    }

    private void initEnterTzDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PatientsListActivity.this);
        builder.setTitle(" ENTER THE TZ'S PATIENT");

        // add edit text
        final EditText updateInput = new EditText(PatientsListActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        updateInput.setLayoutParams(lp);
        builder.setView(updateInput);

        // Add the buttons
        final boolean[] flag = {false};
        final Patient[] patient = {null};
        builder.setPositiveButton("ENTER", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String updateMess = updateInput.getText().toString();
                if (!updateMess.equals("")) {
                    Communicator.checkNewPatientExistence(updateMess, flag, patient, PatientsListActivity.this, findViewById(android.R.id.content));
                    mAdapter.notifyDataSetChanged();
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
        builder.show();
    }

    private void initPatientAdapter() {
        //todo maybe send a null array in patients instead of communicator.
        FirebaseAuth myAuth = FirebaseAuth.getInstance();
        final FirebaseUser myUser = myAuth.getCurrentUser();
        ArrayList<Patient> patients = Communicator.getPatientsListForUser(myUser.getUid());
        mAdapter = new PatientsAdapter(this, patients);
        Communicator.createLiveQueryPatientList(mAdapter, mAdapter.getmDataset());
        mAdapter.notifyDataSetChanged();
    }

    public void onClickPatient(Patient patient) {
        mAdapter.notifyDataSetChanged();
        openActivityPatientInfo(patient);
    }

    public void openActivityAddPatient() {
        Intent intent = new Intent(this, AddPatientActivity.class);
        startActivity(intent);
        mAdapter.notifyDataSetChanged();
    }

    private void openActivityPatientInfo(Patient patient) {
        Intent intent = new Intent(this, PatientInfoActivity.class);
        String patientString = gson.toJson(patient);
        intent.putExtra(Constants.PATIENT_ID_KEY, patientString);
        startActivity(intent);
    }

    @Override
    public void onRestart() {
        super.onRestart();
        Communicator.createLiveQueryPatientList(mAdapter, mAdapter.getmDataset());
        mAdapter.notifyDataSetChanged();
    }

}
