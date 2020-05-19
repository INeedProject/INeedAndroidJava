package com.ineed.senior;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


// Import necessary Firebase and Google services namespaces
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.*;
import com.google.firebase.auth.*;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    // Create a referancess to hold the realted object
    private FirebaseAuth auth;
    private TextView email,paswd;
    Intent homePage;
    final String TAG="JSARR";
    private Button signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Get and assign a FireBaseAuth object to a refence type
        auth = FirebaseAuth.getInstance();
        // Assing the user email and password TextView objects to a refence type
        email = (EditText)findViewById(R.id.text_email);
        paswd = (EditText)findViewById(R.id.text_password);

        // Intantiate and assign an Intent object to a refence type
       homePage = new Intent(this, HomeActivity.class);

        signup = (Button) findViewById(R.id.signup);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText().toString().isEmpty() || paswd.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Email or Password cannot be empty! Please Try Again",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                auth.createUserWithEmailAndPassword(email.getText().toString(), paswd.getText().toString())
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    Login(getWindow().getDecorView().getRootView());
                                }
                                else{
                                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                                    email.setText("");
                                    paswd.setText("");
                                }

                            }
                        });
            }
        });

    }



    // Method that will be called when the user clicks the Login button
    public void Login(View view) {
        // Check if the email or the password is empty. If one of them is empty, create a toast message tothe user
        if(email.getText().toString().isEmpty() || paswd.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Email or Password cannot be empty! Please Try Again", Toast.LENGTH_LONG).show();


        }
        // If the email or the password not empty contiune with the login process
        else {
            // Call the sign in method of he FireBaseAuth object with the email and the password from the user
        auth.signInWithEmailAndPassword(email.getText().toString(), paswd.getText().toString())
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If the login process is successfull then get the user email (or name) put it into the "Extra" and start the activity
                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                          //  homePage.putExtra("usr",user.getEmail().toString());
                            startActivity(homePage);

                        }
                        // If the login failed clear the TextVew and show a toast message
                        else {
                            email.setText("");
                            paswd.setText("");
                            Toast.makeText(getApplicationContext(), "Wrong Email or Password! Please Try Again", Toast.LENGTH_LONG).show();
                        }
                    }
                });
        }
    }

}
