package com.teddybrothers.co_teddy.dentist;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import com.teddybrothers.co_teddy.dentist.customadapter.CustomAdapterDokter;
import com.teddybrothers.co_teddy.dentist.entity.Dokter;
import com.teddybrothers.co_teddy.dentist.entity.Jadwal;
import com.teddybrothers.co_teddy.dentist.entity.Pasien;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DokterActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseDatabase databaseUtama;
    DatabaseReference mUserRef, mRoot, mPasien, mUser, mDokter;
    Dokter listDataDokter;
    public String dokterKey, status,statusUser;
    boolean flags = false;
    static final int PICK_PASIEN_REQUEST = 4;
    static final int PICK_DOKTER_REQUEST = 1;
    static final String DOKTER_KEY = "docKey";
    static final String DOKTER_NAME = "docName";
    static final String DOKTER_TTD = "docTtd";
    Utilities util = new Utilities();
    ProgressBar progressBar;
    final ArrayList<Dokter> listDokter = new ArrayList<Dokter>();
    ListView lv;
    TextView tvStatus;
    EditText etSearch;
    ImageView ivCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dokter);

        System.out.println("ONCREATE DOKTER ACTIVITY");

        if (databaseUtama == null) {
            databaseUtama = FirebaseDatabase.getInstance();
        }

        mRoot = databaseUtama.getReference();
        mPasien = mRoot.child("pasien");
        mDokter = mRoot.child("dokter");
        mUser = mRoot.child("users");

        statusUser = util.getStatus(DokterActivity.this);
        listDokter.clear();
        etSearch = findViewById(R.id.etSearch);
        lv = findViewById(R.id.ListItem);
        ivCancel = findViewById(R.id.ivCancel);
        tvStatus = findViewById(R.id.tvStatus);
        lv.setTextFilterEnabled(true);
        progressBar = findViewById(R.id.progressBar);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (statusUser.equalsIgnoreCase("Pasien"))
        {
            fab.setVisibility(View.GONE);

        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DokterActivity.this, InputDokterActivity.class));
            }
        });


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            status = extras.getString("pilihDokter");
        }

        if (statusUser.equalsIgnoreCase("Pasien")||statusUser.equalsIgnoreCase("Administrator"))
        {
            System.out.println("status activity dokter = "+status);
            if (status!=null)
            {
                fab.setVisibility(View.GONE);
                dataDokter(Dokter.COMPARE_BY_NAME_ASC,mDokter.orderByChild("statusUser").equalTo("Aktif"));
            }


        }

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println("listDokter = " + listDokter);
                listDataDokter = listDokter.get(i);
                System.out.println("namaDokter = " + listDataDokter.getNama());
//                Toast.makeText(DokterActivity.this, "Nama Dokter = " + listDataDokter.getNama(), Toast.LENGTH_SHORT).show();


                mDokter.orderByChild("userID").equalTo(listDataDokter.userID).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        dokterKey = dataSnapshot.getKey();
                        if (status != null) {

                            Intent intent = new Intent();
                            intent.putExtra(DOKTER_KEY, dokterKey);
                            intent.putExtra(DOKTER_NAME, listDataDokter.getNama());
                            intent.putExtra(DOKTER_TTD, listDataDokter.getTtdUrl());
                            setResult(PICK_DOKTER_REQUEST, intent);
                            finish();

                        } else {

                            Intent intent = new Intent(DokterActivity.this, ProfileActivity.class);
                            intent.putExtra("userID", listDataDokter.userID);
                            intent.putExtra("statusIntent", "dariListDokter");
                            intent.putExtra("idDataUser", dokterKey);
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

        System.out.println("ONSTART DOKTER ACTIVITY");
        etSearch.setText("");

        tvStatus.setVisibility(View.GONE);
        listDokter.clear();
        if (status==null)
        {
            dataDokter(Dokter.COMPARE_BY_NAME_ASC,mDokter);
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


    public void dataDokter(final Comparator<Dokter> sortirParameter,Query query)
    {
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getSupportActionBar().setSubtitle(dataSnapshot.getChildrenCount() + " Dokter");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listDokter.clear();
                System.out.println("listDokter = " + listDokter);
                for (DataSnapshot dokter : dataSnapshot.getChildren()) {
                    final Dokter dataDokter = dokter.getValue(Dokter.class);
                    dataDokter.idDokter = dataSnapshot.getKey();

                    listDokter.add(dataDokter);
                    System.out.println("data klinik 1 = " + listDokter);

                }


                Collections.sort(listDokter, sortirParameter);
                final CustomAdapterDokter arrayAdapter = new CustomAdapterDokter(DokterActivity.this, R.layout.card_view_dokter, listDokter);
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

                        if (s.length()>0) {
                            ivCancel.setVisibility(View.VISIBLE);
                        } else if (s.length()==0) {
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
        listDokter.clear();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem today = menu.findItem(R.id.today).setVisible(false);
        final MenuItem sortir = menu.findItem(R.id.sortir);
        if (status!=null)
        {
            sortir.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id==android.R.id.home)
        {
            onBackPressed();
            return true;
        }
        else if (id==R.id.sortir)
        {
            if (!flags) {
                dataDokter(Dokter.COMPARE_BY_NAME_DESC, mDokter);
                flags = true;
            } else if (flags) {
                dataDokter(Dokter.COMPARE_BY_NAME_ASC, mDokter);
                flags = false;
            }
        }

        return super.onOptionsItemSelected(item);
    }


}
