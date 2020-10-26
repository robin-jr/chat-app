package com.jrnspark.whatsappcustom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private EditText mPhoneNumber, mCode,mName;
    private Button mSend;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);

        mPhoneNumber = findViewById(R.id.phoneNumber);
        mCode = findViewById(R.id.code);
        mSend = findViewById(R.id.veriftybtn);
        mName = findViewById(R.id.name);

       // startActivity(new Intent(getApplicationContext(),MainPageActivity.class));

        userIsLoggedIn();

        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVerificationId != null) {
                    Log.d(TAG, "onClick: Step 1");
                    verifyPhoneNumberWithCode();
                } else {
                    Log.d(TAG, "onClick: Step 2");
                    startPhoneNumberVerification();

                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Toast.makeText(LoginActivity.this, "Verification Completed", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onVerificationCompleted: in");
                signInWithPhoneAuthCredentials(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(LoginActivity.this, "Verification Failed", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onVerificationFailed: in ");
                Log.d(TAG, e.toString());
                
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                Toast.makeText(LoginActivity.this, "Code sent", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onCodeSent: in");
                super.onCodeSent(s, forceResendingToken);
                mVerificationId = s;
                mSend.setText("Verify Code");
            }
        };


    }

    private void verifyPhoneNumberWithCode() {
        Log.d(TAG, "verifyPhoneNumberWithCode: in");
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, mCode.getText().toString());
        signInWithPhoneAuthCredentials(credential);
    }

    private void signInWithPhoneAuthCredentials(PhoneAuthCredential phoneAuthCredential) {
        Log.d(TAG, "signInWithPhoneAuthCredentials: in");
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: task successful");
                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("user").child(user.getUid());

                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Log.d(TAG, "onDataChange: in");
                            if(!snapshot.exists()){
                                Log.d(TAG, "onDataChange: data null");
                                Map<String, Object> map = new HashMap<>();
                                map.put("name",mName.getText().toString());
                                map.put("phone",user.getPhoneNumber());

                                databaseReference.updateChildren(map);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d(TAG, "onCancelled: in");

                        }
                    });

                    userIsLoggedIn();
                }
            }
        });
    }

    private void userIsLoggedIn() {
        Log.d(TAG, "userIsLoggedIn: in");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            startActivity(new Intent(getApplicationContext(), MainPageActivity.class));
            finish();
            return;
        }

    }

    private void startPhoneNumberVerification() {
        Log.d(TAG, "startPhoneNumberVerification: in");
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91"+mPhoneNumber.getText().toString(),
                60, TimeUnit.SECONDS, this, mCallbacks
        );
    }
}