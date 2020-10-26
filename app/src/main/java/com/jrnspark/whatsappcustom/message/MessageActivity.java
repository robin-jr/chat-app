package com.jrnspark.whatsappcustom.message;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jrnspark.whatsappcustom.R;
import com.jrnspark.whatsappcustom.user.UserListAdapter;

import java.util.ArrayList;
import java.util.Objects;

public class MessageActivity extends AppCompatActivity {
    private static final String TAG = "MessageActivity";

    ArrayList<MessageObject> messagesList;

    RecyclerView messagesView;
    RecyclerView.Adapter messagesViewAdapter;
    RecyclerView.LayoutManager messagesViewLayoutManager;

    TextView messager;
    EditText msgTextToSend;
    Button sendButton;

    String chatid = null;
    String userName, receiverName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

       messagesList = new ArrayList<>();
        messager = findViewById(R.id.messager);
        msgTextToSend = findViewById(R.id.msgTextToSend);
        sendButton = findViewById(R.id.msgSendbtn);


        setup();
        initializeRecyclerView();
        messageSender();


    }



    private void messageSender() {
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = msgTextToSend.getText().toString().trim();
                if (msg.length() > 0) {
                    DatabaseReference chatref = FirebaseDatabase.getInstance().getReference().child("chats").child(chatid).push();
                    chatref.child("message").setValue(msg);
                    chatref.child("sender").setValue(FirebaseAuth.getInstance().getUid());
                }
                msgTextToSend.setText("");
            }
        });
    }


    private void setup() {
        String receiverid = getIntent().getStringExtra("receiverid");
        String userid = getIntent().getStringExtra("userid");
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("user");

        userRef.child(userid).child("chat").child(receiverid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                    chatid = snapshot.getValue().toString();
                else
                    chatid = null;
                Log.d(TAG, "Chat id is: " + chatid);
                populateMessageList();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: in.. error is " + error.getMessage());
            }
        });

        userRef.child(userid).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                    userName = snapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: in.. error is " + error.getMessage());
            }
        });
        userRef.child(receiverid).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                    receiverName = snapshot.getValue().toString();
                messager.setText(receiverName);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: in.. error is " + error.getMessage());
            }
        });
    }

    private boolean isInMessageList(MessageObject obj) {
        for (MessageObject messages : messagesList) {
            if (messages.getMessageId().equals(obj.getMessageId()))
                return true;
        }
        return false;
    }

    private void populateMessageList() {
        DatabaseReference chatref = FirebaseDatabase.getInstance().getReference().child("chats");
        chatref.child(chatid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists())
                    return;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Log.d(TAG, "snapshot:  "+ds.toString());
                    if(ds.child("sender").getValue()==null || ds.child("message").getValue()== null)
                        return;
                    MessageObject msgObj = new MessageObject(ds.child("sender").getValue().toString(), receiverName, ds.child("message").getValue().toString(), ds.getKey());
                    if (!isInMessageList(msgObj))
                        messagesList.add(msgObj);
                }
                messagesViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: in.. error is " + error.getMessage());
            }
        });
    }


    private void initializeRecyclerView() {
        messagesView = findViewById(R.id.messageView);
        messagesView.setHasFixedSize(false);
        messagesView.setNestedScrollingEnabled(false);

        messagesViewAdapter = new MessageViewAdapter(messagesList);
        messagesView.setAdapter(messagesViewAdapter);

        messagesViewLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        messagesView.setLayoutManager(messagesViewLayoutManager);

    }
}