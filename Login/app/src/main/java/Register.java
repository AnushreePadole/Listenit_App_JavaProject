package com.example.login;

import static androidx.fragment.app.FragmentManager.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.login.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Register extends AppCompatActivity
{

    ActivityRegisterBinding binding;

    TextView alreadyhaveaccount;
    private EditText inputemail,inputpass,inputrepass,inputphone;

    String username,password;

    private Button btnRegister;

    Button resendcode;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    public static String resultemail="",email="";
    FirebaseDatabase firebaseDatabase;

    DatabaseReference reference;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        firebaseDatabase=FirebaseDatabase.getInstance();
        reference=firebaseDatabase.getReference();
        setContentView(R.layout.activity_register);
        TextView btn=findViewById(R.id.sign_in_reg);
        inputphone =findViewById(R.id.inputephone);
        alreadyhaveaccount=findViewById(R.id.alreadyhaveaccount);
        inputemail=findViewById(R.id.inputemail);
        inputpass=findViewById(R.id.inputPass);
        inputrepass=findViewById(R.id.inputrepass);
        btnRegister=findViewById(R.id.btnRegister);
        //resendcode=findViewById(R.id.resendcode);
        progressDialog = new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PerforAuth();
            }
        });


        alreadyhaveaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this,login.class));
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this,login.class));
            }
        });
    }

    private void PerforAuth()
     {
        email=inputemail.getText().toString().trim();
        String password=inputpass.getText().toString().trim();
        String confirmpass=inputrepass.getText().toString().trim();
        String phone=inputphone.getText().toString().trim();

        if (!email.matches(emailPattern))
        {
            inputemail.setError("Enter Correct Email");
        }
         else if (password.isEmpty()||password.length()<6) {
            inputpass.setError("Enter Proper Password");
        } else if (!password.equals(confirmpass)) {
            inputrepass.setError("Password not Matched both field");
        } else if (phone.isEmpty()|| phone.length()<10) {
            inputphone.setError("Enter Correct Phone Number");
        } else {
            progressDialog.setMessage("Please wait while registration...");
            progressDialog.setTitle("Resgistration");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();


            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful())
                    {
                        final Users user=new Users(
                                email,password,phone
                        );

                            FirebaseUser fuser= mAuth.getCurrentUser();

                            String UserID = fuser.getEmail();
                            resultemail = UserID.replace(".", "");


                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(resultemail).child("User Details")
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(Register.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                                                sendUserToNextActivity();
                                            }
                                        }
                                    });
                            progressDialog.dismiss();


                        //Toast.makeText(Register.this,"Registration Successful!",Toast.LENGTH_SHORT).show();

                    }
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(Register.this,""+task.getException(),Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }



    private void sendUserToNextActivity() {
        Intent intent=new Intent(Register.this,login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}