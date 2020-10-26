package com.jrnspark.whatsappcustom.user;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jrnspark.whatsappcustom.R;
import com.jrnspark.whatsappcustom.message.MessageActivity;

import java.util.ArrayList;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListViewHolder> {
    private static final String TAG = "UserListAdapter";
    ArrayList<UserObject> userList;


    public UserListAdapter(ArrayList<UserObject> userList) {
        this.userList = userList;

    }

    @NonNull
    @Override
    public UserListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, null, false);

        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(params);

        UserListViewHolder viewHolder = new UserListViewHolder(view);
        return viewHolder;
    }

    static Boolean canProceed = true;

    @Override
    public void onBindViewHolder(@NonNull UserListViewHolder holder, final int position) {
        holder.mName.setText(userList.get(position).getName());
        holder.mPhone.setText(userList.get(position).getPhone());
        holder.mUserItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final String userid = FirebaseAuth.getInstance().getUid();
                final String receiverid = userList.get(position).getUid();
                final DatabaseReference dsref = FirebaseDatabase.getInstance().getReference().child("user").child(userid).child("chat").child(receiverid);

                final Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (canProceed) {
                            String chatKey = FirebaseDatabase.getInstance().getReference().child("chats").push().getKey();

                            FirebaseDatabase.getInstance().getReference().child("chats").child(chatKey).setValue("A message");

                            FirebaseDatabase.getInstance().getReference().child("user").child(receiverid).child("chat").child(userid).setValue(chatKey);
                            FirebaseDatabase.getInstance().getReference().child("user").child(userid).child("chat").child(receiverid).setValue(chatKey);
                        }
                        Intent intent = new Intent(v.getContext(),MessageActivity.class);
                        intent.putExtra("userid",userid);
                        intent.putExtra("receiverid",receiverid);
                        v.getContext().startActivity(intent);
                    }
                });
                Thread thread2 = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        dsref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists())
                                    canProceed = false;
                                else
                                    canProceed = true;
                                Log.d(TAG, "onDataChange: canProceedValue" + canProceed.toString());
                                thread.start();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.d(TAG, "onCancelled: in.. error is :" + error.getMessage());
                            }
                        });
                    }
                });
                thread2.start();
            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }


    public class UserListViewHolder extends RecyclerView.ViewHolder {
        public TextView mName, mPhone;
        public LinearLayout mUserItem;

        public UserListViewHolder(View view) {
            super(view);
            mName = view.findViewById(R.id.name);
            mPhone = view.findViewById(R.id.phone);
            mUserItem = view.findViewById(R.id.userItem);

        }
    }
}
