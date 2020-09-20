package com.teddybrothers.co_teddy.dentist;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.teddybrothers.co_teddy.dentist.entity.Dokter;
import com.teddybrothers.co_teddy.dentist.entity.Formulir;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.UUID;

public class FormActivity extends AppCompatActivity {

    public static int REQUEST_PERMISSIONS = 1;
    boolean boolean_permission;
    boolean isSigned = false;
    Bitmap bitmap;
    Button btnPasien, btnDokter;
    RadioGroup rgPilihan;
    String formKey,flags,idPasien;
    static final String PASIEN_KEY = "pasienKey";
    static final String PASIEN_NAME = "pasienName";
    static final String PASIEN_ALAMAT = "pasienAlamat";
    static final String PASIEN_JENKEL = "pasienJenKel";
    static final String PASIEN_UMUR = "pasienUmur";
    ProgressBar pbTtdDokter;
    static final String DOC_KEY = "docKey";
    static final String DOC_NAME = "docName";
    static final String DOC_TTD = "docTtd";
    static final int PICK_DOKTER_REQUEST = 1;
    String pasienName, pasienKey, pasienAlamat, pasienJenKel, dokterKey, dokterName,dokterTtd,umurPasien;
    TextView tvPilihan, tvPasien, tvAlamat, tvJenKel, tvUmur, tvNamaDokter, tvNamaPasien;
    TextView titikk, titik11, titik21, titik31, nama1, umur1, alamat1, jenkel1, tvTanggal,tvTahun;
    EditText etNama2, etUmur2, etAlamat2, etLainnnya;
    ProgressDialog progressDialog;
    Spinner spJenKel2, spTerhadap;
    String[] JenisKelamin, Terhadap;
    private SignaturePad mSignaturePadPasien;
    private Button  mClearButtonPasien,btnEdit;
    RadioButton rbPersetujuan,rbPenolakan;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    String returnTTDUrl = "null";
    String namaTindakan;
    String userId;
    Uri contentUriPasien;
    LinearLayout llForm;
    ImageView mSignaturePad,ivSignaturePadPasien;
    Utilities util = new Utilities();
    FirebaseDatabase mDatabase;
    DatabaseReference mRoot, mUserRef, mJadwalRef, mDokterRef, mPasienRef, mForm,mTindakan;
    FirebaseAuth mAuth;
    static final int PICK_PASIEN_REQUEST = 4;
    RelativeLayout rlDokter;
    MultiAutoCompleteTextView acTindakan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        rlDokter =  findViewById(R.id.rlDokter);
        pbTtdDokter =  findViewById(R.id.pbTtdDokter);
        llForm =  findViewById(R.id.llForm);
        acTindakan = findViewById(R.id.acTindakan);
        btnPasien =  findViewById(R.id.btnPasien);
        btnEdit =  findViewById(R.id.btnEditTtd);
        btnDokter =  findViewById(R.id.btnDokter);
        etNama2 =  findViewById(R.id.etNama2);
        tvTahun =  findViewById(R.id.tvTahun);
        tvUmur =  findViewById(R.id.tvUmur);
        tvNamaPasien =  findViewById(R.id.tvNamaPasien);
        tvNamaDokter =  findViewById(R.id.tvNamaDokter);
        tvPilihan = findViewById(R.id.tvPilihan);
        tvJenKel =  findViewById(R.id.tvJenKel);
        etUmur2 = findViewById(R.id.etUmur2);
        tvAlamat = findViewById(R.id.tvAlamat);
        tvTanggal = findViewById(R.id.tvTanggal);
        etAlamat2 = findViewById(R.id.etAlamat2);
        etLainnnya = findViewById(R.id.etLainnya);
        titikk = findViewById(R.id.titikk);
        titik11 =  findViewById(R.id.titik11);
        titik21 = findViewById(R.id.titik21);
        titik31 = findViewById(R.id.titik31);
        nama1 =  findViewById(R.id.Nama1);
        umur1 = findViewById(R.id.Umur1);
        alamat1 =  findViewById(R.id.alamat1);
        jenkel1 = findViewById(R.id.jenkel1);
        tvPasien = findViewById(R.id.tvPasien);
        spJenKel2 = findViewById(R.id.spJenKel2);
        spTerhadap = findViewById(R.id.spTerhadap);
        rgPilihan =  findViewById(R.id.rgPilihan);
        rbPenolakan = findViewById(R.id.rbPenolakan);
        rbPersetujuan = findViewById(R.id.rbPersetujuan);
        //Dokter
        mSignaturePad = findViewById(R.id.signature_pad);
        ivSignaturePadPasien =  findViewById(R.id.iv_signature_pad_pasien);
        //Pasien
        mSignaturePadPasien =  findViewById(R.id.signature_pad_pasien);
        mClearButtonPasien =  findViewById(R.id.clear_button_pasien);



        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
        }
        mRoot = mDatabase.getReference();
        mUserRef = mRoot.child("users");
        mJadwalRef = mRoot.child("jadwal");
        mPasienRef = mRoot.child("pasien");
        mDokterRef = mRoot.child("dokter");
        mForm = mRoot.child("formulir");
        mTindakan = mRoot.child("tindakan");
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        final ArrayAdapter<String> autoComplete = new ArrayAdapter<String>(FormActivity.this, android.R.layout.simple_list_item_1);
        mTindakan.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot namaTindakan : dataSnapshot.getChildren()) {
                    String tindakan = namaTindakan.child("namaTindakan").getValue(String.class);
                    System.out.println("Tindakan = " + tindakan);
                    autoComplete.add(tindakan);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        acTindakan.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        acTindakan.setThreshold(1);
        acTindakan.setAdapter(autoComplete);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            formKey = extras.getString("formkey");
            namaTindakan = extras.getString("namaTindakan");
            idPasien = extras.getString("idPasien");
        }
        System.out.println("formKey = " + formKey+" namaTindakan = "+namaTindakan+" idPasien = "+idPasien);



        if (formKey!=null)
        {
            getSupportActionBar().setTitle("Perbaharui Data Formulir");
            isSigned = true;
            mSignaturePadPasien.setVisibility(View.GONE);
            mClearButtonPasien.setVisibility(View.GONE);
//            rgPilihan.setVisibility(View.GONE);
            mForm.child(formKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    idPasien = dataSnapshot.child("idPasien").getValue(String.class);
                    String idDokter = dataSnapshot.child("idDokter").getValue(String.class);
                    String statusForm = dataSnapshot.child("status").getValue(String.class);
                    String terhadap = dataSnapshot.child("terhadap").getValue(String.class);
                    System.out.println("terhadap = "+terhadap);
                    String tglForm = dataSnapshot.child("tglForm").getValue(String.class);
                    String tindakan = dataSnapshot.child("tindakan").getValue(String.class);
                    String ttdPasien = dataSnapshot.child("ttdPasien").getValue(String.class);



                    mPasienRef.child(idPasien).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String nama = dataSnapshot.child("nama").getValue(String.class);
                            String jenKel = dataSnapshot.child("jenisKelamin").getValue(String.class);
                            String alamat = dataSnapshot.child("alamat").getValue(String.class);
                            String umur = dataSnapshot.child("umur").getValue(String.class);
                            if (jenKel.equalsIgnoreCase("0"))
                            {
                                jenKel = "Laki-laki";
                            }
                            else if (jenKel.equalsIgnoreCase("1"))
                            {
                                jenKel = "Perempuan";
                            }
                            btnPasien.setText(nama);
                            tvJenKel.setText(jenKel);
                            tvAlamat.setText(alamat);
                            tvNamaPasien.setText(nama);
                            tvUmur.setText(umur+" Tahun");
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    tvPilihan.setText(statusForm);
                    tvTanggal.setText(tglForm);

                    System.out.println("statusForm = "+statusForm);
                    if (statusForm.equalsIgnoreCase("PERSETUJUAN"))
                    {
                        System.out.println("Tes setuju");
                        rgPilihan.check(rgPilihan.getChildAt(0).getId());

                    }else if (statusForm.equalsIgnoreCase("PENOLAKAN"))
                    {
                        System.out.println("Tes nolak");
                        rgPilihan.check(rgPilihan.getChildAt(1).getId());
                    }

                    mDokterRef.child(idDokter).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String nama = dataSnapshot.child("nama").getValue(String.class);
                            String ttdDokter = dataSnapshot.child("ttdUrl").getValue(String.class);
                            System.out.println("ttdDokter= "+ttdDokter);
                            btnDokter.setText(nama);
                            tvNamaDokter.setText("drg. "+nama);
                            if (ttdDokter!=null)
                            {
                                Glide.with(getApplicationContext()).load(ttdDokter)
                                        .thumbnail(0.5f)
                                        .crossFade()
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .into(mSignaturePad);
                                pbTtdDokter.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                    acTindakan.setText(tindakan);
                    if (ttdPasien!=null)
                    {
                        Glide.with(getApplicationContext()).load(ttdPasien)
                                .thumbnail(0.5f)
                                .crossFade()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(ivSignaturePadPasien);
                    }

                    if (terhadap.equalsIgnoreCase("0"))
                    {
                        spTerhadap.setSelection(Integer.parseInt(terhadap));

                    }
                    else if (terhadap!="0")
                    {
                        String alamatLain = dataSnapshot.child("alamatLain").getValue(String.class);
                        String jenKelLain = dataSnapshot.child("jenKelLain").getValue(String.class);
                        String namaLain = dataSnapshot.child("namaLain").getValue(String.class);
                        String umurLain = dataSnapshot.child("umurLain").getValue(String.class);
                        etLainnnya.setVisibility(View.VISIBLE);
                        nama1.setVisibility(View.VISIBLE);
                        umur1.setVisibility(View.VISIBLE);
                        alamat1.setVisibility(View.VISIBLE);
                        jenkel1.setVisibility(View.VISIBLE);
                        titik11.setVisibility(View.VISIBLE);
                        titikk.setVisibility(View.VISIBLE);
                        titik21.setVisibility(View.VISIBLE);
                        titik31.setVisibility(View.VISIBLE);
                        etNama2.setVisibility(View.VISIBLE);
                        etUmur2.setVisibility(View.VISIBLE);
                        etAlamat2.setVisibility(View.VISIBLE);
                        spJenKel2.setVisibility(View.VISIBLE);

                        spTerhadap.setSelection(1);
                        etLainnnya.setText(terhadap);
                        etNama2.setText(namaLain);
                        etUmur2.setText(umurLain);
                        etAlamat2.setText(alamatLain);
                        spJenKel2.setSelection(Integer.parseInt(jenKelLain));


                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isSigned = false;
                    btnEdit.setVisibility(View.GONE);
                    mClearButtonPasien.setVisibility(View.VISIBLE);
                    ivSignaturePadPasien.setVisibility(View.GONE);
                    mSignaturePadPasien.setVisibility(View.VISIBLE);
                }
            });
        }
        else if (idPasien!=null && namaTindakan!=null)
        {
            acTindakan.setText(namaTindakan);

            mPasienRef.child(idPasien).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String nama = dataSnapshot.child("nama").getValue(String.class);
                    String jenKel = dataSnapshot.child("jenisKelamin").getValue(String.class);
                    String alamat = dataSnapshot.child("alamat").getValue(String.class);
                    String umur = dataSnapshot.child("umur").getValue(String.class);
                    if (jenKel.equalsIgnoreCase("0"))
                    {
                        jenKel = "Laki-laki";
                    }
                    else if (jenKel.equalsIgnoreCase("1"))
                    {
                        jenKel = "Perempuan";
                    }
                    btnPasien.setText(nama);
                    tvJenKel.setText(jenKel);
                    tvAlamat.setText(alamat);
                    tvNamaPasien.setText(nama);
                    tvUmur.setText(umur+" Tahun");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        else
        {
            btnEdit.setVisibility(View.GONE);
        }




        mUserRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String status = dataSnapshot.child("status").getValue(String.class);
                System.out.println("status snapshot= " + status);
                if (status.equalsIgnoreCase("Dokter"))
                {
                    rlDokter.setVisibility(View.GONE);
                    mDokterRef.orderByChild("userID").equalTo(userId).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Dokter dokter = dataSnapshot.getValue(Dokter.class);
                            String ttdDokter = dokter.getTtdUrl();
                            System.out.println("ttdDokter= "+ttdDokter);
                            if (ttdDokter!=null)
                            {

                                Glide.with(getApplicationContext()).load(ttdDokter)
                                        .thumbnail(0.5f)
                                        .crossFade()
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .into(mSignaturePad);
                                pbTtdDokter.setVisibility(View.GONE);
                            }
                            tvNamaDokter.setText("drg. "+dokter.getNama());
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

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnPasien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FormActivity.this, PasienActivity.class);
                String status = "pilihPasien";
                intent.putExtra("pilihPasien", status);
                startActivityForResult(intent, PICK_PASIEN_REQUEST);
            }
        });


        btnDokter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FormActivity.this, DokterActivity.class);
                startActivityForResult(intent, PICK_DOKTER_REQUEST);
            }
        });


        Date date = new Date();
        String tglForm = new SimpleDateFormat("dd" + "/" + "MM" + "/" + "yyyy").format(date);

        tvTanggal.setText(tglForm);
        //TTD PASIEN
        mSignaturePadPasien.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
                tvPasien.setVisibility(View.GONE);
            }

            @Override
            public void onSigned() {
                isSigned = true;
                mClearButtonPasien.setEnabled(true);
                String tipe = "pasien";
                Bitmap signatureBitmapPasien = mSignaturePadPasien.getSignatureBitmap();
                addJpgSignatureToGallery(signatureBitmapPasien, tipe);

            }

            @Override
            public void onClear() {

                mClearButtonPasien.setEnabled(false);
            }
        });

        mClearButtonPasien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSigned = false;
                mSignaturePadPasien.clear();
            }
        });





        JenisKelamin = new String[]
                {
                        "Laki - Laki", "Perempuan"
                };
        Terhadap = new String[]
                {
                        "Saya", "Lainnya"
                };

        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(FormActivity.this, android.R.layout.simple_list_item_1, Terhadap);
        ArrayAdapter<String> mAdapterJenKel = new ArrayAdapter<String>(FormActivity.this, android.R.layout.simple_list_item_1, JenisKelamin);
        spJenKel2.setAdapter(mAdapterJenKel);
        spTerhadap.setAdapter(mAdapter);

        spTerhadap.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).equals("Lainnya")) {
                    etLainnnya.setVisibility(View.VISIBLE);
                    nama1.setVisibility(View.VISIBLE);
                    umur1.setVisibility(View.VISIBLE);
                    alamat1.setVisibility(View.VISIBLE);
                    jenkel1.setVisibility(View.VISIBLE);
                    titik11.setVisibility(View.VISIBLE);
                    titikk.setVisibility(View.VISIBLE);
                    titik21.setVisibility(View.VISIBLE);
                    titik31.setVisibility(View.VISIBLE);
                    etNama2.setVisibility(View.VISIBLE);
                    etUmur2.setVisibility(View.VISIBLE);
                    etAlamat2.setVisibility(View.VISIBLE);
                    spJenKel2.setVisibility(View.VISIBLE);
                } else {
                    etLainnnya.setVisibility(View.GONE);
                    nama1.setVisibility(View.GONE);
                    umur1.setVisibility(View.GONE);
                    alamat1.setVisibility(View.GONE);
                    jenkel1.setVisibility(View.GONE);
                    titik11.setVisibility(View.GONE);
                    titikk.setVisibility(View.GONE);
                    titik21.setVisibility(View.GONE);
                    titik31.setVisibility(View.GONE);
                    etNama2.setVisibility(View.GONE);
                    etUmur2.setVisibility(View.GONE);
                    etAlamat2.setVisibility(View.GONE);
                    spJenKel2.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(FormActivity.this,android.R.layout.simple_list_item_1,JenisKelamin);
//        spJenKel.setAdapter(adapter);



        rgPilihan.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.rbPersetujuan) {
                    tvPilihan.setText("PERSETUJUAN ");
                } else if (checkedId == R.id.rbPenolakan) {
                    tvPilihan.setText("PENOLAKAN ");
                }

            }
        });




    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_PASIEN_REQUEST&&data!=null) {
            pasienName = data.getStringExtra(PASIEN_NAME);
            pasienKey = data.getStringExtra(PASIEN_KEY);
            umurPasien = data.getStringExtra(PASIEN_UMUR);
            pasienAlamat = data.getStringExtra(PASIEN_ALAMAT);
            pasienJenKel = data.getStringExtra(PASIEN_JENKEL);
            System.out.println("Pasien NAME = " + pasienName);
            btnPasien.setText(pasienName);
            if (pasienJenKel.equalsIgnoreCase("0"))
            {
                pasienJenKel = "Laki-laki";
            }
            else if (pasienJenKel.equalsIgnoreCase("1"))
            {
                pasienJenKel = "Perempuan";
            }
            tvJenKel.setText(pasienJenKel);
            tvAlamat.setText(pasienAlamat);
            tvNamaPasien.setText(pasienName);
            tvUmur.setText(umurPasien+" Tahun");

        } else if (requestCode == PICK_DOKTER_REQUEST && data!=null) {
            dokterName = data.getStringExtra(DOC_NAME);
            dokterKey = data.getStringExtra(DOC_KEY);
            dokterTtd = data.getStringExtra(DOC_TTD);
            pbTtdDokter.setVisibility(View.VISIBLE);
            System.out.println("Dokter NAME = " + dokterName);
            btnDokter.setText(dokterName);
            tvNamaDokter.setText(dokterName);
            System.out.println("Dokter TTD = "+dokterTtd);
            if (dokterTtd!=null)
            {
                Glide.with(getApplicationContext()).load(dokterTtd)
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(mSignaturePad);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_form, menu);
        MenuItem done = menu.findItem(R.id.done);
        MenuItem update = menu.findItem(R.id.update);

        if (formKey != null) {
            update.setVisible(true);
            done.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.done) {

            flags="save";

            mUserRef.child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String status = dataSnapshot.child("status").getValue(String.class);

                    if (status.equalsIgnoreCase("Dokter"))
                    {
                        mDokterRef.orderByChild("userID").equalTo(userId).addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                String idDokter = dataSnapshot.getKey();
                                save(idDokter);
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
                    else if (status.equalsIgnoreCase("Administrator"))
                    {
                        String idDokter = dokterKey;
                        save(idDokter);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else if (id==R.id.update)
        {

             flags="update";

            mUserRef.child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String status = dataSnapshot.child("status").getValue(String.class);

                    if (status.equalsIgnoreCase("Dokter"))
                    {
                        mDokterRef.orderByChild("userID").equalTo(userId).addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                String idDokter = dataSnapshot.getKey();
                                save(idDokter);
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
                    else if (status.equalsIgnoreCase("Administrator"))
                    {
                        String idDokter = dokterKey;
                        save(idDokter);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        return super.onOptionsItemSelected(item);
    }


    private void save (final String dokterKey)
    {

        if (flags.equalsIgnoreCase("update"))
        {
            mForm.child(formKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String idDokter = dokterKey;
                    String terhadap = null;
                    String ttdPasien = util.getTtdPasien(FormActivity.this);

                        idPasien = dataSnapshot.child("idPasien").getValue(String.class);


                    if (spTerhadap.getSelectedItem().toString().equalsIgnoreCase("Lainnya")) {
                        terhadap = etLainnnya.getText().toString();
                    } else if (spTerhadap.getSelectedItem().toString().equalsIgnoreCase("Saya")) {
                        terhadap = String.valueOf(spTerhadap.getSelectedItemPosition());
                    }
                    String namaPasien = btnPasien.getText().toString();
                    String namaTindakan = acTindakan.getText().toString();
                    String namaLain = etNama2.getText().toString();
                    String umurLain = etUmur2.getText().toString();
                    String alamatLain = etAlamat2.getText().toString();
                    String jenKelLain = String.valueOf(spJenKel2.getSelectedItemPosition());
                    System.out.println("JenKel Positiion = "+jenKelLain);
                    String statusPilihan = tvPilihan.getText().toString();
                    String tglForm = tvTanggal.getText().toString();


                    if (namaPasien.equalsIgnoreCase("Pilih Pasien ..."))
                    {
                        Toast.makeText(FormActivity.this,"Silahkan Pilih Pasien",Toast.LENGTH_SHORT).show();

                    }
                    else if (TextUtils.isEmpty(namaTindakan))
                    {
                        acTindakan.setError("Tindakan tidak boleh kosong");


                    } else if (!isSigned)
                    {
                        Toast.makeText(FormActivity.this,"Tanda Tangan tidak boleh kosong",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        if (terhadap.equalsIgnoreCase("0")) {
                            progressDialog = new ProgressDialog(FormActivity.this);
                            progressDialog.setMessage("Loading...");
                            progressDialog.show();
                            System.out.println("ttd Pasien = " + ttdPasien);
                            createFormSaya(idPasien, idDokter, terhadap, namaTindakan, tglForm, ttdPasien, statusPilihan,namaPasien);
                        }
                        else {

                            if (TextUtils.isEmpty(terhadap))
                            {
                                etLainnnya.setError("kolom tidak boleh kosong");
                            }
                            else if (TextUtils.isEmpty(namaLain))
                            {
                                etNama2.setError("nama tidak boleh kosong");
                            }
                            else if (TextUtils.isEmpty(umurLain))
                            {
                                etUmur2.setError("umur tidak boleh kosong");
                            }
                            else if (TextUtils.isEmpty(alamatLain))
                            {
                                etAlamat2.setError("alamat tidak boleh kosong");
                            }
                            else
                            {
                                progressDialog = new ProgressDialog(FormActivity.this);
                                progressDialog.setMessage("Loading...");
                                progressDialog.show();
                                createForm(idPasien, idDokter, terhadap, namaTindakan, namaLain, umurLain, alamatLain, jenKelLain, tglForm, ttdPasien,  statusPilihan,namaPasien);
                            }

                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else if (flags.equalsIgnoreCase("save"))
        {
            String idDokter = dokterKey;
            String terhadap = null;
            String ttdPasien = util.getTtdPasien(FormActivity.this);
            if (pasienKey!=null)
            {
                idPasien = pasienKey;
            }

            if (pasienName==null)
            {
                pasienName = btnPasien.getText().toString();
            }



            if (spTerhadap.getSelectedItem().toString().equalsIgnoreCase("Lainnya")) {
                terhadap = etLainnnya.getText().toString();
            } else if (spTerhadap.getSelectedItem().toString().equalsIgnoreCase("Saya")) {
                terhadap = String.valueOf(spTerhadap.getSelectedItemPosition());
            }

            String namaTindakan = acTindakan.getText().toString();
            String namaLain = etNama2.getText().toString();
            String umurLain = etUmur2.getText().toString();
            String alamatLain = etAlamat2.getText().toString();
            String jenKelLain = String.valueOf(spJenKel2.getSelectedItemPosition());
            System.out.println("JenKel Positiion = "+jenKelLain);
            String statusPilihan = tvPilihan.getText().toString();
            String tglForm = tvTanggal.getText().toString();
            String namaPasien = btnPasien.getText().toString();

            if (namaPasien.equalsIgnoreCase("Pilih Pasien ..."))
            {
                Toast.makeText(FormActivity.this,"Silahkan Pilih Pasien",Toast.LENGTH_SHORT).show();
            }
            else if (TextUtils.isEmpty(namaTindakan))
            {
                acTindakan.setError("Tindakan tidak boleh kosong");


            } else if (!isSigned)
            {
                Toast.makeText(FormActivity.this,"Tanda Tangan tidak boleh kosong",Toast.LENGTH_SHORT).show();
            }
            else
            {
                if (terhadap.equalsIgnoreCase("0")) {
                    System.out.println("ttd Pasien = " + ttdPasien);
                    progressDialog = new ProgressDialog(FormActivity.this);
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();
                    createFormSaya(idPasien, idDokter, terhadap, namaTindakan, tglForm, ttdPasien, statusPilihan,pasienName);
                }
                else {

                    if (TextUtils.isEmpty(terhadap))
                    {
                        etLainnnya.setError("kolom tidak boleh kosong");
                    }
                    else if (TextUtils.isEmpty(namaLain))
                    {
                        etNama2.setError("nama tidak boleh kosong");
                    }
                    else if (TextUtils.isEmpty(umurLain))
                    {
                        etUmur2.setError("umur tidak boleh kosong");
                    }
                    else if (TextUtils.isEmpty(alamatLain))
                    {
                        etAlamat2.setError("alamat tidak boleh kosong");
                    }
                    else
                    {
                        progressDialog = new ProgressDialog(FormActivity.this);
                        progressDialog.setMessage("Loading...");
                        progressDialog.show();
                        createForm(idPasien, idDokter, terhadap, namaTindakan, namaLain, umurLain, alamatLain, jenKelLain, tglForm, ttdPasien,  statusPilihan,pasienName);
                    }

                }
            }



        }




    }


    private void createForm(String idPasien, String idDokter, String terhadap, String tindakan, String namaLain, String umurLain, String alamatLain, String jenKelLain, String tglForm,
                            String ttdPasien, String status,String namaPasien) {

        Formulir formulir = new Formulir(idPasien, idDokter, terhadap, tindakan, namaLain, umurLain, alamatLain, jenKelLain, tglForm, ttdPasien,status,namaPasien);
        if (flags.equalsIgnoreCase("update"))
        {
            mForm.child(formKey).setValue(formulir).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(FormActivity.this,"Data Formulir Berhasil Diperbaharui",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    finish();

                }
            });
        }
        else if (flags.equalsIgnoreCase("save"))
        {
            mForm.push().setValue(formulir).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(FormActivity.this,"Data Formulir Berhasil Disimpan",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    finish();

                }
            });
        }

    }

    private void createFormSaya(String idPasien, String idDokter, String terhadap, String namaTindakan, String tglForm, String ttdPasien,  String status,String namaPasien) {

        Formulir formulir = new Formulir(idPasien, idDokter, terhadap, namaTindakan, null, null, null, null, tglForm, ttdPasien,status,namaPasien);

        if (flags.equalsIgnoreCase("update"))
        {
            mForm.child(formKey).setValue(formulir).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressDialog.dismiss();
                    finish();

                }
            });
        } else  if (flags.equalsIgnoreCase("save"))
        {
            mForm.push().setValue(formulir).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressDialog.dismiss();
                    finish();

                }
            });
        }

    }


    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("SignaturePad", "Directory not created");
        }
        return file;
    }

    public void saveBitmapToJPG(Bitmap bitmap, File photo) throws IOException {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        OutputStream stream = new FileOutputStream(photo);
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        stream.close();
    }


    public boolean addJpgSignatureToGallery(Bitmap signature, String tipe) {
        boolean result = false;

        try {
            File photo = new File(getAlbumStorageDir("SignaturePadPasien"), String.format("Signature_%d.jpg", System.currentTimeMillis()));
            saveBitmapToJPG(signature, photo);
            scanMediaFile(photo, tipe);
            System.out.println("tipe TDD = " + tipe);
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;

    }


    private String scanMediaFile(File photo, final String tipe) {

        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        contentUriPasien = Uri.fromFile(photo);
        mediaScanIntent.setData(contentUriPasien);
        System.out.println("URI TDD = " + contentUriPasien);

        String path = "ttd/" + UUID.randomUUID() + ".jpg";
        StorageReference ttdref = storage.getReference(path);

        UploadTask uploadTask = ttdref.putFile(contentUriPasien);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FormActivity.this, "Upload Gagal", Toast.LENGTH_LONG).show();

            }
        }).addOnSuccessListener(FormActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                Toast.makeText(FormActivity.this, "Upload Success", Toast.LENGTH_LONG).show();

                @SuppressWarnings("VisibleForTests") Uri url = taskSnapshot.getDownloadUrl();
                returnTTDUrl = url.toString();
                System.out.println("INI URL TTD!!!!" + returnTTDUrl);
                System.out.println("tipe = " + tipe);
                util.setTtdPasien(FormActivity.this, returnTTDUrl);



            }
        });
        FormActivity.this.sendBroadcast(mediaScanIntent);
        System.out.println("INI URL TTD LUAR !!!!" + returnTTDUrl);
        return returnTTDUrl;
    }



    protected void onStart() {
        super.onStart();



    }


}
