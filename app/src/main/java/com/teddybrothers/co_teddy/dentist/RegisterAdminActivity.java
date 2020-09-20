package com.teddybrothers.co_teddy.dentist;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.teddybrothers.co_teddy.dentist.entity.Admin;
import com.teddybrothers.co_teddy.dentist.entity.User;

import java.util.Calendar;

public class RegisterAdminActivity extends AppCompatActivity {

    FirebaseAuth mAuthAdm, mAuthNewUser;
    FirebaseDatabase mDatabase;
    DatabaseReference mUserRef, mRoot, mAdminRef;
    Button btnSignUp;
    public Calendar cal = Calendar.getInstance();
    public int day = cal.get(Calendar.DAY_OF_MONTH);
    public int month = cal.get(Calendar.MONTH);
    public int year = cal.get(Calendar.YEAR);
    public static final String[] MONTHS = {"Januari", "Febuari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
    Spinner spGender;
    EditText etPassword, etFullname, etEmail, etAlamat;
    Utilities util = new Utilities();
    ProgressBar progressBar;
    String[] jenisKelamin;
    String statusUser = "Aktif", idAdmin, statusIntent;
    LinearLayout ll_email, ll_password, ll_fullname;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_admin);

        spGender = (Spinner) findViewById(R.id.spGender);
        etAlamat = (EditText) findViewById(R.id.etAlamat);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etFullname = (EditText) findViewById(R.id.etFullname);
        etEmail = (EditText) findViewById(R.id.etEmail);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        ll_email = (LinearLayout) findViewById(R.id.ll_email);
        ll_password = (LinearLayout) findViewById(R.id.ll_password);
        ll_fullname = (LinearLayout) findViewById(R.id.ll_fullname);
        mAuthAdm = FirebaseAuth.getInstance();

        filterAlpabetic(etAlamat);
        filterAlpabetic(etFullname);


        jenisKelamin = new String[]
                {
                        "Laki - Laki", "Perempuan"
                };

        ArrayAdapter<String> mAdapterJenKel = new ArrayAdapter<String>(RegisterAdminActivity.this, android.R.layout.simple_list_item_1, jenisKelamin);
        spGender.setAdapter(mAdapterJenKel);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (btnSignUp.getText().toString().equalsIgnoreCase("UPDATE")) {
                    String alamat = etAlamat.getText().toString();
                    final String gender = String.valueOf(spGender.getSelectedItemPosition());
                    if (TextUtils.isEmpty(alamat)) {
                        etEmail.setError("Email belum terisi");
                    } else {
                        progressDialog = new ProgressDialog(RegisterAdminActivity.this);
                        progressDialog.setMessage("Loading...");
                        progressDialog.show();
                        updateAdmin(alamat, gender);
                    }


                }
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                String fullname = etFullname.getText().toString();
                String namaSortir = etFullname.getText().toString().toLowerCase();
                String alamat = etAlamat.getText().toString();

                final String gender = String.valueOf(spGender.getSelectedItemPosition());
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    etEmail.setError("Masukkan email yang valid");
                } else if (TextUtils.isEmpty(password)) {
                    etPassword.setError("Password harus diisi!");
                } else if (password.length() < 6) {
                    etPassword.setError("Password harus lebih dari 6 digits!");

                } else if (TextUtils.isEmpty(fullname)) {
                    etFullname.setError("fullname belum terisi");
                } else if (TextUtils.isEmpty(gender)) {

                    Toast.makeText(RegisterAdminActivity.this, "Please select your gender", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog = new ProgressDialog(RegisterAdminActivity.this);
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();
                    String deviceToken = FirebaseInstanceId.getInstance().getToken();
                    System.out.println("Device Token = " + deviceToken);
                    createUser(email, password, fullname, namaSortir, deviceToken, alamat, gender, "Administrator", statusUser);
                }

            }
        });

    }

    public void filterAlpabetic(EditText editText) {
        editText.setFilters(new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence cs, int start,
                                               int end, Spanned spanned, int dStart, int dEnd) {
                        // TODO Auto-generated method stub
                        if (cs.equals("")) { // for backspace
                            return cs;
                        }
                        if (cs.toString().matches("[a-zA-Z ]+")) {
                            return cs;
                        }
                        return "";
                    }
                }
        });

    }


    private void createUser(final String email, final String password, final String fullname, final String namaSortir, final String deviceToken, final String alamat, final String gender, final String status, final String statusUser) {
        mAuthNewUser = FirebaseAuth.getInstance();
        mAuthNewUser.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    mAuthNewUser.getCurrentUser().sendEmailVerification();
                    cal = Calendar.getInstance();
                    year = cal.get(Calendar.YEAR);
                    month = cal.get(Calendar.MONTH);
                    day = cal.get(Calendar.DAY_OF_MONTH);
                    String mon = MONTHS[month];
                    String dateCreated = day + " " + mon + " " + year;
                    System.out.println("date created = " + dateCreated);

                    Admin admin = new Admin(fullname, namaSortir, dateCreated, alamat, gender, statusUser, null);

                    mAdminRef.child(mAuthNewUser.getCurrentUser().getUid()).setValue(admin);

                    User user = new User(email, deviceToken, fullname, status, statusUser, dateCreated);
                    mUserRef.child(mAuthNewUser.getCurrentUser().getUid()).setValue(user);

                    mAuthNewUser.signOut();

                    mAuthAdm.signInWithEmailAndPassword(util.getEmail(getApplicationContext()), util.getPassword(getApplicationContext())).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegisterAdminActivity.this, "Data Administrator Berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                                finish();
                                progressDialog.dismiss();
                            }
                        }
                    });
                } else {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(RegisterAdminActivity.this, "Email Sudah Terdaftar !", Toast.LENGTH_SHORT).show();
                        etEmail.requestFocus();
                        etEmail.setError("Email Sudah Terdaftar !");
                        progressDialog.dismiss();
                    }
                }
            }
        });
    }

    private void updateAdmin(String alamat, String jenKel) {

        mAdminRef.child(idAdmin).child("alamat").setValue(alamat);
        mAdminRef.child(idAdmin).child("jenisKelamin").setValue(jenKel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                finish();
                progressDialog.dismiss();
                Toast.makeText(RegisterAdminActivity.this, "Data Berhasil diperbaharui !", Toast.LENGTH_SHORT).show();
            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
        }

        mAuthAdm = FirebaseAuth.getInstance();
        mRoot = mDatabase.getReference();
        mUserRef = mRoot.child("users");
        mAdminRef = mRoot.child("admin");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            idAdmin = extras.getString("idAdmin");
            statusIntent = extras.getString("statusIntent");
        }
        System.out.println("ID Admin = " + idAdmin + " Status Intent = " + statusIntent);

        if (idAdmin != null && statusIntent.equalsIgnoreCase("UPDATE")) {
            mAdminRef.child(idAdmin).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String alamat = dataSnapshot.child("alamat").getValue(String.class);
                    String jenisKelamin = dataSnapshot.child("jenisKelamin").getValue(String.class);

                    etAlamat.setText(alamat);
                    spGender.setSelection(Integer.parseInt(jenisKelamin));
                    ll_email.setVisibility(View.GONE);
                    ll_password.setVisibility(View.GONE);
                    ll_fullname.setVisibility(View.GONE);
                    btnSignUp.setText(statusIntent);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


    }
}
