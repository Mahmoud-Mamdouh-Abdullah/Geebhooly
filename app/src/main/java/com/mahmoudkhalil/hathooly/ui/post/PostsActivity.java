package com.mahmoudkhalil.hathooly.ui.post;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.mahmoudkhalil.hathooly.R;
import com.mahmoudkhalil.hathooly.databinding.ActivityPostsBinding;
import com.mahmoudkhalil.hathooly.model.Post;
import com.mahmoudkhalil.hathooly.ui.home.HomeActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PostsActivity extends AppCompatActivity {

    ActivityPostsBinding binding;
    Gson gson;
    PostsAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_posts);

        gson = new Gson();
         adapter = new PostsAdapter();

        String category = getIntent().getStringExtra("cat");

        getSupportActionBar().setTitle(category);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.progressBar.setVisibility(View.VISIBLE);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<Post> postList = new ArrayList<>();
        db.collection("Posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    if(category.equals("All")) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                                Post post = document.toObject(Post.class);
                                postList.add(post);
                        }
                    } else if (category.equals("Lost things")) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if(document.getString("key").equals("Lost")) {
                                Post post = document.toObject(Post.class);
                                postList.add(post);
                            }
                        }
                    } else if (category.equals("Found things")) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if(document.getString("key").equals("Found")) {
                                Post post = document.toObject(Post.class);
                                postList.add(post);
                            }
                        }
                    }  else {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if(document.getString("categoryName").equals(category)) {
                                Post post = document.toObject(Post.class);
                                postList.add(post);
                            }
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
                Intent intent = new Intent(PostsActivity.this, PostDetailsActivity.class);
                String postJson = gson.toJson(post);
                intent.putExtra("post", postJson);
                startActivity(intent);
            }
        });
        binding.postsRecyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Type here to search..");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s.toLowerCase(Locale.ROOT));
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            this.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(PostsActivity.this, HomeActivity.class));
        finish();
    }
}