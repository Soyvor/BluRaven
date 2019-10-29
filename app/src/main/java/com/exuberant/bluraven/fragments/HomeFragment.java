package com.exuberant.bluraven.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonAdapter;
import com.ethanhua.skeleton.SkeletonScreen;
import com.exuberant.bluraven.R;
import com.exuberant.bluraven.adapters.HomeFeedAdapter;
import com.exuberant.bluraven.models.Report;
import com.exuberant.bluraven.models.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReportsReference;
    private TextView localityTextView;
    private RecyclerView homeRecyclerView;
    private SkeletonScreen skeletonScreen;
    private SharedPreferences sharedPreferences;
    private List<Report> filteredReports;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initialize(view);
        String userString = sharedPreferences.getString("User", "");
        Gson gson = new Gson();
        final User user = gson.fromJson(userString, User.class);
        localityTextView.setText(user.getLocality());
        final String postalCode = sharedPreferences.getString("PostalCode", "");
        getLocalPosts(user, "110037");

        return view;
    }

    private void getLocalPosts(final User user, final String postalCode) {

        mReportsReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Report report = dataSnapshot.getValue(Report.class);
                if (postalCode.equals(report.getPostalCode())) {
                    filteredReports.add(report);
                    updateScreen(filteredReports, user);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateScreen(List<Report> filteredReports, User user) {
        HomeFeedAdapter adapter = new HomeFeedAdapter(filteredReports, user);
        homeRecyclerView.setVisibility(View.VISIBLE);
        homeRecyclerView.setAdapter(adapter);
    }

    void initialize(View view) {
        localityTextView = view.findViewById(R.id.tv_home_locality);
        filteredReports = new ArrayList<>();
        sharedPreferences = getActivity().getSharedPreferences("UserData", MODE_PRIVATE);
        mDatabase = FirebaseDatabase.getInstance();
        mReportsReference = mDatabase.getReference().child("reports");
        homeRecyclerView = view.findViewById(R.id.rv_home);
        SkeletonAdapter skeletonAdapter = new SkeletonAdapter();
        skeletonScreen = Skeleton.bind(homeRecyclerView)
                .adapter(skeletonAdapter)
                .shimmer(true)
                .angle(20)
                .duration(1200)
                .load(R.layout.skeleton_item_history)
                .count(10)
                .show();
    }


}

