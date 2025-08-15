package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class login extends AppCompatActivity {

    TextView createnewaccount,forgotpassword;
    EditText inputemail, inputpass;
    Button btnLogin;
    String emailPattern ="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    Button google;
     public static String loginresultemail="";

    FirebaseDatabase firebaseDatabase;



    DatabaseReference reference;
    DatabaseReference reference2;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseDatabase=FirebaseDatabase.getInstance();
        reference=firebaseDatabase.getReference();
        inputemail = findViewById(R.id.inputemail);
        inputpass = findViewById(R.id.inputPass);
        createnewaccount = findViewById(R.id.createnewaccount);
        btnLogin = findViewById(R.id.btnRegister);
        //loginresultemail=Register.resultemail;
        //femail=Register.resultemail;


        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        TextView btn = findViewById(R.id.signUp);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(login.this, Register.class));
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                perforLogin();
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() != null){
            Intent intent=new Intent(login.this,Navigation.class);
            startActivity(intent);
            finish();
        }
        else{

        }
    }

    private void perforLogin() {
        String email = inputemail.getText().toString();
        String password = inputpass.getText().toString();


        if (!email.matches(emailPattern)) {
            inputemail.setError("Enter Correct Email");
        } else if (password.isEmpty() || password.length() < 6) {
            inputpass.setError("Enter Proper Password");
        } else {
            progressDialog.setMessage("Please wait while Login...");
            progressDialog.setTitle("Login");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth=FirebaseAuth.getInstance();
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        FirebaseUser fuser= mAuth.getCurrentUser();
                        String UserID=fuser.getEmail();
                        loginresultemail=UserID.replace(".","");
                        reference=firebaseDatabase.getInstance().getReference().child("Users");

                            reference2 = reference.child(loginresultemail);
                            progressDialog.dismiss();
                            Toast.makeText(login.this, "Login Successfull", Toast.LENGTH_SHORT).show();
                            sendUserToNextActivity();

                        }
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(login.this,""+task.getException(),Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    private void sendUserToNextActivity() {
        Intent intent=new Intent(login.this,Navigation.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}