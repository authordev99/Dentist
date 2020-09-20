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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.teddybrothers.co_teddy.dentist.customadapter.CustomAdapterAdmin;
import com.teddybrothers.co_teddy.dentist.entity.Admin;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AdminActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseDatabase databaseUtama, databaseKlinik;
    DatabaseReference mUserRef, mRoot, mAdmin, mUser;
    Admin listDataAdmin;
    public String AdminKey, status;
    ProgressBar progressBar;
    boolean flags = false;
    final ArrayList<Admin> listAdmin = new ArrayList<Admin>();
    ListView lv;
    ImageView ivCancel;
    TextView tvStatus;
    EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        System.out.println("ONCREATE Admin ACTIVITY");

        if (databaseUtama == null) {
            databaseUtama = FirebaseDatabase.getInstance();
        }

        mRoot = databaseUtama.getReference();
        mAdmin = mRoot.child("admin");
        mUser = mRoot.child("users");

        listAdmin.clear();
        etSearch = findViewById(R.id.etSearch);
        ivCancel = findViewById(R.id.ivCancel);
        lv = findViewById(R.id.ListItem);
        tvStatus = findViewById(R.id.tvStatus);
        lv.setTextFilterEnabled(true);
        progressBar = findViewById(R.id.progressBar);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminActivity.this, RegisterAdminActivity.class));
            }
        });


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println("listAdmin = " + listAdmin);
                listDataAdmin = listAdmin.get(i);
                System.out.println("namaAdmin = " + listDataAdmin.getNama());
//                Toast.makeText(AdminActivity.this, "Nama Admin = " + listDataAdmin.getNama(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(AdminActivity.this, ProfileActivity.class);
                intent.putExtra("userID", listDataAdmin.getIdAdmin());
                intent.putExtra("statusIntent", "dariListAdmin");
                intent.putExtra("idDataUser", listDataAdmin.getIdAdmin());
                startActivity(intent);


            }
        });


    }





    @Override
    protected void onStart() {

        System.out.println("ONSTART Admin ACTIVITY");
        etSearch.setText("");

        tvStatus.setVisibility(View.GONE);

        dataAdmin(Admin.COMPARE_BY_NAME_ASC, mAdmin);

        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etSearch.getText().clear();
                ivCancel.setVisibility(View.GONE);
            }
        });

        super.onStart();

    }


    public void dataAdmin(final Comparator<Admin> sortirParameter, Query query)
    {

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getSupportActionBar().setSubtitle(dataSnapshot.getChildrenCount() + " Administrator");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listAdmin.clear();
                System.out.println("listAdmin = " + listAdmin);
                for (DataSnapshot Admin : dataSnapshot.getChildren()) {
                    final Admin dataAdmin = Admin.getValue(Admin.class);
                    dataAdmin.idAdmin = Admin.getKey();

                    listAdmin.add(dataAdmin);
                    System.out.println("data klinik 1 = " + listAdmin);

                }


                Collections.sort(listAdmin, sortirParameter);
                final CustomAdapterAdmin arrayAdapter = new CustomAdapterAdmin(AdminActivity.this, R.layout.card_view_admin, listAdmin);
                lv.setFastScrollEnabled(true);
                lv.setFastScrollAlwaysVisible(true);
                lv.setEmptyView(tvStatus);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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
        listAdmin.clear();
        super.onPause();
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

        if (id==android.R.id.home)
        {
            onBackPressed();
            return true;
        }
        else if (id==R.id.sortir)
        {
            if (!flags) {
                dataAdmin(Admin.COMPARE_BY_NAME_DES, mAdmin);
                flags = true;
            } else if (flags) {
                dataAdmin(Admin.COMPARE_BY_NAME_ASC, mAdmin);
                flags = false;
            }
        }

        return super.onOptionsItemSelected(item);
    }



}
