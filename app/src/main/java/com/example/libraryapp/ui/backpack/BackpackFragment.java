package com.example.libraryapp.ui.backpack;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.libraryapp.R;
import com.example.libraryapp.databinding.FragmentBackpackBinding;
import com.example.libraryapp.ui.library.LibraryListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class BackpackFragment extends Fragment {

    private FragmentBackpackBinding binding;

    List<String> title;
    List<String> author;
    List<Integer> duration;

    ListView lView;
    BackpackListAdapter lAdapter;
    private ListView listView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_backpack , container, false);

        // Initialise these arraylists
        title = new ArrayList<>();
        author = new ArrayList<>();
        duration = new ArrayList<>();

        // Get Current Loaned Books from DB
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db.collection("Books").whereEqualTo("Available", false).whereEqualTo("User", user.getUid().toString()).orderBy("Duration").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        title.add(document.get("Title").toString());
                        author.add(document.get("Author").toString());
                        duration.add(Integer.valueOf(document.get("Duration").toString()));
                    }
                } else {
                    Log.d("[Backpack Fragment]", "Error getting documents: ", task.getException());
                }
                listView = (ListView)view.findViewById(R.id.backpackList);
                listView.setAdapter(new BackpackListAdapter(getActivity(), title, author, duration));
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
