package com.preritrajput.testapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    PhoneAuthProvider.ForceResendingToken forceResendingToken;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;

    private static final String  TAG="MAIN_TAG";
    FirebaseAuth firebaseAuth;

    ImageView next;
    EditText mobile;

    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black_trans80));
        setContentView(R.layout.activity_login);

        firebaseAuth=FirebaseAuth.getInstance();

        next=findViewById(R.id.add);
        mobile=findViewById(R.id.editText);
        relativeLayout=findViewById(R.id.relative);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone=mobile.getText().toString().trim();
                if(TextUtils.isEmpty(phone))
                {
                    Toast.makeText(LoginActivity.this, "Please enter your mobile no.", Toast.LENGTH_SHORT).show();
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
                                    LoginActivity.this,
                                    new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                                        @Override
                                        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                                        }

                                        @Override
                                        public void onVerificationFailed(@NonNull FirebaseException e) {
                                            relativeLayout.setVisibility(View.GONE);
                                            Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                                        }

                                        @Override
                                        public void onCodeSent(@NonNull String newVerificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                            Intent intent=new Intent(LoginActivity.this,OTPActivity.class);
                                            intent.putExtra("phone",phone);
                                            intent.putExtra("verificationId",newVerificationId);
                                            intent.putExtra("login","login");
                                            Toast.makeText(LoginActivity.this, "OTP Sent", Toast.LENGTH_SHORT).show();
                                            relativeLayout.setVisibility(View.GONE);
                                            startActivity(intent);
                                        }
                                    });
                }
            }
        });
    }

    public void signup(View view) {
        startActivity( new Intent(getApplicationContext(),SignUpActivity.class));

    }
}