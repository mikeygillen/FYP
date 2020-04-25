package com.example.fyp.Activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fyp.Classes.User;
import com.example.fyp.Maps.HomePageActivity;
import com.example.fyp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity{
    private static final String TAG = "MainActivity";

    private EditText _nameText, _emailText, _passwordText, txtHeight, txtWeight;
    private DatePicker datePicker;
    private Spinner Gender;
    private Button _signupButton;
    private TextView _loginLink;
    private FirebaseAuth firebaseAuth;
    //private DatabaseReference newUser = FirebaseDatabase.getInstance().getReference("Users");

    final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
    final DatabaseReference[] newUser = new DatabaseReference[1];
    final FirebaseUser[] mCurrentUser = new FirebaseUser[1];
    //mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fixGoogleMapBug();

        _nameText = (EditText) findViewById(R.id.input_name);
        _emailText = (EditText) findViewById(R.id.input_email);
        txtHeight = (EditText) findViewById(R.id.text_height);
        txtWeight = (EditText) findViewById(R.id.text_weight);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _signupButton = (Button) findViewById(R.id.btn_signup);
        _loginLink = (TextView) findViewById(R.id.link_login);Gender = (Spinner) findViewById(R.id.text_gender);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Gender.setAdapter(adapter);

        final Calendar myCalendar = Calendar.getInstance();
        datePicker = findViewById(R.id.text_dob);
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

            private void updateLabel() {
                String myFormat = "dd/mm/yy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
            }
        };

        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getApplication(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() !=null){
            finish();
            startActivity(new Intent(getApplicationContext(), HomePageActivity.class));
        }

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });
    }

    private void fixGoogleMapBug(){
        SharedPreferences googleBug = getSharedPreferences("google_bug", Context.MODE_PRIVATE);
        if (!googleBug.contains("fixed")){
            File corruptedZoomTables = new File(getFilesDir(), "ZoomTables.data");
            corruptedZoomTables.delete();
            googleBug.edit().putBoolean("fixed", true).apply();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private int calculateAge(String birth) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
        Date d = sdf.parse(birth);
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int date = c.get(Calendar.DATE);
        LocalDate l1 = LocalDate.of(year, month, date);
        LocalDate now1 = LocalDate.now();
        Period diff1 = Period.between(l1, now1);

        return diff1.getYears();
    }


    private void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        final String name = _nameText.getText().toString();
        final String email = _emailText.getText().toString();
        final String password = _passwordText.getText().toString();
        final String h = txtHeight.getText().toString().trim();
        final String w = txtWeight.getText().toString().trim();
        final String d = "" + datePicker.getDayOfMonth() + "-" + (datePicker.getMonth() + 1) + "-" + datePicker.getYear();


        final String g;
        if (Gender != null && Gender.getSelectedItem() != null) {
            g = (String) Gender.getSelectedItem();
        } else {
            g = "Null";
        }

        // TODO: Implement your own signup logic here.
        progressDialog.setMessage("Registering new Account...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    Log.d(TAG, "createUserWithEmail:success");
                    Toast.makeText(MainActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();

                    mCurrentUser[0] = task.getResult().getUser();
                    newUser[0] = mDatabase.child(mCurrentUser[0].getUid());
                    newUser[0].child("Name").setValue(name);

                    User user = new User(name, email, 0, 0);
                    //newUser[0].setValue(user);
                    newUser[0].child("Name").setValue(name);
                    newUser[0].child("Email").setValue(email);
                    newUser[0].child("Height").setValue(h);
                    newUser[0].child("Weight").setValue(w);
                    try {
                        newUser[0].child("Age").setValue(calculateAge(d));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    newUser[0].child("Birth Date").setValue(d);
                    newUser[0].child("Gender").setValue(g);
                    newUser[0].child("Total Distance").setValue((double) 0.0);
                    newUser[0].child("Total Runs").setValue((int) 0);


                    onSignupSuccess();
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    Toast.makeText(MainActivity.this, "Registration Unsuccessful, Please try again" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    onSignupFailed();
                }
            }
        });
    }

    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
        startActivity(new Intent(getApplicationContext(), HomePageActivity.class));
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        try {
            String name = _nameText.getText().toString();
            String email = _emailText.getText().toString();
            String password = _passwordText.getText().toString();
            int h = Integer.parseInt(txtHeight.getText().toString());
            int w = Integer.parseInt(txtWeight.getText().toString());

            if (name.isEmpty()) {
                _nameText.setError("Enter a name");
                valid = false;
            } else {
                _nameText.setError(null);
            }

            if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                _emailText.setError("Enter valid email address");
                valid = false;
            } else {
                _emailText.setError(null);
            }

            if (password.isEmpty() || password.length() < 6 || password.length() > 10) {
                _passwordText.setError("Password must be between 6 and 10 characters");
                valid = false;
            } else {
                _passwordText.setError(null);
            }

            if (h == 0) {
                txtHeight.setError("Enter Height");
                valid = false;
            } else {
                txtHeight.setError(null);
            }

            if (w == 0) {
                txtWeight.setError("Enter Weight");
                valid = false;
            } else {
                txtWeight.setError(null);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Registration Unsuccessful, Error with one of your fields" , Toast.LENGTH_LONG).show();
            valid = false;
        }

        return valid;
    }

}

