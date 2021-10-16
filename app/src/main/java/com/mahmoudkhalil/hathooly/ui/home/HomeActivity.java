package com.mahmoudkhalil.hathooly.ui.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.mahmoudkhalil.hathooly.R;
import com.mahmoudkhalil.hathooly.model.User;
import com.mahmoudkhalil.hathooly.databinding.ActivityHomeBinding;
import com.mahmoudkhalil.hathooly.ui.post.AddPostActivity;
import com.mahmoudkhalil.hathooly.ui.MainActivity;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityHomeBinding binding;
    private TextView nameTextView, emailTextView, profileTextView;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);

        init();
        setSupportActionBar(binding.toolbar);

        binding.navView.setNavigationItemSelectedListener(this);

        binding.addPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, AddPostActivity.class));
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
            binding.navView.setCheckedItem(R.id.nav_home);
            getSupportActionBar().setTitle("Home");
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HomeFragment()).commit();
                getSupportActionBar().setTitle("Home");
                break;
            case R.id.nav_favourite:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new SavedFragment()).commit();
                getSupportActionBar().setTitle("My Posts");
                break;

            case R.id.nav_logout:
                editor.putBoolean("remember", false);
                editor.apply();
                startActivity(new Intent(HomeActivity.this, MainActivity.class));
                finish();
                break;
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private String get2FromName(String name) {
        String[] nameWords = name.split(" ");
        return nameWords[0].charAt(0) + String.valueOf(nameWords[nameWords.length - 1].charAt(0));
    }

    private void init() {
        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        user = new Gson().fromJson(sharedPreferences.getString("user", null), User.class);
        nameTextView = binding.navView.getHeaderView(0).findViewById(R.id.full_name);
        nameTextView.setText(user.getName());
        emailTextView = binding.navView.getHeaderView(0).findViewById(R.id.email);
        emailTextView.setText(user.getEmail());
        profileTextView = binding.navView.getHeaderView(0).findViewById(R.id.profile_text);
        profileTextView.setText(get2FromName(user.getName()));
    }
}