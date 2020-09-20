package com.teddybrothers.co_teddy.dentist;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
import com.teddybrothers.co_teddy.dentist.entity.BlockDate;
import com.teddybrothers.co_teddy.dentist.entity.Jadwal;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class InputJadwalActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {


    //GalleryPhotoStorage
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    Uri imageUri;
    String photoUrl = "null";
    public static int REQUEST_PERMISSIONS = 1;
    boolean boolean_permission;
    public String mCurrentPhotoPath, urlPhoto;
    static final String PASIEN_KEY = "pasienKey";
    static final String PASIEN_NAME = "pasienName";
    ProgressDialog progressDialog;
    EditText etKeluhan;
    Button btnTime, btnDate, btnDokter, btnPasien;
    public static final String[] MONTHS = {"Januari", "Febuari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
    private java.util.Calendar cal;
    private int day;
    private int month;
    private int year;
    Context context = this;
    static final String DOC_KEY = "docKey";
    static final String DOC_NAME = "docName";
    public String dokterKey, dokterName, pasienName, pasienKey, idPasien, tanggal, waktu, keluhan, jadwalKey, status, statusEdit;
    LinearLayout llPasien, llDokter;
    View viewPasien, viewDokter;
    final ArrayList<String> listDate = new ArrayList<>();
    FirebaseDatabase mDatabase;
    DatabaseReference mRoot, mUserRef, mJadwalRef, mDokterRef, mPasienRef, mNotifJadwal, notifRef,mAbout,mBlockDate;
    FirebaseAuth mAuth;
    ImageView ivPhotoUpload;
    static final int PICK_DOKTER_REQUEST = 1;
    static final int PICK_PASIEN_REQUEST = 4;
    private static final int GALERY_INTENT = 2;
    static final int CAMERA_PIC_REQUEST = 3;
    ArrayList<CustomObject> choice = new ArrayList<CustomObject>();
    CustomObject pilihan;
    Utilities util = new Utilities();
    String statusUser, rencanaPerawatan, currentDate, currentTime;
    String TAG = "MatkulAddActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_jadwal);

        ivPhotoUpload = (ImageView) findViewById(R.id.ivPhotoUpload);
        etKeluhan = (EditText) findViewById(R.id.etKeluhan);
        btnTime = (Button) findViewById(R.id.btnTime);
        btnDate = (Button) findViewById(R.id.btnDate);
        btnDokter = (Button) findViewById(R.id.btnDokter);
        btnPasien = (Button) findViewById(R.id.btnPasien);
        viewDokter = findViewById(R.id.viewDokter);
        viewPasien = findViewById(R.id.viewPasien);
        llDokter = (LinearLayout) findViewById(R.id.llDokter);
        llPasien = (LinearLayout) findViewById(R.id.llPasien);
        btnDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
               pickDate();

            }
        });
        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pickTime();
            }
        });

        CustomObject choice1 = new CustomObject(R.drawable.photocamera, "camera", "Camera", 0);
        choice.add(choice1);
        choice1 = new CustomObject(R.drawable.gallery, "gallery", "Gallery", 0);
        choice.add(choice1);

        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
        }
        mRoot = mDatabase.getReference();
        mUserRef = mRoot.child("users");
        mJadwalRef = mRoot.child("jadwal");
        mPasienRef = mRoot.child("pasien");
        mDokterRef = mRoot.child("dokter");
        mAbout = mRoot.child("about");
        mNotifJadwal = mRoot.child("notifJadwal");
        mBlockDate = mRoot.child("blockDate");
        notifRef = mRoot.child("notification");


        mAuth = FirebaseAuth.getInstance();

        statusUser = util.getStatus(InputJadwalActivity.this);
        if (statusUser.equalsIgnoreCase("Pasien")) {
            llPasien.setVisibility(View.GONE);
            viewPasien.setVisibility(View.GONE);
        } else if (statusUser.equalsIgnoreCase("Dokter")) {
            llDokter.setVisibility(View.GONE);
            viewDokter.setVisibility(View.GONE);
        }


        mBlockDate.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listDate.clear();
                for (DataSnapshot blockDate : dataSnapshot.getChildren()) {
                    final BlockDate dataBlockDate = blockDate.getValue(BlockDate.class);
                    String tanggalBlock = blockDate.child("dateBlock").getValue(String.class);
                    listDate.add(tanggalBlock);
                    dataBlockDate.idBlockDate = blockDate.getKey();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            System.out.println(extras);
            jadwalKey = extras.getString("jadwalKey");
            rencanaPerawatan = extras.getString("rencanaPerawatan");
        }

        if (rencanaPerawatan != null && statusUser.equalsIgnoreCase("Pasien")) {
            btnDate.setText(getDate(Long.parseLong(rencanaPerawatan)));
        }

        if (jadwalKey != null) {

            getSupportActionBar().setTitle("Perbaharui Data Jadwal");

            currentDate = extras.getString("tanggal");
            currentTime = extras.getString("waktu");
            btnDate.setText(currentDate);
            btnTime.setText(currentTime);
            etKeluhan.setText(extras.getString("keluhan"));
            dokterKey = extras.getString("IDdokter");
            pasienKey = extras.getString("IDpasien");
            dokterName = extras.getString("namaDokter");
            photoUrl = extras.getString("photoUrl");

            System.out.println("UPDATE = " + dokterKey + pasienKey);

            mDokterRef.child(dokterKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String dokterName = dataSnapshot.child("nama").getValue(String.class);
                    btnDokter.setText("drg. " + dokterName);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            mPasienRef.child(pasienKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String pasienName = dataSnapshot.child("nama").getValue(String.class);
                    btnPasien.setText(pasienName);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            Glide.with(InputJadwalActivity.this).load(photoUrl)
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivPhotoUpload);

            status = "Jadwal Ulang";
        }



        btnDokter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InputJadwalActivity.this, DokterActivity.class);
                String status = "pilihDokter";
                intent.putExtra("pilihDokter", status);
                startActivityForResult(intent, PICK_DOKTER_REQUEST);
            }
        });

        btnPasien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InputJadwalActivity.this, PasienActivity.class);
                String status = "pilihPasien";
                intent.putExtra("pilihPasien", status);
                startActivityForResult(intent, PICK_PASIEN_REQUEST);
            }
        });

        fn_permission();

    }


    public void pickDate() {

        Calendar now = Calendar.getInstance();
        int day = 0;
        if (now.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY)
        {
            day = now.get(Calendar.DAY_OF_MONTH)+1;
        }
        else
        {
            day = now.get(Calendar.DAY_OF_MONTH);
        }
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                InputJadwalActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                day
        );

        now.set(now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                day);
        dpd.setMinDate(now);



        Calendar sunday;
        List<Calendar> weekends = new ArrayList<>();
        int weeks = 1000;

        for (int i = 0; i < (weeks * 7) ; i = i + 7) {
            sunday = Calendar.getInstance();

            sunday.add(Calendar.DAY_OF_YEAR, (Calendar.SUNDAY - sunday.get(Calendar.DAY_OF_WEEK) + 7 + i));
            // saturday = Calendar.getInstance();
            // saturday.add(Calendar.DAY_OF_YEAR, (Calendar.SATURDAY - saturday.get(Calendar.DAY_OF_WEEK) + i));
            // weekends.add(saturday);
            weekends.add(sunday);
        }
        Calendar[] disabledDays = weekends.toArray(new Calendar[weekends.size()]);
        dpd.setDisabledDays(disabledDays);

        if (statusUser.equalsIgnoreCase("Pasien"))
        {
            Calendar maxDate = Calendar.getInstance();
            maxDate.set(maxDate.get(Calendar.YEAR),maxDate.get(Calendar.MONTH),maxDate.get(Calendar.DAY_OF_MONTH)+6);
            dpd.setMaxDate(maxDate);
        }



        for (int i = 0; i < listDate.size(); i++) {

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            java.util.Date date = null;
            try {
                date = sdf.parse(listDate.get(i));
                System.out.println("date convert = " + date);
                now = dateToCalendar(date);
                System.out.println(now.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            List<Calendar> dates = new ArrayList<>();
            dates.add(now);
            Calendar[] disabledDays1 = dates.toArray(new Calendar[dates.size()]);
            dpd.setDisabledDays(disabledDays1);

        }
//
        dpd.show(getFragmentManager(), "Datepickerdialog");

    }

    public void pickTime() {
        final Calendar now = Calendar.getInstance();
        final TimePickerDialog tpd = TimePickerDialog.newInstance(
                InputJadwalActivity.this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
        );



//        tpd.setMinTime(now.get(Calendar.HOUR_OF_DAY),now.get(Calendar.MINUTE),now.get(Calendar.SECOND));



        mAbout.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("interval").getValue()!=null)
                {
                    int intervalMinutes = dataSnapshot.child("interval").getValue(Integer.class);
                    if (intervalMinutes>0)
                    {
                        tpd.setTimeInterval(1,intervalMinutes);
                    }

                }
                int jamBuka = dataSnapshot.child("jamBuka").getValue(Integer.class);
                int menitBuka = dataSnapshot.child("menitBuka").getValue(Integer.class);
                int jamTutup = dataSnapshot.child("jamTutup").getValue(Integer.class);
                int menitTutup = dataSnapshot.child("menitTutup").getValue(Integer.class);

                if (statusUser.equalsIgnoreCase("Pasien"))
                {
                    if (now.get(Calendar.DAY_OF_WEEK)!=Calendar.SUNDAY)
                    {
                        if (now.get(Calendar.HOUR_OF_DAY)>=8 && now.get(Calendar.MINUTE)>0)
                        {

                            tpd.setMinTime(now.get(Calendar.HOUR_OF_DAY),now.get(Calendar.MINUTE),now.get(Calendar.SECOND));


                        }
                        else if (now.get(Calendar.HOUR_OF_DAY)<8)
                        {

                            tpd.setMinTime(jamBuka,menitBuka,0);
                        }
                    }
                    else
                    {
                        tpd.setMinTime(jamBuka,menitBuka,0);
                    }


                    tpd.setMaxTime(jamTutup,menitTutup,0);
                }


                tpd.show(getFragmentManager(), "Timepickerdialog");


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    private Calendar dateToCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    private void createJadwal(String keluhan, String tanggal, String waktu, String pasienKey, String idDokter, String namaPasien, String namaDokter) {
        String userId = mAuth.getCurrentUser().getUid();
        System.out.println("ID USER = " + userId);
        System.out.println("ID PASIEN 3 = " + pasienKey);
        String status = "Belum Konfirmasi";
        System.out.println("Tanggal di createJadwal = " + tanggal + " " + waktu);
        Long timeStamp = convertTimeStamp(tanggal, waktu);
        Jadwal jadwal = new Jadwal(null, keluhan, timeStamp, waktu, pasienKey, idDokter, status, namaPasien, namaDokter);
        String keyJadwal = mJadwalRef.push().getKey();
        HashMap<String, String> keyDokter = new HashMap<>();
        keyDokter.put("idDokter", idDokter);
        keyDokter.put("idJadwal", keyJadwal);
        keyDokter.put("idPasien", pasienKey);
        mNotifJadwal.push().setValue(keyDokter);
        mJadwalRef.child(keyJadwal).setValue(jadwal).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, task.toString());
                Toast.makeText(InputJadwalActivity.this, "Data Jadwal Berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                finish();

            }
        });
    }

    private void createJadwalFoto(String foto, String keluhan, String tanggal, String waktu, String pasienKey, String idDokter, String namaPasien, String namaDokter) {
        String userId = mAuth.getCurrentUser().getUid();
        System.out.println("ID USER = " + userId);
        System.out.println("ID PASIEN 3 = " + pasienKey);
        String status = "Belum Konfirmasi";


        Long timeStamp = convertTimeStamp(tanggal, waktu);

        Jadwal jadwal = new Jadwal(foto, keluhan, timeStamp, waktu, pasienKey, idDokter, status, namaPasien, namaDokter);
        String keyJadwal = mJadwalRef.push().getKey();
        HashMap<String, String> keyDokter = new HashMap<>();
        keyDokter.put("idDokter", idDokter);
        keyDokter.put("idJadwal", keyJadwal);
        keyDokter.put("idPasien", pasienKey);
        mNotifJadwal.push().setValue(keyDokter);
        mJadwalRef.child(keyJadwal).setValue(jadwal).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, task.toString());
                Toast.makeText(InputJadwalActivity.this, "Data Jadwal Berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                finish();
            }
        });
    }

    public void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, CAMERA_PIC_REQUEST);
        }
    }

    public Long convertTimeStamp(String tanggal, String waktu) {
        System.out.println("tanggal = " + tanggal + " waktu = " + waktu);
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Date date2 = null;
        try {
            date2 = formatter.parse(tanggal + " " + waktu);
        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("Log = " + e);
        }
        Long timeStamp = date2.getTime();
        return timeStamp;
    }


    public void openGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);

        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureDirectoryPath = pictureDirectory.getPath();

        Uri data = Uri.parse(pictureDirectoryPath);
        photoPickerIntent.setDataAndType(data, "image/*");
        startActivityForResult(photoPickerIntent, GALERY_INTENT);

    }

    void saveState() {

        createJadwalFoto(photoUrl, keluhan, tanggal, waktu, pasienKey, dokterKey, pasienName, dokterName);


    }

    private void uploadFoto() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        String path = "perawatan/" + UUID.randomUUID() + ".jpg";
        StorageReference jadwalRef = storage.getReference(path);

        UploadTask uploadTask = jadwalRef.putFile(imageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(InputJadwalActivity.this, "Upload Gagal", Toast.LENGTH_LONG).show();

            }
        }).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(InputJadwalActivity.this, "Upload Success", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
                @SuppressWarnings("VisibleForTests") Uri url = taskSnapshot.getDownloadUrl();
                photoUrl = url.toString();
                System.out.println("INI URL PHOTO!!!!" + photoUrl);
                saveState();
            }
        });
        progressDialog.dismiss();

    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        MenuItem update = menu.findItem(R.id.reschedule);
        MenuItem done = menu.findItem(R.id.done);
        MenuItem foto = menu.findItem(R.id.addphoto);

        if (jadwalKey != null) {
            update.setVisible(true);
            done.setVisible(false);
            foto.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.done) {
            progressDialog = new ProgressDialog(InputJadwalActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();

            waktu = btnTime.getText().toString();
            tanggal = btnDate.getText().toString();
            keluhan = etKeluhan.getText().toString();

            if (waktu.equalsIgnoreCase("Pilih waktu ...")) {
                Toast.makeText(InputJadwalActivity.this, "Silahkan pilih waktu kunjungan anda...", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            } else if (tanggal.equalsIgnoreCase("Pilih tanggal . . .")) {
                Toast.makeText(InputJadwalActivity.this, "Silahkan pilih tanggal kunjungan anda...", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            } else if (TextUtils.isEmpty(keluhan)) {
                Toast.makeText(InputJadwalActivity.this, "Silahkan isi keluhan anda...", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            } else if (pasienKey != null || dokterKey != null) {
                if (statusUser.equalsIgnoreCase("Pasien")) {
                    final String userId = mAuth.getCurrentUser().getUid();
                    mPasienRef.orderByChild("userID").equalTo(userId).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            pasienKey = dataSnapshot.getKey();
                            pasienName = dataSnapshot.child("nama").getValue(String.class);
                            System.out.println("ID PASIEN = " + pasienKey);

                            if (imageUri != null) {
                                uploadFoto();
                            } else {
                                createJadwal(keluhan, tanggal, waktu, pasienKey, dokterKey, pasienName, dokterName);
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
                } else if (statusUser.equalsIgnoreCase("Dokter")) {
                    final String userId = mAuth.getCurrentUser().getUid();
                    mDokterRef.orderByChild("userID").equalTo(userId).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            dokterKey = dataSnapshot.getKey();
                            dokterName = dataSnapshot.child("nama").getValue(String.class);
                            System.out.println("ID Dokter = " + dokterKey);
                            if (imageUri != null) {
                                uploadFoto();
                            } else {
                                createJadwal(keluhan, tanggal, waktu, pasienKey, dokterKey, pasienName, dokterName);
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
                } else if (statusUser.equalsIgnoreCase("Administrator")) {
                    if (imageUri != null) {
                        uploadFoto();
                    } else {
                        createJadwal(keluhan, tanggal, waktu, pasienKey, dokterKey, pasienName, dokterName);
                    }
                }
            } else if (btnPasien.getText().toString().equalsIgnoreCase("Pilih Pasien ...")) {
                Toast.makeText(InputJadwalActivity.this, "Silahkan pilih pasien...", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            } else if (btnDokter.getText().toString().equalsIgnoreCase("Pilih Dokter ...")) {
                Toast.makeText(InputJadwalActivity.this, "Silahkan pilih dokter...", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }


        } else if (id == R.id.reschedule) {
            progressDialog = new ProgressDialog(InputJadwalActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();

            waktu = btnTime.getText().toString();
            tanggal = btnDate.getText().toString();
            keluhan = etKeluhan.getText().toString();

            String namaPasien = btnPasien.getText().toString();

            System.out.println("RESCHEDULE = " + waktu + "" + tanggal + "" + keluhan + "" + dokterKey + "" + pasienKey + "" + status);
            System.out.println("pasien key = " + pasienKey + " idPasien = " + idPasien);
            System.out.println("imageuri reschedule= " + imageUri);

            if (waktu.equalsIgnoreCase(currentTime) && tanggal.equalsIgnoreCase(currentDate)) {
                status = "Belum Konfirmasi";
            }
            Long timeStamp = convertTimeStamp(tanggal, waktu);
            System.out.println("photoUrl reschedule= " + photoUrl);
            String foto = null;

            if (photoUrl != null) {
                foto = photoUrl;
            }

            Jadwal jadwal = new Jadwal(foto, keluhan, timeStamp, waktu, pasienKey, dokterKey, status, namaPasien, dokterName);


            mJadwalRef.child(jadwalKey).setValue(jadwal).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        finish();
                    }
                }
            });

            if (!waktu.equalsIgnoreCase(currentTime) || !tanggal.equalsIgnoreCase(currentDate)) {
                mPasienRef.child(pasienKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String userIDPASIEN = dataSnapshot.child("userID").getValue(String.class);
                        System.out.println("USER ID = " + userIDPASIEN);
                        mDokterRef.child(dokterKey).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String userIDDOKTER = dataSnapshot.child("userID").getValue(String.class);
                                HashMap map = new HashMap();
                                map.put("idPasien", pasienKey);
                                map.put("idJadwal", jadwalKey);
                                map.put("idDokterUser", userIDDOKTER);
                                map.put("idUser", userIDPASIEN);
                                System.out.println("MAP = " + map);
                                notifRef.push().setValue(map);
                                Log.d("Jadwal KEY UPDATE: ", jadwalKey);
                                System.out.println("idPasien, idJadwal, idDokterUser, idUserPasien = " + pasienKey + " " + jadwalKey + " " + userIDDOKTER);
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


        } else if (id == R.id.addphoto) {
            final AlertDialog.Builder alBuilder = new AlertDialog.Builder(this);
            LayoutInflater inflater = LayoutInflater.from(this);
            final View dialog = (View) inflater.inflate(R.layout.dialog_foto, null);
            final ListView lv = (ListView) dialog.findViewById(R.id.ListItem);

            final CustomAdapter adapter = new CustomAdapter(this, R.layout.row_list_pilihan, choice);


            alBuilder.setView(dialog);
            alBuilder.create();
            lv.setAdapter(adapter);
            final AlertDialog alertDialog = alBuilder.show();
            alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    alertDialog.dismiss();
                    pilihan = choice.get(position);

                    if (pilihan.getSingkatan().equalsIgnoreCase("gallery")) {
                        openGallery();
                    } else if (pilihan.getSingkatan().equalsIgnoreCase("camera")) {
                        openCamera();
                    }
                }
            });
            return false;

        }

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_DOKTER_REQUEST && data != null) {
            dokterName = data.getStringExtra(DOC_NAME);
            dokterKey = data.getStringExtra(DOC_KEY);
            System.out.println("Dokter NAME = " + dokterName);
            btnDokter.setText("drg. " + dokterName);
        } else if (requestCode == GALERY_INTENT && resultCode == RESULT_OK) {
            //kalau foto berhasil masuk
            //alamat gambar di memori

            imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
            System.out.println("INI URI IMAGE: " + imageUri);
            mCurrentPhotoPath = imageUri.getPath();
            System.out.println("Path:" + mCurrentPhotoPath);


        } else if (requestCode == CAMERA_PIC_REQUEST && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap photo = (Bitmap) extras.get("data");
            ivPhotoUpload.setImageBitmap(photo);
            imageUri = getImageUri(getApplicationContext(), photo);
            File finalFile = new File(getRealPathFromUri(imageUri));
            System.out.println("TEMP URI =" + imageUri);


        } else if (requestCode == PICK_PASIEN_REQUEST && data != null) {

            pasienName = data.getStringExtra(PASIEN_NAME);
            pasienKey = data.getStringExtra(PASIEN_KEY);
            System.out.println("Pasien NAME = " + pasienName);
            btnPasien.setText(pasienName);
        }


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                System.out.println("INI URI IMAGE CROP: " + imageUri);
                InputStream inputStream;
                try {
                    inputStream = getContentResolver().openInputStream(imageUri);

                    //ambil bitmap dari stream
                    Bitmap image = BitmapFactory.decodeStream(inputStream);
                    System.out.println("INI URI IMAGE: " + imageUri);
                    imageUri = getImageUri(getApplicationContext(), image);
                    File finalFile = new File(getRealPathFromUri(imageUri));
                    ivPhotoUpload.setImageBitmap(image);
                    System.out.println("UPLOADDD");


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(InputJadwalActivity.this, "Gambar gagal di buka", Toast.LENGTH_LONG).show();
                }


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //COMPRESS FOTO
    public Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        System.out.println("Path =" + path);
        return Uri.parse(path);
    }

    public String getRealPathFromUri(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {


        if (requestCode == REQUEST_PERMISSIONS) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                boolean_permission = true;


            } else {
                Toast.makeText(getApplicationContext(), "Please allow the permission", Toast.LENGTH_LONG).show();

            }
        }
    }

    private void fn_permission() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {


            if ((ActivityCompat.shouldShowRequestPermissionRationale(InputJadwalActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE))) {
            } else {
                ActivityCompat.requestPermissions(InputJadwalActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);
            }
            if ((ActivityCompat.shouldShowRequestPermissionRationale(InputJadwalActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE))) {
            } else {
                ActivityCompat.requestPermissions(InputJadwalActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);

            }
        } else {
            boolean_permission = true;


        }
    }


    protected void onStart() {
        super.onStart();
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
        }
        mRoot = mDatabase.getReference();
        mUserRef = mRoot.child("users");
        mJadwalRef = mRoot.child("jadwal");
        mPasienRef = mRoot.child("pasien");
        mDokterRef = mRoot.child("dokter");
        mNotifJadwal = mRoot.child("notifJadwal");
        notifRef = mRoot.child("notification");
        mAuth = FirebaseAuth.getInstance();
    }

    private String getDate(long timeStamp) {

        try {

            SimpleDateFormat dd = new SimpleDateFormat("dd-MM-yyyy");
            Date netDate = (new Date(timeStamp));
            String day = dd.format(netDate);

            return day;
        } catch (Exception ex) {
            System.out.println("Log = " + ex);
            return "xx";
        }
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        MainActivity main = new MainActivity();
        String mon = main.MONTHS[monthOfYear];
        int month = monthOfYear + 1;
        btnDate.setText(dayOfMonth + "-" + month + "-" + year);
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        AboutDentalActivity aboutDentalActivity = new AboutDentalActivity();
        btnTime.setText(aboutDentalActivity.cekJam(hourOfDay,minute));
    }
}
