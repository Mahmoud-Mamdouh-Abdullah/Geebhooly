package com.mahmoudkhalil.hathooly.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mahmoudkhalil.hathooly.R;
import com.mahmoudkhalil.hathooly.model.User;
import com.mahmoudkhalil.hathooly.databinding.ActivitySignupBinding;

import java.util.Locale;

public class SignupActivity extends AppCompatActivity {

    ActivitySignupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        binding.signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.progressBar.setVisibility(View.VISIBLE);
                if (validate()) {
                    User user = new User(binding.fullName.getEditText().getText().toString().trim(),
                                         binding.email.getEditText().getText().toString().trim().toLowerCase(Locale.ROOT),
                                         binding.password.getEditText().getText().toString().trim(),
                                         binding.phone.getEditText().getText().toString().trim());


                    DocumentReference docIdRef = db.collection("Users").document(user.getEmail());
                    docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if(document.exists()) {
                                    Toast.makeText(SignupActivity.this, "This Email already exists", Toast.LENGTH_SHORT).show();
                                }else {
                                    db.collection("Users")
                                            .document(user.getEmail())
                                            .set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });
                                }
                            }
                        }
                    });

                    binding.progressBar.setVisibility(View.INVISIBLE);
                    startActivity(new Intent(SignupActivity.this, MainActivity.class));
                }
            }
        });
    }

    private boolean validate() {
        boolean res = true;
        String name = binding.fullName.getEditText().getText().toString();
        String email = binding.email.getEditText().getText().toString();
        String password = binding.password.getEditText().getText().toString();
        String phone = binding.phone.getEditText().getText().toString();

        if (name.isEmpty()) {
            binding.fullName.setError("Enter your name");
            res = false;
        }
        if (email.isEmpty()) {
            binding.fullName.setError("Enter your email");
            res = false;
        }
        if (password.isEmpty() || password.length() < 6) {
            binding.password.setError("Enter a valid password");
            res = false;
        }
        if (phone.isEmpty()) {
            binding.phone.setError("Enter your phone number");
            res = false;
        }
        return res;
    }

    private boolean ifEmailExists(FirebaseFirestore db, String email) {
        final boolean[] res = {true};
        db.collection("Users")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(QueryDocumentSnapshot doc : task.getResult()) {
                    if(doc.getString("email").equals(email)) {
                        res[0] = false;
                        break;
                    }
                }
            }
        });
        return res[0];
    }
}