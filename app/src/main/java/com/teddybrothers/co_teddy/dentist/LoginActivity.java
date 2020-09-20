package com.teddybrothers.co_teddy.dentist;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {

    private SignInButton btnGoogle;
    private static final int RC_SIGN_IN = 1;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "LoginActivity";
    private FirebaseAuth.AuthStateListener mAuthListener;
    Button btnLogin, btnRegis;
    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    DatabaseReference mUserRef, mRoot,mPasien,mDokter;
    ProgressDialog progressDialog;
    String current_user_id;
    EditText etEmail, etPassword;
    Utilities util = new Utilities();
    TextView tvForgetPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




         mDatabase = FirebaseDatabase.getInstance();

        setContentView(R.layout.activity_login);

        btnLogin = (Button) findViewById(R.id.btnSignIn);
//        btnRegis = (Button) findViewById(R.id.btnSignGoogle);
        tvForgetPassword = (TextView) findViewById(R.id.tvForgetPassword);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        tvForgetPassword.setPaintFlags(tvForgetPassword.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        tvForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,ForgetPasswordActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    etEmail.setError("Email harus diisi!");
                } else if (TextUtils.isEmpty(password)) {
                    etPassword.setError("Password harus diisi!");
                } else {
                    progressDialog = new ProgressDialog(LoginActivity.this);
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();
                    signIn(email, password);

                }

            }
        });
    }


    private void signIn(final String email, final String password) {


        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            System.out.println(email + " " + password);
                            util.setEmail(LoginActivity.this, email);
                            util.setPassword(LoginActivity.this, password);
                            System.out.println("UTIL GET" + util.getEmail(LoginActivity.this));
                            mUserRef.orderByChild("email").equalTo(email).addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    System.out.println("datasnapshot = " + dataSnapshot);
                                    current_user_id = dataSnapshot.getKey();
                                    System.out.println("USER ID LOGIN = " + current_user_id);
                                    String deviceToken = FirebaseInstanceId.getInstance().getToken();
                                    String status = dataSnapshot.child("status").getValue(String.class);
                                    String statusUser = dataSnapshot.child("statusUser").getValue(String.class);
                                    if (status.equalsIgnoreCase("Pasien"))
                                    {
                                        mPasien.orderByChild("userID").equalTo(current_user_id).addChildEventListener(new ChildEventListener() {
                                            @Override
                                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                                String idPasien = dataSnapshot.getKey();
                                                util.setIdPasienLogin(LoginActivity.this,idPasien);
                                            }

                                            @Override
                                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                            }

                                            @Override
                                            public void onChildRemoved(DataSnapshot dataSnapshot) {

                                            }

                                            @Override
                                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                    else if (status.equalsIgnoreCase("Dokter"))
                                    {
                                        mDokter.orderByChild("userID").equalTo(current_user_id).addChildEventListener(new ChildEventListener() {
                                            @Override
                                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                                String idDokter=dataSnapshot.getKey();
                                                util.setIdDokter(LoginActivity.this,idDokter);
                                            }

                                            @Override
                                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                            }

                                            @Override
                                            public void onChildRemoved(DataSnapshot dataSnapshot) {

                                            }

                                            @Override
                                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }

                                    if (statusUser.equalsIgnoreCase("Aktif"))
                                    {
                                        mUserRef.child(current_user_id).child("device_token").setValue(deviceToken);
                                        Toast.makeText(LoginActivity.this, "Selamat Datang", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        finish();
                                        progressDialog.dismiss();
                                    }
                                    else if (statusUser.equalsIgnoreCase("Non Aktif"))
                                    {
                                        Toast.makeText(LoginActivity.this,"Maaf, Akun anda non aktif! Silahkan hubungi administrator..",Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }

                                }

                                @Override
                                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                }

                                @Override
                                public void onChildRemoved(DataSnapshot dataSnapshot) {

                                }

                                @Override
                                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        } else {
                            Toast.makeText(LoginActivity.this, "Email dan Password Salah", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();

                        }
                    }
                });
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
//        if (requestCode == RC_SIGN_IN) {
//            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//            if (result.isSuccess()) {
//                // Google Sign In was successful, authenticate with Firebase
//                GoogleSignInAccount account = result.getSignInAccount();
//                firebaseAuthWithGoogle(account);
//            } else {
//                // Google Sign In failed, update UI appropriately
//                // ...
//            }
//        }
//    }

//    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
//
//        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithCredential:success");
//
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            Toast.makeText(LoginActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//
//                        }
//
//                        // ...
//                    }
//                });
//    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
        }
        mAuth = FirebaseAuth.getInstance();
        mRoot = mDatabase.getReference();
        mUserRef = mRoot.child("users");
        mPasien = mRoot.child("pasien");
        mDokter = mRoot.child("dokter");

    }
}
