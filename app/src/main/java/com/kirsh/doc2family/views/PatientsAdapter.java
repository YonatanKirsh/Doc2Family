package com.kirsh.doc2family.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kirsh.doc2family.R;
import com.kirsh.doc2family.logic.Communicator;
import com.kirsh.doc2family.logic.Patient;

import java.util.ArrayList;

public class PatientsAdapter extends RecyclerView.Adapter<PatientsAdapter.PatientHolder> {

    private ArrayList<Patient> mDataset;
    private Context mContext;

    public PatientsAdapter(@NonNull Context context) {
        //todo userId? here? from where?
        mDataset = Communicator.getUsersPatients("userId");

        mContext = context;
    }

    @NonNull
    @Override
    public PatientHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View patientView = LayoutInflater.from(parent.getContext()).inflate(R.layout.name_in_adapter, parent, false);
        final PatientHolder patientHolder = new PatientHolder(patientView);
        patientView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Patient currentPatient = mDataset.get(patientHolder.getAbsoluteAdapterPosition());
                if (mContext instanceof ListPatientsActivity){
                    ((ListPatientsActivity)mContext).onClickPatient(currentPatient);
                }
            }
        });
        return patientHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PatientHolder holder, int position) {
        Patient patient = mDataset.get(position);
        holder.textView.setText(patient.getFullName());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    static class PatientHolder extends RecyclerView.ViewHolder{

        TextView textView;

        public PatientHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_view_name_in_adapter);
        }
    }
}
