package com.example.swapn.findyourfriend;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    EditText signUpEmail;
    EditText signUpPassword;
    Button signUpButton;
    TextView signUpTextView;
    ProgressBar signUpProgressBar;

    FirebaseAuth mAuth;
    DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();

        signUpEmail = findViewById(R.id.SignUpUsername);
        signUpPassword = findViewById(R.id.SignupPassword);
        signUpButton = findViewById(R.id.SignUpButton);
        signUpTextView = findViewById(R.id.GoToLoginActivity);
        signUpProgressBar = findViewById(R.id.SignUpProgressBar);

        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(), LoginActivity.class));
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

    }

    private void registerUser() {
        String sEmail = signUpEmail.getText().toString().trim();
        String sPassword = signUpPassword.getText().toString().trim();

        if (sEmail.isEmpty()){
            signUpEmail.setError("Email is required");
            signUpEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()){
            signUpEmail.setError("Please enter a valid email");
            signUpEmail.requestFocus();
            return;
        }

        if (sPassword.isEmpty()){
            signUpPassword.setError("Password is required");
            signUpPassword.requestFocus();
            return;
        }

        if (sPassword.length() < 6){
            signUpPassword.setError("Password is too short. Must be at least 6 characters");
            signUpPassword.requestFocus();
            return;
        }

        signUpProgressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(sEmail, sPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                signUpProgressBar.setVisibility(View.GONE);
                if (task.isSuccessful()){
                    Intent intent = new Intent(SignUpActivity.this, SetYourProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                    Toast.makeText(getApplicationContext(), "User Registered Successfully", Toast.LENGTH_SHORT).show();
                    writeNewUser();
                }
                else if (task.getException() instanceof FirebaseAuthUserCollisionException){
                    Toast.makeText(getApplicationContext(), "You are already registered. Go to the login page", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void writeNewUser() {
        Friend friend = new Friend("No name yet", 0, 0,"No address yet");
        database.child("users").child(mAuth.getCurrentUser().getUid()).setValue(friend);
        database.child("users").child(mAuth.getCurrentUser().getUid()).child("FriendID").setValue(mAuth.getCurrentUser().getUid());

    }

}
