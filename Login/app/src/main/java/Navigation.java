package com.example.login;

import static com.example.login.login.loginresultemail;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Toolbar;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Navigation extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private FirebaseAuth mFirebaseAuth;
    DatabaseReference reference;
    DatabaseReference reference2;

    private HomeFragment homeFragment;
    FirebaseDatabase firebaseDatabase;
    String email= loginresultemail;

    TextView tta;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        mFirebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        reference=firebaseDatabase.getReference();

        homeFragment = new HomeFragment();

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout=findViewById(R.id.drawerlayout);

        FirebaseUser fuser= mFirebaseAuth.getCurrentUser();
        String UserID=fuser.getEmail();
        loginresultemail=UserID.replace(".","");
        reference=firebaseDatabase.getInstance().getReference().child("Users");

        reference2 = reference.child(loginresultemail);



        NavigationView navigationView=findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open_nav,R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        if(savedInstanceState==null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
                break;

            case R.id.nav_aboutus:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AboutFragment()).commit();
                break;

            case R.id.nav_savefile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new SaveFileFragment()).commit();
                break;

            case R.id.nav_logout:
                mFirebaseAuth.signOut();
                Toast.makeText(this,"Logout successfull!",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(Navigation.this,login.class);
                startActivity(intent);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;

    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else if (homeFragment.isVisible())
        {
            finish();
        }
        else {
            super.onBackPressed();
        }
    }
}