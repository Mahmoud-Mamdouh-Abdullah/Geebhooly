package com.mahmoudkhalil.hathooly.ui.home;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.mahmoudkhalil.hathooly.R;
import com.mahmoudkhalil.hathooly.databinding.FragmentSavedBinding;
import com.mahmoudkhalil.hathooly.model.Post;
import com.mahmoudkhalil.hathooly.model.User;
import com.mahmoudkhalil.hathooly.ui.post.PostDetailsActivity;
import com.mahmoudkhalil.hathooly.ui.post.PostsAdapter;

import java.util.ArrayList;
import java.util.List;


public class SavedFragment extends Fragment {



    public SavedFragment() {
        // Required empty public constructor
    }


    Gson gson;
    private SharedPreferences sharedPreferences;
    User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentSavedBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_saved, container, false);
        View view = binding.getRoot();

        sharedPreferences = getActivity().getSharedPreferences("user", MODE_PRIVATE);
        gson = new Gson();
        user = gson.fromJson(sharedPreferences.getString("user", null), User.class);
        PostsAdapter adapter = new PostsAdapter();

        binding.progressBar.setVisibility(View.VISIBLE);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<Post> postList = new ArrayList<>();
        db.collection("Posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if(document.getString("userEmail").equals(user.getEmail())) {
                            Post post = document.toObject(Post.class);
                            postList.add(post);
                        }
                    }
                    if(postList.size() == 0) {
                        binding.emptyTextView.setVisibility(View.VISIBLE);
                    }
                    adapter.setPostList(postList);
                }
                binding.progressBar.setVisibility(View.INVISIBLE);
            }
        });

        adapter.setOnItemClickListener(new PostsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Post post) {
                Intent intent = new Intent(getActivity(), PostDetailsActivity.class);
                String postJson = gson.toJson(post);
                intent.putExtra("post", postJson);
                startActivity(intent);
            }
        });
        binding.postsRecyclerView.setAdapter(adapter);

        return view;
    }
}