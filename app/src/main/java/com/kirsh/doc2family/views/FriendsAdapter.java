package com.kirsh.doc2family.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kirsh.doc2family.R;
import com.kirsh.doc2family.logic.Constants;
import com.kirsh.doc2family.logic.Friend;
import com.kirsh.doc2family.logic.Patient;

import java.util.ArrayList;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendHolder> {

    private ArrayList<Friend> mDataset;
    private Context mContext;

    public FriendsAdapter(Context context, ArrayList<Friend> friends){
        mDataset = friends;
        mContext = context;
    }

    @NonNull
    @Override
    public FriendHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View friendView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_solo_text_view, parent ,false);
        final FriendHolder friendHolder = new FriendHolder(friendView);
        friendView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Friend currentFriend = mDataset.get(friendHolder.getAbsoluteAdapterPosition());
                if (mContext instanceof FriendsListActivity){
                    ((FriendsListActivity)mContext).onClickFriend(currentFriend);
                }
            }
        });
        return friendHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendHolder holder, int position) {
        Friend friend = mDataset.get(position);
        holder.textView.setText(friend.getName());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    static class FriendHolder extends RecyclerView.ViewHolder{

        TextView textView;

        public FriendHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_view_solo);
        }
    }
}
