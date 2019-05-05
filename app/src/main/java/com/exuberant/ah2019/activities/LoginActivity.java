package com.exuberant.ah2019.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.exuberant.ah2019.R;
import com.exuberant.ah2019.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static maes.tech.intentanim.CustomIntent.customType;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;
    public static final int PERMISSIONS_MULTIPLE_REQUEST = 9004;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mUserReference;
    private GoogleSignInClient mGoogleSignInClient;
    private MaterialButton googleSignInButton, adminLoginButton;
    private SharedPreferences sharedPreferences;
    private Geocoder geocoder;
    private List<Address> addresses;
    private FusedLocationProviderClient mFusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initialize();
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        adminLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                startActivity(intent);
                customType(LoginActivity.this, "fadein-to-fadeout");
            }
        });
    }

    void initialize() {
        googleSignInButton = findViewById(R.id.btn_google_signin);
        adminLoginButton = findViewById(R.id.btn_admin_login);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mUserReference = mDatabase.getReference().child("users");
        sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        disableAllButtons();
        checkPermission();
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            showSnackBar("Authentication Successful", 1);
                            final FirebaseUser user = mAuth.getCurrentUser();
                            mUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(user.getUid())) {
                                        User fetchedUser = dataSnapshot.child(user.getUid()).getValue(User.class);
                                        saveDetails(fetchedUser);
                                    } else {
                                        createUser(user);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            showSnackBar("Authentication Failed", 0);
                        }

                    }
                });
    }

    void launchHome() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        customType(LoginActivity.this, "fadein-to-fadeout");
        finishAfterTransition();
    }

    void showSnackBar(String message, int color) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.login_container), message, Snackbar.LENGTH_SHORT);
        if (color == 0) {
            snackbar.getView().setBackgroundResource(R.color.colorErrorSnackbar);
        } else {
            snackbar.getView().setBackgroundResource(R.color.colorSuccessSnackbar);
        }
        snackbar.show();
    }

    void createUser(final FirebaseUser firebaseUser) {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            showSnackBar("Cannot fetch location, Please try again!", 0);
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            geocoder = new Geocoder(LoginActivity.this, Locale.getDefault());
                            try {
                                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                                String postalCode = addresses.get(0).getPostalCode();
                                String addressLine = addresses.get(0).getAddressLine(0);
                                savePostalCode(postalCode);
                                List<String> reports = new ArrayList<>();
                                final User user = new User(firebaseUser.getDisplayName(),
                                        firebaseUser.getUid(),
                                        firebaseUser.getEmail(),
                                        firebaseUser.getPhotoUrl().toString(),
                                        reports,
                                        postalCode,
                                        addressLine,
                                        0);
                                mUserReference.child(user.getUserId()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {
                                            showSnackBar("User Created", 1);
                                            saveDetails(user);
                                        } else {
                                            showSnackBar("Error creating user", 0);
                                        }
                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    void saveDetails(User user) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String userString = gson.toJson(user);
        editor.putString("User", userString);
        editor.apply();
        launchHome();
    }

    void savePostalCode(String postalCode){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("PostalCode", postalCode);
        editor.apply();
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) + ContextCompat
                .checkSelfPermission(this,
                        Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (this, Manifest.permission.ACCESS_FINE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (this, Manifest.permission.CAMERA)) {

                Snackbar snackbar = Snackbar.make(this.findViewById(android.R.id.content),
                        "We really need these permissions for app to work",
                        Snackbar.LENGTH_INDEFINITE).setAction("Grant",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requestPermissions(
                                        new String[]{Manifest.permission
                                                .ACCESS_FINE_LOCATION, Manifest.permission.CAMERA},
                                        PERMISSIONS_MULTIPLE_REQUEST);
                            }
                        });
                snackbar.getView().setBackgroundResource(R.color.colorErrorSnackbar);
                snackbar.show();
            } else {
                requestPermissions(
                        new String[]{Manifest.permission
                                .ACCESS_FINE_LOCATION, Manifest.permission.CAMERA},
                        PERMISSIONS_MULTIPLE_REQUEST);
            }
        } else {
            enableAllButtons();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSIONS_MULTIPLE_REQUEST) {
            if (grantResults.length > 0) {
                boolean cameraPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                boolean locationPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                if (cameraPermission && locationPermission) {
                    enableAllButtons();
                } else {
                    Snackbar snackbar = Snackbar.make(this.findViewById(android.R.id.content),
                            "We really need these permissions for app to work",
                            Snackbar.LENGTH_INDEFINITE).setAction("Grant",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    requestPermissions(
                                            new String[]{Manifest.permission
                                                    .ACCESS_FINE_LOCATION, Manifest.permission.CAMERA},
                                            PERMISSIONS_MULTIPLE_REQUEST);
                                }
                            });
                    snackbar.getView().setBackgroundResource(R.color.colorErrorSnackbar);
                    snackbar.show();
                }
            }
        }
    }

    void disableAllButtons() {
        googleSignInButton.setEnabled(false);
    }

    void enableAllButtons() {
        googleSignInButton.setEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        String userString = sharedPreferences.getString("User", null);
        if (userString != null) {
            launchHome();
        }
    }


}
