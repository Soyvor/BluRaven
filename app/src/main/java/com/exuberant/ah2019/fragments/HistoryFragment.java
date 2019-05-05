package com.exuberant.ah2019.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonAdapter;
import com.ethanhua.skeleton.SkeletonScreen;
import com.exuberant.ah2019.R;
import com.exuberant.ah2019.activities.MainActivity;
import com.exuberant.ah2019.adapters.HistoryFeedAdapter;
import com.exuberant.ah2019.models.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HistoryFragment extends Fragment {

    private CircleImageView circleImageView;
    private ImageView logout;
    private TextView nameTextView, postTextView, localityTextView;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReportsReference;
    private RecyclerView historyRecyclerView;
    private TextView noFileReportedTextView;
    private SharedPreferences sharedPreferences;
    private SkeletonScreen skeletonScreen;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        initialize(view);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.getLogoutInterface().logout();
            }
        });
        String userString = sharedPreferences.getString("User", "");
        Gson gson = new Gson();
        User user = gson.fromJson(userString, User.class);
        nameTextView.setText(user.getDisplayName());
        postTextView.setText(String.valueOf(user.getReports().size()));
        localityTextView.setText(user.getLocality());
        List<String> reports = user.getReports();
        if (reports == null || reports.size() == 0) {
            noFileReportedTextView.setVisibility(View.VISIBLE);
            historyRecyclerView.setVisibility(View.GONE);
        } else {
            SkeletonAdapter skeletonAdapter = new SkeletonAdapter();
            skeletonScreen = Skeleton.bind(historyRecyclerView)
                    .adapter(skeletonAdapter)
                    .shimmer(true)
                    .angle(20)
                    .duration(1200)
                    .load(R.layout.skeleton_item_history)
                    .count(10)
                    .show();
            noFileReportedTextView.setVisibility(View.GONE);
            HistoryFeedAdapter adapter = new HistoryFeedAdapter(reports, mReportsReference, user);
            historyRecyclerView.setVisibility(View.VISIBLE);
            historyRecyclerView.setAdapter(adapter);
        }
        String photoPath = user.getPhotoUrl();
        String originalPieceOfUrl = "s96-c/photo.jpg";
        String newPieceOfUrlToAdd = "s400-c/photo.jpg";
        String newString = photoPath.replace(originalPieceOfUrl, newPieceOfUrlToAdd);
        Glide.with(this).load(newString).into(circleImageView);
        return view;
    }

    void initialize(View view) {
        nameTextView = view.findViewById(R.id.tv_user_name);
        postTextView = view.findViewById(R.id.tv_post_number);
        localityTextView = view.findViewById(R.id.tv_user_locality);
        circleImageView = view.findViewById(R.id.iv_profile_image);
        logout = view.findViewById(R.id.iv_logout);
        historyRecyclerView = view.findViewById(R.id.rv_history);
        noFileReportedTextView = view.findViewById(R.id.tv_no_reports);
        sharedPreferences = getActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        mDatabase = FirebaseDatabase.getInstance();
        mReportsReference = mDatabase.getReference().child("reports");
    }
}
