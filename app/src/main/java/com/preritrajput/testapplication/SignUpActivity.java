package com.preritrajput.testapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class SignUpActivity extends AppCompatActivity {

    PhoneAuthProvider.ForceResendingToken forceResendingToken;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;

    RelativeLayout relativeLayout;
    private static final String  TAG="MAIN_TAG";
    FirebaseAuth firebaseAuth;

    TextView error;

    ImageView next;
    EditText mobile,username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black_trans80));
        setContentView(R.layout.activity_sign_up);


        firebaseAuth=FirebaseAuth.getInstance();

        next=findViewById(R.id.add);
        mobile=findViewById(R.id.editText3);
        username=findViewById(R.id.editText2);
        relativeLayout=findViewById(R.id.relative);
        error=findViewById(R.id.error);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                error.setVisibility(View.INVISIBLE);
                String phone=mobile.getText().toString().trim();
                String uName=username.getText().toString().trim();
                if(TextUtils.isEmpty(phone) || TextUtils.isEmpty(uName))
                {
                    error.setText("Please enter all details.");
                    error.setVisibility(View.VISIBLE);
                }
                else if(!Patterns.PHONE.matcher(mobile.getText().toString().trim()).matches())
                {
                    error.setText("Enter a valid phone number.");
                    error.setVisibility(View.VISIBLE);
                }
                else
                {
                    relativeLayout.setVisibility(View.VISIBLE);

                    PhoneAuthProvider
                            .getInstance()
                            .verifyPhoneNumber(
                                    "+91" + phone,
                                    60,
                                    TimeUnit.SECONDS,
                                    SignUpActivity.this,
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
                                            Intent intent=new Intent(SignUpActivity.this,OTPActivity.class);
                                            intent.putExtra("phone",phone);
                                            intent.putExtra("username",uName);
                                            intent.putExtra("verificationId",newVerificationId);
                                            Toast.makeText(SignUpActivity.this, "OTP Sent", Toast.LENGTH_SHORT).show();
                                            relativeLayout.setVisibility(View.GONE);
                                            startActivity(intent);
                                        }
                                    });

                }
            }
        });
    }

    public void back(View view) {
        SignUpActivity.super.onBackPressed();
    }
}