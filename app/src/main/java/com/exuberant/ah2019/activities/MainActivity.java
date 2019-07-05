package com.exuberant.ah2019.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.exuberant.ah2019.R;
import com.exuberant.ah2019.Utils;
import com.exuberant.ah2019.adapters.HomeViewPagerAdapter;
import com.exuberant.ah2019.interfaces.FileReportInterface;
import com.exuberant.ah2019.interfaces.LogoutInterface;
import com.exuberant.ah2019.models.Report;
import com.exuberant.ah2019.models.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.fotoapparat.result.BitmapPhoto;
import io.fotoapparat.result.PhotoResult;
import io.fotoapparat.result.WhenDoneListener;

import static io.fotoapparat.result.transformer.ResolutionTransformersKt.scaled;
import static maes.tech.intentanim.CustomIntent.customType;

public class MainActivity extends AppCompatActivity {

    private ViewPager homeViewPager;
    private FusedLocationProviderClient mFusedLocationClient;
    private Geocoder geocoder;
    private List<Address> addresses;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReportsReference, mUserReference;
    private FirebaseStorage mStorage;
    private StorageReference mStorageReference;
    private static FileReportInterface fileReportInterface;
    private static LogoutInterface logoutInterface;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intitialize();
        HomeViewPagerAdapter adapter = new HomeViewPagerAdapter(getSupportFragmentManager());
        homeViewPager.setAdapter(adapter);
        homeViewPager.setCurrentItem(1);
    }

    void intitialize() {
        homeViewPager = findViewById(R.id.fragment_container);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mDatabase = FirebaseDatabase.getInstance();
        mReportsReference = mDatabase.getReference().child("reports");
        mUserReference = mDatabase.getReference().child("users");
        mStorage = FirebaseStorage.getInstance();
        mStorageReference = mStorage.getReference().child("report_images");
        sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        fileReportInterface = new FileReportInterface() {
            @Override
            public void addReport(String description, String reportType, List<PhotoResult> pictures) {
                fileReport(description, reportType, pictures);
            }
        };
        logoutInterface = new LogoutInterface() {
            @Override
            public void logout() {
                logoutUser();
            }
        };
    }

    void fileReport(final String description, final String reportType, final List<PhotoResult> photos) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
        }

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                            try {
                                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                                String postalCode = addresses.get(0).getPostalCode();
                                String addressLine = addresses.get(0).getAddressLine(0);
                                savePostalCode(postalCode);
                                final String reportId = mReportsReference.push().getKey();
                                String userString = sharedPreferences.getString("User", "");
                                Gson gson = new Gson();
                                User user = gson.fromJson(userString, User.class);
                                user.setPostalCode(postalCode);
                                user.setLocality(addressLine);
                                saveDetails(user);
                                mUserReference.child(user.getUserId()).setValue(user);
                                List<String> dateList = Utils.getCurrentTime();
                                Report report = new Report(reportId, user.getUserId(), user.getDisplayName(),"",location.getLatitude(), location.getLongitude(), postalCode, reportType, "This is a sample Title", description, dateList.get(0), dateList.get(1), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), false);
                                mReportsReference.child(reportId).setValue(report);
                                List<String> reports = user.getReports();
                                if (reports == null || reports.size() == 0) {
                                    reports = new ArrayList<>();
                                }
                                reports.add(reportId);
                                user.setReports(reports);
                                mUserReference.child(user.getUserId()).setValue(user);
                                saveDetails(user);
                                for (int i = 0; i < photos.size(); i++) {
                                    final int position = i;
                                    PhotoResult photoResult = photos.get(i);
                                    photoResult
                                            .toBitmap(scaled(0.25f))
                                            .whenDone(new WhenDoneListener<BitmapPhoto>() {
                                                @Override
                                                public void whenDone(@Nullable BitmapPhoto bitmapPhoto) {
                                                    if (bitmapPhoto == null) {
                                                        return;
                                                    }
                                                    uploadImage(bitmapPhoto.bitmap, reportId, position);
                                                }
                                            });
                                }
                            } catch (IOException e){
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }


    void uploadImage(Bitmap bitmap, final String reportId, int count) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        final StorageReference photoRef = mStorageReference.child(reportId + "_" + String.valueOf(count));
        UploadTask uploadTask = photoRef.putBytes(data);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return photoRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downUri = task.getResult();
                    updateReportPictures(reportId, downUri.toString());
                }
            }
        });
    }

    void updateReportPictures(final String reportId, final String downloadUrl) {
        mReportsReference.child(reportId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Report report = dataSnapshot.getValue(Report.class);
                List<String> url = report.getImages();
                if (url == null || url.size() == 0) {
                    url = new ArrayList<>();
                }
                url.add(downloadUrl);
                report.setImages(url);
                mReportsReference.child(reportId).setValue(report);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static FileReportInterface getFileReportInterface() {
        return fileReportInterface;
    }

    public static LogoutInterface getLogoutInterface() {
        return logoutInterface;
    }

    void saveDetails(User user) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String userString = gson.toJson(user);
        editor.putString("User", userString);
        editor.apply();
    }

    void savePostalCode(String postalCode){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("PostalCode", postalCode);
        editor.apply();
    }

    void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        customType(MainActivity.this, "fadein-to-fadeout");
        finishAfterTransition();
    }

}
