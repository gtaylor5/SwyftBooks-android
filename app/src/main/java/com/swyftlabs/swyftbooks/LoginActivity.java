package com.swyftlabs.swyftbooks;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.*;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText emailField;
    EditText passwordField;
    
    Button loginButton;
    
    TextView createAccount;
    TextView skip;
    TextView forgotPassword;
    TextView appName;
    
    RelativeLayout bg;

    //method to change activity to sign up
    public void goToSignUp(View view){
        startActivity(new Intent(LoginActivity.this, SignUp.class));
    }

    //method to skip login/signup
    public void skip(View view){
        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        finish();
    }

    // recover lost password
    public void getPassword(View view){
        final String email = String.valueOf(this.emailField.getText());
        ParseUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(getApplicationContext(), ("A password reset email has been sent to " + email), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), ("Something went wrong. Please ensure your email is correct and try again."), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //typefaces
        Typeface type = Typeface.createFromAsset(getAssets(), "fonts/RobotoSlab-Thin.ttf");
        Typeface type2 = Typeface.createFromAsset(getAssets(), "fonts/RobotoSlab-Regular.ttf");
        
        //link variables and attributes
        appName = (TextView)findViewById(R.id.AppNameTextView);
        forgotPassword = (TextView)findViewById(R.id.forgotPassword);
        emailField = (EditText)findViewById(R.id.EmailEditText);
        passwordField = (EditText)findViewById(R.id.PasswordEditText);
        loginButton = (Button)findViewById(R.id.LogInButton);
        createAccount = (TextView)findViewById(R.id.SignUpTextView);
        skip = (TextView)findViewById(R.id.skipTextView);
        
        //italicize swyft in swyftbooks and set typeface
        String myString = "<i>" + "Swyft" + "</i>" + "Books";
        appName.setText(Html.fromHtml(myString));
        appName.setTypeface(type2);
        
        //set Typefaces 
        emailField.setTypeface(type);
        forgotPassword.setTypeface(type);
        passwordField.setTypeface(type);
        loginButton.setTypeface(type);
        createAccount.setTypeface(type)
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
        
        ParseUser.logInInBackground(email,password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                //check credentials and start home activity
                if(user != null){
                    Toast.makeText(getApplicationContext(), "Welcome!",
                            Toast.LENGTH_LONG).show();
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();

                }else{
                        Toast.makeText(getApplicationContext(), "The email/password combination you entered was not recognized. Please try again.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
