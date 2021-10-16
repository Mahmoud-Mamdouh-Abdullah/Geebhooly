package com.mahmoudkhalil.hathooly.ui.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.mahmoudkhalil.hathooly.R;
import com.mahmoudkhalil.hathooly.databinding.FragmentHomeBinding;
import com.mahmoudkhalil.hathooly.ui.post.PostsActivity;

import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {


    public HomeFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    Gson gson;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentHomeBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        View view = binding.getRoot();
        List<String> categoryList = Arrays.asList(getResources().getStringArray(R.array.real_categories));
        CategoryAdapter adapter = new CategoryAdapter();
        adapter.setCategoryList(categoryList);

        adapter.setOnItemClickListener(new CategoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String category) {
                Intent intent = new Intent(getActivity(), PostsActivity.class);
                intent.putExtra("cat", category);
                startActivity(intent);
                getActivity().finish();
            }
        });
        binding.postsRecyclerView.setAdapter(adapter);
        return view;
    }
}