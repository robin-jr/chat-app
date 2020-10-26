package com.jrnspark.whatsappcustom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jrnspark.whatsappcustom.chat.ChatObject;
import com.jrnspark.whatsappcustom.chat.ChatViewAdapter;
import com.jrnspark.whatsappcustom.user.FindUserActivity;

import java.util.ArrayList;

public class MainPageActivity extends AppCompatActivity {
    private static final String TAG = "MainPageActivity";

    private Button mLogoutBtn, mFindUserBtn;

    private RecyclerView mChatView;
    private RecyclerView.Adapter mChatViewAdapter;
    private RecyclerView.LayoutManager mChatViewLayoutManager;

    private ArrayList<ChatObject> chatList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        mLogoutBtn = findViewById(R.id.logoutbtn);
        mFindUserBtn = findViewById(R.id.findUserbtn);
        chatList = new ArrayList<>();

        mLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return;
            }
        });
        mFindUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), FindUserActivity.class));

            }
        });
        getPermissions();
        initializeRecyclerView();
        getUserChatList();
    }
    private boolean isInChatList(ChatObject chatObject){

        for(ChatObject ob : chatList){
            if(ob.getChatId().equals(chatObject.getChatId()))
                return true;
        }
        return false;
    }
    private void getUserChatList(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("chat");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot ds : snapshot.getChildren()){
                        ChatObject chatObject = new ChatObject(ds.getKey());
                        if(isInChatList(chatObject))
                           { mChatViewAdapter.notifyDataSetChanged();
                            return;}
                        chatList.add(chatObject);
                        mChatViewAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: error :"+error.getMessage());
            }
        });
    }

    private void initializeRecyclerView() {
        mChatView = findViewById(R.id.chatView);
        mChatView.setHasFixedSize(false);
        mChatView.setNestedScrollingEnabled(false);

        mChatViewAdapter = new ChatViewAdapter(chatList);
        mChatView.setAdapter(mChatViewAdapter);

        mChatViewLayoutManager = new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false);
        mChatView.setLayoutManager(mChatViewLayoutManager);
    }

    private void getPermissions() {
        requestPermissions(new String[]{Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_CONTACTS},1);
    }
}