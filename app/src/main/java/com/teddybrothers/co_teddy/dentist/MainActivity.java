package com.teddybrothers.co_teddy.dentist;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.teddybrothers.co_teddy.dentist.customadapter.CustomAdapterJadwal;
import com.teddybrothers.co_teddy.dentist.customadapter.CustomAdapterLogMedis;
import com.teddybrothers.co_teddy.dentist.entity.Jadwal;
import com.teddybrothers.co_teddy.dentist.entity.RekamMedis;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    boolean doubleBackToExitPressedOnce = false;
    FirebaseRemoteConfig mFirebaseRemoteConfig;
    FirebaseDatabase mDatabase;
    FirebaseAuth mAuth;
    DatabaseReference mRoot, mUser, mJadwal, mPasien, mRekamMedis, mDokter, mLogRencanaPerawatan;
    Jadwal listDataJadwal;
    public String JadwalKey, status;
    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    String[] permissionsRequired = new String[]{android.Manifest.permission.READ_CONTACTS,
            android.Manifest.permission.WRITE_CONTACTS,
            android.Manifest.permission.CALL_PHONE,
            android.Manifest.permission.SEND_SMS,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static final String[] MONTHS = {"Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
    public Calendar cal1 = Calendar.getInstance();
    public int day1 = cal1.get(Calendar.DAY_OF_MONTH);
    public int month1 = cal1.get(Calendar.MONTH);
    public int year1 = cal1.get(Calendar.YEAR);
    public Calendar cal = Calendar.getInstance();
    public int day = cal.get(Calendar.DAY_OF_MONTH);
    public int month = cal.get(Calendar.MONTH);
    public int year = cal.get(Calendar.YEAR);
    ArrayList<CustomObject> choice = new ArrayList<CustomObject>();
    CustomObject pilihan;
    private SharedPreferences permissionStatus;
    private boolean sentToSettings = false;
    boolean flagsCari = false;
    ProgressBar progressBar;
    Button btnDate;
    final ArrayList<Jadwal> listJadwal = new ArrayList<Jadwal>();
    ListView lv;
    ImageView ivCancel;
    TextView tvStatus, tvNama, tvEmail;
    EditText etSearch;
    ProgressDialog progressDialog;
    CircleImageView ivFotoProfil;
    String userId, today, mon1, filterTanggal;
    Long timeStampStart, timeStampPickDate, timeStampEndPickDate;

    private static final String LATEST_VERSION = "latest_version";
    private static final String WELCOME_MESSAGE_KEY = "welcome_message";
    private static final String WELCOME_MESSAGE_CAPS_KEY = "welcome_message_caps";
    Utilities util = new Utilities();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            System.out.println("mAuth null = " + mAuth.getCurrentUser());
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        } else {


            mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
            FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                    .setDeveloperModeEnabled(BuildConfig.DEBUG)
                    .build();
            mFirebaseRemoteConfig.setConfigSettings(configSettings);

            long cacheExpiration = 0;
            mFirebaseRemoteConfig.fetch(cacheExpiration)
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
//                                Toast.makeText(MainActivity.this, "Fetch Succeeded",
//                                        Toast.LENGTH_SHORT).show();

                                // After config data is successfully fetched, it must be activated before newly fetched
                                // values are returned.
                                mFirebaseRemoteConfig.activateFetched();
                            } else {
//                                Toast.makeText(MainActivity.this, "Fetch Failed",
//                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    });




            setContentView(R.layout.activity_main2);
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            System.out.println("ONCREATE Jadwal ACTIVITY");


            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setSubtitle("Daftar Jadwal");
            tvStatus = (TextView) findViewById(R.id.tvStatusData);

            if (mDatabase == null) {
                mDatabase = FirebaseDatabase.getInstance();
            }

            mRoot = mDatabase.getReference();
            mJadwal = mRoot.child("jadwal");
            mDokter = mRoot.child("dokter");
            mPasien = mRoot.child("pasien");
            mRekamMedis = mRoot.child("rekammedis");
            mLogRencanaPerawatan = mRoot.child("logRenPer");
            mUser = mRoot.child("users");

            listJadwal.clear();
            etSearch = findViewById(R.id.etSearch);
            ivCancel = findViewById(R.id.ivCancel);
            lv = findViewById(R.id.lv);
            lv.setTextFilterEnabled(true);
            progressBar = findViewById(R.id.progressBar);

            final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, InputJadwalActivity.class));
                }
            });


            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    System.out.println("listJadwal = " + listJadwal);
                    listDataJadwal = listJadwal.get(i);
                    System.out.println("status user lv click = " + status);
                    if (status.equalsIgnoreCase("Pasien")) {
                        touchCard(mPasien, listDataJadwal, userId);
                    } else if (status.equalsIgnoreCase("Dokter")) {
                        touchCard(mDokter, listDataJadwal, userId);
                    } else if (status.equalsIgnoreCase("Administrator")) {
                        Intent intent = new Intent(MainActivity.this, DetailPasienActivity.class);
                        util.setIdPasien(MainActivity.this, listDataJadwal.idPasien);
                        util.setIdJadwal(MainActivity.this, listDataJadwal.idJadwal);
                        util.setIdDokter(MainActivity.this, listDataJadwal.idDokter);
                        startActivity(intent);

                    }


                }
            });

            lv.setOnScrollListener(new AbsListView.OnScrollListener() {
                private int mLastFirstVisibleItem;

                @Override
                public void onScrollStateChanged(AbsListView absListView, int i) {

                }

                @Override
                public void onScroll(AbsListView absListView, int firstVisible, int visibleItemCount, int totalItemCount) {
                    if (mLastFirstVisibleItem < firstVisible) {
                        fab.hide();

                    }
                    if (mLastFirstVisibleItem > firstVisible) {
                        fab.show();

                    }
                    mLastFirstVisibleItem = firstVisible;
                }
            });

            btnDate = (Button) findViewById(R.id.btnDate);
            btnDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pickDate();
                }
            });

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            final NavigationView navigationView = findViewById(R.id.nav_view);

            navigationView.setNavigationItemSelectedListener(this);
            View header = navigationView.getHeaderView(0);
            ivFotoProfil = (CircleImageView) header.findViewById(R.id.ivFotoProfil);
            tvNama = (TextView) header.findViewById(R.id.tvNama);
            tvEmail = (TextView) header.findViewById(R.id.tvEmail);


            permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);
            fn_permission();


            userId = mAuth.getCurrentUser().getUid();

            SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE");
            Date dateToday = new Date(year1, month1, day1 - 1);
            String dayOfWeek = simpledateformat.format(dateToday);
            mon1 = MONTHS[month1];
            btnDate.setText(dayOfWeek + ", " + day1 + " " + mon1 + " " + year1);
            int month = month1 + 1;
            today = day1 + "-" + month + "-" + year1;

            System.out.println("Filter On Start Today = " + today);
            timeStampStart = convertTimeStamp(today, "00:00");
            System.out.println("timeStamp start = " + timeStampStart);
            Long timeStampEnd = convertTimeStamp(today, "23:59");
            System.out.println("timeStamp end = " + timeStampEnd);
            Query query = mJadwal.orderByChild("timeStamp").startAt(timeStampStart);
            dataJadwal(Jadwal.COMPARE_BY_TIMESTAMP_ASC, query);

            mUser.child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String statusUser = dataSnapshot.child("statusUser").getValue(String.class);
                    if (statusUser.equalsIgnoreCase("Non Aktif")) {
                        mAuth.signOut();
                        finish();
                        Toast.makeText(MainActivity.this, "Maaf akun anda telah dinonaktifkan!", Toast.LENGTH_SHORT).show();

                    }
                    status = dataSnapshot.child("status").getValue(String.class);
                    util.setStatus(MainActivity.this, status);


                    Menu nav_Menu = navigationView.getMenu();
                    System.out.println("STATUS =" + status);

                    if (status.equalsIgnoreCase("Dokter")) {
                        nav_Menu.findItem(R.id.dokter).setVisible(false);
                        nav_Menu.findItem(R.id.rencanaPerawatan).setVisible(false);
                        nav_Menu.findItem(R.id.rekammedis).setVisible(false);
                        nav_Menu.findItem(R.id.pengaturan).setVisible(false);
                        progressDialog.dismiss();
                    } else if (status.equalsIgnoreCase("Pasien")) {
                        nav_Menu.findItem(R.id.pasien).setVisible(false);
                        nav_Menu.findItem(R.id.tindakan).setVisible(false);
                        nav_Menu.findItem(R.id.regisAdmin).setVisible(false);
                        nav_Menu.findItem(R.id.pengaturan).setVisible(false);
                        progressDialog.dismiss();
                    } else if (status.equalsIgnoreCase("Administrator")) {
                        nav_Menu.findItem(R.id.rekammedis).setVisible(false);
                        nav_Menu.findItem(R.id.rencanaPerawatan).setVisible(false);
                        nav_Menu.findItem(R.id.history).setVisible(false);
                        nav_Menu.findItem(R.id.tindakan).setVisible(false);
                        nav_Menu.findItem(R.id.form).setVisible(false);
                        nav_Menu.findItem(R.id.billing).setVisible(false);
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


    }


    public void touchCard(DatabaseReference mRef, final Jadwal listDataJadwal, String userID) {
        mRef.orderByChild("userID").equalTo(userID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String id = dataSnapshot.getKey();
                if (id.equalsIgnoreCase(listDataJadwal.idPasien) || id.equalsIgnoreCase(listDataJadwal.idDokter)) {

                    Intent intent = new Intent(MainActivity.this, DetailPasienActivity.class);
                    util.setIdPasien(MainActivity.this, listDataJadwal.idPasien);
                    util.setIdJadwal(MainActivity.this, listDataJadwal.idJadwal);
                    util.setIdDokter(MainActivity.this, listDataJadwal.idDokter);
                    startActivity(intent);
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
    }


    public void pickDate() {

        cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);

        android.app.DatePickerDialog.OnDateSetListener listener = new android.app.DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int myear, int monthOfYear, int dayOfMonth) {
                month = monthOfYear + 1;
                String mon = MONTHS[month - 1];
                day = dayOfMonth;
                year = myear;


                SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE");
                Date date = new Date(year, monthOfYear, dayOfMonth - 1);
                String dayOfWeek = simpledateformat.format(date);
                System.out.println("dayOfWeek = " + dayOfWeek);

                btnDate.setText(dayOfWeek + ", " + day + " " + mon + " " + year);
                filterTanggal = btnDate.getText().toString();

                String selectedDate = day + "-" + month + "-" + year;
                System.out.println("selectedDate = " + selectedDate);
                timeStampPickDate = convertTimeStamp(selectedDate, "00:00");
                System.out.println("timeStamp start pick date = " + timeStampPickDate);

                Query query = mJadwal.orderByChild("timeStamp").startAt(timeStampPickDate);
                dataJadwal(Jadwal.COMPARE_BY_TIMESTAMP_ASC, query);
                if (status.equalsIgnoreCase("Pasien")) {
                    view.setMinDate(System.currentTimeMillis() - 1000);
                }


            }
        };

        android.app.DatePickerDialog dpDialog = new android.app.DatePickerDialog(MainActivity.this, listener, year, month, day);
        if (status.equalsIgnoreCase("Pasien")) {
            dpDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        }


        dpDialog.show();

    }

    public Long convertTimeStamp(String tanggal, String waktu) {
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

    private void fn_permission() {
        if ((ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissionsRequired[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissionsRequired[2])
                    || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissionsRequired[3])
                    || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissionsRequired[4])
                    || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissionsRequired[5])
                    || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissionsRequired[6])) {


                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs some permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(MainActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else if (permissionStatus.getBoolean(permissionsRequired[0], false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs some permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        Toast.makeText(getBaseContext(), "Go to Permissions Application", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(MainActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
            }
            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(permissionsRequired[0], true);
            editor.commit();
        } else {
            //You already have the permission, just go ahead.
//            proceedAfterPermission();
        }


    }

    private void proceedAfterPermission() {

        Toast.makeText(getBaseContext(), "We got All Permissions", Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                proceedAfterPermission();
            }
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                proceedAfterPermission();
            }
        }
    }

    @Override
    protected void onStart() {

        System.out.println("ONSTART Jadwal ACTIVITY");
        etSearch.setText("");


        System.out.println("timeStampPickDate = " + timeStampPickDate);
        if (timeStampPickDate != null) {
            listJadwal.clear();
            Query query = mJadwal.orderByChild("timeStamp").startAt(timeStampPickDate);
            dataJadwal(Jadwal.COMPARE_BY_TIMESTAMP_ASC, query);
        }


//        SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE");
//        Date dateToday = new Date(year1, month1, day1 - 1);
//        String dayOfWeek = simpledateformat.format(dateToday);
//        mon1 = MONTHS[month1];
//        btnDate.setText(dayOfWeek + ", " + day1 + " " + mon1 + " " + year1);

        mUser.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nama = dataSnapshot.child("fullname").getValue(String.class);
                String email = dataSnapshot.child("email").getValue(String.class);
                String foto = dataSnapshot.child("photoUrl").getValue(String.class);
                status = dataSnapshot.child("status").getValue(String.class);
                util.setStatus(MainActivity.this, status);
                if (foto != null) {
                    Glide.with(getApplicationContext()).load(foto)
                            .thumbnail(0.5f)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(ivFotoProfil);
                }

                System.out.println("nama =  " + nama);
                if (status.equalsIgnoreCase("Dokter")) {
                    tvNama.setText("Welcome drg. " + nama);
                } else {
                    tvNama.setText("Welcome " + nama);
                }

                tvEmail.setText(email);

//                SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE");
//                Date dateToday = new Date(year1, month1, day1-1);
//                String dayOfWeek = simpledateformat.format(dateToday);
//                mon1 = MONTHS[month1];
//                int month = month1+1;
//                today = day1 + "-" + month + "-" + year1;
//                convertTimeStamp()


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etSearch.getText().clear();
                ivCancel.setVisibility(View.GONE);
            }
        });



        String versionName = "";
        int versionCode = -1;
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = packageInfo.versionName;
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String latest_version = mFirebaseRemoteConfig.getString(LATEST_VERSION);
        System.out.println("version_name = " + versionName + " " + "latest_version = " + latest_version+" statusDialog = "+getStatusDialog());
        System.out.println("getStatusDialog = "+getStatusDialog()+" "+"!getStatusDialog = "+!getStatusDialog());
        if (versionName != null && latest_version != null) {
            if (!versionName.equalsIgnoreCase(latest_version)) {
                if (!getStatusDialog()) {
                    updateDialog();
                }
            }


        }

        super.onStart();

    }


    public void dataJadwal(final Comparator<Jadwal> sortirParameter, Query query) {
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listJadwal.clear();
                System.out.println("listJadwal = " + listJadwal);
                for (DataSnapshot Jadwal : dataSnapshot.getChildren()) {
                    final Jadwal dataJadwal = Jadwal.getValue(Jadwal.class);
                    dataJadwal.idJadwal = Jadwal.getKey();

                    listJadwal.add(dataJadwal);
                    System.out.println("data klinik 1 = " + listJadwal);

                }


                Collections.sort(listJadwal, sortirParameter);
                final CustomAdapterJadwal arrayAdapter = new CustomAdapterJadwal(MainActivity.this, R.layout.card_view_main, listJadwal);
                lv.setFastScrollEnabled(true);
                lv.setEmptyView(tvStatus);
                lv.setDivider(null);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                lv.setAdapter(arrayAdapter);
                arrayAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                etSearch.getText().clear();

                etSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int i, int i1, int count) {
                        arrayAdapter.getFilter().filter(s.toString());
                        arrayAdapter.keyWord = s.toString();

                        if (s.length() > 0) {
                            ivCancel.setVisibility(View.VISIBLE);
                        } else if (s.length() == 0) {
                            ivCancel.setVisibility(View.GONE);
                        }

                        System.out.println("arrayAdapter.keyWord = " + arrayAdapter.keyWord);
                        System.out.println("ADAPTER textchanged= " + arrayAdapter.getCount());

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Glide.get(this).clearMemory();
    }

    @Override
    protected void onPause() {
        System.out.println("onPause");

        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.today) {
            SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE");
            Date dateToday = new Date(year1, month1, day1 - 1);
            String dayOfWeek = simpledateformat.format(dateToday);
            mon1 = MONTHS[month1];
            btnDate.setText(dayOfWeek + ", " + day1 + " " + mon1 + " " + year1);
            int month = month1 + 1;
            today = day1 + "-" + month + "-" + year1;

            System.out.println("Filter On Start Today = " + today);
            final Long timeStampStart = convertTimeStamp(today, "00:00");
            System.out.println("timeStamp start = " + timeStampStart);
            Query query = mJadwal.orderByChild("timeStamp").startAt(timeStampStart);
            dataJadwal(Jadwal.COMPARE_BY_TIMESTAMP_ASC, query);
        }


        return super.onOptionsItemSelected(item);
    }


    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.pasien) {
            startActivity(new Intent(MainActivity.this, PasienActivity.class));
        } else if (id == R.id.dokter) {
            startActivity(new Intent(MainActivity.this, DokterActivity.class));
        } else if (id == R.id.tindakan) {
            startActivity(new Intent(MainActivity.this, TindakanActivity.class));
        } else if (id == R.id.profil) {
            final Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            if (status.equalsIgnoreCase("Dokter")) {
                System.out.println("useridDokter = " + userId);
                mDokter.orderByChild("userID").equalTo(userId).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        String idDokter = dataSnapshot.getKey();
                        System.out.println("idDokter main = " + idDokter);
                        intent.putExtra("idDataUser", idDokter);
                        intent.putExtra("userID", userId);
                        intent.putExtra("statusIntent", "dariMain");
                        startActivity(intent);
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

            } else if (status.equalsIgnoreCase("Pasien")) {

                mPasien.orderByChild("userID").equalTo(userId).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        String idPasien = dataSnapshot.getKey();
                        intent.putExtra("idDataUser", idPasien);
                        intent.putExtra("userID", userId);
                        intent.putExtra("statusIntent", "dariMain");
                        startActivity(intent);
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


            } else if (status.equalsIgnoreCase("Administrator")) {

                intent.putExtra("idDataUser", userId);
                intent.putExtra("userID", userId);
                intent.putExtra("statusIntent", "dariMain");
                startActivity(intent);

            }


        } else if (id == R.id.billing) {
            startActivity(new Intent(MainActivity.this, HistoryBillingActivity.class));
        } else if (id == R.id.signout) {
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            Toast.makeText(MainActivity.this, "Terima Kasih", Toast.LENGTH_SHORT).show();
            finish();
        } else if (id == R.id.rekammedis) {
            final Intent intent = new Intent(MainActivity.this, RekamMedisActivity.class);
            mPasien.orderByChild("userID").equalTo(userId).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    String idPasien = dataSnapshot.getKey();
                    intent.putExtra("idPasien", idPasien);
                    String statusProfil = "profil";
                    intent.putExtra("status", statusProfil);
                    startActivity(intent);
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

        } else if (id == R.id.form) {
            startActivity(new Intent(MainActivity.this, FormulirActivity.class));
        } else if (id == R.id.ubahPassword) {
            startActivity(new Intent(MainActivity.this, UbahPasswordActivity.class));
        } else if (id == R.id.about) {
            startActivity(new Intent(MainActivity.this, AboutDentalActivity.class));
        } else if (id == R.id.regisAdmin) {
            startActivity(new Intent(MainActivity.this, AdminActivity.class));
        } else if (id == R.id.history) {
            final Intent intent = new Intent(MainActivity.this, HistoryJadwal.class);

            if (status.equalsIgnoreCase("Dokter")) {
                System.out.println("useridDokter = " + userId);
                mDokter.orderByChild("userID").equalTo(userId).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        String idDokter = dataSnapshot.getKey();
                        System.out.println("idDokter main = " + idDokter);
                        intent.putExtra("idDataUser", idDokter);
                        startActivity(intent);
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

            } else if (status.equalsIgnoreCase("Pasien")) {

                mPasien.orderByChild("userID").equalTo(userId).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        String idPasien = dataSnapshot.getKey();
                        intent.putExtra("idDataUser", idPasien);
                        startActivity(intent);
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
        } else if (id == R.id.rencanaPerawatan) {


            final AlertDialog.Builder alBuilder = new AlertDialog.Builder(MainActivity.this);
            final ArrayList<com.teddybrothers.co_teddy.dentist.entity.RekamMedis> listLogRekamMedis = new ArrayList<RekamMedis>();
            LayoutInflater inflater = getLayoutInflater();
            final View dialog = (View) inflater.inflate(R.layout.dialog_rencanaperawatan, null);
            alBuilder.setView(dialog);

            final TextView tvRencanaPerawatan = dialog.findViewById(R.id.tvRencanaPerawatan);
            final TextView tvStatus = dialog.findViewById(R.id.tvStatusData);
            final EditText etKetRencanaPerawatan = dialog.findViewById(R.id.etKeteranganRenPer);
            final ListView lvHistoryRencanaPerawatan = dialog.findViewById(R.id.lvHistoryRencanaPerawatan);

            String idPasienLogin = util.getIdPasienLogin(MainActivity.this);

            mRekamMedis.child(idPasienLogin).child("renPerawatan").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    listLogRekamMedis.clear();
                    System.out.println("listLogRekamMedis = " + listLogRekamMedis);
                    for (DataSnapshot rekamMedis : dataSnapshot.getChildren()) {
                        com.teddybrothers.co_teddy.dentist.entity.RekamMedis dataTindakan = rekamMedis.getValue(com.teddybrothers.co_teddy.dentist.entity.RekamMedis.class);

                        listLogRekamMedis.add(dataTindakan);
                        System.out.println("data klinik 1 = " + listLogRekamMedis);

                    }


                    Collections.sort(listLogRekamMedis, com.teddybrothers.co_teddy.dentist.entity.RekamMedis.COMPARE_BY_TGL_RENPER);
                    final CustomAdapterLogMedis arrayAdapter = new CustomAdapterLogMedis(MainActivity.this, R.layout.card_view_log_data_medis, listLogRekamMedis, "rencanaPerawatan");
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    lvHistoryRencanaPerawatan.setAdapter(arrayAdapter);
                    progressBar.setVisibility(View.GONE);
                    arrayAdapter.notifyDataSetChanged();


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            mRekamMedis.child(idPasienLogin).child("renPerawatan").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    System.out.println("rencanaPerawatan snapshot = " + dataSnapshot);

                    if (dataSnapshot.getValue() != null) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            com.teddybrothers.co_teddy.dentist.entity.RekamMedis dataRencanaPerawatan = snapshot.getValue(com.teddybrothers.co_teddy.dentist.entity.RekamMedis.class);
                            final String rencanaPerawatan = dataRencanaPerawatan.rencanaPerawatan;
                            String ketRencanaPerawatan = dataRencanaPerawatan.ketRencanaPerawatan;
                            System.out.println("rencanaPerawatan = " + rencanaPerawatan);
                            if (rencanaPerawatan != null) {
                                tvRencanaPerawatan.setText(getDate(Long.parseLong(rencanaPerawatan)));

                            }
                            if (ketRencanaPerawatan != null) {
                                etKetRencanaPerawatan.setText(ketRencanaPerawatan);

                            }


                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            String positifButton = null;
            String tglRencanaPerawatan = tvRencanaPerawatan.getText().toString();
            System.out.println("tvRencana = " + tglRencanaPerawatan);
            if (tglRencanaPerawatan.equalsIgnoreCase("Belum ada rencana perawatan")) {
                positifButton = "OK";
                System.out.println("tvRencana = " + tglRencanaPerawatan);

            } else {
                positifButton = "Buat Jadwal";
                System.out.println("tvRencana = " + tglRencanaPerawatan);
            }

            alBuilder.setCancelable(true).setPositiveButton(positifButton, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String tglRencanaPerawatan = tvRencanaPerawatan.getText().toString();
                    String rencanaPerawatan = etKetRencanaPerawatan.getText().toString();
                    if (!tglRencanaPerawatan.equalsIgnoreCase("Belum ada rencana perawatan")) {
                        Intent intent = new Intent(MainActivity.this, InputJadwalActivity.class);
                        intent.putExtra("rencanaPerawatan", rencanaPerawatan);
                        startActivity(intent);
                    }


                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });


            AlertDialog alertDialog = alBuilder.create();
            alertDialog.show();


        } else if (id == R.id.pengaturan) {
            startActivity(new Intent(MainActivity.this, SettingActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private String getDate(long timeStamp) {

        try {

            SimpleDateFormat ee = new SimpleDateFormat("EEEE");
            SimpleDateFormat dd = new SimpleDateFormat("dd");
            SimpleDateFormat MM = new SimpleDateFormat("MM");
            SimpleDateFormat yyyy = new SimpleDateFormat("yyyy");
            Date netDate = (new Date(timeStamp));
            String dayWeek = ee.format(netDate);
            String day = dd.format(netDate);
            String month = MM.format(netDate);
            String nameMonth = MONTHS[Integer.parseInt(month) - 1];
            String year = yyyy.format(netDate);
            String tanggal = dayWeek + ", " + day + " " + nameMonth + " " + year;
            return tanggal;
        } catch (Exception ex) {
            System.out.println("Log = " + ex);
            return "xx";
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem sortir = menu.findItem(R.id.sortir).setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    private void updateDialog() {


        AlertDialog.Builder alBuilder = new AlertDialog.Builder(MainActivity.this);
        alBuilder.setMessage("Update tersedia!, Kami telah memperbaharui app ini, Apakah anda ingin update?");
        LayoutInflater inflater = getLayoutInflater();
        final View dialog = (View) inflater.inflate(R.layout.dialog_update_app, null);
        alBuilder.setView(dialog);

        final CheckBox cbShow = dialog.findViewById(R.id.cbShow);

        cbShow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    statusDialog(true);
                    Toast.makeText(MainActivity.this, "checked", Toast.LENGTH_SHORT).show();
                } else {
                    statusDialog(false);
                    Toast.makeText(MainActivity.this, "unCheck", Toast.LENGTH_SHORT).show();
                }
            }
        });

        alBuilder.setCancelable(true).setPositiveButton("Perbaharui", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.teddybrothers.co_teddy.dentist"));
                startActivity(intent);
            }
        }).setNegativeButton("Nanti", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = alBuilder.create();
        alertDialog.show();
    }

    private void statusDialog(boolean isChecked) {
        SharedPreferences mSharedPreferences = getSharedPreferences("CheckItem", MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putBoolean("check", isChecked);
        mEditor.apply();
    }

    private boolean getStatusDialog() {
        SharedPreferences mSharedPreferences = getSharedPreferences("CheckItem", MODE_PRIVATE);

        return mSharedPreferences.getBoolean("check", false);

    }


    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {

        } else {
            message = "Sorry! Not connected to internet";
            color = Color.WHITE;
            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.fab), message, Snackbar.LENGTH_LONG);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
        }


    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }


}
