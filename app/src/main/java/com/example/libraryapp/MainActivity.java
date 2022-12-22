package com.example.libraryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void loginButton(){
        Intent signupIntent = new Intent(MainActivity.this, SignUpActivity.class);
        startActivity(signupIntent);
    }

    public void signupButton(){
        Intent signupIntent = new Intent(MainActivity.this, SignUpActivity.class);
        startActivity(signupIntent);
    }
}