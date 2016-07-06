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
    RelativeLayout bg;


    public void goToSignUp(View view){

        startActivity(new Intent(LoginActivity.this, SignUp.class));

    }

    public void skip(View view){

        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        finish();

    }

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

        TextView appName;
        appName = (TextView)findViewById(R.id.AppNameTextView);
        Typeface type = Typeface.createFromAsset(getAssets(), "fonts/RobotoSlab-Thin.ttf");
        Typeface type2 = Typeface.createFromAsset(getAssets(), "fonts/RobotoSlab-Regular.ttf");
        forgotPassword = (TextView)findViewById(R.id.forgotPassword);
        emailField = (EditText)findViewById(R.id.EmailEditText);
        passwordField = (EditText)findViewById(R.id.PasswordEditText);
        loginButton = (Button)findViewById(R.id.LogInButton);
        createAccount = (TextView)findViewById(R.id.SignUpTextView);
        skip = (TextView)findViewById(R.id.skipTextView);
        String myString = "<i>" + "Swyft" + "</i>" + "Books";
        appName.setText(Html.fromHtml(myString));
        appName.setTypeface(type2);
        emailField.setTypeface(type);
        forgotPassword.setTypeface(type);
        emailField.getBackground().setAlpha(26);
        passwordField.setTypeface(type);
        passwordField.getBackground().setAlpha(26);
        loginButton.setTypeface(type);
        loginButton.getBackground().setAlpha(128);
        createAccount.setTypeface(type);
        skip.setTypeface(type);

    }

    @Override
    public void onClick(View v) {

        Log.i("AppInfo", String.valueOf(emailField.getText()));
        Log.i("AppInfo", String.valueOf(passwordField.getText()));

        final String email = String.valueOf(emailField.getText());
        final String password = String.valueOf(passwordField.getText());

        ParseUser.logInInBackground(email,password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {

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
