package com.kirsh.doc2family.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kirsh.doc2family.R;
import com.kirsh.doc2family.logic.Update;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class UpdatesAdapter extends RecyclerView.Adapter<UpdatesAdapter.UpdateHolder> {

    private ArrayList<Update> mDataset;
    private Context mContext;
    private DateTimeFormatter mFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:mm");

    public UpdatesAdapter(Context context, ArrayList<Update> dataset){
        dataset.sort(new Update.UpdateSorter());
        mDataset = dataset;
        mContext = context;
    }

    @NonNull
    @Override
    public UpdateHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View updateView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_update, parent, false);
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
        holder.textViewDate.setText(update.getDate().format(mFormatter));
        holder.textViewContent.setText(update.getContent());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    static class UpdateHolder extends RecyclerView.ViewHolder{

        TextView textViewDate;
        TextView textViewContent;

        public UpdateHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.text_view_update_date);
            textViewContent = itemView.findViewById(R.id.text_view_update_content);
        }
    }
}
