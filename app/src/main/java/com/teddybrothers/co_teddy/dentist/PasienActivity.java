package com.teddybrothers.co_teddy.dentist;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.teddybrothers.co_teddy.dentist.customadapter.CustomAdapterPasien;
import com.teddybrothers.co_teddy.dentist.entity.Pasien;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PasienActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseDatabase databaseUtama;
    DatabaseReference mUserRef, mRoot, mPasien, mUser;
    Pasien listDataPasien;
    public String pasienKey, status, statusUser;
    boolean flags = false;
    static final int PICK_PASIEN_REQUEST = 4;
    static final String PASIEN_KEY = "pasienKey";
    static final String PASIEN_UMUR = "pasienUmur";
    static final String PASIEN_NAME = "pasienName";
    static final String PASIEN_ALAMAT = "pasienAlamat";
    static final String PASIEN_JENKEL = "pasienJenKel";
    int position = 0;

    final ArrayList<Pasien> listPasien = new ArrayList<Pasien>();
    ListView lv;
    TextView tvStatus;
    EditText etSearch;
    ImageView ivCancel;
    ProgressBar progressBar;

    Utilities util = new Utilities();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pasien);

        System.out.println("ONCREATE PASIEN ACTIVITY");
        statusUser = util.getStatus(PasienActivity.this);
        if (databaseUtama == null) {
            databaseUtama = FirebaseDatabase.getInstance();
        }

        mRoot = databaseUtama.getReference();
        mPasien = mRoot.child("pasien");
        mUser = mRoot.child("users");

        listPasien.clear();
        etSearch = findViewById(R.id.etSearch);
        lv = findViewById(R.id.ListItem);
        ivCancel = findViewById(R.id.ivCancel);
        tvStatus = findViewById(R.id.tvStatus);
        lv.setTextFilterEnabled(true);
        progressBar = findViewById(R.id.progressBar);
        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PasienActivity.this, DaftarPasienActivity.class));
            }
        });


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            status = extras.getString("pilihPasien");
        }

        if (statusUser.equalsIgnoreCase("Administrator") || statusUser.equalsIgnoreCase("Dokter")) {
            if (status != null) {
                fab.setVisibility(View.GONE);
                dataPasien(Pasien.COMPARE_BY_NAME, mPasien.orderByChild("statusUser").equalTo("Aktif"));

            }


        }


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println("listPasien = " + listPasien);
                listDataPasien = listPasien.get(i);
                System.out.println("namaPasien = " + listDataPasien.getNama());
//                Toast.makeText(PasienActivity.this, "Nama Pasien = " + listDataPasien.getNama(), Toast.LENGTH_SHORT).show();


                mPasien.orderByChild("userID").equalTo(listDataPasien.userID).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        pasienKey = dataSnapshot.getKey();
                        if (status != null) {
                            Intent intent = new Intent();
                            intent.putExtra(PASIEN_KEY, pasienKey);
                            intent.putExtra(PASIEN_NAME, listDataPasien.nama);
                            intent.putExtra(PASIEN_ALAMAT, listDataPasien.alamat);
                            intent.putExtra(PASIEN_UMUR, listDataPasien.umur);
                            intent.putExtra(PASIEN_JENKEL, listDataPasien.jenisKelamin);
                            setResult(PICK_PASIEN_REQUEST, intent);
                            finish();
                        } else {

                            Intent intent = new Intent(PasienActivity.this, ProfileActivity.class);
                            intent.putExtra("userID", listDataPasien.userID);
                            intent.putExtra("statusIntent", "dariListPasien");
                            intent.putExtra("idDataUser", pasienKey);
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
        });


    }

    @Override
    protected void onStart() {

        System.out.println("ONSTART PASIEN ACTIVITY");
        etSearch.setText("");

        tvStatus.setVisibility(View.GONE);
        listPasien.clear();
        if (status == null) {
            dataPasien(Pasien.COMPARE_BY_NAME, mPasien);
        }


        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etSearch.getText().clear();
                ivCancel.setVisibility(View.GONE);
            }
        });


        super.onStart();

    }


    public void dataPasien(final Comparator<Pasien> sortirParameter, Query query) {

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getSupportActionBar().setSubtitle(dataSnapshot.getChildrenCount() + " Pasien");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listPasien.clear();
                System.out.println("listPasien = " + listPasien);
                for (DataSnapshot pasien : dataSnapshot.getChildren()) {
                    final Pasien dataPasien = pasien.getValue(Pasien.class);
                    dataPasien.idPasien = pasien.getKey();
                    listPasien.add(dataPasien);
                    System.out.println("data klinik 1 = " + listPasien);

                }


                Collections.sort(listPasien, sortirParameter);
                final CustomAdapterPasien arrayAdapter = new CustomAdapterPasien(PasienActivity.this, R.layout.card_view_pasien, listPasien);
                lv.setFastScrollEnabled(true);
                lv.setFastScrollAlwaysVisible(true);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                lv.setEmptyView(tvStatus);
                lv.setAdapter(arrayAdapter);
                progressBar.setVisibility(View.GONE);
                arrayAdapter.notifyDataSetChanged();

                etSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int i, int i1, int count) {
                        arrayAdapter.getFilter().filter(s.toString());
                        arrayAdapter.keyWord = s.toString();
                        System.out.println("count text pasien= " + s.length());
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
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Glide.get(this).clearMemory();
    }


    @Override
    protected void onPause() {
        System.out.println("onPause");
        listPasien.clear();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sortir, menu);
        final MenuItem sortir = menu.findItem(R.id.sortir);

        if (status != null) {
            sortir.setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.sortir) {
            if (!flags) {
                dataPasien(Pasien.COMPARE_BY_DATE, mPasien);
                flags = true;
            } else if (flags) {
                dataPasien(Pasien.COMPARE_BY_NAME, mPasien);
                flags = false;
            }
        } else if (id == R.id.filter) {
            dialogFilter();
        }

        return super.onOptionsItemSelected(item);
    }

    void dialogFilter() {

        final CharSequence[] kategori = {"Semua","Umum", "Sekolah Kusuma Bangsa", "MDP Group"};
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setTitle("Pilih Kategori");

        mBuilder.setSingleChoiceItems(kategori, position, new DialogInterface
                .OnClickListener() {
            public void onClick(final DialogInterface dialog, int item) {

                Query queryKategori = null;

                if (item == 0) {
                    queryKategori = mPasien;

                } else{

                    int category = item-1;
                    queryKategori = mPasien.orderByChild("kategori").equalTo(String.valueOf(category));
                }
                position = item;

                dataPasien(Pasien.COMPARE_BY_DATE,queryKategori);
                dialog.dismiss();
                // dismiss the alertbox after chose option

            }
        });
        AlertDialog alert = mBuilder.create();
        alert.show();


    }

}
