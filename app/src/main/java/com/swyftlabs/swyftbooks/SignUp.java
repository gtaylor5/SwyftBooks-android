package com.swyftlabs.swyftbooks;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.w3c.dom.Text;


@ParseClassName("SignUp")
public class SignUp extends AppCompatActivity{

    EditText emailField;
    EditText passwordField;
    EditText confirmPasswordField;
    Button signUpButton;
    TextView backToLogin;

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
            if(email != null) {
                ParseUser newUser = new ParseUser();
                newUser.setUsername(email);
                newUser.setPassword(password);
                
                //sign user up if everything is okay
                newUser.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Toast.makeText(getApplicationContext(), "Uh Oh! Something went wrong. Are you already registered?",
                                    Toast.LENGTH_LONG).show();
                        }else{
                            startActivity(new Intent(SignUp.this, HomeActivity.class));
                            finish();
                        }
                    }
                });
            }else{
                Toast.makeText(getApplicationContext(), "Your email address cannot be blank.",
                        Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(view.getContext(), "Your Passwords did not match. Please try again.",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        TextView appName;
        appName = (TextView)findViewById(R.id.AppNameTextView);
        Typeface type = Typeface.createFromAsset(getAssets(), "fonts/RobotoSlab-Thin.ttf");
        Typeface type2 = Typeface.createFromAsset(getAssets(), "fonts/RobotoSlab-Regular.ttf");


        String myString = "<i>" + "Swyft" + "</i>" + "Books";
        appName.setText(Html.fromHtml(myString));
        appName.setTypeface(type2);

        backToLogin = (TextView)findViewById(R.id.backToLogin);
        backToLogin.setTypeface(type);
        emailField = (EditText)findViewById(R.id.SignUpEmailEditText);
        emailField.getBackground().setAlpha(26);
        passwordField = (EditText)findViewById(R.id.SignUpPasswordEditText);
        passwordField.getBackground().setAlpha(26);
        confirmPasswordField = (EditText)findViewById(R.id.SignUpConfirmPasswordEditText);
        confirmPasswordField.getBackground().setAlpha(26);
        signUpButton = (Button)findViewById(R.id.SignUpButton);
        signUpButton.getBackground().setAlpha(128);
        emailField.setTypeface(type);
        passwordField.setTypeface(type);
        confirmPasswordField.setTypeface(type);
        signUpButton.setTypeface(type);

    }
}
