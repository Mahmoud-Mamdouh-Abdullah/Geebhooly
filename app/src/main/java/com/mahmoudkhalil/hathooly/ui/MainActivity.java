package com.mahmoudkhalil.hathooly.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.mahmoudkhalil.hathooly.R;
import com.mahmoudkhalil.hathooly.model.User;
import com.mahmoudkhalil.hathooly.databinding.ActivityMainBinding;
import com.mahmoudkhalil.hathooly.ui.home.HomeActivity;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        gson = new Gson();
        binding.progressBar.setVisibility(View.VISIBLE);
        if(sharedPreferences.getBoolean("remember",false)) {
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
        }else {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }

        binding.signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.progressBar.setVisibility(View.VISIBLE);
                if (validate()) {
                    String email = binding.email.getEditText().getText().toString().trim().toLowerCase(Locale.ROOT);
                    String pass = binding.password.getEditText().getText().toString().trim();
                    db.collection("Users")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        boolean bol = false;
                                        for (QueryDocumentSnapshot doc : task.getResult()) {
                                            if (doc.getString("email").equals(email) &&
                                                    doc.getString("password").equals(pass)) {
                                                bol = true;
                                                User user = new User(doc.getString("name"),
                                                        doc.getString("email"),
                                                        doc.getString("password"),
                                                        doc.getString("phone"));
                                                String userStr = gson.toJson(user);
                                                editor.putString("user", userStr);
                                                if(binding.rememberMe.isChecked()) {
                                                    editor.putBoolean("remember", true);
                                                }
                                                editor.apply();
                                                startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                                finish();
                                            }
                                        }
                                        if (!bol) {
                                            binding.progressBar.setVisibility(View.INVISIBLE);
                                            Toast.makeText(MainActivity.this, "Email or Password is Wrong", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });
                    return;
                }
                binding.progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(MainActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
            }
        });

        binding.signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SignupActivity.class));
            }
        });
    }

    private boolean validate() {
        boolean res = true;
        String email = binding.email.getEditText().getText().toString();
        String password = binding.password.getEditText().getText().toString();

        if (email.isEmpty()) {
            binding.email.setError("Enter your email");
            res = false;
        }
        if (password.isEmpty() || password.length() < 6) {
            binding.password.setError("Enter a valid password");
            res = false;
        }
        return res;
    }
}