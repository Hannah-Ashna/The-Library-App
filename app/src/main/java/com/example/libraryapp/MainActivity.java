package com.example.libraryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.example.libraryapp.ui.backpack.BackpackListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    Boolean subscriptionStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            // Check for any books due
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("Books").whereEqualTo("Available", false).whereEqualTo("User", user.getUid().toString()).orderBy("Duration").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {

                            Timestamp timeStamp = document.getTimestamp("Duration");

                            Calendar dueDate = Calendar.getInstance();
                            dueDate.setTime(timeStamp.toDate());

                            Calendar now = Calendar.getInstance();
                            now.add(Calendar.HOUR, 24);
                            int diff = now.compareTo(dueDate);

                            if (diff == 1) {
                                subscriptionStatus = true;
                                break;
                            } else {
                                subscriptionStatus = false;
                            }
                        }
                    }

                    if (subscriptionStatus){
                        // Handle Messaging - Subscribe to topic
                        FirebaseMessaging.getInstance().subscribeToTopic("book_due")
                                .addOnCompleteListener(new OnCompleteListener<Void>(){
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task){
                                        String msg;

                                        if(!task.isSuccessful()){
                                            msg = "Failed";
                                        } else {
                                            msg = "Success";
                                        }

                                        Log.d("FCM Subscribe" , msg);
                                    }
                                });
                    } else {
                        // Handle Messaging - Unsubscribe from topic
                        FirebaseMessaging.getInstance().unsubscribeFromTopic("book_due")
                                .addOnCompleteListener(new OnCompleteListener<Void>(){
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task){
                                        String msg;

                                        if(!task.isSuccessful()){
                                            msg = "Failed";
                                        } else {
                                            msg = "Success";
                                        }

                                        Log.d("FCM Unsubscribe" , msg);
                                    }
                                });
                    }
                }
            });

            Intent intent = new Intent(MainActivity.this, NavigationActivity.class);
            startActivity(intent);
            finish();
        }

    }

    public void loginButton(View view){
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }

    public void signupButton(View view){
        Intent signupIntent = new Intent(MainActivity.this, SignUpActivity.class);
        startActivity(signupIntent);
    }

}