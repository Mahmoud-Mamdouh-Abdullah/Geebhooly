package com.mahmoudkhalil.hathooly.ui.post;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;
import com.mahmoudkhalil.hathooly.R;
import com.mahmoudkhalil.hathooly.databinding.ActivityPostDetailsBinding;
import com.mahmoudkhalil.hathooly.model.Post;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;

public class PostDetailsActivity extends AppCompatActivity {

    private Gson gson;
    private ActivityPostDetailsBinding binding;
    private SliderAdapter sliderAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_post_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        gson = new Gson();
        Post post = gson.fromJson(getIntent().getStringExtra("post"), Post.class);

        binding.profileText.setText(get2FromName(post.getUserName()));
        binding.usernameTextview.setText(post.getUserName());
        binding.timeTextview.setText(post.getTimeStamp());
        binding.titleTextview.setText(post.getTitle());
        binding.categoryTextview.setText(post.getCategoryName());
        binding.descriptionTextview.setText(post.getDescription());
        binding.locationTextview.setText("Location : " + post.getLocation());

        sliderAdapter = new SliderAdapter(post.getImageUrls());
        binding.sliderView.setSliderAdapter(sliderAdapter);
        binding.sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        binding.sliderView.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
        binding.sliderView.startAutoCycle();

        binding.callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialContactPhone(post.getUserPhone());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            this.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void dialContactPhone(final String phoneNumber) {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
    }

    private String get2FromName(String name) {
        String[] nameWords = name.split(" ");
        return nameWords[0].charAt(0) + String.valueOf(nameWords[nameWords.length - 1].charAt(0));
    }
}