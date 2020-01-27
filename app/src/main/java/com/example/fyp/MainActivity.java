package com.example.fyp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "MainActivity";

    private Button register;
    private EditText editTextEmail, editTextPassword;
    private TextView textViewSignup;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() !=null){
            finish();
            startActivity(new Intent(getApplicationContext(), HomePage.class));
        }

        progressDialog = new ProgressDialog(this);
        register = (Button) findViewById(R.id.buttonRegister);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        textViewSignup = (TextView) findViewById(R.id.textViewSignUp);

        register.setOnClickListener(this);
        textViewSignup.setOnClickListener(this);

    }

    private void registerUser(){
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please enter an Email", Toast.LENGTH_SHORT).show();
            return;
        }else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Email is not valid",
                    Toast.LENGTH_SHORT);
            toast.show();
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter a Password", Toast.LENGTH_SHORT).show();
            return;
        }else{
            progressDialog.setMessage("Registering new User...");
            progressDialog.show();

            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        Log.d(TAG, "createUserWithEmail:success");
                        Toast.makeText(MainActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(getApplicationContext(), HomePage.class));
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(MainActivity.this, "Registration Unsuccessful, Please try again" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View view){
        if(view == register){
            registerUser();
        }
        if(view == textViewSignup){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

}

