package com.teddybrothers.co_teddy.dentist;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.teddybrothers.co_teddy.dentist.entity.About;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

public class InputAboutDental extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    ImageView ivCall, ivEmail, ivLocation;
    TextView tvJamBuka, tvJamTutup;
    public static int REQUEST_PERMISSIONS = 1;
    int PLACE_PICKER_REQUEST = 1;
    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    DatabaseReference mUserRef, mRoot, mAbout;
    boolean boolean_permission;
    ProgressDialog progressDialog;
    Utilities util = new Utilities();
    GPSTracker gps;
    String alamat, latitude, longitude;
    EditText etNama, etTelp, etEmail, etTelpSeluler;
    Button btnLokasi;
    RelativeLayout rlJamBuka, rlJamTutup;
    Boolean flags = false;
    int jamBuka, menitBuka, jamTutup, menitTutup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_about_dental);

        progressDialog = new ProgressDialog(InputAboutDental.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
        }
        mAuth = FirebaseAuth.getInstance();
        mRoot = mDatabase.getReference();
        mUserRef = mRoot.child("users");
        mAbout = mRoot.child("about");

        rlJamBuka = findViewById(R.id.rlJamBuka);
        rlJamTutup = findViewById(R.id.rlJamTutup);
        tvJamBuka = findViewById(R.id.tvJamBuka);
        tvJamTutup = findViewById(R.id.tvJamTutup);
        etNama = findViewById(R.id.etNama);
        btnLokasi = findViewById(R.id.btnLokasi);
        etTelp = findViewById(R.id.etTelp);
        etTelpSeluler = findViewById(R.id.etTelpSeluler);
        etEmail = findViewById(R.id.etEmail);

        rlJamBuka.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickTime();
                flags = true;
            }
        });

        rlJamTutup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickTime();
                flags = false;
            }
        });

        btnLokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    Intent intentMap = builder.build(InputAboutDental.this);
                    startActivityForResult(intentMap, PLACE_PICKER_REQUEST);
                    System.out.println("alamat = " + alamat);

                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }

            }
        });


        mAbout.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("datasnapshot about = " + dataSnapshot);
                if (dataSnapshot.getValue() != null) {
                    About about = dataSnapshot.getValue(About.class);

                    latitude = dataSnapshot.child("latitude").getValue(String.class);
                    longitude = dataSnapshot.child("longitude").getValue(String.class);
                    jamBuka = about.getJamBuka();
                    menitBuka = about.getMenitBuka();
                    jamTutup = about.getJamTutup();
                    menitTutup = about.getMenitTutup();

                    etNama.setText(about.getNamaKlinik());
                    btnLokasi.setText(about.getAlamat());
                    etTelp.setText(about.getNoTelp());
                    etTelpSeluler.setText(about.getTelpSeluler());
                    etEmail.setText(about.getEmail());
                    AboutDentalActivity aboutDentalActivity = new AboutDentalActivity();
                    tvJamBuka.setText(aboutDentalActivity.cekJam(jamBuka,menitBuka));
                    tvJamTutup.setText(aboutDentalActivity.cekJam(jamTutup,menitTutup));
                    progressDialog.dismiss();
                } else {
                    progressDialog.dismiss();
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    public void pickTime() {
        Calendar now = Calendar.getInstance();
        final TimePickerDialog tpd = TimePickerDialog.newInstance(
                InputAboutDental.this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
        );
        tpd.setTitle("Pilih Waktu");

        tpd.show(getFragmentManager(), "Timepickerdialog");


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_perawatan_lain, menu);
        final MenuItem addphoto = menu.findItem(R.id.addphoto).setVisible(false);
        final MenuItem pdf = menu.findItem(R.id.pdf).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.save) {

            progressDialog = new ProgressDialog(InputAboutDental.this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();

            final String namaKlinik = etNama.getText().toString();
            final String alamatKlinik = btnLokasi.getText().toString();
            final String telp = etTelp.getText().toString();
            final String telpSeluler = etTelpSeluler.getText().toString();
            final String email = etEmail.getText().toString();
            int interval = 1;

            if (TextUtils.isEmpty(namaKlinik)) {
                etNama.setError("nama klinik harus diisi!");
                progressDialog.dismiss();
            } else if (alamatKlinik.equalsIgnoreCase("Pilih Lokasi klinik . . .")) {
                btnLokasi.setError("alamat klinik harus terpilih!");
                progressDialog.dismiss();
            } else if (TextUtils.isEmpty(telp)) {
                etTelp.setError("telepon klinik harus diisi!");
                progressDialog.dismiss();
            } else if (TextUtils.isEmpty(email)) {
                etEmail.setError("email klinik harus diisi!");
                progressDialog.dismiss();
            } else if (TextUtils.isEmpty(telpSeluler)) {
                etEmail.setError("telp seluler klinik harus diisi!");
                progressDialog.dismiss();
            }  else if (tvJamBuka.getText().toString().equalsIgnoreCase("pilih jam buka")) {
                Toast.makeText(this, "Silahkan pilih jam buka", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            } else if (tvJamTutup.getText().toString().equalsIgnoreCase("pilih jam tutup")) {
                Toast.makeText(this, "Silahkan pilih jam tutup", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            } else if (jamBuka == jamTutup) {
                Toast.makeText(this, "Jam tutup harus diatas jam buka", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }else {
                saveAbout(namaKlinik, alamatKlinik, telp, telpSeluler, email, latitude, longitude, interval, jamBuka, menitBuka, jamTutup, menitTutup);
            }


        }


        return super.

                onOptionsItemSelected(item);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);

                alamat = String.valueOf(place.getAddress());
                btnLokasi.setText(alamat);
                latitude = String.valueOf(place.getLatLng().latitude);
                longitude = String.valueOf(place.getLatLng().longitude);

            }
        }


    }

    private void saveAbout(String namaKlinik, String alamat, String telp, String telpSeluler, String email, String latitude, String longitude, int interval, int jamBuka, int menitBuka,
                           int jamTutup, int menitTutup) {
        System.out.println("latitude = " + latitude);
        About about = new About(namaKlinik, alamat, telp, telpSeluler, email, latitude, longitude, interval, jamBuka, menitBuka, jamTutup, menitTutup);

        mAbout.setValue(about).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    finish();
                    progressDialog.dismiss();
                    Toast.makeText(InputAboutDental.this, "Data Profil Klinik Berhasil disimpan", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        String jam = null, menit = null;

        if (hourOfDay < 10) {
            jam = "0" + hourOfDay;
        } else {
            jam = String.valueOf(hourOfDay);
        }
        if (minute < 10) {
            menit = "0" + minute;
        } else {
            menit = String.valueOf(minute);
        }

        if (flags) {
            jamBuka = hourOfDay;
            menitBuka = minute;
            tvJamBuka.setText(jam + ":" + menit);
        } else if (!flags) {
            jamTutup = hourOfDay;
            menitTutup = minute;
            System.out.println("jamTutup = " + hourOfDay + ":" + minute);
            tvJamTutup.setText(jam + ":" + menit);
        }


    }
}
