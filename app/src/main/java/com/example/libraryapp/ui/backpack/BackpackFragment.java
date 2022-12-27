package com.example.libraryapp.ui.backpack;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.libraryapp.databinding.FragmentBackpackBinding;

public class BackpackFragment extends Fragment {

    private FragmentBackpackBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        BackpackViewModel backpackViewModel = new ViewModelProvider(this).get(BackpackViewModel.class);

        binding = FragmentBackpackBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textBackpack;
        backpackViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
