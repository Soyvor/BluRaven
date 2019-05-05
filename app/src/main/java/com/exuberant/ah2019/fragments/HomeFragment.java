package com.exuberant.ah2019.fragments;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.exuberant.ah2019.R;
import com.exuberant.ah2019.adapters.HomeFeedAdapter;
import com.exuberant.ah2019.models.Report;
import com.exuberant.ah2019.models.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReportsReference;
    private TextView localityTextView;
    private RecyclerView homeRecyclerView;
    private FirebaseFunctions mFunctions;
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
        mReportsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Report report = snapshot.getValue(Report.class);
                    if (postalCode.equals(report.getPostalCode())) {
                        filteredReports.add(report);
                    }
                }
                HomeFeedAdapter adapter = new HomeFeedAdapter(filteredReports, user);
                homeRecyclerView.setVisibility(View.VISIBLE);
                homeRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }

    void initialize(View view) {
        localityTextView = view.findViewById(R.id.tv_home_locality);
        filteredReports = new ArrayList<>();
        mFunctions = FirebaseFunctions.getInstance();
        sharedPreferences = getActivity().getSharedPreferences("UserData", MODE_PRIVATE);
        mDatabase = FirebaseDatabase.getInstance();
        mReportsReference = mDatabase.getReference().child("reports");
        homeRecyclerView = view.findViewById(R.id.rv_home);
    }

    private Task<String> homeList(String postalCode) {
        Map<String, String> data = new HashMap<>();
        data.put("pin", postalCode);
        return mFunctions
                .getHttpsCallable("homeList")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        String result = (String) task.getResult().getData();
                        return result;
                    }
                });
    }

}

