package com.example.libraryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class HiddenAdminActivity extends AppCompatActivity {

    // NFC Variables
    NfcAdapter      NFCAdapter;
    PendingIntent   pendingIntent;
    IntentFilter    writingTagFilters[];
    boolean         writeMode;
    Tag             NFCTag;
    Context         context;

    // NFC Messages
    public static final String Error_Detected = "No NFC Tag Detected";
    public static final String Write_Success = "Text Written Successfully";
    public static final String Write_Error = "Error during Write - Try Again";
    public static final String No_NFC_Support = "Warning: This device does not support NFCs";

    // NFC UI Items
    TextView        addBookAuthor;
    TextView        addBookTitle;
    TextView        addBookSummary;
    TextView        updateNFCContent;
    Button          updateNFCButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfcactivity);

        addBookAuthor       = (TextView) findViewById(R.id.addBookAuthor);
        addBookTitle        = (TextView) findViewById(R.id.addBookTitle);
        addBookSummary      = (TextView) findViewById(R.id.addBookSummary);

        updateNFCContent    = (TextView) findViewById(R.id.updateNFCContent);
        updateNFCButton     = (Button) findViewById(R.id.updateNFCButton);
        context             = this;

        updateNFCButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                try {
                    if (NFCTag == null) {
                        Toast.makeText(context, Error_Detected, Toast.LENGTH_LONG).show();
                    } else {
                        write(updateNFCContent.getText().toString(), NFCTag);
                        Toast.makeText(context, Write_Success, Toast.LENGTH_LONG).show();
                    }
                } catch (IOException e) {
                    Toast.makeText(context, Write_Error, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                } catch (FormatException e) {
                    Toast.makeText(context, Write_Error, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });

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

    }

    private void write (String text, Tag tag) throws IOException, FormatException {
        NdefRecord[] records = { createRecord(text)};
        NdefMessage message = new NdefMessage(records);

        Ndef ndef = Ndef.get(tag);
        ndef.connect();
        ndef.writeNdefMessage(message);
        ndef.close();
    }

    private NdefRecord createRecord (String text) throws UnsupportedEncodingException {
        String lang = "en";
        byte[] textBytes = text.getBytes();
        byte[] langBytes = lang.getBytes("US-ASCII");
        int langLength = langBytes.length;
        int textLength = textBytes.length;
        byte[] payload = new byte[1 + langLength + textLength];

        payload[0] = (byte) langLength;

        System.arraycopy(langBytes, 0, payload, 1, langLength);
        System.arraycopy(textBytes, 0, payload,1 + langLength, textLength);

        NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload);

        return recordNFC;
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
        WriteModeOff();
    }

    @Override
    public void onResume() {
        super.onResume();
        WriteModeOn();
    }

    private void WriteModeOn(){
        writeMode = true;
        NFCAdapter.enableForegroundDispatch(this, pendingIntent, writingTagFilters, null);
    }

    private void WriteModeOff(){
        writeMode = false;
        NFCAdapter.disableForegroundDispatch(this);
    }
}