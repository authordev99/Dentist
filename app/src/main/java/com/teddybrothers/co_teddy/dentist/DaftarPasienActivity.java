package com.teddybrothers.co_teddy.dentist;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.teddybrothers.co_teddy.dentist.entity.Pasien;
import com.teddybrothers.co_teddy.dentist.entity.User;

import java.util.Calendar;

public class DaftarPasienActivity extends AppCompatActivity {

    TextView tvNo, tvPengguna;
    EditText etNama, etTempatLahir, etTanggalLahir, etNoKTP, etSuku, etPekerjaan, etAlamat, etTelpRumah, etTelpSeluler,
            etGolDarah, etTekananDarah, etJantung, etDiabetes, etHaemopilia, etHepatitis, etGastring, etLainnya, etAlergiObar,
            etAlergiMakanan, etPassword, etEmail;
    String nama, namaSortir, tempatLahir, tanggalLahir, noIdentitas, jenKel, pekerjaan, suku, alamat, telpRumah, telpSeluler, kategori,golDarah, tekDarah, jantung, ketJantung, diabetes, ketDiabetes, haemopilia, ketHaemopilia, hepatitis, ketHepatitis, gastring, ketGastring, lainnya, ketPenyakitLain, alergiObat, ketAlergiObat, alergiMakanan, ketAlergiMakanan;
    String statusUserCurrent;
    String tanggalCatat;
    String umur,umurCurrent;
    RadioGroup rgJenKel, rgGolDar, rgJantung, rgDiabetes, rgHaemopilia, rgHepatitis, rgGastring, rgLainnya, rgAlergiObat, rgAlergiMakanan;
    RadioButton rbJenKel, rbGolDar,rbJantung, rbDiabetes, rbHaemopilia, rbHepatitis, rbGastring, rbLainnya, rbAlergiObar, rbAlergiMakanan;
    int idJenKel, idJantung, idDiabetes, idHaemopilia, idHepatitis, idGastring, idLainnya, idAlergiObat, idAlergiMakanan,idGolDar;
    int noUrut;
    public static final String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agust", "Sept", "Okt", "Nov", "Des"};
    public Calendar cal = Calendar.getInstance();
    public int day = cal.get(Calendar.DAY_OF_MONTH);
    public int month = cal.get(Calendar.MONTH);
    public int year = cal.get(Calendar.YEAR);
    public int yearCurrent = cal.get(Calendar.YEAR);

    FirebaseDatabase mDatabase;
    DatabaseReference mRoot, mUserRef, mPasienRef;
    FirebaseAuth mAuthAdm, mAuthPasien;
    ProgressDialog progressDialog;
    public String noFile, userID, mon, tglHariini, noPasien;
    String idPasien, userId, status = "null", userIDPasien;
    long pasienCount;
    Utilities util = new Utilities();
    String AuthPasien;
    LinearLayout ll_dataPengguna;
    Button btnDate;
    Spinner spKategori;
    public String[] Kategori = new String[]
            {
                    "Umum", "Sekolah Kusuma Bangsa", "MDP Group"
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_pasien);
        System.out.println("ON CREATED");
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
        }
        mRoot = mDatabase.getReference();
        mUserRef = mRoot.child("users");
        mPasienRef = mRoot.child("pasien");
        mAuthAdm = FirebaseAuth.getInstance();

        spKategori = findViewById(R.id.spKategori);
        ll_dataPengguna = (LinearLayout) findViewById(R.id.ll_dataPengguna);
        tvPengguna = (TextView) findViewById(R.id.tvPengguna);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        tvNo = (TextView) findViewById(R.id.tvNo);
        etNama = (EditText) findViewById(R.id.etNama);
        etNoKTP = (EditText) findViewById(R.id.etNoKTP);
        etTempatLahir = (EditText) findViewById(R.id.etTempatLahir);
        btnDate = (Button) findViewById(R.id.btnDate);
        etPekerjaan = (EditText) findViewById(R.id.etPekerjaan);
        etAlamat = (EditText) findViewById(R.id.etAlamat);
        etSuku = (EditText) findViewById(R.id.etSuku);
        etAlamat = (EditText) findViewById(R.id.etAlamatRumah);
        etTelpRumah = (EditText) findViewById(R.id.etTeleponRumah);
        etTelpSeluler = (EditText) findViewById(R.id.etTeleponSelular);
        etTekananDarah = (EditText) findViewById(R.id.etTekananDarah);
        rgJantung = (RadioGroup) findViewById(R.id.rgJantung);
        rgGolDar = (RadioGroup) findViewById(R.id.rgGolDar);
        rgJenKel = (RadioGroup) findViewById(R.id.rgJenKel);
        rgDiabetes = (RadioGroup) findViewById(R.id.rgDiabetes);
        rgHaemopilia = (RadioGroup) findViewById(R.id.rgHaemopilia);
        rgHepatitis = (RadioGroup) findViewById(R.id.rgHepatitis);
        rgGastring = (RadioGroup) findViewById(R.id.rgGastring);
        rgLainnya = (RadioGroup) findViewById(R.id.rgPenyakitLain);
        rgAlergiObat = (RadioGroup) findViewById(R.id.rgAlergi);
        rgAlergiMakanan = (RadioGroup) findViewById(R.id.rgAlergiMakanan);
        etJantung = (EditText) findViewById(R.id.etJantung);
        etDiabetes = (EditText) findViewById(R.id.etDiabetes);
        etHaemopilia = (EditText) findViewById(R.id.etHaemopilia);
        etHepatitis = (EditText) findViewById(R.id.etHepatitis);
        etGastring = (EditText) findViewById(R.id.etGastring);
        etLainnya = (EditText) findViewById(R.id.etPenyakitLain);
        etAlergiObar = (EditText) findViewById(R.id.etAlergiObat);
        etAlergiMakanan = (EditText) findViewById(R.id.etAlergiMakanan);



        filterAlpabetic(etNama);
        filterAlpabetic(etTempatLahir);
        filterAlpabetic(etSuku);
        filterAlpabetic(etPekerjaan);

        etJantung.setEnabled(false);
        etDiabetes.setEnabled(false);
        etHaemopilia.setEnabled(false);
        etHepatitis.setEnabled(false);
        etGastring.setEnabled(false);
        etLainnya.setEnabled(false);
        etAlergiObar.setEnabled(false);
        etAlergiMakanan.setEnabled(false);

        idGolDar = rgGolDar.getCheckedRadioButtonId();
        idJenKel = rgJenKel.getCheckedRadioButtonId();
        idJantung = rgJantung.getCheckedRadioButtonId();
        idDiabetes = rgDiabetes.getCheckedRadioButtonId();
        idHaemopilia = rgHaemopilia.getCheckedRadioButtonId();
        idHepatitis = rgHepatitis.getCheckedRadioButtonId();
        idGastring = rgGastring.getCheckedRadioButtonId();
        idLainnya = rgLainnya.getCheckedRadioButtonId();
        idAlergiObat = rgAlergiObat.getCheckedRadioButtonId();
        idAlergiMakanan = rgAlergiMakanan.getCheckedRadioButtonId();


        ArrayAdapter<String> mAdapterPembayaran = new ArrayAdapter<String>(DaftarPasienActivity.this, android.R.layout.simple_list_item_1, Kategori);
        spKategori.setAdapter(mAdapterPembayaran);

        rbGolDar = findViewById(idGolDar);
        rbJenKel = (RadioButton) findViewById(idJenKel);
        rbJantung = (RadioButton) findViewById(idJantung);
        rbDiabetes = (RadioButton) findViewById(idDiabetes);
        rbHaemopilia = (RadioButton) findViewById(idHaemopilia);
        rbHepatitis = (RadioButton) findViewById(idHepatitis);
        rbGastring = (RadioButton) findViewById(idGastring);
        rbLainnya = (RadioButton) findViewById(idLainnya);
        rbAlergiObar = (RadioButton) findViewById(idAlergiObat);
        rbAlergiMakanan = (RadioButton) findViewById(idAlergiMakanan);
        mon = MONTHS[month];
        int monAja = month+1;
        tglHariini = day + "-" +monAja + "-" + yearCurrent;
        System.out.println("TODAY = " + tglHariini);

        btnDate.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                pickDate();
            }
        });

        rgGolDar.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                rbGolDar = (RadioButton) findViewById(checkedId);
                System.out.println("Radio BUtton = " + rbGolDar.getText().toString());
            }
        });


        rgJenKel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                rbJenKel = (RadioButton) findViewById(checkedId);
                System.out.println("Radio BUtton = " + rbJenKel.getText().toString());
            }
        });

        rgJantung.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                rbJantung = (RadioButton) findViewById(checkedId);
                System.out.println("Radio BUtton = " + rbJantung.getText().toString());
                if (rbJantung.getText().toString().equalsIgnoreCase("Ada")) {
                    etJantung.setEnabled(true);
                } else {
                    etJantung.setEnabled(false);
                }
            }
        });


        rgDiabetes.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                rbDiabetes = (RadioButton) findViewById(checkedId);
                System.out.println("Radio BUtton DIABETES = " + rgDiabetes.indexOfChild(rbDiabetes));
                if (rbDiabetes.getText().toString().equalsIgnoreCase("Ada")) {
                    etDiabetes.setEnabled(true);
                } else {
                    etDiabetes.setEnabled(false);
                }

            }
        });


        rgHaemopilia.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                rbHaemopilia = (RadioButton) findViewById(checkedId);
                System.out.println("Radio BUtton = " + rbHaemopilia.getText().toString());
                if (rbHaemopilia.getText().toString().equalsIgnoreCase("Ada")) {
                    etHaemopilia.setEnabled(true);
                } else {
                    etHaemopilia.setEnabled(false);
                }
            }
        });

        rgHepatitis.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                rbHepatitis = (RadioButton) findViewById(checkedId);
                System.out.println("Radio BUtton = " + rbHepatitis.getText().toString());
                if (rbHepatitis.getText().toString().equalsIgnoreCase("Ada")) {
                    etHepatitis.setEnabled(true);
                } else {
                    etHepatitis.setEnabled(false);
                }
            }
        });

        rgGastring.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                rbGastring = (RadioButton) findViewById(checkedId);
                System.out.println("Radio BUtton = " + rbGastring.getText().toString());
                if (rbGastring.getText().toString().equalsIgnoreCase("Ada")) {
                    etGastring.setEnabled(true);
                } else {
                    etGastring.setEnabled(false);
                }
            }
        });

        rgLainnya.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                rbLainnya = (RadioButton) findViewById(checkedId);
                System.out.println("Radio BUtton = " + rbLainnya.getText().toString());
                if (rbLainnya.getText().toString().equalsIgnoreCase("Ada")) {
                    etLainnya.setEnabled(true);
                } else {
                    etLainnya.setEnabled(false);
                }
            }
        });

        rgAlergiObat.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                rbAlergiObar = (RadioButton) findViewById(checkedId);
                System.out.println("Radio BUtton = " + rbAlergiObar.getText().toString());
                if (rbAlergiObar.getText().toString().equalsIgnoreCase("Ada")) {
                    etAlergiObar.setEnabled(true);
                } else {
                    etAlergiObar.setEnabled(false);
                }
            }
        });

        rgAlergiMakanan.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                rbAlergiMakanan = (RadioButton) findViewById(checkedId);
                System.out.println("Radio BUtton = " + rbAlergiMakanan.getText().toString());
                if (rbAlergiMakanan.getText().toString().equalsIgnoreCase("Ada")) {
                    etAlergiMakanan.setEnabled(true);
                } else {
                    etAlergiMakanan.setEnabled(false);
                }
            }
        });


        mPasienRef.limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                    idPasien = objSnapshot.getKey();
                    pasienCount = dataSnapshot.getChildrenCount();
                    System.out.println("pasien count = " + pasienCount);

                }

                if (idPasien != null) {
                    System.out.println("idPasien = " + idPasien);
                    noUrut = Integer.parseInt(idPasien);

                } else {
                    System.out.println("year = " + yearCurrent + " " + "month = " + month);
                    String bulan;
                    int bln = month+1;
                    if (bln<10)
                    {
                        bulan = "0"+bln;
                    }
                    else
                    {
                        bulan = String.valueOf(bln);
                    }
                    noUrut = Integer.parseInt(String.valueOf(yearCurrent) + bulan + "0001");
                    System.out.println("NO URUT 1 = " + noUrut);
                }

                mPasienRef.orderByKey().equalTo(String.valueOf(noUrut)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        System.out.println("DATA EXISTS = " + dataSnapshot.exists());
                        if (dataSnapshot.exists() == true) {
                            System.out.println("year 1 = " + yearCurrent + " " + "month = " + month);
                            int bulan = month+1;
                            String mon;
                            if (bulan<10)
                            {
                                mon = "0"+bulan;
                            }
                            else
                            {
                                mon = String.valueOf(bulan);
                            }

                            System.out.println("bulan = "+bulan);
                            noUrut = Integer.parseInt(String.valueOf(yearCurrent) + mon + "0001");



                        }

                        mPasienRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                noUrut = (int) (noUrut + dataSnapshot.getChildrenCount());
                                System.out.println("noUrut dalem = " + noUrut + dataSnapshot.getChildrenCount());
                                System.out.println("NO URUT 2 = " + noUrut);
                                if (!status.equals("ubah")) {
                                    tvNo.setText(String.valueOf(noUrut));
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });



                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        userId = mUserRef.push().getKey();


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            idPasien = extras.getString("idPasien");
            status = extras.getString("ubah");
            userIDPasien = extras.getString("userIDPasien");
        }
        System.out.println("ID Pasien = " + idPasien);

        if (idPasien != null) {

            getSupportActionBar().setTitle("Perbaharui Data Pasien");

            mPasienRef.child(idPasien).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    noPasien = dataSnapshot.getKey();
                    System.out.println("noPasien");
                    String userID = dataSnapshot.child("userID").getValue(String.class);
                    String tempatLahir = dataSnapshot.child("tempatLahir").getValue(String.class);
                    String tanggalLahir = dataSnapshot.child("tanggalLahir").getValue(String.class);
                    String noIdentitas = dataSnapshot.child("noIdentitas").getValue(String.class);
                    String jenKel = dataSnapshot.child("jenisKelamin").getValue(String.class);
                    String suku = dataSnapshot.child("suku").getValue(String.class);
                    String pekerjaan = dataSnapshot.child("pekerjaan").getValue(String.class);
                    String alamat = dataSnapshot.child("alamat").getValue(String.class);
                    String telpRumah = dataSnapshot.child("teleponRumah").getValue(String.class);
                    String telpSeluler = dataSnapshot.child("teleponSeluler").getValue(String.class);
                    String kategori = dataSnapshot.child("kategori").getValue(String.class);
                    String golDarah = dataSnapshot.child("golonganDarah").getValue(String.class);
                    String tekDarah = dataSnapshot.child("tekananDarah").getValue(String.class);
                    String jantung = dataSnapshot.child("penyakitJantung").getValue(String.class);
                    String ketJantung = dataSnapshot.child("ketPenyakitJantung").getValue(String.class);
                    String diabetes = dataSnapshot.child("diabetes").getValue(String.class);
                    String ketDiabetes = dataSnapshot.child("ketDiabetes").getValue(String.class);
                    String haemopilia = dataSnapshot.child("haemopilia").getValue(String.class);
                    String ketHaemopilia = dataSnapshot.child("ketHaemopilia").getValue(String.class);
                    String hepatitis = dataSnapshot.child("hepatitis").getValue(String.class);
                    String ketHepatitis = dataSnapshot.child("ketHepatitis").getValue(String.class);
                    String gastring = dataSnapshot.child("gastring").getValue(String.class);
                    String ketGastring = dataSnapshot.child("ketGastring").getValue(String.class);
                    String lainnya = dataSnapshot.child("penyakitLainnya").getValue(String.class);
                    String ketPenyakitLain = dataSnapshot.child("ketPenyakitLainnnya").getValue(String.class);
                    String alergiObat = dataSnapshot.child("alergiObat").getValue(String.class);
                    String ketAlergiObat = dataSnapshot.child("ketAlergiObat").getValue(String.class);
                    String alergiMakanan = dataSnapshot.child("alergiMakanan").getValue(String.class);
                    String ketAlergiMakanan = dataSnapshot.child("ketAlergiMakanan").getValue(String.class);
                    statusUserCurrent = dataSnapshot.child("statusUser").getValue(String.class);
                    tanggalCatat = dataSnapshot.child("tanggalCatat").getValue(String.class);
                    umurCurrent = dataSnapshot.child("umur").getValue(String.class);
                    String nama = dataSnapshot.child("nama").getValue(String.class);
                    System.out.println("NO PASIEN = " + noPasien);
                    tvNo.setText(noPasien);
                    etNama.setText(nama);
                    etNoKTP.setText(noIdentitas);
                    etTempatLahir.setText(tempatLahir);
                    btnDate.setText(tanggalLahir);
                    etPekerjaan.setText(pekerjaan);
                    etAlamat.setText(alamat);
                    etSuku.setText(suku);
                    etTelpRumah.setText(telpRumah);
                    etTelpSeluler.setText(telpSeluler);
                    spKategori.setSelection(Integer.parseInt(kategori));
                    etTekananDarah.setText(tekDarah);
                    rgJantung.check(rgJantung.getChildAt(Integer.parseInt(jantung)).getId());
                    rgGolDar.check(rgGolDar.getChildAt(Integer.parseInt(golDarah)).getId());
                    rgJenKel.check(rgJenKel.getChildAt(Integer.parseInt(jenKel)).getId());
                    rgDiabetes.check(rgDiabetes.getChildAt(Integer.parseInt(diabetes)).getId());
                    rgHaemopilia.check(rgHaemopilia.getChildAt(Integer.parseInt(haemopilia)).getId());
                    rgHepatitis.check(rgHepatitis.getChildAt(Integer.parseInt(hepatitis)).getId());
                    rgGastring.check(rgGastring.getChildAt(Integer.parseInt(gastring)).getId());
                    rgLainnya.check(rgLainnya.getChildAt(Integer.parseInt(lainnya)).getId());
                    rgAlergiObat.check(rgAlergiObat.getChildAt(Integer.parseInt(alergiObat)).getId());
                    rgAlergiMakanan.check(rgAlergiMakanan.getChildAt(Integer.parseInt(alergiMakanan)).getId());
                    etJantung.setText(ketJantung);
                    etDiabetes.setText(ketDiabetes);
                    etHaemopilia.setText(ketHaemopilia);
                    etHepatitis.setText(ketHepatitis);
                    etGastring.setText(ketGastring);
                    etLainnya.setText(ketPenyakitLain);
                    etAlergiObar.setText(ketAlergiObat);
                    etAlergiMakanan.setText(ketAlergiMakanan);
                    ll_dataPengguna.setVisibility(View.GONE);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }


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


    public void pickDate() {

        cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int myear, int monthOfYear, int dayOfMonth) {
                month = monthOfYear + 1;
                String mon = MONTHS[month - 1];
                day = dayOfMonth;
                year = myear;
                btnDate.setText(day + " " + mon + " " + year);
            }
        };

        DatePickerDialog dpDialog = new DatePickerDialog(this, listener, year, month, day);
        dpDialog.show();

    }

    private void createPasien(final String nama, String namaSortir, String tempatLahir, String tanggalLahir, String umur, String noIdentitas, String jenKel, String pekerjaan, String suku, String alamat, String telpRumah, String telpSeluler, String kategori,String golDarah, String tekDarah,
                              String jantung, String ketJantung, String diabetes, String ketDiabetes, String haemopilia, String ketHaemopilia, String hepatitis, String ketHepatitis, String gastring, String ketGastring, String lainnya, String ketLainnya,
                              String alergiObat, String ketAlergiObat, String alergiMakanan, String ketAlergiMakanan, String tanggal, final String userId, final String tipe, String statusUser) {

        System.out.println("Umur createPasien = " + umur);
        System.out.println("nama sortir = " + namaSortir);
        if (tipe.equalsIgnoreCase("update")) {
            statusUser = statusUserCurrent;
        }
        Pasien pasien = new Pasien(nama, namaSortir, tempatLahir, tanggalLahir, umur, noIdentitas, jenKel, alamat, suku, pekerjaan, telpRumah, telpSeluler,kategori, golDarah, tekDarah,
                jantung, ketJantung, diabetes, ketDiabetes, haemopilia, ketHaemopilia, hepatitis, ketHepatitis, gastring, ketGastring, lainnya, ketLainnya,
                alergiObat, ketAlergiObat, alergiMakanan, ketAlergiMakanan, tanggal, statusUser, userId, null);

        mPasienRef.child(noFile).setValue(pasien).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (tipe.equalsIgnoreCase("update")) {
                    mUserRef.child(userId).child("fullname").setValue(nama);
                    progressDialog.dismiss();
                    finish();
                }


            }
        });
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        MenuItem update = menu.findItem(R.id.reschedule);
        MenuItem addPhoto = menu.findItem(R.id.addphoto);
        MenuItem done = menu.findItem(R.id.done);
        addPhoto.setVisible(false);

        if (idPasien != null && status.equalsIgnoreCase("ubah")) {
            update.setVisible(true);
            addPhoto.setVisible(false);
            done.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.done) {


            noFile = tvNo.getText().toString();
            nama = etNama.getText().toString();
            namaSortir = etNama.getText().toString().toLowerCase();
            tempatLahir = etTempatLahir.getText().toString();
            tanggalLahir = btnDate.getText().toString();
            noIdentitas = etNoKTP.getText().toString();
            jenKel = String.valueOf(rgJenKel.indexOfChild(rbJenKel));
            suku = etSuku.getText().toString();
            pekerjaan = etPekerjaan.getText().toString();
            alamat = etAlamat.getText().toString();
            telpRumah = etTelpRumah.getText().toString();
            telpSeluler = etTelpSeluler.getText().toString();
            golDarah = String.valueOf(rgGolDar.indexOfChild(rbGolDar));
            tekDarah = etTekananDarah.getText().toString();
            jantung = String.valueOf(rgJantung.indexOfChild(rbJantung));
            ketJantung = etJantung.getText().toString();
            diabetes = String.valueOf(rgDiabetes.indexOfChild(rbDiabetes));
            ketDiabetes = etDiabetes.getText().toString();
            haemopilia = String.valueOf(rgHaemopilia.indexOfChild(rbHaemopilia));
            ketHaemopilia = etHaemopilia.getText().toString();
            hepatitis = String.valueOf(rgHepatitis.indexOfChild(rbHepatitis));
            ketHepatitis = etHepatitis.getText().toString();
            gastring = String.valueOf(rgGastring.indexOfChild(rbGastring));
            ketGastring = etGastring.getText().toString();
            lainnya = String.valueOf(rgLainnya.indexOfChild(rbLainnya));
            ketPenyakitLain = etLainnya.getText().toString();
            alergiObat = String.valueOf(rgAlergiObat.indexOfChild(rbAlergiObar));
            ketAlergiObat = etAlergiObar.getText().toString();
            alergiMakanan = String.valueOf(rgAlergiMakanan.indexOfChild(rbAlergiMakanan));
            ketAlergiMakanan = etAlergiMakanan.getText().toString();
            kategori = String.valueOf(spKategori.getSelectedItemPosition());
            String password = etPassword.getText().toString();
            String email = etEmail.getText().toString();

            if (TextUtils.isEmpty(nama)) {
                etNama.setError("Nama harus diisi!");
                etNama.requestFocus();
                Toast.makeText(DaftarPasienActivity.this, "kolom nama kosong", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(tempatLahir)) {
                etTempatLahir.setError("Tempat Lahir harus diisi!");
                Toast.makeText(DaftarPasienActivity.this, "kolom tempat lahir kosong", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(noIdentitas)) {
                etNoKTP.setError("No Identitas harus diisi!");
                Toast.makeText(DaftarPasienActivity.this, "kolom no identitas kosong", Toast.LENGTH_SHORT).show();
            } else if (tanggalLahir.equalsIgnoreCase("Pilih tanggal lahir . . .")) {
                Toast.makeText(DaftarPasienActivity.this, "silahkan pilih tanggal lahir", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(suku)) {
                etSuku.setError("Suku harus diisi!");
                Toast.makeText(DaftarPasienActivity.this, "kolom suku kosong", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(pekerjaan)) {
                etPekerjaan.setError("Pekerjaan harus diisi!");
                Toast.makeText(DaftarPasienActivity.this, "kolom pekerjaan kosong", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(alamat)) {
                etAlamat.setError("Alamat harus diisi!");
                Toast.makeText(DaftarPasienActivity.this, "kolom alamat kosong", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(telpRumah)) {
                etTelpRumah.setError("Telp Rumah harus diisi!");
                Toast.makeText(DaftarPasienActivity.this, "kolom telp rumah kosong", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(telpSeluler)) {
                etTelpSeluler.setError("Telepon Seluler harus diisi!");
                Toast.makeText(DaftarPasienActivity.this, "kolom telp seluler kosong", Toast.LENGTH_SHORT).show();
            }  else if (TextUtils.isEmpty(tekDarah)) {
                etTekananDarah.setError("Tekanan Darah harus diisi!");
                Toast.makeText(DaftarPasienActivity.this, "kolom tekanan darah kosong", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(email)) {
                etEmail.setError("Email harus diisi!");
                Toast.makeText(DaftarPasienActivity.this, "kolom email kosong", Toast.LENGTH_SHORT).show();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError("Masukkan email yang valid");
                Toast.makeText(DaftarPasienActivity.this, "email tidak valid", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(password)) {
                etPassword.setError("Password harus diisi!");
                Toast.makeText(DaftarPasienActivity.this, "kolom password kosong", Toast.LENGTH_SHORT).show();
            } else if (password.length() < 6) {
                etPassword.setError("Password harus lebih dari 6 digits!");
                Toast.makeText(DaftarPasienActivity.this, "password error", Toast.LENGTH_SHORT).show();
            } else {
                progressDialog = new ProgressDialog(DaftarPasienActivity.this);
                progressDialog.setMessage("Loading...");
                progressDialog.show();

                String deviceToken = FirebaseInstanceId.getInstance().getToken();
                String status = "Pasien";

                System.out.println("year yang dipilih = " + year);
                int umur = yearCurrent - year;
                System.out.println("year yang dipilih = " + umur + " " + yearCurrent);
                createUser(email, password, deviceToken, status);
            }


        } else if (id == R.id.reschedule)

        {


            noFile = tvNo.getText().toString();
            nama = etNama.getText().toString();
            namaSortir = etNama.getText().toString().toLowerCase();
            tempatLahir = etTempatLahir.getText().toString();
            tanggalLahir = btnDate.getText().toString();
            noIdentitas = etNoKTP.getText().toString();
            jenKel = String.valueOf(rgJenKel.indexOfChild(rbJenKel));
            suku = etSuku.getText().toString();
            pekerjaan = etPekerjaan.getText().toString();
            alamat = etAlamat.getText().toString();
            telpRumah = etTelpRumah.getText().toString();
            telpSeluler = etTelpSeluler.getText().toString();
            golDarah = String.valueOf(rgGolDar.indexOfChild(rbGolDar));
            tekDarah = etTekananDarah.getText().toString();
            jantung = String.valueOf(rgJantung.indexOfChild(rbJantung));
            ketJantung = etJantung.getText().toString();
            diabetes = String.valueOf(rgDiabetes.indexOfChild(rbDiabetes));
            ketDiabetes = etDiabetes.getText().toString();
            haemopilia = String.valueOf(rgHaemopilia.indexOfChild(rbHaemopilia));
            ketHaemopilia = etHaemopilia.getText().toString();
            hepatitis = String.valueOf(rgHepatitis.indexOfChild(rbHepatitis));
            ketHepatitis = etHepatitis.getText().toString();
            gastring = String.valueOf(rgGastring.indexOfChild(rbGastring));
            ketGastring = etGastring.getText().toString();
            lainnya = String.valueOf(rgLainnya.indexOfChild(rbLainnya));
            ketPenyakitLain = etLainnya.getText().toString();
            alergiObat = String.valueOf(rgAlergiObat.indexOfChild(rbAlergiObar));
            ketAlergiObat = etAlergiObar.getText().toString();
            alergiMakanan = String.valueOf(rgAlergiMakanan.indexOfChild(rbAlergiMakanan));
            ketAlergiMakanan = etAlergiMakanan.getText().toString();
            kategori = String.valueOf(spKategori.getSelectedItemPosition());
            System.out.println("year yang dipilih = " + year);
            System.out.println("umurCurrent = " + umurCurrent);
            System.out.println("yearCurrent = " + yearCurrent);
            if (yearCurrent!=year)
            {
                umur = String.valueOf(yearCurrent - year);
            }
            else
            {
                umur = umurCurrent;
            }

            System.out.println("umur yang update = " + umur);

            if (TextUtils.isEmpty(nama)) {
                etNama.setError("Nama harus diisi!");
            } else if (TextUtils.isEmpty(tempatLahir)) {
                etTempatLahir.setError("Tempat Lahir harus diisi!");
            } else if (TextUtils.isEmpty(noIdentitas)) {
                etNoKTP.setError("No Identitas harus diisi!");
            } else if (TextUtils.isEmpty(suku)) {
                etSuku.setError("Suku harus diisi!");
            } else if (TextUtils.isEmpty(pekerjaan)) {
                etPekerjaan.setError("Pekerjaan harus diisi!");
            } else if (TextUtils.isEmpty(alamat)) {
                etAlamat.setError("Alamat harus diisi!");
            } else if (TextUtils.isEmpty(telpRumah)) {
                etTelpRumah.setError("Telp Rumah harus diisi!");
            } else if (TextUtils.isEmpty(telpSeluler)) {
                etTelpSeluler.setError("Telepon Seluler harus diisi!");
            }  else if (TextUtils.isEmpty(tekDarah)) {
                etTekananDarah.setError("Tekanan Darah harus diisi!");
            } else {
                progressDialog = new ProgressDialog(DaftarPasienActivity.this);
                progressDialog.setMessage("Loading...");
                progressDialog.show();
                createPasien(nama, namaSortir, tempatLahir, tanggalLahir, String.valueOf(umur), noIdentitas, jenKel, pekerjaan, suku, alamat, telpRumah, telpSeluler, kategori, golDarah, tekDarah,
                        jantung, ketJantung, diabetes, ketDiabetes, haemopilia, ketHaemopilia, hepatitis, ketHepatitis, gastring, ketGastring, lainnya, ketPenyakitLain,
                        alergiObat, ketAlergiObat, alergiMakanan, ketAlergiMakanan, tanggalCatat, userIDPasien, "update", "Aktif");
            }


        }
        return super.onOptionsItemSelected(item);
    }


    private void createUser(final String email, final String password, final String deviceToken, final String status) {
        mAuthPasien = FirebaseAuth.getInstance();
        mAuthPasien.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful())
                {
                    mAuthPasien.getCurrentUser().sendEmailVerification();
                    String dateCreated = day + "-" + month + "-" + yearCurrent;

                    System.out.println("date created = " + dateCreated);
                    String statusUser = "Aktif";
                    System.out.println("statusUser createUser = " + statusUser);

                    User user = new User(email, deviceToken, nama, status, statusUser, tanggalCatat);
                    mUserRef.child(mAuthPasien.getCurrentUser().getUid()).setValue(user);

                    int umur = yearCurrent - year;

                    System.out.println("Umur createPasien 1 = " + umur);
                    createPasien(nama, namaSortir, tempatLahir, tanggalLahir, String.valueOf(umur), noIdentitas, jenKel, pekerjaan, suku, alamat, telpRumah, telpSeluler,kategori, golDarah, tekDarah,
                            jantung, ketJantung, diabetes, ketDiabetes, haemopilia, ketHaemopilia, hepatitis, ketHepatitis, gastring, ketGastring, lainnya, ketPenyakitLain,
                            alergiObat, ketAlergiObat, alergiMakanan, ketAlergiMakanan, tglHariini, mAuthPasien.getCurrentUser().getUid(), "simpan", statusUser);
                    mAuthPasien.signOut();
                    System.out.println("UTIl = " + util.getEmail(DaftarPasienActivity.this));
                    mAuthAdm.signInWithEmailAndPassword(util.getEmail(DaftarPasienActivity.this), util.getPassword(DaftarPasienActivity.this)).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
//                            startActivity(new Intent(DaftarPasienActivity.this, ListPasienActivity.class));
                                System.out.println("LOGIN ADMIN LAGI = ");
                                Toast.makeText(DaftarPasienActivity.this, "Data Pasien Berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                                finish();
                                progressDialog.dismiss();
                            }

                        }
                    });
                }
                else {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(DaftarPasienActivity.this, "Email Sudah Terdaftar !", Toast.LENGTH_SHORT).show();
                        etEmail.requestFocus();
                        etEmail.setError("Email Sudah Terdaftar !");
                        progressDialog.dismiss();
                    }
                }

//                String mon = MONTHS[month-1];



            }
        });


    }

//    public final static boolean isValidEmail(CharSequence email)
//    {
//        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
//    }


    protected void onStart() {
        super.onStart();

        System.out.println("ON START CREATED");
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
        }

        mRoot = mDatabase.getReference();
        mUserRef = mRoot.child("users");
        mPasienRef = mRoot.child("pasien");
        mUserRef.keepSynced(true);
        mPasienRef.keepSynced(true);


    }


}
