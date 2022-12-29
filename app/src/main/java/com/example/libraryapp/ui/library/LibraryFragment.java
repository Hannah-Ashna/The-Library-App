package com.example.libraryapp.ui.library;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.libraryapp.R;
import com.example.libraryapp.databinding.FragmentLibraryBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LibraryFragment extends Fragment {

    private FragmentLibraryBinding binding;

    List<String> title;
    List<String> author;
    List<String> summary;
    List<Boolean> available;

    ListView lView;
    LibraryListAdapter lAdapter;
    private ListView listView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_library , container, false);

        // Initialise these arraylists
        title = new ArrayList<>();
        author = new ArrayList<>();
        summary = new ArrayList<>();
        available = new ArrayList<>();

        // Get Current Books from DB
        db.collection("Books").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        title.add(document.get("Title").toString());
                        author.add(document.get("Author").toString());
                        summary.add(document.get("Summary").toString());
                        available.add(Boolean.valueOf(document.get("Available").toString()));
                    }
                } else {
                    Log.d("[Library Fragment]", "Error getting documents: ", task.getException());
                }

                listView = (ListView)view.findViewById(R.id.bookList);
                listView.setAdapter(new LibraryListAdapter(getActivity(), title, author, summary, available));
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
