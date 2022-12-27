package com.example.libraryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            //User is signed in use an intent to move to another activity
        }
    }

    public void signup(Map newUserData, String email, String password, String customID){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d("MainActivity", "createUserWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    Toast.makeText(SignUpActivity.this, "Authentication success.", Toast.LENGTH_SHORT).show();

                    // Add new User to the Firestore Database Collection
                    db.collection("Users").document(customID).set(newUserData).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("MainActivity", "Error adding document", e);
                        }
                    });

                    //user has been signed in, use an intent to move to the next activity
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }

                else {
                    // If sign in fails, display a message to the user
                    Log.w("MainActivity", "createUserWithEmail:failure", task.getException());
                    Toast.makeText(SignUpActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void signupButtonClicked(View view){
        EditText name = findViewById(R.id.editTextTextSignUpName);
        EditText studentID = findViewById(R.id.editTextTextSignUpStudentID);
        EditText email = findViewById(R.id.editTextTextSignupEmail);
        EditText password = findViewById(R.id.editTextTextSignupPassword);

        String sName = name.getText().toString();
        String sStudentID = studentID.getText().toString();
        String sEmail = email.getText().toString();
        String sPassword = password.getText().toString();

        // Create Hashmap for database input
        Map<String, Object> newUser = new HashMap();
            newUser.put("Full Name", sName);
            newUser.put("Email", sEmail);
            newUser.put("ID", sStudentID);
            newUser.put("Admin Status", false);

        signup(newUser, sEmail, sPassword, sStudentID);
    }
}
