package com.mahmoudkhalil.hathooly.ui.post;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.greentoad.turtlebody.imagepreview.ImagePreview;
import com.greentoad.turtlebody.imagepreview.core.ImagePreviewConfig;
import com.mahmoudkhalil.hathooly.ImageCompressor;
import com.mahmoudkhalil.hathooly.model.Post;
import com.mahmoudkhalil.hathooly.R;
import com.mahmoudkhalil.hathooly.model.User;
import com.mahmoudkhalil.hathooly.databinding.ActivityAddPostBinding;
import com.mahmoudkhalil.hathooly.ui.home.HomeActivity;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class AddPostActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_MULTIPLE = 1;
    private ActivityAddPostBinding binding;
    private SharedPreferences sharedPreferences;
    private User user;
    private Gson gson;
    ArrayList<Uri> mArrayUri;
    List<String> imageUrls;
    public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_post);

        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        gson = new Gson();
        user = gson.fromJson(sharedPreferences.getString("user", null), User.class);
        mArrayUri = new ArrayList<Uri>();
        imageUrls = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        binding.addPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.progressBar.setVisibility(View.VISIBLE);
                String title = binding.title.getEditText().getText().toString().trim();
                String location = binding.location.getEditText().getText().toString().trim();
                String description = binding.description.getEditText().getText().toString().trim();
                String categoryName = binding.spinner.getText().toString();
                String key = (binding.lostRadio.isChecked()) ? "Lost" : "Found";
                if (validate()) {
                    setImages(mArrayUri);
                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Do something after 5s
                            Calendar cal = Calendar.getInstance();
                            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
                            String date = sdf.format(cal.getTime());
                            String id = db.collection("Posts").document().getId();
                            Post post = new Post(id,
                                    title,
                                    description,
                                    categoryName,
                                    user.getEmail(),
                                    user.getName(),
                                    user.getPhone(),
                                    location,
                                    imageUrls,
                                    date,
                                    key);
                            addPost(post, db);
                            startActivity(new Intent(AddPostActivity.this, HomeActivity.class));
                            finish();
                        }
                    }, 8000);

                } else {
                    binding.progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });

        binding.addImagesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mArrayUri.clear();
                // initialising intent
                Intent intent = new Intent();

                // setting type to select to be image
                intent.setType("image/*");

                // allowing multiple image to be selected
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
            }
        });
    }

    private void addPost (Post post, FirebaseFirestore db) {
        db.collection("Posts")
                .document(post.getId())
                .set(post)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // When an Image is picked
        if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK && null != data) {
            // Get the Image from data
            if (data.getClipData() != null) {
                ClipData mClipData = data.getClipData();
                int count = mClipData.getItemCount();
                for (int i = 0; i < count; i++) {
                    // adding imageUrl in array
                    Uri imageUrl = mClipData.getItemAt(i).getUri();
                    mArrayUri.add(imageUrl);
                }
                startMultiImagePreview(mArrayUri);
            } else {
                Uri imageUrl = data.getData();
                mArrayUri.add(imageUrl);
                binding.imageCountTextView.setVisibility(View.VISIBLE);
                binding.imageCountTextView.setText(String.valueOf(1));
            }
        } else {
            // show this if no image is selected
            Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }

    private boolean validate() {
        String title = binding.title.getEditText().getText().toString().trim();
        String location = binding.location.getEditText().getText().toString().trim();
        String description = binding.description.getEditText().getText().toString().trim();
        String categoryName = binding.spinner.getText().toString();
        boolean res = true;
        if (title.isEmpty()) {
            res = false;
            binding.title.setError("Enter the post title");
        }
        if (location.isEmpty()) {
            res = false;
            binding.location.setError("Enter the post location");
        }
        if (description.isEmpty()) {
            res = false;
            binding.description.setError("Enter the post description");
        }
        if (categoryName.equals("Select Category") || categoryName.isEmpty() || mArrayUri.size() == 0) {
            res = false;
        }
        if(!binding.lostRadio.isChecked() && !binding.foundRadio.isChecked()) {
            res = false;
        }
        return res;
    }

    private void startMultiImagePreview(ArrayList<Uri> uris) {

        ImagePreviewConfig config = new ImagePreviewConfig().setAllowAddButton(false).setUris(uris);

        ImagePreview.ImagePreviewImpl imagePreview = ImagePreview.with(this);

        imagePreview
                .setConfig(config)
                .setListener(new ImagePreview.ImagePreviewImpl.OnImagePreviewListener() {
                    @Override
                    public void onDone(@NotNull ArrayList<Uri> data) {
                        //after done all uri is sent back
                        binding.imageCountTextView.setVisibility(View.VISIBLE);
                        binding.imageCountTextView.setText(String.valueOf(data.size()));
                    }

                    @Override
                    public void onAddBtnClicked() {
                        //trigger when button clicked
                    }
                })
                .start();
    }

    private Uri compressImage(Uri uri) {
        Bitmap bitmap = uriToBitmap(uri);
        String code = ImageCompressor.encode_Image_To_String(bitmap, 45);
        Bitmap codedBitmap = ImageCompressor.decode_String_To_Image(code);
        return bitMapToUri(codedBitmap);
    }
    private void setImages(ArrayList<Uri> data) {
        for(int i = 0; i < data.size(); i ++) {
            Uri imageAfterCompression = compressImage(data.get(i));
            StorageReference storageRef = FirebaseStorage.getInstance().getReference("Images");
            final StorageReference ref = storageRef.child(imageAfterCompression.getLastPathSegment());
            ref.putFile(imageAfterCompression)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            if (taskSnapshot.getMetadata() != null) {
                                if (taskSnapshot.getMetadata().getReference() != null) {
                                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                    result.addOnSuccessListener(uri -> {
                                        String imageUrl = uri.toString();
                                        imageUrls.add(imageUrl);
                                    });
                                }
                            }
                        }
                    });
        }
    }

    public Uri bitMapToUri(Bitmap bmp) {
        // for Image send ignore URI error
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Uri bmpUri=null;
        try {
            File file = new File(this.getExternalCacheDir(), System.currentTimeMillis() + ".jpg");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    private Bitmap uriToBitmap(Uri uri) {
        Bitmap bitmap = null;
        if (Build.VERSION.SDK_INT >= 29) {
            ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), uri);
            try {
                bitmap = ImageDecoder.decodeBitmap(source);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }
}