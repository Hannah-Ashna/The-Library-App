package com.example.libraryapp;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

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

public class NavigationActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityNavigationBinding binding;
    private FirebaseAuth mAuth;

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

        FirebaseUser currentUser = mAuth.getCurrentUser();

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
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void adminButtonClicked(MenuItem item){
        final View menuItemView = findViewById(R.id.action_admin);
        Snackbar adminSnackBar = Snackbar.make(menuItemView, "Scanning for Admin NFC Card", Snackbar.LENGTH_LONG);
        adminSnackBar.show();

        // Dismiss snackbar once connection succeeded or failed
        // NFC Scanning Code goes hereeee!!
    }
}