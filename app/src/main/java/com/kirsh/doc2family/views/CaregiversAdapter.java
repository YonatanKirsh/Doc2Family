package com.kirsh.doc2family.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kirsh.doc2family.R;
import com.kirsh.doc2family.logic.User;

import java.util.ArrayList;

public class CaregiversAdapter extends RecyclerView.Adapter<CaregiversAdapter.CaregiverHolder> {

    private ArrayList<User> mDataset;
    private Context mContext;

    public CaregiversAdapter(@NonNull Context context, ArrayList<User> dataset) {
        mDataset = dataset;
        mContext = context;
    }

    @NonNull
    @Override
    public CaregiverHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View caregiverItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.solo_text_view, parent, false);
        final CaregiverHolder caregiverHolder = new CaregiverHolder(caregiverItemView);
        caregiverItemView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                User currentUser = mDataset.get(caregiverHolder.getAbsoluteAdapterPosition());
                if (mContext instanceof CaregiversListActivity){
                    ((CaregiversListActivity)mContext).onLongClickCaregiver(currentUser);
                }
                return false;
            }
        });
        return caregiverHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CaregiverHolder holder, int position) {
        User user = mDataset.get(position);
        holder.textView.setText(user.getFullName());

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    public ArrayList<User> getmDataset() {
        return mDataset;
    }

    public void setmDataset(ArrayList<User> mDataset) {
        this.mDataset = mDataset;
    }

    static class CaregiverHolder extends RecyclerView.ViewHolder{

        TextView textView;

        public CaregiverHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_view_solo);
        }
    }
}
