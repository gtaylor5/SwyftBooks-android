package com.swyftlabs.swyftbooks;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class SignUp extends AppCompatActivity{

    EditText emailField;
    EditText passwordField;
    EditText confirmPasswordField;
    AutoCompleteTextView userSchoolField;
    Button signUpButton;
    TextView backToLogin;


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;

    ArrayList<String> schools = new ArrayList<String>();
    String[] schoolsAsArray;
    //go back to login
    public void goToLogIn(View view){
        startActivity(new Intent(SignUp.this, LoginActivity.class));
    }

    //signUp method
    public void signUp(View view){

        String email = String.valueOf(emailField.getText());
        String password = String.valueOf(passwordField.getText());
        String confirm = String.valueOf(confirmPasswordField.getText());
        
        //check if password is valid
        if(confirm.equals(password) && password != null){
            //check if email is valid NEED TO CHECK FOR @ SYMBOL
            if(email != null && email.contains("@")) {
                
                //sign user up if everything is okay

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            showToast("There was an error signing you up. Please try again.", "long");
                        }

                        FirebaseUser user = mAuth.getCurrentUser();
                        if(user != null){
                            Calendar c = Calendar.getInstance();
                            SimpleDateFormat df = new SimpleDateFormat("MMM-dd-yyyy");
                            Map<String, Object> userAttributes = new HashMap<String, Object>();
                            userAttributes.put("Date Joined", df.format(c.getTime()));
                            userAttributes.put("User", String.valueOf(emailField.getText()));
                            userAttributes.put("Unique Searches", "0");
                            userAttributes.put("Platform", "Android");
                            if(!String.valueOf(userSchoolField.getText()).isEmpty()){
                                userAttributes.put("School", String.valueOf(userSchoolField.getText()));
                            }
                            mDatabase.child("Users").child(user.getUid()).setValue(userAttributes);
                            mDatabase.child("Schools").child(String.valueOf(userSchoolField.getText())).child("Students").child(user.getUid()).setValue(String.valueOf(emailField.getText()));

                            startActivity(new Intent(SignUp.this, HomeActivity.class));
                            finish();
                        }else {
                            showToast("There was an error signing you up, please try again.", "long");
                        }
                    }
                });

            }else{

                if(email == null) {
                    showToast("Your email address cannot be blank.", "long");
                }else{
                    showToast("The email address you entered is not valid.", "long");
                }
            }
        }else{
            showToast("Your passwords did not match. Please try again.", "long");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        TextView appName;
        appName = (TextView)findViewById(R.id.AppNameTextView);
        Typeface type = Typeface.createFromAsset(getAssets(), "fonts/RobotoSlab-Thin.ttf");
        Typeface type2 = Typeface.createFromAsset(getAssets(),"fonts/Arimo-Regular.ttf");

        String temp = "Swyft";
        String myString = "<i>Swyft</i>";
        appName.setText(Html.fromHtml("<i>"+temp+"</i><b>Books</b>"));
        appName.setTypeface(type2);

        backToLogin = (TextView)findViewById(R.id.backToLogin);
        backToLogin.setTypeface(type);
        emailField = (EditText)findViewById(R.id.SignUpEmailEditText);
        emailField.getBackground().setAlpha(26);
        passwordField = (EditText)findViewById(R.id.SignUpPasswordEditText);
        passwordField.getBackground().setAlpha(26);
        confirmPasswordField = (EditText)findViewById(R.id.SignUpConfirmPasswordEditText);
        confirmPasswordField.getBackground().setAlpha(26);
        userSchoolField = (AutoCompleteTextView) findViewById(R.id.schoolTextView);
        userSchoolField.getBackground().setAlpha(26);
        signUpButton = (Button)findViewById(R.id.SignUpButton);
        signUpButton.getBackground().setAlpha(128);
        emailField.setTypeface(type);
        userSchoolField.setTypeface(type);
        passwordField.setTypeface(type);
        confirmPasswordField.setTypeface(type);
        signUpButton.setTypeface(type);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        };
        try {
            fillSchools();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void fillSchools() throws IOException{
        BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("names.txt")));
        String line;
        while((line = reader.readLine()) != null){
            schools.add(line);
        }
        schoolsAsArray = new String[schools.size()];
        schoolsAsArray = schools.toArray(schoolsAsArray);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, schoolsAsArray);
        userSchoolField.setAdapter(adapter);
        userSchoolField.setThreshold(1);
    }


    public void showToast(String message, String lengthOfToast){
        if(lengthOfToast.equalsIgnoreCase("long")) {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
