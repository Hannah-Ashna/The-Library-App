package com.example.libraryapp.ui.home;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.libraryapp.R;
import com.example.libraryapp.databinding.FragmentHomeBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeFragment extends Fragment implements View.OnClickListener{

    private FragmentHomeBinding binding;

    // NFC Variables
    public static final String TAG = "DevSocBook";
    private NfcAdapter NFCAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        NFCAdapter = NfcAdapter.getDefaultAdapter(getActivity());

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Activate NFC Scanning Feature
        ImageView activateNFC = (ImageView)root.findViewById(R.id.bookNFCButton);
        activateNFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.blink);
                activateNFC.startAnimation(animation);
                Snackbar scanSnackBar = Snackbar.make(view, "Scanning for Book NFC", Snackbar.LENGTH_INDEFINITE);
                scanSnackBar.show();

                // Do NFC Scanning Logic then stop animation upon success or failure
                if (NFCAdapter == null){
                    // Do not proceed, NFC is a necessary permission for this feature
                    scanSnackBar.dismiss();
                    Toast.makeText(getActivity(), "This device does not support NFC", Toast.LENGTH_LONG).show();
                    animation.reset();
                    return;
                }

                if (NFCAdapter.isEnabled()){
                    animation.reset();
                    scanSnackBar.dismiss();
                    Toast.makeText(getActivity(), "NFC is disabled", Toast.LENGTH_LONG).show();
                } else {
                    animation.cancel();
                    scanSnackBar.dismiss();
                    Toast.makeText(getActivity(), "NFC is enabled!", Toast.LENGTH_LONG).show();
                }

                handleIntent(getActivity().getIntent());
            }
        });

        return root;
    }

    private void handleIntent (Intent intent){
        // Pending Stuff
    }

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