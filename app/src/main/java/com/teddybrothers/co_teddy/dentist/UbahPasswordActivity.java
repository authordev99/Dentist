package com.teddybrothers.co_teddy.dentist;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UbahPasswordActivity extends AppCompatActivity {

    FirebaseDatabase mDatabase;
    FirebaseAuth mAuth;
    DatabaseReference mRoot, mUserRef;
    EditText etCurrentPassword, etNewPassword, etPasswordAgain;
    FirebaseUser user;
    ProgressDialog progressDialog;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubah_password);

        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
        }
        mRoot = mDatabase.getReference();
        mUserRef = mRoot.child("users");
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();


        etCurrentPassword = (EditText) findViewById(R.id.etCurrentPassword);
        etNewPassword = (EditText) findViewById(R.id.etNewPassword);
        etPasswordAgain = (EditText) findViewById(R.id.etPasswordAgain);
        btnSave = (Button) findViewById(R.id.btnSave);


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(UbahPasswordActivity.this);
                progressDialog.setMessage("Loading...");
                progressDialog.show();

                String currentPassword = etCurrentPassword.getText().toString();
                if (TextUtils.isEmpty(currentPassword)) {
                    etCurrentPassword.setError("Password Lama harus diisi!");
                    progressDialog.dismiss();
                } else {
                    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                String newPassword = etNewPassword.getText().toString();
                                String passwordAgain = etPasswordAgain.getText().toString();
                                if (TextUtils.isEmpty(newPassword)) {
                                    etNewPassword.setError("Password Baru harus diisi!");
                                    progressDialog.dismiss();
                                } else if (TextUtils.isEmpty(passwordAgain)) {
                                    etPasswordAgain.setError("Konfirmasi Password Baru harus diisi!");
                                    progressDialog.dismiss();
                                } else {
                                    if (newPassword.equalsIgnoreCase(passwordAgain)) {
                                        user.updatePassword(passwordAgain).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {

                                                    Toast.makeText(UbahPasswordActivity.this, "Password telah diperbaharui!", Toast.LENGTH_SHORT).show();
                                                    mAuth.signOut();
                                                    startActivity(new Intent(UbahPasswordActivity.this, LoginActivity.class));
                                                    finish();
                                                    progressDialog.dismiss();

                                                } else {
                                                    Toast.makeText(UbahPasswordActivity.this, "Password Error!, Please Try Again! ", Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();
                                                }
                                            }
                                        });
                                    } else {
                                        Toast.makeText(UbahPasswordActivity.this, "Password baru tidak sama", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                }


                            }else {
                                Toast.makeText(UbahPasswordActivity.this, "Password lama tidak sesuai", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
                }


            }
        });

    }
}
