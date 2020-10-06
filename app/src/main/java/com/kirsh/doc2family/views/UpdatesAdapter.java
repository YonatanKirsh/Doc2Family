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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UpdatesAdapter extends RecyclerView.Adapter<UpdatesAdapter.UpdateHolder> {

    private ArrayList<Update> mDataset;
    private Context mContext;
    private SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
    private Communicator communicator;


    public UpdatesAdapter(Context context, ArrayList<Update> dataset){
        communicator = Communicator.getSingleton();
        dataset.sort(new Update.UpdateSorter());
        mDataset = dataset;
        mContext = context;
    }

    @NonNull
    @Override
    public UpdateHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View updateView = LayoutInflater.from(parent.getContext()).inflate(R.layout.update_item, parent, false);
        final UpdateHolder updateHolder = new UpdateHolder(updateView);
        updateView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Update currentUpdate = mDataset.get(updateHolder.getAbsoluteAdapterPosition());
                if (mContext instanceof PatientInfoActivity){
                    ((PatientInfoActivity)mContext).onLongClickUpdate(currentUpdate);
                }
                return false;
            }
        });
        return updateHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final UpdateHolder holder, int position) {
        Update update = mDataset.get(position);
        Date resultdate = new Date(update.getDateCreated());
        holder.textViewDate.setText(sdf.format(resultdate));
        holder.textViewContent.setText(update.getContent());
        String userID = update.getIssuingCareGiverId();
        communicator.updateUpdateAdapterFullname(userID, holder);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public ArrayList<Update> getmDataset() {
        return mDataset;
    }

    public void setmDataset(ArrayList<Update> mDataset) {
        mDataset.sort(new Update.UpdateSorter());
        this.mDataset = mDataset;
    }

    public static class UpdateHolder extends RecyclerView.ViewHolder{

        TextView textViewContent;
        public TextView texViewIssuer;
        TextView textViewDate;

        public UpdateHolder(@NonNull View itemView) {
            super(itemView);
            textViewContent = itemView.findViewById(R.id.update_item_text_view_content);
            textViewDate = itemView.findViewById(R.id.update_item_text_view_date_created);
            texViewIssuer = itemView.findViewById(R.id.update_item_text_view_issuer);
        }
    }
}
