package com.example.sinfaboletafinal.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sinfaboletafinal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class CodeVerificationActivity extends AppCompatActivity {

    Button btnContinuar;
    TextView txtMensaje;
    private String verificationId;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private EditText CodeVerification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_verification);

        mAuth = FirebaseAuth.getInstance();//Firebase

        txtMensaje = findViewById(R.id.textViewMensaje);
        btnContinuar = findViewById(R.id.btnContinuar);
        txtMensaje = findViewById(R.id.textViewMensaje);
        progressBar = findViewById(R.id.progressBar);
        CodeVerification = findViewById(R.id.txtCodeVerification);

        String phonenumber = getIntent().getStringExtra("phonenumber");
        sendVerificationCode(phonenumber);
        txtMensaje.setText("Espere mientras se envía el codigo de verificación al número "+phonenumber+". Esto puede tardar unos segundos...");
        btnContinuar.setEnabled(false);
        CodeVerification.setEnabled(false
        );


        btnContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = CodeVerification.getText().toString().trim();

                if(code.isEmpty()|| code.length()<6){
                    CodeVerification.setError("Ingrese código...");
                    CodeVerification.requestFocus();
                    return;
                }else{
                    CodeVerification.setEnabled(true);
                    btnContinuar.setEnabled(true);
                    verifyCode(code);
                }
            }
        });
    }
    private void verifyCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,code);
        SignInWithCredential(credential);
    }
    public void SignInWithCredential(PhoneAuthCredential credential){
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(CodeVerificationActivity.this, ConsultaActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }else{
                    Toast.makeText(CodeVerificationActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void sendVerificationCode(String number){
        progressBar.setVisibility(View.VISIBLE);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,60, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD,
                mCallback
        );
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if(code!=null){
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

            Toast.makeText(CodeVerificationActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
            Intent intent = new Intent(CodeVerificationActivity.this, MainActivity.class);
            startActivity(intent);
        }
    };
}
