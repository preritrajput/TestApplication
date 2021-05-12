package com.preritrajput.testapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class OTPActivity extends AppCompatActivity {

    PhoneAuthProvider.ForceResendingToken forceResendingToken;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    String  nV;

    String phoneTxt,uName;
    private static final String  TAG="MAIN_TAG";
    FirebaseAuth firebaseAuth;

    RelativeLayout relativeLayout;

    TextView displayPhone,resend,error;
    Button submit;
    private PinView OTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black_trans80));
        setContentView(R.layout.activity_o_t_p);

        firebaseAuth=FirebaseAuth.getInstance();

        mVerificationId=getIntent().getStringExtra("verificationId");
        phoneTxt=getIntent().getStringExtra("phone");
        uName=getIntent().getStringExtra("username");

        displayPhone=findViewById(R.id.textView3);
        OTP=findViewById(R.id.otp);
        submit=findViewById(R.id.submit);
        resend=findViewById(R.id.textView4);
        relativeLayout=findViewById(R.id.relative);
        error=findViewById(R.id.error);

        displayPhone.setText("We have sent an SMS on +91 "+phoneTxt+" with 6 digit verification code.");

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                error.setVisibility(View.INVISIBLE);
                if (OTP.getText().toString().isEmpty()) {
                    error.setText("Enter your OTP");
                    error.setVisibility(View.VISIBLE);
                } else if (mVerificationId != null) {
                    relativeLayout.setVisibility(View.VISIBLE);
                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(mVerificationId, OTP.getText().toString());
                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    String uid = user.getUid();

                                    HashMap<Object, String> hashMap = new HashMap<>();
                                    hashMap.put("username", uName);
                                    hashMap.put("phone", phoneTxt);
                                    hashMap.put("uid", uid);

                                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                    DatabaseReference databaseReference = firebaseDatabase.getReference("Users");
                                    databaseReference.child(uid).setValue(hashMap);

                                    relativeLayout.setVisibility(View.GONE);

                                    Intent intent = new Intent(OTPActivity.this, DashboardActivity.class);
                                    startActivity(intent);
                                    finish();


                                } else {

                                    error.setText("The code is invalid.");
                                    error.setVisibility(View.VISIBLE);
                                    relativeLayout.setVisibility(View.GONE);
                                }
                            });
                }
            }
        });


        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                error.setVisibility(View.INVISIBLE);
                relativeLayout.setVisibility(View.VISIBLE);

                PhoneAuthProvider
                        .getInstance()
                        .verifyPhoneNumber(
                                "+91" + phoneTxt,
                                60,
                                TimeUnit.SECONDS,
                                OTPActivity.this,
                                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                                    @Override
                                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                                    }

                                    @Override
                                    public void onVerificationFailed(@NonNull FirebaseException e) {
                                        relativeLayout.setVisibility(View.GONE);
                                        error.setText(""+e.getMessage());
                                        error.setVisibility(View.VISIBLE);

                                    }

                                    @Override
                                    public void onCodeSent(@NonNull String newVerificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                        mVerificationId=newVerificationId;
                                        relativeLayout.setVisibility(View.GONE);
                                        Toast.makeText(OTPActivity.this, "New code sent", Toast.LENGTH_SHORT).show();
                                    }
                                });
            }
        });

    }

    public void back(View view) {
        OTPActivity.super.onBackPressed();
    }
}