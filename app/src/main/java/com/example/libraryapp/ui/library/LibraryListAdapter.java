package com.example.libraryapp.ui.library;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.libraryapp.HiddenAdminActivity;
import com.example.libraryapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class LibraryListAdapter extends BaseAdapter {
    Context context;
    private final List<String> title;
    private final List<String> author;
    private final List<String> summary;
    private final List<String> user;
    private final List<Boolean> available;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public LibraryListAdapter(Context context, List<String> title, List<String> author, List<String> summary, List<String> user, List<Boolean> available){
        //super(context, R.layout.single_list_app_item, utilsArrayList);
        this.context = context;
        this.title = title;
        this.author = author;
        this.summary = summary;
        this.user = user;
        this.available = available;

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    @Override
    public int getCount() {
        return title.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder;
        final View result;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.single_list_item,
                    parent, false);
            viewHolder.txtName = (TextView)
                    convertView.findViewById(R.id.aTitleTxt);
            viewHolder.txtVersion = (TextView)
                    convertView.findViewById(R.id.aSummaryTxt);
            viewHolder.icon = (ImageView)
                    convertView.findViewById(R.id.appIconIV);
            result=convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        // Check if User has access to Admin View
        // Open special Activity for ADMINS
        if(currentUser != null){
            // Check the current admin status of the user
            db.collection("Users").document(currentUser.getUid()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    if (task.getResult().getBoolean("Admin Status")){
                        String bookUser = user.get(position);

                        if (bookUser != "") {
                            db.collection("Users").document(bookUser).get().addOnCompleteListener(taskNew -> {
                                String userID;
                                if (taskNew.isSuccessful() && taskNew.getResult() != null) {
                                    userID = taskNew.getResult().getString("ID");
                                    viewHolder.txtName.setText(title.get(position) + "\n\nAuthor: " + author.get(position) + "\nUser: " + userID);
                                    viewHolder.txtVersion.setText(summary.get(position));
                                }
                            });
                        } else {
                            viewHolder.txtName.setText(title.get(position) + "\n\nAuthor: " + author.get(position) + "\nUser: ---");
                            viewHolder.txtVersion.setText(summary.get(position));
                        }
                    } else {
                        viewHolder.txtName.setText(title.get(position) + "\n\nAuthor: " + author.get(position));
                        viewHolder.txtVersion.setText(summary.get(position));
                    }
                } else {
                    viewHolder.txtName.setText(title.get(position) + "\n\nAuthor: " + author.get(position));
                    viewHolder.txtVersion.setText(summary.get(position));
                }
            });
        }

        // Display Icon based on Availability
        if (available.get(position)){
            viewHolder.icon.setImageResource(R.drawable.ic_list_book_available);
        } else {
            viewHolder.icon.setImageResource(R.drawable.ic_list_book_taken);
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView txtName;
        TextView txtVersion;
        ImageView icon;
    }
}
