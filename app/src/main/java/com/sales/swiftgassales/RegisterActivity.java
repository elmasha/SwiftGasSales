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

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout Username,Email,Password,PhoneNumber;
    private Button BtnRegister;
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference SalesRef = db.collection("Sales_team");
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        Username = findViewById(R.id.UserName);
        Email = findViewById(R.id.Email);
        Password = findViewById(R.id.Password);
        PhoneNumber = findViewById(R.id.PhoneNumber);
        BtnRegister = findViewById(R.id.BtnRegister);

        BtnRegister.setOnClickListener(new View.OnClickListener() {
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
        progressDialog.setMessage("Saving details..");
        progressDialog.setCancelable(false);
        progressDialog.show();


        String email = Email.getEditText().getText().toString();
        String password = Password.getEditText().getText().toString();

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    SaveDetails();
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

    void SaveDetails(){
        String email = Email.getEditText().getText().toString();
        String password = Password.getEditText().getText().toString();
        String username = Username.getEditText().getText().toString();
        String phone = PhoneNumber.getEditText().getText().toString();

        HashMap<String,Object> save= new HashMap<>();
        save.put("Name",username);
        save.put("Email",email);
        save.put("Password",password);
        save.put("Phone",phone);
        save.put("Activated_shops",0);
        SalesRef.document(mAuth.getCurrentUser().getUid()).set(save)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            startActivity(new Intent(getApplicationContext(),ActivateActivity.class));
                            progressDialog.dismiss();
                        }else {
                            ToastBack(task.getException().getMessage());
                            progressDialog.dismiss();
                        }
                    }
                });
    }


    private Toast backToast;
    void ToastBack(String message) {


        backToast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        View view = backToast.getView();

        //Gets the actual oval background of the Toast then sets the colour filter
        view.getBackground().setColorFilter(Color.parseColor("#242A37"), PorterDuff.Mode.SRC_IN);

        //Gets the TextView from the Toast so it can be editted
        TextView text = view.findViewById(android.R.id.message);
        text.setTextColor(Color.parseColor("#FF8900"));
        backToast.show();
    }

    private boolean Validation() {

        String email = Email.getEditText().getText().toString();
        String password = Password.getEditText().getText().toString();
        String username = Username.getEditText().getText().toString();
        String phone = PhoneNumber.getEditText().getText().toString();

        if (email.isEmpty()) {
            Email.setError("Email is empty");
            return false;

        }
        if (phone.isEmpty()) {
            PhoneNumber.setError("Phone number is empty");
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

}