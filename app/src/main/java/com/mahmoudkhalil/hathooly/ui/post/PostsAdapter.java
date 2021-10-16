package com.mahmoudkhalil.hathooly.ui.post;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mahmoudkhalil.hathooly.R;
import com.mahmoudkhalil.hathooly.model.Post;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostsViewHolder> {

    List<Post> postList = new ArrayList<>();
    private List<Post> copyList = new ArrayList<>();
    OnItemClickListener onItemClickListener;

    @NonNull
    @Override
    public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        return new PostsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostsViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.titleTextView.setText(postList.get(position).getTitle());
        holder.timeTextView.setText(postList.get(position).getTimeStamp());
        holder.usernameTextView.setText(postList.get(position).getUserName());
        holder.profileTextView.setText(get2FromName(postList.get(position).getUserName()));
        Picasso.get().load(postList.get(position).getImageUrls().get(0)).into(holder.image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(postList.get(position));
            }
        });
    }

    public interface OnItemClickListener {
        void onItemClick(Post post);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private String get2FromName(String name) {
        String[] nameWords = name.split(" ");
        return nameWords[0].charAt(0) + String.valueOf(nameWords[nameWords.length - 1].charAt(0));
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public void setPostList(List<Post> postList) {
        this.postList = postList;
        copyList = new ArrayList<>(postList);
        notifyDataSetChanged();
    }

    public Filter getFilter() {
        return copyFilter;
    }

    private Filter copyFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Post> filteredList = new ArrayList<>();
            if(constraint == null || constraint.length() == 0) {
                filteredList.addAll(copyList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for(Post post : copyList) {
                    if(post.getTitle().toLowerCase(Locale.ROOT).contains(filterPattern) ||
                       post.getDescription().toLowerCase(Locale.ROOT).contains(filterPattern) ||
                       post.getLocation().toLowerCase(Locale.ROOT).contains(filterPattern) ||
                       post.getKey().toLowerCase(Locale.ROOT).contains(filterPattern)) {
                        filteredList.add(post);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            postList.clear();
            postList.addAll((ArrayList)results.values);
            notifyDataSetChanged();
        }
    };


    public static class PostsViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, profileTextView, timeTextView, usernameTextView;
        ImageView image;
        public PostsViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_textview);
            profileTextView = itemView.findViewById(R.id.profile_text);
            timeTextView = itemView.findViewById(R.id.time_textview);
            usernameTextView = itemView.findViewById(R.id.username_textview);
            image = itemView.findViewById(R.id.post_imageview);
        }
    }
}
