package com.teddybrothers.co_teddy.dentist;

import android.app.ProgressDialog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.teddybrothers.co_teddy.dentist.entity.DetailTindakan;
import com.teddybrothers.co_teddy.dentist.entity.Dokter;
import com.teddybrothers.co_teddy.dentist.entity.Invoice;
import com.teddybrothers.co_teddy.dentist.entity.Pasien;
import com.teddybrothers.co_teddy.dentist.entity.Tindakan;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class BillingActivity extends AppCompatActivity implements TextWatcher {


    FirebaseDatabase mDatabase;
    FirebaseAuth mAuth;
    DatabaseReference mRoot, mUserRef, mTindakan, mPerawatan, mRekamMedis, mTindakanPerawatan, mPasien, mJadwal, mInvoice,mDokter;
    RecyclerView mRecyclerview;
    Spinner spPembayaran;
    java.util.Calendar mcurrentTime = java.util.Calendar.getInstance();
    public int hour = mcurrentTime.get(java.util.Calendar.HOUR_OF_DAY);
    public int minute = mcurrentTime.get(java.util.Calendar.MINUTE);
    private String current = "";
    FirebaseRecyclerAdapter<Tindakan, BillingHolder> mAdapter;
    ProgressBar progressBarPerawatan;
    LinearLayoutManager mManager;

    ProgressDialog progressDialog;

    int total = 0;
    int totalHarga = 0;

    public TextView tvNama, tvHarga, tvAllTotal, tvTotalHarga, tvNamaPasien, tvIdPasien, tvJamInvoice, tvTanggalInvoice, tvAllTotalHarga1, tvStatus, tvInvoice,tvDiskon;
    Tindakan displayedTindakan = new Tindakan();
    public String tindakan;
    public static final String[] MONTHS = {"Januari", "Febuari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
    public Calendar cal1 = Calendar.getInstance();
    public int day1 = cal1.get(Calendar.DAY_OF_MONTH);
    public int month1 = cal1.get(Calendar.MONTH);
    public int year1 = cal1.get(Calendar.YEAR);
    public String mon1, hariIni, keyTindakan;
    public ArrayList<String> listTindakan = new ArrayList<>();
    public String nama[], harga[];
    public ArrayList<Tindakan> tindakanList = new ArrayList<>();
    Utilities util = new Utilities();
    String idPasien, idDokter, jadwalKey, statusUser,statusIntent,namaPasien,namaDokter;
    int noInvoiceCurrent;
    Long timeStamp;
    DetailTindakan ambil;
    EditText etDiskon;
    String[] Pembayaran;
    final ArrayList<DetailTindakan> detailTindakanList = new ArrayList<DetailTindakan>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);
        progressDialog = new ProgressDialog(BillingActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

//        final ListView lvBilling = (ListView) findViewById(R.id.lvBilling);
        spPembayaran = (Spinner) findViewById(R.id.spPembayaran);
        tvNama = (TextView) findViewById(R.id.tvNamaTindakan);
        tvInvoice = (TextView) findViewById(R.id.tvInvoice);
        tvHarga = (TextView) findViewById(R.id.tvHargaTindakan);
        tvAllTotal = (TextView) findViewById(R.id.tvAllTotal);
        tvAllTotalHarga1 = (TextView) findViewById(R.id.tvAllTotalHarga1);
        tvTotalHarga = (TextView) findViewById(R.id.tvTotalHarga);
        tvNamaPasien = (TextView) findViewById(R.id.tvNamaPasien);
        tvIdPasien = (TextView) findViewById(R.id.tvIDPasien);
        tvJamInvoice = (TextView) findViewById(R.id.tvJamInvoice);
        tvTanggalInvoice = (TextView) findViewById(R.id.tvTanggalInvoice);
        etDiskon = (EditText) findViewById(R.id.etDiskon);

        tvStatus = (TextView) findViewById(R.id.tvStatus);
        tvDiskon = findViewById(R.id.tvDiskon);


        Pembayaran = new String[]
                {
                        "Tunai", "Kartu Debit", "Kartu Kredit", "Sekolah Kusuma Bangsa", "MDP Group"
                };

        ArrayAdapter<String> mAdapterPembayaran = new ArrayAdapter<String>(BillingActivity.this, R.layout.spinner_item, Pembayaran);
        spPembayaran.setAdapter(mAdapterPembayaran);


        System.out.println("ON CREATED OK");
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
        }
        mRoot = mDatabase.getReference();
        mUserRef = mRoot.child("users");
        mJadwal = mRoot.child("jadwal");
        mTindakan = mRoot.child("tindakan");
        mRekamMedis = mRoot.child("rekammedis");
        mTindakanPerawatan = mRoot.child("tindakanperawatan");
        mPerawatan = mRoot.child("perawatan");
        mPasien = mRoot.child("pasien");
        mDokter = mRoot.child("dokter");
        mInvoice = mRoot.child("invoice");
        mAuth = FirebaseAuth.getInstance();


        mon1 = MONTHS[month1];
        hariIni = day1 + " " + mon1 + " " + year1;
        System.out.println("Hari ini = " + hariIni);
        mRecyclerview = (RecyclerView) findViewById(R.id.recycleViewBilling);
//        mRecyclerview.setHasFixedSize(true);
        mManager = new LinearLayoutManager(BillingActivity.this);
//        mManager.setReverseLayout(true);
//        mManager.setStackFromEnd(true);
        mRecyclerview.setLayoutManager(mManager);

        idPasien = util.getIdPasien(BillingActivity.this);
        idDokter = util.getIdDokter(BillingActivity.this);
        jadwalKey = util.getIdJadwal(BillingActivity.this);
        statusUser = util.getStatus(BillingActivity.this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            System.out.println(extras);
            jadwalKey = extras.getString("idJadwalHistory");
            statusIntent = extras.getString("dariListBilling");
            idPasien = extras.getString("idPasien");
            idDokter = extras.getString("idDokter");
            namaPasien = extras.getString("namaPasien");

        }


        mInvoice.child(jadwalKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    detailTindakanList.clear();
                    String jamInvoice = dataSnapshot.child("jamInvoice").getValue(String.class);
                    String totalHarga = dataSnapshot.child("totalHarga").getValue(String.class);

                    String ttlHarga = totalHarga.replace("Rp ","");
                    int finalTotalHarga = Integer.parseInt(ttlHarga.replace(",",""));
                    System.out.println("finalTotalHarga= "+finalTotalHarga);

                    String diskon = dataSnapshot.child("diskon").getValue(String.class);
                    String noInvoice = dataSnapshot.child("noInvoice").getValue(String.class);
                    Long tanggalInvoice = dataSnapshot.child("tanggalInvoice").getValue(Long.class);
                    String metodePembayaran = dataSnapshot.child("metodePembayaran").getValue(String.class);

                    DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                    symbols.setDecimalSeparator(',');
                    DecimalFormat decimalFormat = new DecimalFormat("Rp ###,###,###,###", symbols);
                    int diskonHarga = Integer.parseInt(diskon);
                    if (diskonHarga==0)
                    {
                        if (statusUser.equalsIgnoreCase("Pasien"))
                        {
                            etDiskon.setVisibility(View.GONE);
                            tvDiskon.setVisibility(View.GONE);
                        }

                    }
                    else
                    {
                        final String RpDiskon = decimalFormat.format(diskonHarga);
                        etDiskon.setText(RpDiskon);
                        etDiskon.setEnabled(false);

                    }


                    final String alltotalharga = decimalFormat.format(finalTotalHarga);
                    int grandTotal = finalTotalHarga-diskonHarga;
                    System.out.println("grand total = "+grandTotal);
                    final String RptotalHarga = decimalFormat.format(grandTotal);

                    tvInvoice.setText(noInvoice);
                    tvAllTotalHarga1.setText(RptotalHarga);
                    tvTotalHarga.setText(RptotalHarga);
                    tvAllTotal.setText(alltotalharga);


                    tvJamInvoice.setText(jamInvoice);
                    tvTanggalInvoice.setText(getDate(tanggalInvoice));
                    spPembayaran.setSelection(Integer.parseInt(metodePembayaran));
                    spPembayaran.setEnabled(false);
                    mTindakanPerawatan = mRoot.child("tindakanperawatan").child(jadwalKey);


                    mAdapter = new FirebaseRecyclerAdapter<Tindakan, BillingHolder>(
                            Tindakan.class, R.layout.card_view_tindakan_billing, BillingHolder.class, mTindakanPerawatan) {
                        @Override
                        protected void populateViewHolder(final BillingHolder viewHolder, Tindakan model, final int position) {
                            System.out.println("POSITION = " + position);
                            final DatabaseReference tindakanPerawatan = getRef(position);
                            keyTindakan = tindakanPerawatan.getKey();
                            System.out.println("tindakanKey = " + keyTindakan);

                            mTindakanPerawatan.child(keyTindakan).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String namaTindakan = dataSnapshot.child("tindakan").getValue(String.class);
                                    final String noGigi = dataSnapshot.child("noGigi").getValue(String.class);
                                    final String hargaTindakan = dataSnapshot.child("hargaTindakan").getValue(String.class);
                                    DecimalFormatSymbols symbols1 = new DecimalFormatSymbols();
                                    symbols1.setDecimalSeparator(',');
                                    DecimalFormat decimalFormat1 = new DecimalFormat("Rp ###,###,###,###", symbols1);
                                    int harga = Integer.parseInt(hargaTindakan);
                                    String hargaFormat = decimalFormat1.format(harga);
                                    viewHolder.tvHarga.setText(hargaFormat);
                                    viewHolder.tvNama.setText(namaTindakan);
                                    viewHolder.tvNoGigi.setText(noGigi);

                                    DetailTindakan detailTindakan = new DetailTindakan(hargaTindakan,noGigi,namaTindakan);
                                    detailTindakanList.add(detailTindakan);


                                    mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                                        @Override
                                        public void onItemRangeInserted(int positionStart, int itemCount) {
                                            super.onItemRangeInserted(positionStart, itemCount);

                                            int friendlyMessageCount = mAdapter.getItemCount();
                                            int lastVisiblePosition =
                                                    mManager.findLastCompletelyVisibleItemPosition();
                                            // If the recycler view is initially being loaded or the
                                            // user is at the bottom of the list, scroll to the bottom
                                            // of the list to show the newly added message.
                                            if (lastVisiblePosition == -1 ||
                                                    (positionStart >= (friendlyMessageCount - 1) &&
                                                            lastVisiblePosition == (positionStart - 1))) {
                                                mRecyclerview.scrollToPosition(positionStart);
                                            }
                                        }


                                    });
                                    progressDialog.dismiss();





                                }


                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                        }
                    };

                    mRecyclerview.setAdapter(mAdapter);

                } else {

                    if (statusUser.equalsIgnoreCase("Pasien"))
                    {
                        etDiskon.setVisibility(View.GONE);
                        tvDiskon.setVisibility(View.GONE);
                    }

                    String hours = "00";
                    if (hour<10)
                    {
                        hours = "0"+hour;
                    }
                    else
                    {
                        hours = String.valueOf(hour);
                    }

                    String minutes = "00";
                    if (minute<10)
                    {
                        minutes = "0"+minute;
                    }
                    else
                    {
                        minutes = String.valueOf(minute);
                    }
                    tvJamInvoice.setText(hours + " : " + minutes);
                    mTindakanPerawatan = mRoot.child("tindakanperawatan").child(jadwalKey);
                    System.out.println("mTindakanPerawatan = " + mTindakanPerawatan.getRoot());

                    mAdapter = new FirebaseRecyclerAdapter<Tindakan, BillingHolder>(
                            Tindakan.class, R.layout.card_view_tindakan_billing, BillingHolder.class, mTindakanPerawatan) {
                        @Override
                        protected void populateViewHolder(final BillingHolder viewHolder, Tindakan model, final int position) {
                            System.out.println("POSITION = " + position);
                            final DatabaseReference tindakanPerawatan = getRef(position);
                            keyTindakan = tindakanPerawatan.getKey();
                            System.out.println("tindakanKey = " + keyTindakan);

                            mTindakanPerawatan.child(keyTindakan).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    String namaTindakan = dataSnapshot.child("tindakan").getValue(String.class);
                                    final String noGigi = dataSnapshot.child("noGigi").getValue(String.class);
                                    final String hargaTindakan = dataSnapshot.child("hargaTindakan").getValue(String.class);
                                    DecimalFormatSymbols symbols1 = new DecimalFormatSymbols();
                                    symbols1.setDecimalSeparator(',');
                                    DecimalFormat decimalFormat1 = new DecimalFormat("Rp ###,###,###,###", symbols1);
                                    int harga = Integer.parseInt(hargaTindakan);
                                    String hargaFormat = decimalFormat1.format(harga);
                                    viewHolder.tvHarga.setText(hargaFormat);
                                    viewHolder.tvNama.setText(namaTindakan);
                                    viewHolder.tvNoGigi.setText(noGigi);


                                    total = total + harga;
                                    DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                                    symbols.setDecimalSeparator(',');
                                    final DecimalFormat decimalFormat = new DecimalFormat("Rp ###,###,###,###", symbols);
                                    final DecimalFormat diskonFormat = new DecimalFormat("###,###,###,###", symbols);
                                    final String totalHarga = decimalFormat.format(total);
                                    tvAllTotal.setText(String.valueOf(totalHarga));
                                    tvAllTotalHarga1.setText(String.valueOf(totalHarga));
                                    tvTotalHarga.setText(String.valueOf(totalHarga));
                                    final int value = 0;

                                    etDiskon.addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                        }

                                        @Override
                                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

//                                            etDiskon.setText(diskonFormat.format(Integer.parseInt(charSequence.toString())));

                                            System.out.println("charsequence = "+charSequence.toString());
                                            if (etDiskon.getText().toString().isEmpty())
                                            {
                                                etDiskon.setText(""+value);
                                            }
                                            else
                                            {
                                                int totalSetelahDiskon = total - (value+Integer.parseInt(charSequence.toString()));
                                                if (Integer.parseInt(charSequence.toString())<total)
                                                {
                                                    final String totalSetelahDiskonFormat = decimalFormat.format(totalSetelahDiskon);
                                                    tvAllTotalHarga1.setText(totalSetelahDiskonFormat);
                                                    tvTotalHarga.setText(totalSetelahDiskonFormat);
                                                }
                                                else
                                                {
                                                    Toast.makeText(BillingActivity.this, "Maaf, Diskon anda melebihi total harga", Toast.LENGTH_SHORT).show();
                                                    etDiskon.setText(""+value);
                                                }

                                            }




                                        }

                                        @Override
                                        public void afterTextChanged(Editable editable) {

                                        }
                                    });





                                    mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                                        @Override
                                        public void onItemRangeInserted(int positionStart, int itemCount) {
                                            super.onItemRangeInserted(positionStart, itemCount);

                                            int friendlyMessageCount = mAdapter.getItemCount();
                                            int lastVisiblePosition =
                                                    mManager.findLastCompletelyVisibleItemPosition();
                                            // If the recycler view is initially being loaded or the
                                            // user is at the bottom of the list, scroll to the bottom
                                            // of the list to show the newly added message.
                                            if (lastVisiblePosition == -1 ||
                                                    (positionStart >= (friendlyMessageCount - 1) &&
                                                            lastVisiblePosition == (positionStart - 1))) {
                                                mRecyclerview.scrollToPosition(positionStart);
                                            }
                                        }


                                    });
                                    progressDialog.dismiss();


                                }


                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                        }
                    };

                    System.out.println("mAdapter = " + mRecyclerview.getChildCount());
                    if (mRecyclerview.getChildCount() > 0) {
                        tvStatus.setVisibility(View.GONE);

                    } else {
                        tvStatus.setVisibility(View.VISIBLE);

                    }
                    progressDialog.dismiss();
                    mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                        @Override
                        public void onItemRangeInserted(int positionStart, int itemCount) {
                            super.onItemRangeInserted(positionStart, itemCount);
                            mRecyclerview.smoothScrollToPosition(mAdapter.getItemCount());
                            System.out.println("mAdapterDalem = " + mAdapter.getItemCount());
                            System.out.println("itemCount = " + itemCount);
                            System.out.println("positionStart = " + positionStart);
                            if (mAdapter.getItemCount() > 0) {
                                tvStatus.setVisibility(View.GONE);
                                progressDialog.dismiss();
                            }
                        }
                    });
                    mRecyclerview.setAdapter(mAdapter);
//                        mRecyclerview.setVisibility(View.GONE);
//                        tvStatus.setVisibility(View.VISIBLE);
//                        progressDialog.dismiss();


                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





        mInvoice.child(jadwalKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()==null)
                {
                    mInvoice.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            System.out.println("datasnapshot no invoice = " + dataSnapshot.getChildrenCount());

                            noInvoiceCurrent = (int) (10001 + dataSnapshot.getChildrenCount());

                            System.out.println("NO URUT 1 = " + noInvoiceCurrent);

                            mInvoice.orderByChild("noInvoice").equalTo(String.valueOf(noInvoiceCurrent)).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    System.out.println("DATA EXISTS = " + dataSnapshot.exists());
                                    if (dataSnapshot.exists() == true) {
                                        noInvoiceCurrent = noInvoiceCurrent + 1;
                                    }

                                    System.out.println("NO URUT 2 = " + noInvoiceCurrent);

                                    tvInvoice.setText(String.valueOf(noInvoiceCurrent));


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

                    System.out.println(idPasien + " " + idDokter + " " + jadwalKey + " ");


                    mJadwal.child(jadwalKey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            timeStamp = dataSnapshot.child("timeStamp").getValue(Long.class);
                            String tanggal = getDate(timeStamp);
                            tvTanggalInvoice.setText(tanggal);
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

        tvIdPasien.setText(idPasien);

        mPasien.child(idPasien).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nama = dataSnapshot.child("nama").getValue(String.class);
                tvNamaPasien.setText(nama);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDokter.child(idDokter).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                namaDokter = dataSnapshot.child("nama").getValue(String.class);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (statusIntent!=null)
        {
            tvNamaPasien.setText(namaPasien);
            tvIdPasien.setText(idPasien);
        }







    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public static class BillingHolder extends RecyclerView.ViewHolder {

        TextView tvNama, tvHarga, tvNoGigi;

        public BillingHolder(final View itemView) {
            super(itemView);
            tvNama = (TextView) itemView.findViewById(R.id.tvNamaTindakan);
            tvHarga = (TextView) itemView.findViewById(R.id.tvHargaTindakan);
            tvNoGigi = (TextView) itemView.findViewById(R.id.tvNoGigi);
        }

    }


    @Override
    protected void onStart() {
        super.onStart();

        System.out.println("mInvoice = " + mInvoice);

    }

    private void saveBilling(final String jadwalKey, Long timeStamp, String noInvoice, String totalHarga, String diskon, String jamInvoice, String metodePembayaran) {

        String namePasien = tvNamaPasien.getText().toString();
        Invoice invoice = new Invoice(noInvoice,timeStamp, totalHarga, diskon, jamInvoice, metodePembayaran,idPasien,namePasien,idDokter,namaDokter);

        mInvoice.child(jadwalKey).setValue(invoice).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    finish();
                    progressDialog.dismiss();
                    mJadwal.child(jadwalKey).child("status").setValue("Selesai");
                    Toast.makeText(BillingActivity.this, "Data Billing Berhasil disimpan", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_perawatan_lain, menu);
        final MenuItem save = menu.findItem(R.id.save);
        final MenuItem addPhoto = menu.findItem(R.id.addphoto);
        final MenuItem pdf = menu.findItem(R.id.pdf);
        pdf.setVisible(false);
        addPhoto.setVisible(false);

        if (statusUser.equalsIgnoreCase("Pasien") || statusUser.equalsIgnoreCase("Administrator")) {
            save.setVisible(false);
            pdf.setVisible(false);
        }

        mInvoice.child(jadwalKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    save.setVisible(false);
                    pdf.setVisible(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.save) {

            progressDialog = new ProgressDialog(BillingActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            final String noInvoice = tvInvoice.getText().toString();
            final String jamInvoice = tvJamInvoice.getText().toString();
            final String totalHarga = tvAllTotal.getText().toString();
            String diskon = etDiskon.getText().toString();
            if (TextUtils.isEmpty(diskon))
            {
                diskon="0";
            }

            final String metodePembayaran = String.valueOf(spPembayaran.getSelectedItemPosition());
            saveBilling(jadwalKey, timeStamp, noInvoice, totalHarga, diskon, jamInvoice, metodePembayaran);


        }else if (id==android.R.id.home)
        {
            onBackPressed();
            return true;
        }
        else if (id==R.id.pdf)
        {

                mPasien.child(idPasien).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final Pasien pasien = dataSnapshot.getValue(Pasien.class);

                        mDokter.child(idDokter).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final Dokter dokter = dataSnapshot.getValue(Dokter.class);
                                mInvoice.child(jadwalKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Invoice invoice = dataSnapshot.getValue(Invoice.class);

                                        try {
                                            createPdf(pasien,dokter,invoice);
                                        } catch (DocumentException e) {
                                            e.printStackTrace();
                                        } catch (IOException e) {
                                            e.printStackTrace();
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





        }


        return super.onOptionsItemSelected(item);
    }

    private String getDate(long timeStamp){

        try{
            SimpleDateFormat ee = new SimpleDateFormat("EEEE");
            SimpleDateFormat dd = new SimpleDateFormat("dd");
            SimpleDateFormat MM = new SimpleDateFormat("MM");
            SimpleDateFormat yyyy = new SimpleDateFormat("yyyy");
            Date netDate = (new Date(timeStamp));
            String dayWeek = ee.format(netDate);
            String day = dd.format(netDate);
            String month = MM.format(netDate);
            String nameMonth = MONTHS[Integer.parseInt(month)-1];
            String year = yyyy.format(netDate);
            String tanggal = dayWeek+", "+day+" "+nameMonth+" "+year;
            return tanggal;
        }
        catch(Exception ex){
            System.out.println("Log = "+ex);
            return "xx";
        }
    }


    private void viewPdf(File myFile) {

        Intent intent = new Intent(BillingActivity.this, PdfViewerActivity.class);
        intent.putExtra("file", myFile);
        intent.putExtra("title", "Invoice");
        startActivity(intent);


    }

    public static class TableHeader {


        public static class HeaderTable extends PdfPageEventHelper {
            protected PdfPTable table;
            protected float tableHeight;

            public HeaderTable() {
                table = new PdfPTable(1);
                table.setTotalWidth(523);
                table.setWidthPercentage(100);
                table.setLockedWidth(true);
                Font colfont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
                Font alamat = FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, Font.BOLD);
                Font nama = FontFactory.getFont(FontFactory.TIMES_ROMAN, 16, Font.BOLD);

                PdfPCell cell = new PdfPCell(new Phrase("PRAKTEK DOKTER GIGI", colfont));
                PdfPCell cell2 = new PdfPCell(new Phrase("drg. Hansen dan drg. Endola Tantono", nama));
                PdfPCell cell3 = new PdfPCell(new Phrase("Gedung MDP Dempo, Lantai 2, Jln. Lingkaran 1 No. 305 A-E, Palembang, Sumatera Selatan. Telp : 0711-322-222", alamat));
                PdfPCell cell5 = new PdfPCell(new Phrase("SIP No 446/IPD/0213/DPMPTSP-PPK/2018 | SIP No 446/IPD/0212/DPMPTSP-PPK/2018", alamat));
                PdfPCell cell4 = new PdfPCell(new Phrase("--------------------------------------------------------------------------------------------------------------------------------------------------------------------", alamat));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorder(PdfPCell.NO_BORDER);
                cell2.setBorder(PdfPCell.NO_BORDER);
                cell3.setBorder(PdfPCell.NO_BORDER);
                cell4.setBorder(PdfPCell.NO_BORDER);
                cell5.setBorder(PdfPCell.NO_BORDER);
                table.setSpacingAfter(0);
                table.setSpacingBefore(0);
                table.addCell(cell);
                table.addCell(cell2);
                table.addCell(cell5);
                table.addCell(cell3);
                table.addCell(cell4);
                tableHeight = table.getTotalHeight();



            }

            public float getTableHeight() {
                return tableHeight;
            }

            public void onEndPage(PdfWriter writer, Document document) {
                table.writeSelectedRows(0, -1,
                        document.left(),
                        document.top() + ((document.topMargin() + tableHeight) / 2),
                        writer.getDirectContent());
            }
        }
    }

    static class myFooter extends PdfPageEventHelper {
        Font ffont = new Font(Font.FontFamily.UNDEFINED, 5, Font.ITALIC);



        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            Date date = new Date();
            String dateCreated = new SimpleDateFormat("dd" + "/" + "MM" + "/" + "yyyy").format(date);
            Phrase footer = new Phrase("Tanggal Cetak : " + dateCreated, ffont);


            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, footer, (document.right() - document.left()) / 2 + document.leftMargin(),
                    document.bottom() - 10, 0);
        }
    }

    private void createPdf(Pasien pasien,Dokter dokter,Invoice invoice) throws DocumentException, IOException {


        File pdfFolder = new File("/sdcard", "pdfDentistReceipt");

        if (!pdfFolder.exists()) {
            pdfFolder.mkdir();

        }

        System.out.println("namaPasien method = " + namaPasien);
        TableHeader.HeaderTable event = new TableHeader.HeaderTable();

        //Create time stamp
        Date date = new Date();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);
        String dateCreated = new SimpleDateFormat("dd" + "/" + "MM" + "/" + "yyyy").format(date);
        File myFile = new File("/sdcard/pdfDentistReceipt/" + namaPasien+"_"+timeStamp+ ".pdf");


        System.out.println("pdf file = " + myFile);

        FileOutputStream output = new FileOutputStream(myFile);


        //Step 1
        Document document = new Document(PageSize.A4, 36, 36, 40 + event.getTableHeight(), 36);

        //Step
        PdfWriter pdfWriter = PdfWriter.getInstance(document, output);
        pdfWriter.setPageEvent(event);
        //Step 3
        document.open();

        BillingActivity.myFooter footer = new BillingActivity.myFooter();
        pdfWriter.setPageEvent(footer);




        Font text = FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, Font.BOLD);
        Font textTitle = FontFactory.getFont(FontFactory.TIMES_ROMAN, 20,Font.BOLD);




        Paragraph title = new Paragraph("INVOICE", textTitle);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        title.setSpacingAfter(15);
        document.add(title);

        final PdfPTable pdfPTable1 = new PdfPTable(5);
        pdfPTable1.setWidthPercentage(100);
        pdfPTable1.setWidths(new float[]{3, 5,1,2,5});
        pdfPTable1.addCell(getCell("DETAIL PASIEN", PdfPCell.ALIGN_LEFT,0));
        pdfPTable1.addCell(getCell("", PdfPCell.ALIGN_LEFT,0));
        pdfPTable1.addCell(getCell("", PdfPCell.ALIGN_LEFT,0));
        pdfPTable1.addCell(getCell("DETAIL", PdfPCell.ALIGN_LEFT,0));
        pdfPTable1.addCell(getCell("", PdfPCell.ALIGN_LEFT,0));
        pdfPTable1.setSpacingAfter(8);
        pdfPTable1.setSpacingBefore(10);
        document.add(pdfPTable1);



        final PdfPTable pdfPTable = new PdfPTable(5);
        pdfPTable.setWidthPercentage(100);
        pdfPTable.setWidths(new float[]{2, 5,1,2,5});
        pdfPTable.addCell(getCell("ID Pasien", PdfPCell.ALIGN_LEFT,0));
        pdfPTable.addCell(getCell(": "+idPasien, PdfPCell.ALIGN_LEFT,1));
        pdfPTable.addCell(getCell("", PdfPCell.ALIGN_LEFT,0));
        pdfPTable.addCell(getCell("No Invoice", PdfPCell.ALIGN_LEFT,0));
        pdfPTable.addCell(getCell(": "+invoice.getNoInvoice(), PdfPCell.ALIGN_LEFT,1));
        pdfPTable.setSpacingAfter(4);
        document.add(pdfPTable);


        final PdfPTable pdfPTable2 = new PdfPTable(5);
        pdfPTable2.setWidthPercentage(100);
        pdfPTable2.setWidths(new float[]{2, 5,1,2,5});
        pdfPTable2.addCell(getCell("Nama", PdfPCell.ALIGN_LEFT,0));
        pdfPTable2.addCell(getCell(": "+pasien.getNama(), PdfPCell.ALIGN_LEFT,1));
        pdfPTable2.addCell(getCell("", PdfPCell.ALIGN_LEFT,0));
        pdfPTable2.addCell(getCell("Nama", PdfPCell.ALIGN_LEFT,0));
        pdfPTable2.addCell(getCell(": drg. "+dokter.getNama(), PdfPCell.ALIGN_LEFT,1));
        pdfPTable2.setSpacingAfter(4);
        document.add(pdfPTable2);

        final PdfPTable pdfPTable4 = new PdfPTable(5);
        pdfPTable4.setWidthPercentage(100);
        pdfPTable4.setWidths(new float[]{2, 5,1,2,5});
        pdfPTable4.addCell(getCell("TTL", PdfPCell.ALIGN_LEFT,0));
        pdfPTable4.addCell(getCell(": "+pasien.getTempatLahir()+", "+pasien.getTanggalLahir(), PdfPCell.ALIGN_LEFT,1));
        pdfPTable4.addCell(getCell("", PdfPCell.ALIGN_LEFT,0));
        pdfPTable4.addCell(getCell("SIP", PdfPCell.ALIGN_LEFT,0));
        pdfPTable4.addCell(getCell(": "+dokter.getNoSIP(), PdfPCell.ALIGN_LEFT,1));
        pdfPTable4.setSpacingAfter(4);
        document.add(pdfPTable4);


        final PdfPTable pdfPTable3 = new PdfPTable(5);
        pdfPTable3.setWidthPercentage(100);
        pdfPTable3.setWidths(new float[]{2, 5,1,2,5});
        pdfPTable3.addCell(getCell("Telp", PdfPCell.ALIGN_LEFT,0));
        pdfPTable3.addCell(getCell(": "+pasien.getTeleponSeluler(), PdfPCell.ALIGN_LEFT,1));
        pdfPTable3.addCell(getCell("", PdfPCell.ALIGN_LEFT,0));
        pdfPTable3.addCell(getCell("Tanggal", PdfPCell.ALIGN_LEFT,0));
        pdfPTable3.addCell(getCell(": "+invoice.tanggalConvert(), PdfPCell.ALIGN_LEFT,1));
        pdfPTable3.setSpacingAfter(4);
        document.add(pdfPTable3);

        final PdfPTable pdfPTable5 = new PdfPTable(5);
        pdfPTable5.setWidthPercentage(100);
        pdfPTable5.setWidths(new float[]{2, 5,1,2,5});
        pdfPTable5.addCell(getCell("", PdfPCell.ALIGN_LEFT,0));
        pdfPTable5.addCell(getCell("", PdfPCell.ALIGN_LEFT,0));
        pdfPTable5.addCell(getCell("", PdfPCell.ALIGN_LEFT,0));
        pdfPTable5.addCell(getCell("Pembayaran", PdfPCell.ALIGN_LEFT,0));
        String metode = Pembayaran[Integer.parseInt(invoice.getMetodePembayaran())];

//        if (Integer.parseInt(invoice.getMetodePembayaran())==0)
//        {
//            metode = "Tunai";
//        }
//        else if (Integer.parseInt(invoice.getMetodePembayaran())==1)
//        {
//            metode = "Kartu Debit";
//        }
//        else if (Integer.parseInt(invoice.getMetodePembayaran())==2)
//        {
//            metode = "Kartu Kredit";
//        }
//        else if (Integer.parseInt(invoice.getMetodePembayaran())==3)
//        {
//            metode = "Sekolah Kusuma Bangsa";
//        }
//        else if (Integer.parseInt(invoice.getMetodePembayaran())==4)
//        {
//            metode = "MDP Group";
//        }
        pdfPTable5.addCell(getCell(": "+metode, PdfPCell.ALIGN_LEFT,1));
        pdfPTable5.setSpacingAfter(4);
        document.add(pdfPTable5);

        Paragraph tindakan = new Paragraph("DETAIL TINDAKAN/PERAWATAN", text);
        tindakan.setAlignment(Paragraph.ALIGN_LEFT);
        tindakan.setSpacingAfter(5);
        document.add(tindakan);

        if (detailTindakanList != null && detailTindakanList.size() > 0) {


            final PdfPTable tindakanDetail = new PdfPTable(5);
            tindakanDetail.setWidthPercentage(100);
            tindakanDetail.setWidths(new float[]{1, 2, 8, 2, 4});
            tindakanDetail.addCell(getCellBorderBold("NO", PdfPCell.ALIGN_CENTER));
            tindakanDetail.addCell(getCellBorderBold("KODE GIGI", PdfPCell.ALIGN_CENTER));
            tindakanDetail.addCell(getCellBorderBold("TINDAKAN", PdfPCell.ALIGN_CENTER));
            tindakanDetail.addCell(getCellBorderBold("JUMLAH", PdfPCell.ALIGN_CENTER));
            tindakanDetail.addCell(getCellBorderBold("HARGA", PdfPCell.ALIGN_CENTER));

            System.out.println("TESS = " + detailTindakanList.get(0).getHargaTindakan());

            for (int pp = 0; pp <= detailTindakanList.size() - 1; pp++) {
                ambil = detailTindakanList.get(pp);
                tindakanDetail.addCell(getCellBorder(String.valueOf(pp + 1), PdfPCell.ALIGN_CENTER,1));
                tindakanDetail.addCell(getCellBorder(ambil.getNoGigi(), PdfPCell.ALIGN_CENTER,1));
                tindakanDetail.addCell(getCellBorder(ambil.getTindakan(), PdfPCell.ALIGN_LEFT,1));
                tindakanDetail.addCell(getCellBorder("1", PdfPCell.ALIGN_CENTER,1));
                tindakanDetail.addCell(getCellBorder(rupiahFormat(ambil.getHargaTindakan()), PdfPCell.ALIGN_CENTER,1));
                totalHarga = Integer.parseInt(ambil.getHargaTindakan())+totalHarga;

            }

            document.add(tindakanDetail);

            final PdfPTable total = new PdfPTable(5);
            total.setWidthPercentage(100);
            total.setWidths(new float[]{1,2,8,2,4});
            total.addCell(getCell("", PdfPCell.ALIGN_CENTER,1));
            total.addCell(getCell("", PdfPCell.ALIGN_CENTER,1));
            total.addCell(getCell("", PdfPCell.ALIGN_CENTER,1));
            total.addCell(getCellBorderBold("TOTAL", PdfPCell.ALIGN_CENTER));
            total.addCell(getCellBorder(rupiahFormat(String.valueOf(totalHarga)), PdfPCell.ALIGN_CENTER,1));
            document.add(total);
        }




        if (invoice.getDiskon()!=null)
        {
            final PdfPTable diskon = new PdfPTable(5);
            diskon.setWidthPercentage(100);
            diskon.setWidths(new float[]{1,2,8,2,4});
            diskon.addCell(getCell("", PdfPCell.ALIGN_CENTER,1));
            diskon.addCell(getCell("", PdfPCell.ALIGN_CENTER,1));
            diskon.addCell(getCell("", PdfPCell.ALIGN_CENTER,1));
            diskon.addCell(getCellBorderBold("DISKON", PdfPCell.ALIGN_CENTER));
            diskon.addCell(getCellBorder(rupiahFormat(invoice.getDiskon()), PdfPCell.ALIGN_CENTER,1));
            document.add(diskon);
        }




        final PdfPTable grandTotal = new PdfPTable(5);
        grandTotal.setWidthPercentage(100);
        grandTotal.setWidths(new float[]{1,2,8,2,4});
        grandTotal.addCell(getCell("", PdfPCell.ALIGN_CENTER,1));
        grandTotal.addCell(getCell("", PdfPCell.ALIGN_CENTER,1));
        grandTotal.addCell(getCell("", PdfPCell.ALIGN_CENTER,1));
        grandTotal.addCell(getCellBorderBold("GRAND TOTAL", PdfPCell.ALIGN_CENTER));
        grandTotal.addCell(getCellBorder(rupiahFormat(String.valueOf(totalHarga-Integer.parseInt(invoice.getDiskon()))), PdfPCell.ALIGN_CENTER,1));
        document.add(grandTotal);

        document.close();


        System.out.println("PDF OK");

        viewPdf(myFile);
//        sendEmail(myFile);


    }

    public static PdfPCell getCell(String text, int aligment,int flags) throws IOException, DocumentException {

        Font bold;

        if (flags==0)
        {
            bold = FontFactory.getFont(FontFactory.TIMES, 10,Font.BOLD);
        }
        else
        {
            bold = FontFactory.getFont(FontFactory.TIMES, 10);
        }

        PdfPCell cell = new PdfPCell(new Phrase(text, bold));
        cell.setPadding(0);
        cell.setHorizontalAlignment(aligment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    protected void sendEmail(File myFile) {
        Log.i("Send email", "");

        String[] TO = {"federicoteddy@gmail.com"};
//        String[] CC = {"xyz@gmail.com"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:mdphimsi@gmail.com"));
        emailIntent.setType("vnd.android.cursor.dir/email");
        Uri path = Uri.fromFile(myFile);

        System.out.println("myFile sendemail = "+path);
        emailIntent.putExtra(Intent.EXTRA_STREAM, path);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
//        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "[INVOICE] Klinik drg. Hansen dan drg. Endola");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Dear Federico");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
            Log.i("Finished sending", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(BillingActivity.this,
                    "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }



    public PdfPCell getCellBorderBold(String text, int aligment) {
        Font judul = FontFactory.getFont(FontFactory.TIMES, 10, Font.BOLD);
        PdfPCell cell = new PdfPCell(new Phrase(text,judul));
        cell.setHorizontalAlignment(aligment);
        return cell;
    }

    public PdfPCell getCellBorder(String text, int aligment,int flags) {
        Font font;
        if (flags==0)
        {
            font = FontFactory.getFont(FontFactory.TIMES, 10);
        }
        else
        {
            font = FontFactory.getFont(FontFactory.TIMES, 9);
        }


        PdfPCell cell = new PdfPCell(new Phrase(text,font));
        cell.setHorizontalAlignment(aligment);
        return cell;
    }

    protected String rupiahFormat(String angka)
    {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        DecimalFormat decimalFormat = new DecimalFormat("Rp ###,###,###,###", symbols);
        int harga = Integer.parseInt(angka);
        String hargaFormat = decimalFormat.format(harga);

        return hargaFormat;
    }



}


