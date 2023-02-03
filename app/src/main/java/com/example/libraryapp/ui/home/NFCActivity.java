package com.example.libraryapp.ui.home;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.libraryapp.NavigationActivity;
import com.example.libraryapp.R;

public class NFCActivity extends AppCompatActivity {

    // NFC Variables
    NfcAdapter      NFCAdapter;
    PendingIntent   pendingIntent;
    IntentFilter    writingTagFilters[];
    Tag             NFCTag;
    Context         context;

    // NFC Messages
    public static final String Error_Detected = "No NFC Tag Detected";

    // NFC UI Items
    TextView        nfc_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfcactivity);

        nfc_content     = (TextView) findViewById(R.id.nfc_contents);
        context         = this;

        NFCAdapter = NfcAdapter.getDefaultAdapter(this);
        if (NFCAdapter == null) {
            Toast.makeText(this, "This Device Does Not Support NFCs", Toast.LENGTH_LONG).show();
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
                Toast.makeText(this, "BOOK FOUND - READING INTENT NOW", Toast.LENGTH_LONG).show();
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

        nfc_content.setText("NFC Content: " + text);
        Toast.makeText(this, "BOOK FOUND - RETURNING NOW", Toast.LENGTH_LONG).show();
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