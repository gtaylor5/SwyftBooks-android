package com.swyftlabs.swyftbooks;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.*;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText emailField;
    EditText passwordField;
    
    Button loginButton;
    
    TextView createAccount;
    TextView skip;
    TextView forgotPassword;
    TextView appName;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference ref;
    
    RelativeLayout bg;
    ValueEventListener guestLogin;
    ValueEventListener signUpListener;

    //method to change activity to sign up
    public void goToSignUp(View view){
        createSignUpListener();
        startActivity(new Intent(LoginActivity.this, SignUp.class));
    }

    //method to skip login/signup
    public void skip(View view){
        createGuestLoginListener();
        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        finish();
    }

    // recover lost password
    public void getPassword(View view){
        final String email = String.valueOf(this.emailField.getText());
        System.out.println(email);
        if(!email.isEmpty()) {
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "A password reset email was sent to: " + email, Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "The email address could not be found.", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "The email field cannot be blank. Please try again.", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    ref.child(user.getUid()).child("Logins").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int loginCount = dataSnapshot.getValue(Integer.class);
                            loginCount++;
                            ref.child(user.getUid()).child("Logins").setValue(loginCount);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "The email/password combination you entered was not recognized. Please try again.", Toast.LENGTH_LONG).show();
                }
            }
        };

        ref = FirebaseDatabase.getInstance().getReference();
        FirebaseDatabase.getInstance().goOnline();
        //typefaces
        Typeface type = Typeface.createFromAsset(getAssets(), "fonts/RobotoSlab-Thin.ttf");
        Typeface type2 = Typeface.createFromAsset(getAssets(), "fonts/Arimo-Regular.ttf");
        
        //link variables and attributes
        appName = (TextView)findViewById(R.id.AppNameTextView);
        forgotPassword = (TextView)findViewById(R.id.forgotPassword);
        emailField = (EditText)findViewById(R.id.EmailEditText);
        passwordField = (EditText)findViewById(R.id.PasswordEditText);
        loginButton = (Button)findViewById(R.id.LogInButton);
        createAccount = (TextView)findViewById(R.id.SignUpTextView);
        skip = (TextView)findViewById(R.id.skipTextView);
        
        //italicize swyft in swyftbooks and set typeface
        String temp = "Swyft";
        String myString = "<i>Swyft</i>";
        appName.setText(Html.fromHtml("<i>"+temp+"</i><b>Books</b>"));
        appName.setTypeface(type2);
        
        //set Typefaces 
        emailField.setTypeface(type);
        forgotPassword.setTypeface(type);
        passwordField.setTypeface(type);
        loginButton.setTypeface(type);
        createAccount.setTypeface(type);
        skip.setTypeface(type);
        
        //set transparency for textfields and button
        emailField.getBackground().setAlpha(26);
        passwordField.getBackground().setAlpha(26);
        loginButton.getBackground().setAlpha(128);

    }

    @Override
    public void onClick(View v) {
        final String email = String.valueOf(emailField.getText());
        final String password = String.valueOf(passwordField.getText());

        Log.i("AppInfo", email);

        if(!email.isEmpty() && !password.isEmpty()) {
            Log.i("AppInfo", email +" "+ password);
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "The email/password combination you entered was not recognized. Please try again.", Toast.LENGTH_LONG).show();
                    }else {
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();
                    }
                }
            });
        }else{
            Toast.makeText(getApplicationContext(), "The email/password field cannot be blank.", Toast.LENGTH_LONG).show();
        }
    }

    // create listeners

    public void createGuestLoginListener(){


    }

    public void createSignUpListener(){
        signUpListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int value = dataSnapshot.getValue(Integer.class);
                value++;
                ref.child("Sign Up Attempts").setValue(value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        ref.child("Sign Up Attempts").addListenerForSingleValueEvent(signUpListener);
    }
}
