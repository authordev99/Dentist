package com.teddybrothers.co_teddy.dentist;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.teddybrothers.co_teddy.dentist.customadapter.CustomAdapterTindakan;
import com.teddybrothers.co_teddy.dentist.entity.Tindakan;

import java.util.ArrayList;
import java.util.Collections;

public class TindakanActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseDatabase databaseUtama, databaseKlinik;
    DatabaseReference mUserRef, mRoot, mTindakan, mUser;
    Tindakan listDataTindakan;
    public String TindakanKey, status;
    ProgressDialog progressDialog;
    ImageView ivCancel;
    final ArrayList<Tindakan> listTindakan = new ArrayList<Tindakan>();
    ListView lv;
    static final int TINDAKAN_PIC_REQUERST = 1;
    TextView tvStatus;
    EditText etSearch;
    static final String TINDAKAN_KEY = "keyTindakan";
    static final String TINDAKAN_HARGA = "hargaTindakan";
    static final String TINDAKAN_NAMA = "namaTindakan";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tindakan);

        System.out.println("ONCREATE Tindakan ACTIVITY");

        if (databaseUtama == null) {
            databaseUtama = FirebaseDatabase.getInstance();
        }

        mRoot = databaseUtama.getReference();
        mTindakan = mRoot.child("tindakan");
        mUser = mRoot.child("users");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            status = extras.getString("pilihTindakan");
        }

        listTindakan.clear();
        etSearch = findViewById(R.id.etSearch);
        ivCancel = findViewById(R.id.ivCancel);
        lv = findViewById(R.id.ListItem);
        tvStatus = findViewById(R.id.tvStatus);
        lv.setTextFilterEnabled(true);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder alBuilder = new AlertDialog.Builder(TindakanActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                final View dialog = (View) inflater.inflate(R.layout.insert_tindakan,null);
                alBuilder.setView(dialog);


                final EditText etTindakan = (EditText) dialog.findViewById(R.id.etTindakan);
                final EditText etDeskripsi = (EditText) dialog.findViewById(R.id.etDeskripsi);
                final EditText etHarga = (EditText) dialog.findViewById(R.id.etHarga);



                alBuilder.setCancelable(true).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                final AlertDialog alertDialog = alBuilder.create();
                alertDialog.show();
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        progressDialog = new ProgressDialog(TindakanActivity.this);
                        progressDialog.setMessage("Loading...");
                        progressDialog.show();

                        final String nama = etTindakan.getText().toString();
                        final String deskripsi = etDeskripsi.getText().toString();
                        final String harga = etHarga.getText().toString();

                        if (TextUtils.isEmpty(nama)) {
                            etTindakan.setError("nama harus diisi!");
                        }else  if (TextUtils.isEmpty(deskripsi)) {
                            etDeskripsi.setError("deskripsi harus diisi!");
                        }else  if (TextUtils.isEmpty(harga)) {
                            etHarga.setError("harga harus diisi!");
                        }
                        else
                        {
                            createTindakan(nama,deskripsi,harga);

                            alertDialog.dismiss();
                        }



                        progressDialog.dismiss();


                    }
                });
            }
        });


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println("listTindakan = " + listTindakan);
                listDataTindakan = listTindakan.get(i);
                if (status!=null)
                {
                    Intent intent = new Intent();
                    intent.putExtra(TINDAKAN_KEY, listDataTindakan.idTindakan);
                    intent.putExtra(TINDAKAN_NAMA, listDataTindakan.namaTindakan);
                    intent.putExtra(TINDAKAN_HARGA, listDataTindakan.hargaTindakan);
                    setResult(TINDAKAN_PIC_REQUERST, intent);
                    finish();
                }
                else
                {
                    final AlertDialog.Builder alBuilder = new AlertDialog.Builder(TindakanActivity.this);
                    LayoutInflater inflater = LayoutInflater.from(TindakanActivity.this);
                    final View dialog = (View) inflater.inflate(R.layout.insert_tindakan, null);
                    alBuilder.setView(dialog);


                    final EditText etTindakan = (EditText) dialog.findViewById(R.id.etTindakan);
                    final EditText etDeskripsi = (EditText) dialog.findViewById(R.id.etDeskripsi);
                    final EditText etHarga = (EditText) dialog.findViewById(R.id.etHarga);

                    mTindakan.child(listDataTindakan.getIdTindakan()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String nama = dataSnapshot.child("namaTindakan").getValue(String.class);
                            String harga = dataSnapshot.child("hargaTindakan").getValue(String.class);
                            String deskripsi = dataSnapshot.child("deskripsiTindakan").getValue(String.class);

                            etTindakan.setText(nama);
                            etDeskripsi.setText(deskripsi);
                            etHarga.setText(harga);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                    alBuilder.setCancelable(true).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    final AlertDialog alertDialog = alBuilder.create();
                    alertDialog.show();

                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {


                            final String nama = etTindakan.getText().toString();
                            final String deskripsi = etDeskripsi.getText().toString();
                            final String harga = etHarga.getText().toString();

                            if (TextUtils.isEmpty(nama)) {
                                etTindakan.setError("nama tindakan harus diisi");
                            } else if (TextUtils.isEmpty(deskripsi)) {
                                etDeskripsi.setError("deskripsi tindakan harus diisi");
                            } else if (TextUtils.isEmpty(harga)) {
                                etHarga.setError("harga tindakan harus diisi");
                            } else {
                                updateTindakan(nama, deskripsi, harga);
                                alertDialog.dismiss();
                            }


                        }
                    });
                }
                System.out.println("namaTindakan = " + listDataTindakan.getNamaTindakan());
                Toast.makeText(TindakanActivity.this, "Nama Tindakan = " + listDataTindakan.getNamaTindakan(), Toast.LENGTH_SHORT).show();


            }
        });


    }

    private void createTindakan(String nama,String deskripsi,String harga) {

        Tindakan tindakan = new Tindakan(nama,deskripsi,harga);

        mTindakan.push().setValue(tindakan).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(TindakanActivity.this,"Data Tindakan Berhasil Disimpan",Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void updateTindakan(String nama, String deskripsi, String harga) {

        Tindakan tindakan = new Tindakan(nama, deskripsi, harga);

        mTindakan.child(listDataTindakan.getIdTindakan()).setValue(tindakan).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                Toast.makeText(TindakanActivity.this, "Data Tindakan Berhasil Diperbaharui", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    protected void onStart() {

        System.out.println("ONSTART Tindakan ACTIVITY");
        etSearch.setText("");

        tvStatus.setVisibility(View.GONE);

        mTindakan.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listTindakan.clear();
                System.out.println("listTindakan = " + listTindakan);
                for (DataSnapshot Tindakan : dataSnapshot.getChildren()) {
                    Tindakan dataTindakan = Tindakan.getValue(Tindakan.class);
                    dataTindakan.idTindakan = Tindakan.getKey();
                    listTindakan.add(dataTindakan);
                    System.out.println("data klinik 1 = " + listTindakan);

                }


                Collections.sort(listTindakan, Tindakan.COMPARE_BY_NAME);
                final CustomAdapterTindakan arrayAdapter = new CustomAdapterTindakan(TindakanActivity.this, R.layout.card_view_tindakan, listTindakan);
                lv.setFastScrollEnabled(true);
                lv.setFastScrollAlwaysVisible(true);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                lv.setEmptyView(tvStatus);
                lv.setAdapter(arrayAdapter);
                arrayAdapter.notifyDataSetChanged();

                etSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int i, int i1, int count) {
                        arrayAdapter.getFilter().filter(s.toString());
                        arrayAdapter.keyWord = s.toString();
                        if (count>0) {
                            ivCancel.setVisibility(View.VISIBLE);
                        } else if (count==0) {
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

        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etSearch.getText().clear();
                ivCancel.setVisibility(View.GONE);
            }
        });

        super.onStart();
        Glide.get(this).clearMemory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Glide.get(this).clearMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Glide.get(this).clearMemory();
    }

    @Override
    protected void onPause() {
        System.out.println("onPause");
        listTindakan.clear();
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id==android.R.id.home)
        {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
