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

public class CaregiversAdapter extends UsersAdapter<CaregiversAdapter.CaregiverHolder> {

    public CaregiversAdapter(@NonNull Context context, ArrayList<User> dataset) {
        super(context, dataset);
    }

    @NonNull
    @Override
    public CaregiverHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View caregiverItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.solo_text_view, parent, false);
        final CaregiverHolder caregiverHolder = new CaregiverHolder(caregiverItemView);
        caregiverItemView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                User currentCaregiver = mDataset.get(caregiverHolder.getAbsoluteAdapterPosition());
                if (mContext instanceof CaregiversListActivity){
                    ((CaregiversListActivity)mContext).onLongClickCaregiver(currentCaregiver);
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

    static class CaregiverHolder extends RecyclerView.ViewHolder{

        TextView textView;

        public CaregiverHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_view_solo);
        }
    }
}
