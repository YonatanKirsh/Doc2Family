package com.kirsh.doc2family.views;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kirsh.doc2family.logic.User;

import java.util.ArrayList;

public abstract class UsersAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    ArrayList<User> mDataset;
    Context mContext;

    public UsersAdapter(@NonNull Context context, ArrayList<User> dataset){
        mDataset = dataset;
        mContext = context;
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

}
