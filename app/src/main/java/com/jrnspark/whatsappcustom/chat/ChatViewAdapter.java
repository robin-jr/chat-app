package com.jrnspark.whatsappcustom.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jrnspark.whatsappcustom.R;


import java.util.ArrayList;

public class ChatViewAdapter extends RecyclerView.Adapter<ChatViewAdapter.ViewHolder> {
    ArrayList<ChatObject> chatList;

    public ChatViewAdapter(ArrayList<ChatObject> chatList) {
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat,null,false);

        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(params);

        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mChatText.setText(chatList.get(position).getChatId());

    }

    @Override
    public int getItemCount() {

        return chatList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout mChat;
        private TextView mChatText;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mChat = itemView.findViewById(R.id.chat);
            mChatText = itemView.findViewById(R.id.chat_text);
        }
    }
}
