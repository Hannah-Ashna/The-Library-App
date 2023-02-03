package com.example.libraryapp.ui.home;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.libraryapp.R;
import com.example.libraryapp.databinding.FragmentHomeBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class HomeFragment extends Fragment implements View.OnClickListener{

    private FragmentHomeBinding binding;

    // NFC Messages
    public static final String Error_Detected = "No NFC Tag Detected";
    public static final String Write_Success = "Text Written Successfully";
    public static final String Write_Error = "Error during Write - Try Again";

    // NFC Variables
    private NfcAdapter NFCAdapter;
    PendingIntent pendingIntent;
    IntentFilter writingTagFilters[];
    boolean writeMode;
    Tag NFCTag;
    Context context;

    // REMOVE SOON?
    TextView edit_message;
    TextView nfc_contents;

    // Other Variables
    Snackbar scanSnackBar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // --- TEMP STUFF
        edit_message = (TextView)root.findViewById(R.id.edit_message);
        nfc_contents = (TextView)root.findViewById(R.id.nfc_Contents);
        context = this.getContext();
        // --- TEMP STUFF

        // Activate NFC Scanning Feature
        ImageView activateNFC = (ImageView)root.findViewById(R.id.bookNFCButton);
        activateNFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (NFCTag == null){
                        Toast.makeText(context, Error_Detected, Toast.LENGTH_LONG).show();
                    } else {
                        //write("PlainText |" + edit_message.getText().toString(), NFCTag);
                        write("HELLO HELLO HELLO", NFCTag);
                        Toast.makeText(context, Write_Success, Toast.LENGTH_LONG).show();
                    }
                } catch (IOException e){
                    Toast.makeText(context, Write_Error, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                } catch (FormatException e){
                    Toast.makeText(context, Write_Error, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });

        NFCAdapter = NfcAdapter.getDefaultAdapter(getActivity());

        if (NFCAdapter == null){
            // Do not proceed, NFC is a necessary permission for this feature
            Log.e("NFC Adapter Status: ","NULL");
            Toast.makeText(context, "NFC Adapter Status: NULL", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "NFC Adapter Status: NOT NULL", Toast.LENGTH_LONG).show();
            //Animation animation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.blink);
            //activateNFC.startAnimation(animation);

            readFromIntent(getActivity().getIntent());
            pendingIntent = PendingIntent.getActivity(getActivity(), 0, new Intent(getActivity(), getActivity().getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),0);
            IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
            tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
            writingTagFilters = new IntentFilter[] {tagDetected};
        }

        return root;
    }

    private void readFromIntent (Intent intent){
        String action = intent.getAction();
        Toast.makeText(context, "NFC Adapter Status: " + action, Toast.LENGTH_LONG).show();
        if (NFCAdapter.ACTION_TAG_DISCOVERED.equals(action)
            || NFCAdapter.ACTION_TECH_DISCOVERED.equals(action)
            || NFCAdapter.ACTION_NDEF_DISCOVERED.equals(action)){
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

        nfc_contents.setText("NFC Content: " + text);
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

        if(NFCAdapter.ACTION_TAG_DISCOVERED.equals((intent.getAction()))){
            NFCTag = intent.getParcelableExtra(NFCAdapter.EXTRA_TAG);
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
        NFCAdapter.enableForegroundDispatch(getActivity(), pendingIntent, writingTagFilters, null);
    }

    private void WriteModeOff(){
        writeMode = false;
        NFCAdapter.disableForegroundDispatch(getActivity());
    }


    // Some Extra Bits - Can ignore for now
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View view) {
        // Leave Blank
    }

}