package com.sales.swiftgassales;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private TextInputLayout Username,Email,Password;
    private Button BtnLogIn;
    private TextView newSales;
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference SalesRef = db.collection("Sales_team");
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        Username = findViewById(R.id.UserNamelog);
        Email = findViewById(R.id.Emaillog);
        Password = findViewById(R.id.Passwordlog);
        BtnLogIn = findViewById(R.id.BtnLogin);
        newSales = findViewById(R.id.newSales);


        newSales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(),RegisterActivity.class));

            }
        });


        BtnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Validation()){

                }else {
                    SignIn();
                }
            }
        });

    }


    private void SignIn() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sign In...");
        progressDialog.setCancelable(false);
        progressDialog.show();


        String email = Email.getEditText().getText().toString();
        String password = Password.getEditText().getText().toString();

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    startActivity(new Intent(getApplicationContext(),ActivateActivity.class));
                    progressDialog.dismiss();
                }else {
                    ToastBack(task.getException().getMessage());
                    progressDialog.dismiss();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                // Toast.makeText(LoginActivity.this, "Log in Failed!!", Toast.LENGTH_SHORT).show();

                ToastBack(e.getMessage());

                progressDialog.dismiss();
            }
        });

    }

    private boolean Validation() {

        String email = Email.getEditText().getText().toString();
        String password = Password.getEditText().getText().toString();
        String username = Username.getEditText().getText().toString();

        if (email.isEmpty()) {
            Email.setError("Email is empty");
            return false;

        } if (username.isEmpty()) {
            Username.setError("Username is empty");
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Email.setError("Please enter a Valid email");
            return false;
        }
        else if (password.isEmpty()) {
            Password.setError("Password required");
            return false;
        }else {
            Email.setError(null);
            Password.setError(null);
            Username.setError(null);
            return true;
        }

    }

    private Toast backToast;
    private void ToastBack(String message){

        backToast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        View view = backToast.getView();
        view.getBackground().setColorFilter(Color.parseColor("#242A37"), PorterDuff.Mode.SRC_IN);
        TextView text = view.findViewById(android.R.id.message);
        text.setTextColor(Color.parseColor("#FF8900"));
        backToast.show();
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null)
        {
            if (mAuth.getCurrentUser().getUid() != null){

                startActivity(new Intent(getApplicationContext(),ActivateActivity.class));
            }

        }

    }
}
