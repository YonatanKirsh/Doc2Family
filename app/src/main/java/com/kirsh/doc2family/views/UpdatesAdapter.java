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
import com.kirsh.doc2family.logic.Update;
import com.kirsh.doc2family.logic.User;

import java.util.ArrayList;

public class UpdatesAdapter extends RecyclerView.Adapter<UpdatesAdapter.UpdateHolder> {

    private ArrayList<Update> mDataset;
    private Context mContext;


    public UpdatesAdapter(Context context, ArrayList<Update> dataset){
        dataset.sort(new Update.UpdateSorter());
        mDataset = dataset;
        mContext = context;
    }

    @NonNull
    @Override
    public UpdateHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View updateView = LayoutInflater.from(parent.getContext()).inflate(R.layout.update_item, parent, false);
        final UpdateHolder updateHolder = new UpdateHolder(updateView);
        updateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Update currentUpdate = mDataset.get(updateHolder.getAbsoluteAdapterPosition());
                if (mContext instanceof PatientInfoActivity){
                    ((PatientInfoActivity)mContext).onClickUpdate(currentUpdate);
                }
            }
        });
        return updateHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UpdateHolder holder, int position) {
        Update update = mDataset.get(position);
        holder.textViewDate.setText(update.getDateString());
        holder.textViewContent.setText(update.getContent());
        User issuer = Communicator.getUserById(update.getIssuingCareGiverId());
        holder.texViewIssuer.setText(issuer.getFullName());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    static class UpdateHolder extends RecyclerView.ViewHolder{

        TextView textViewContent;
        TextView texViewIssuer;
        TextView textViewDate;

        public UpdateHolder(@NonNull View itemView) {
            super(itemView);
            textViewContent = itemView.findViewById(R.id.update_item_text_view_content);
            textViewDate = itemView.findViewById(R.id.update_item_text_view_date_created);
            texViewIssuer = itemView.findViewById(R.id.update_item_text_view_issuer);
        }
    }
}
