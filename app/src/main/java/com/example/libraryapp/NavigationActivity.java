package com.example.libraryapp;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.libraryapp.databinding.ActivityNavigationBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class NavigationActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityNavigationBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private Snackbar adminSnackbar;

    NfcAdapter NFCAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        binding = ActivityNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarNavigation.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // Pull user data from Firestore Database
        View headerView = navigationView.getHeaderView(0);
        TextView navbarUserName = (TextView) headerView.findViewById(R.id.navbarName);
        TextView navbarEmail = (TextView)headerView.findViewById(R.id.navbarEmail);

        currentUser = mAuth.getCurrentUser();

        // Display it via the Nav Header
        try {
            db.collection("Users").document(currentUser.getUid()).get().addOnCompleteListener(task -> {
                if(task.isSuccessful() && task.getResult() != null){
                    String fullName = task.getResult().getString("Full Name");
                    String email = task.getResult().getString("Email");
                    String studentID = task.getResult().getString("ID");
                    navbarUserName.setText(fullName + " (" + studentID + ")");
                    navbarEmail.setText(email);
                }else{
                    navbarUserName.setText("The DevSoc Library");
                    navbarEmail.setText("");
                }
            });
        } catch (Exception e) {
            navbarUserName.setText("The DevSoc Library");
            navbarEmail.setText("");
        }

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_backpack, R.id.nav_library, R.id.nav_settings)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_navigation);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_navigation);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    public void adminButtonClicked(MenuItem item){
        final View menuItemView = findViewById(R.id.action_admin);
        NFCAdapter = NfcAdapter.getDefaultAdapter(this);

        // Open special Activity for ADMINS
        if(currentUser != null){

            // Check the current admin status of the user
            db.collection("Users").document(currentUser.getUid()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    if (task.getResult().getBoolean("Admin Status") && NFCAdapter != null){
                        Intent intent = new Intent(getApplicationContext(), HiddenAdminActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        adminSnackbar = Snackbar.make(menuItemView, "I wonder what this button does?", Snackbar.LENGTH_LONG);
                        adminSnackbar.show();
                    }

                } else {
                    Toast.makeText(this, "Error: User does not exist in our records", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}