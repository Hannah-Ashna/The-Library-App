package com.example.libraryapp.ui.home;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.libraryapp.MainActivity;
import com.example.libraryapp.R;
import com.example.libraryapp.databinding.FragmentHomeBinding;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeFragment extends Fragment implements View.OnClickListener{

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //final TextView textView = binding.textHome;
        //homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        // Activate NFC Scanning Feature
        ImageView activateNFC = (ImageView)root.findViewById(R.id.bookNFCButton);
        activateNFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.blink);
                activateNFC.startAnimation(animation);
                Snackbar scanSnackBar = Snackbar.make(view, "Scanning for Book NFC", Snackbar.LENGTH_LONG);
                scanSnackBar.show();

                // Do NFC Scanning Logic then stop animation upon success or failure
            }
        });

        return root;
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