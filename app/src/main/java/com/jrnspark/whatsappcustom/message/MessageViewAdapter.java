package com.jrnspark.whatsappcustom.message;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jrnspark.whatsappcustom.R;

import java.util.ArrayList;

public class MessageViewAdapter extends RecyclerView.Adapter<MessageViewAdapter.ViewHolder> {
    private static final String TAG = "MessageViewAdapter";
    ArrayList<MessageObject> messagesList;


    public MessageViewAdapter(ArrayList<MessageObject> messagesList) {
        this.messagesList = messagesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message,null,false);

        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(params);

        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(!messagesList.get(position).getSenderId().equals(FirebaseAuth.getInstance().getUid()) || messagesList.get(position).getSenderId()==null )
            holder.itemLayout.setGravity(Gravity.LEFT);
        else
            holder.itemLayout.setGravity(Gravity.RIGHT);
        Log.d(TAG, "sender name : "+messagesList.get(position).getSenderId());


        holder.msgText.setText(messagesList.get(position).getMessage());
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView msgText;
        private LinearLayout itemLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            msgText = itemView.findViewById(R.id.msgText);
            itemLayout = itemView.findViewById(R.id.msgViewItem);
        }
    }
}
