package com.teddybrothers.co_teddy.dentist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.teddybrothers.co_teddy.dentist.customadapter.CustomAdapterHistoryJadwal;
import com.teddybrothers.co_teddy.dentist.entity.Jadwal;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class HistoryJadwal extends AppCompatActivity {


    FirebaseDatabase mDatabase;
    FirebaseAuth mAuth;
    DatabaseReference mRoot, mUserRef, mJadwal, mPasien, mDokter;
    ProgressBar progressBar;
    Boolean flags = false;


    final ArrayList<Jadwal> listJadwal = new ArrayList<Jadwal>();
    ListView lv;
    ImageView ivCancel;
    TextView tvStatus;
    EditText etSearch;
    Query queryHistory;

    Utilities util = new Utilities();
    String status, userId, idPasien, idDokter,idDataUser;
    TextView tvStatusData;
    public static final String[] MONTHS = {"Januari", "Febuari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
    public Calendar cal1 = Calendar.getInstance();
    public int day1 = cal1.get(Calendar.DAY_OF_MONTH);
    public int month1 = cal1.get(Calendar.MONTH);
    public int year1 = cal1.get(Calendar.YEAR);
    public Calendar cal = Calendar.getInstance();
    public int day = cal.get(Calendar.DAY_OF_MONTH);
    public int month = cal.get(Calendar.MONTH);
    public int year = cal.get(Calendar.YEAR);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_jadwal);

        System.out.println("ON CREATE OK");
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
        }
        mRoot = mDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        mUserRef = mRoot.child("users");
        mJadwal = mRoot.child("jadwal");
        mPasien = mRoot.child("pasien");
        mDokter = mRoot.child("dokter");

        listJadwal.clear();
        lv = findViewById(R.id.ListItem);
        lv.setTextFilterEnabled(true);

        status = util.getStatus(HistoryJadwal.this);
        userId = mAuth.getCurrentUser().getUid();
        System.out.println("Status = " + status);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            idDataUser = extras.getString("idDataUser");
        }

        if (status.equalsIgnoreCase("Pasien"))
        {
            queryHistory = mJadwal.orderByChild("idPasien").equalTo(idDataUser);
        }else if (status.equalsIgnoreCase("Dokter"))
        {
            queryHistory = mJadwal.orderByChild("idDokter").equalTo(idDataUser);
        }



        dataJadwal(Jadwal.COMPARE_BY_TIMESTAMP_ASC, queryHistory);

        tvStatusData = findViewById(R.id.tvStatusData);
        progressBar = findViewById(R.id.pbRVHistoryPerawatan);


    }

    public void dataJadwal(final Comparator<Jadwal> sortirParameter, Query query) {

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listJadwal.clear();
                System.out.println("listPasien = " + listJadwal);
                for (DataSnapshot jadwal : dataSnapshot.getChildren()) {
                    final Jadwal dataJadwal = jadwal.getValue(Jadwal.class);
                    dataJadwal.idJadwal = jadwal.getKey();
                    listJadwal.add(dataJadwal);
                    System.out.println("data klinik 1 = " + listJadwal);

                }


                Collections.sort(listJadwal, sortirParameter);
                final CustomAdapterHistoryJadwal arrayAdapter = new CustomAdapterHistoryJadwal(HistoryJadwal.this, R.layout.card_view_history, listJadwal);
                lv.setFastScrollEnabled(true);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                lv.setEmptyView(tvStatusData);
                lv.setAdapter(arrayAdapter);
                progressBar.setVisibility(View.GONE);
                arrayAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem today = menu.findItem(R.id.today).setVisible(false);
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
                progressBar.setVisibility(View.GONE);
                dataJadwal(Jadwal.COMPARE_BY_TIMESTAMP_DES, queryHistory);
                flags = true;
            } else if (flags) {
                progressBar.setVisibility(View.GONE);
                dataJadwal(Jadwal.COMPARE_BY_TIMESTAMP_ASC, queryHistory);
                flags = false;
            }

        } else if (id == R.id.today) {
//            progressBar.setVisibility(View.GONE);
//
//            int month = month1 + 1;
//            String today = day1 + "-" + month + "-" + year1;
//
//            System.out.println("Filter On Start Today = " + today);
//            final Long timeStampStart = convertTimeStamp(today, "00:00");
//            final Long timeStampEnd = convertTimeStamp(today, "23:59");
//            System.out.println("timeStamp start = " + timeStampStart);
//            Query query = mJadwal.orderByChild("timeStamp").startAt(timeStampStart).endAt(timeStampEnd);
//            dataJadwal(Jadwal.COMPARE_BY_TIMESTAMP_ASC, query);
        }


        return super.onOptionsItemSelected(item);
    }




}
