package com.example.libraryapp.ui.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.libraryapp.NavigationActivity;
import com.example.libraryapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class NFCActivity extends AppCompatActivity {

    // NFC Variables
    NfcAdapter      NFCAdapter;
    PendingIntent   pendingIntent;
    IntentFilter    writingTagFilters[];
    Tag             NFCTag;
    Context         context;

    // NFC Messages
    public static final String No_NFC_Support = "Warning: This device does not support NFCs";

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    boolean currentBookStatus;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfcactivity);

        context = this;

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        NFCAdapter = NfcAdapter.getDefaultAdapter(this);
        if (NFCAdapter == null) {
            Toast.makeText(this, No_NFC_Support, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getApplicationContext(), NavigationActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        readFromIntent(getIntent());
        pendingIntent = PendingIntent.getActivity(this, 0 ,new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writingTagFilters = new IntentFilter[] { tagDetected };
    }


    private void readFromIntent (Intent intent){
        String action = intent.getAction();

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)){

            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NFCAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs = null;

            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++){
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            }
            buildTagViews(msgs);
        }
    }

    private void buildTagViews (NdefMessage[] msgs) {
        if (msgs == null || msgs.length == 0) return;

        String text = "";
        byte[] payload = msgs[0].getRecords()[0].getPayload();
        String textEncoding = ((payload[0] & 128) == 0)? "UTF-8" : "UTF-16";
        int languageCodeLength = payload[0] & 0063;

        try {
            // Obtain the text
            text = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        } catch (Exception e){
            Log.e("Unsupported Encoding:", e.toString());
        }

        // Check what kind of TAG this is
        if (text.contains("Admin")) {
            updateUserDatabase();
        } else {
            updateBooksDatabase(text);
        }
    }

    private void updateUserDatabase () {
        if(currentUser != null) {
            // Check the current admin status of the user
            db.collection("Users").document(currentUser.getUid()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    Map<String, Object> userData = new HashMap<>();
                    Boolean adminStatus = task.getResult().getBoolean("Admin Status");

                    if (adminStatus) {
                        userData.put("Admin Status", false);
                    } else {
                        userData.put("Admin Status", true);
                    }

                    // Set the new admin status of the user
                    db.collection("Users").document(currentUser.getUid()).update(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("NFC Activity: ", "Update successful!");
                            Toast.makeText(NFCActivity.this, "Success: Your admin status has been modified", Toast.LENGTH_LONG).show();
                        }
                    }) .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("NFC Activity:", String.valueOf(e));
                            Toast.makeText(NFCActivity.this, "Error: Something went wrong, try again", Toast.LENGTH_LONG).show();
                        }
                    });

                } else {
                    Toast.makeText(NFCActivity.this, "Error: User does not exist in our records", Toast.LENGTH_LONG).show();
                }
            });
        }

        Intent intent = new Intent(getApplicationContext(), NavigationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void updateBooksDatabase(String bookID) {
        if(currentUser != null) {

            try {
                // Check the current availability status of the book
                db.collection("Books").document(bookID).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        currentBookStatus = task.getResult().getBoolean("Available");

                        Map<String, Object> bookData = new HashMap<>();
                        Calendar cal = Calendar.getInstance();

                        if (currentBookStatus) {
                            cal.add(Calendar.DAY_OF_MONTH, 2);
                            Date newDate = cal.getTime();

                            bookData.put("Available" , false);
                            bookData.put("User", currentUser.getUid());
                            bookData.put("Duration", new Timestamp(newDate));
                        } else {
                            Date newDate = cal.getTime();

                            bookData.put("Available" , true);
                            bookData.put("User", "");
                            bookData.put("Duration", new Timestamp(newDate));
                        }


                        // Set the new availability status of the book
                        db.collection("Books").document(bookID).update(bookData).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("NFC Activity: ", "Update successful!");
                                Toast.makeText(NFCActivity.this, "Success: Your backpack has been updated", Toast.LENGTH_LONG).show();
                            }
                        }) .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("NFC Activity:", String.valueOf(e));
                                Toast.makeText(NFCActivity.this, "Error: Something went wrong, try again", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        Toast.makeText(NFCActivity.this, "Error: Book does not exist in our records", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                Toast.makeText(NFCActivity.this, "Error: Book does not exist in our records", Toast.LENGTH_LONG).show();
            }
        }

        Intent intent = new Intent(getApplicationContext(), NavigationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onNewIntent (Intent intent){
        super.onNewIntent(intent);
        setIntent(intent);
        readFromIntent(intent);

        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals((intent.getAction()))){
            NFCTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        ReadModeOff();
    }

    @Override
    public void onResume() {
        super.onResume();
        ReadModeOn();
    }

    private void ReadModeOn(){
        NFCAdapter.enableForegroundDispatch(this, pendingIntent, writingTagFilters, null);
    }

    private void ReadModeOff(){
        NFCAdapter.disableForegroundDispatch(this);
    }
}