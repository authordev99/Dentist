package com.teddybrothers.co_teddy.dentist.viewholder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.teddybrothers.co_teddy.dentist.CustomObject;
import com.teddybrothers.co_teddy.dentist.R;
import com.teddybrothers.co_teddy.dentist.Utilities;
import com.teddybrothers.co_teddy.dentist.entity.Perawatan;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by co_teddy on 4/17/2017.
 */

public class MainViewHistoryTindakan extends RecyclerView.ViewHolder {

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    Uri imageUri;
    String photoUrl = "null";
    private static final int GALERY_INTENT = 2;
    public String mCurrentPhotoPath, urlPhoto;
    public String gigiAtas, gigiBawah, gigiKanan, gigiKiri, nama, keterangan, berlubang;

    public TextView tvDeskripsi, tvTanggal, tvStatusPerawatan;
    FirebaseRecyclerAdapter<Perawatan, MainViewPhoto> mAdapter;
    LinearLayoutManager mManager;
    ImageView ivUpload, photoPerawatan;
    Context context;
    Fragment fragment;
    ProgressDialog progressDialog;
    Button btnDate;
    public String perawatanKeyOri;
    public String atas = "null";
    public String bawah = "null";
    public String kanan = "null";
    public String kiri = "null";
    public String lubang = "null";
    ArrayList<CustomObject> listSimbolGigi = new ArrayList<CustomObject>();
    ArrayList<CustomObject> simbolDepan = new ArrayList<CustomObject>();
    ArrayList<CustomObject> mebDepan = new ArrayList<CustomObject>();
    ArrayList<CustomObject> pobDepan = new ArrayList<CustomObject>();
    ArrayList<CustomObject> meb = new ArrayList<CustomObject>();
    ArrayList<CustomObject> pob = new ArrayList<CustomObject>();
    ArrayList<CustomObject> tambal = new ArrayList<CustomObject>();
    ArrayList<CustomObject> choice = new ArrayList<CustomObject>();

    public CustomObject listGigi, listMeb, listPob, listTambal;
    DatabaseReference rootRef, perawatanRef, rekammedisRef, mTindakan;


    public static final String[] MONTHS = {"Januari", "Febuari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
    public Calendar cal = Calendar.getInstance();
    public int day = cal.get(Calendar.DAY_OF_MONTH);
    public int month = cal.get(Calendar.MONTH);
    public int year = cal.get(Calendar.YEAR);
    FirebaseAuth mAuth;
    public FirebaseDatabase database = FirebaseDatabase.getInstance();
    public DatabaseReference mPerawatan = database.getReference().child("perawatan");
    public DatabaseReference mRekamMedis = database.getReference().child("rekammedis");
    CustomObject object;
    ImageView ivGigi, ivMore;
    Perawatan displayedPerawatan = new Perawatan();
    public String gigiKey, perawatanKey, noGigi, kodeGigi, idPerawatan, keyTindakan, obat;
    Utilities util = new Utilities();

    public MainViewHistoryTindakan(View itemView) {
        super(itemView);
        tvDeskripsi = (TextView) itemView.findViewById(R.id.tvNama);
        tvTanggal = (TextView) itemView.findViewById(R.id.tvTanggal);
        tvStatusPerawatan = (TextView) itemView.findViewById(R.id.tvStatusPerawatan);
        ivGigi = (ImageView) itemView.findViewById(R.id.thumb);
        ivMore = (ImageView) itemView.findViewById(R.id.ivMore);

        CustomObject simbol = new CustomObject(R.drawable.amf, "amf", "Tambalan Amalgam (AMF)", R.drawable.amfdetail);
        listSimbolGigi.add(simbol);
        simbol = new CustomObject(R.drawable.cof, "cof", "Tambalan Composite (Cof)", R.drawable.cofdetail);
        listSimbolGigi.add(simbol);
        simbol = new CustomObject(R.drawable.fis, "fis", "Pit dan Fissure Sealant (Fis)", R.drawable.fisdetail);
        listSimbolGigi.add(simbol);
        simbol = new CustomObject(R.drawable.nvt, "nvt", "Gigi non-vital", R.drawable.nvtdetail);
        listSimbolGigi.add(simbol);
        simbol = new CustomObject(R.drawable.rct, "rct", "Perawatan Saluran Akar (rct)", R.drawable.rctdetail);
        listSimbolGigi.add(simbol);
        simbol = new CustomObject(R.drawable.non, "non", "Gigi Tidak ada, tidak diketahui ada atau tidak ada", R.drawable.nondetail);
        listSimbolGigi.add(simbol);
        simbol = new CustomObject(R.drawable.une, "une", "Un-Erupted (une)", R.drawable.unedetail);
        listSimbolGigi.add(simbol);
        simbol = new CustomObject(R.drawable.pre, "pre", "Partial Erupt (Pre)", R.drawable.predetail);
        listSimbolGigi.add(simbol);
        simbol = new CustomObject(R.drawable.sou, "sou", "Normal / Baik (Sou)", R.drawable.soudetail);
        listSimbolGigi.add(simbol);
        simbol = new CustomObject(R.drawable.ano, "ano", "Anomali (Ano)", R.drawable.anodetail);
        listSimbolGigi.add(simbol);
        simbol = new CustomObject(R.drawable.car, "car", "Tambalan Sementara (Car)", R.drawable.cardetail);
        listSimbolGigi.add(simbol);
        simbol = new CustomObject(R.drawable.cfr, "cfr", "Fracture", R.drawable.cfrdetail);
        listSimbolGigi.add(simbol);
        simbol = new CustomObject(R.drawable.amfrct, "amfrct", "Anomali (Ano)", R.drawable.amfrctdetail);
        listSimbolGigi.add(simbol);
        simbol = new CustomObject(R.drawable.fmc, "fmc", "Full Metal Crown pada gigi vital (fmc)", R.drawable.fmcdetail);
        listSimbolGigi.add(simbol);
        simbol = new CustomObject(R.drawable.fmcrct, "fmcrct", "Full Metal Crown pada gigi non-vital (fmc-rct)", R.drawable.fmcrctdetail);
        listSimbolGigi.add(simbol);
        simbol = new CustomObject(R.drawable.poc, "poc", "Porcleain crown pada gigi vital (poc)", R.drawable.pocdetail);
        listSimbolGigi.add(simbol);
        simbol = new CustomObject(R.drawable.pocrct, "pocrct", "Porcleain crown pada gigi non vital (poc-rct)", R.drawable.pocrctdetail);
        listSimbolGigi.add(simbol);
        simbol = new CustomObject(R.drawable.rrx, "rrx", "Sisa Akar (rrx)", R.drawable.rrxdetail);
        listSimbolGigi.add(simbol);
        simbol = new CustomObject(R.drawable.mis, "mis", "Gigi Hilang (mis)", R.drawable.misdetail);
        listSimbolGigi.add(simbol);
        simbol = new CustomObject(R.drawable.ipx, "ipx", "Implant + Porcleain crown (ipx-poc)", R.drawable.ipxdetail);
        listSimbolGigi.add(simbol);
        simbol = new CustomObject(R.drawable.ipx, "ipx", "Implant + Porcleain crown (ipx-poc)", R.drawable.ipxdetail);
        listSimbolGigi.add(simbol);
        simbol = new CustomObject(R.drawable.mebfull, "meb", "Full metal Brigde 3 Units", 0);
        listSimbolGigi.add(simbol);
        simbol = new CustomObject(R.drawable.pobfull, "pob", "Porcelain bridge 4 units", 0);
        listSimbolGigi.add(simbol);
        simbol = new CustomObject(R.drawable.frmacr, "frmacr", "Partial Denture / Full Denture (frm-acr)", R.drawable.frmacrdetail);
        listSimbolGigi.add(simbol);
        simbol = new CustomObject(R.drawable.migrasikanan, "migrasi", "Migrasi / Version / Rotasi dibuat panah sesuai arah)", R.drawable.migrasikanandetail);
        listSimbolGigi.add(simbol);
        System.out.println("List Simbol Gigi = " + listSimbolGigi);


        CustomObject mebBelakang = new CustomObject(R.drawable.fmc, "meb1", "Full metal Brigde 3 Units", R.drawable.meb1);
        meb.add(mebBelakang);
        mebBelakang = new CustomObject(R.drawable.mebx, "meb2", "Full metal Brigde 3 Units", R.drawable.meb2);
        meb.add(mebBelakang);
        mebBelakang = new CustomObject(R.drawable.fmc, "meb3", "Full metal Brigde 3 Units", R.drawable.meb3);
        meb.add(mebBelakang);


        CustomObject pobBelakang = new CustomObject(R.drawable.poc, "pob1", "Porcelain bridge 4 units", R.drawable.pob1);
        pob.add(pobBelakang);
        pobBelakang = new CustomObject(R.drawable.pobx, "pob2", "Porcelain bridge 4 units", R.drawable.pob2);
        pob.add(pobBelakang);
        pobBelakang = new CustomObject(R.drawable.pobx, "pob3", "Porcelain bridge 4 units", R.drawable.pob3);
        pob.add(pobBelakang);
        pobBelakang = new CustomObject(R.drawable.poc, "pob4", "Porcelain bridge 4 units", R.drawable.pob4);
        pob.add(pobBelakang);


        CustomObject tambal1 = new CustomObject(R.drawable.listarsirats, "tambalatas", "Tambal Atas", R.drawable.arsiratas);
        tambal.add(tambal1);
        tambal1 = new CustomObject(R.drawable.listarsirbawah, "tambalbawah", "Tambal Bawah", R.drawable.arsirbawah);
        tambal.add(tambal1);
        tambal1 = new CustomObject(R.drawable.listarsirkiri, "tambalkiri", "Tambal Kiri", R.drawable.arsirkiri);
        tambal.add(tambal1);
        tambal1 = new CustomObject(R.drawable.listarsirkanan, "tambalkanan", "Tambal Kanan", R.drawable.arsirkanan);
        tambal.add(tambal1);
        tambal1 = new CustomObject(R.drawable.listlubang, "ya", "Berlubang", R.drawable.lubang);
        tambal.add(tambal1);


        CustomObject choice1 = new CustomObject(R.drawable.photocamera, "camera", "Camera", 0);
        choice.add(choice1);
        choice1 = new CustomObject(R.drawable.gallery, "gallery", "Gallery", 0);
        choice.add(choice1);


        //GIGI DEPAN
        CustomObject simbolDepanGigi = new CustomObject(R.drawable.listarsirats, "tambalatas", "Tambal Atas", R.drawable.arsiratas);
        simbolDepan.add(simbolDepanGigi);

        simbolDepanGigi = new CustomObject(R.drawable.amf2, "amf2", "Tambalan Amalgam (AMF)", R.drawable.amf2detail);
        simbolDepan.add(simbolDepanGigi);
        simbolDepanGigi = new CustomObject(R.drawable.cof2, "cof2", "Tambalan Composite (Cof)", R.drawable.cof2detail);
        simbolDepan.add(simbolDepanGigi);
        simbolDepanGigi = new CustomObject(R.drawable.fis2, "fis2", "Pit dan Fissure Sealant (Fis)", R.drawable.fis2detail);
        simbolDepan.add(simbolDepanGigi);
        simbolDepanGigi = new CustomObject(R.drawable.nvt2, "nvt2", "Gigi non-vital", R.drawable.nvt2detail);
        simbolDepan.add(simbolDepanGigi);
        simbolDepanGigi = new CustomObject(R.drawable.rct2, "rct2", "Perawatan Saluran Akar (rct)", R.drawable.rct2detail);
        simbolDepan.add(simbolDepanGigi);
        simbolDepanGigi = new CustomObject(R.drawable.non2, "non2", "Gigi Tidak ada, tidak diketahui ada atau tidak ada", R.drawable.non2detail);
        simbolDepan.add(simbolDepanGigi);
        simbolDepanGigi = new CustomObject(R.drawable.une2, "une2", "Un-Erupted (une)", R.drawable.une2detail);
        simbolDepan.add(simbolDepanGigi);
        simbolDepanGigi = new CustomObject(R.drawable.pre2, "pre2", "Partial Erupt (Pre)", R.drawable.pre2detail);
        simbolDepan.add(simbolDepanGigi);
        simbolDepanGigi = new CustomObject(R.drawable.sou2, "sou2", "Normal / Baik (Sou)", R.drawable.sou2detail);
        simbolDepan.add(simbolDepanGigi);
        simbolDepanGigi = new CustomObject(R.drawable.ano2, "ano2", "Anomali (Ano)", R.drawable.ano2detail);
        simbolDepan.add(simbolDepanGigi);
        simbolDepanGigi = new CustomObject(R.drawable.cfr2, "cfr2", "Fracture", R.drawable.cfr2detail);
        simbolDepan.add(simbolDepanGigi);
        simbolDepanGigi = new CustomObject(R.drawable.amfrct2, "amfrct2", "Anomali (Ano)", R.drawable.amfrct2detail);
        simbolDepan.add(simbolDepanGigi);
        simbolDepanGigi = new CustomObject(R.drawable.fmc2, "fmc2", "Full Metal Crown pada gigi vital (fmc)", R.drawable.fmc2detail);
        simbolDepan.add(simbolDepanGigi);
        simbolDepanGigi = new CustomObject(R.drawable.fmcrct2, "fmcrct2", "Full Metal Crown pada gigi non-vital (fmc-rct)", R.drawable.fmcrct2detail);
        simbolDepan.add(simbolDepanGigi);
        simbolDepanGigi = new CustomObject(R.drawable.poc2, "poc2", "Porcleain crown pada gigi vital (poc)", R.drawable.poc2detail);
        simbolDepan.add(simbolDepanGigi);
        simbolDepanGigi = new CustomObject(R.drawable.pocrct2, "pocrct2", "Porcleain crown pada gigi non vital (poc-rct)", R.drawable.pocrct2detail);
        simbolDepan.add(simbolDepanGigi);
        simbolDepanGigi = new CustomObject(R.drawable.rrx2, "rrx2", "Sisa Akar (rrx)", R.drawable.rrx2detail);
        simbolDepan.add(simbolDepanGigi);
        simbolDepanGigi = new CustomObject(R.drawable.mis2, "mis2", "Gigi Hilang (mis)", R.drawable.mis2detail);
        simbolDepan.add(simbolDepanGigi);
        simbolDepanGigi = new CustomObject(R.drawable.ipx2, "ipx2", "Implant + Porcleain crown (ipx-poc)", R.drawable.ipx2detail);
        simbolDepan.add(simbolDepanGigi);
        simbolDepanGigi = new CustomObject(R.drawable.mebfull2, "mebdepan", "Full metal Brigde 3 Units", 0);
        simbolDepan.add(simbolDepanGigi);
        simbolDepanGigi = new CustomObject(R.drawable.pob2full, "pobdepan", "Porcelain bridge 4 units", 0);
        simbolDepan.add(simbolDepanGigi);
        simbolDepanGigi = new CustomObject(R.drawable.frmacr2, "frmacr2", "Partial Denture / Full Denture (frm-acr)", R.drawable.frmacr2detail);
        simbolDepan.add(simbolDepanGigi);
        simbolDepanGigi = new CustomObject(R.drawable.migrasikanan2, "migrasi2", "Migrasi / Version / Rotasi dibuat panah sesuai arah)", R.drawable.migrasikanan2detail);
        simbolDepan.add(simbolDepanGigi);


        CustomObject mebDepan1 = new CustomObject(R.drawable.fmc2, "meb12", "Full metal Brigde ", R.drawable.meb12);
        mebDepan.add(mebDepan1);
        mebDepan1 = new CustomObject(R.drawable.mebx2, "meb22", "Full metal Brigde ", R.drawable.meb22);
        mebDepan.add(mebDepan1);
        mebDepan1 = new CustomObject(R.drawable.fmc2, "meb32", "Full metal Brigde ", R.drawable.meb32);
        mebDepan.add(mebDepan1);


        CustomObject pobDepan1 = new CustomObject(R.drawable.poc2, "pob12", "Porcelain bridge", R.drawable.pob12);
        pobDepan.add(pobDepan1);
        pobDepan1 = new CustomObject(R.drawable.pobx2, "pob22", "Porcelain bridge", R.drawable.pob22);
        pobDepan.add(pobDepan1);
        pobDepan1 = new CustomObject(R.drawable.pobx2, "pob32", "Porcelain bridge", R.drawable.pob32);
        pobDepan.add(pobDepan1);
        pobDepan1 = new CustomObject(R.drawable.poc2, "pob42", "Porcelain bridge", R.drawable.pob42);
        pobDepan.add(pobDepan1);


    }

    public void bindToPost(final String perawatanKey, final Activity activity, final String noGigi) {
//
        this.context = context;
        this.noGigi = noGigi;
        this.perawatanKey = perawatanKey;
        System.out.println("NO GIGI = " + noGigi);
        String idPasien = util.getIdPasien(activity);
        System.out.println("idPasien history tindakan = " + idPasien);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        final DatabaseReference mTindakan = database.getReference().child("tindakan");
        final DatabaseReference mPhoto = database.getReference().child("photo");
        ivMore.setVisibility(View.GONE);
        mPhoto.keepSynced(true);
        final DatabaseReference mPerawatan = database.getReference().child("perawatan");
        mPerawatan.keepSynced(true);
        final DatabaseReference mRekamMedis = database.getReference().child("rekammedis").child(idPasien).child(noGigi).child(perawatanKey);
        mRekamMedis.keepSynced(true);

        final DatabaseReference mJadwal = database.getReference().child("jadwal");
        mJadwal.keepSynced(true);
        mRekamMedis.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                perawatanKeyOri = dataSnapshot.child("idPerawatan").getValue(String.class);
                System.out.println("ori = " + perawatanKeyOri);

                mRekamMedis.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String statusPerawatan = dataSnapshot.child("status").getValue(String.class);
                        tvStatusPerawatan.setText(statusPerawatan);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                mPerawatan.child(perawatanKeyOri).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String namaList = dataSnapshot.child("namaTindakan").getValue(String.class);
                        String kodeGigiList = dataSnapshot.child("kodeGigi").getValue(String.class);
                        String jadwalKey = dataSnapshot.child("jadwalKey").getValue(String.class);

                        mJadwal.child(jadwalKey).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Long timeStamp = dataSnapshot.child("timeStamp").getValue(Long.class);
                                System.out.println("timeStamp history = " + timeStamp);
                                String tanggal = getDate(timeStamp);
                                tvTanggal.setText(tanggal);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        tvDeskripsi.setText(namaList);

                        System.out.println("KodeGigiList = " + kodeGigiList);
                        if (kodeGigiList.equalsIgnoreCase("meb1") || kodeGigiList.equalsIgnoreCase("meb2") || kodeGigiList.equalsIgnoreCase("meb3")) {
                            kodeGigiList = "meb";
                        } else if (kodeGigiList.equalsIgnoreCase("pob1") || kodeGigiList.equalsIgnoreCase("pob2") || kodeGigiList.equalsIgnoreCase("pob3") || kodeGigiList.equalsIgnoreCase("pob4")) {
                            kodeGigiList = "pob";
                        } else if (kodeGigiList.equalsIgnoreCase("meb12") || kodeGigiList.equalsIgnoreCase("meb22") || kodeGigiList.equalsIgnoreCase("meb32")) {
                            kodeGigiList = "mebdepan";
                        } else if (kodeGigiList.equalsIgnoreCase("pob12") || kodeGigiList.equalsIgnoreCase("pob22") || kodeGigiList.equalsIgnoreCase("pob32") || kodeGigiList.equalsIgnoreCase("pob42")) {
                            kodeGigiList = "pobdepan";
                        }
                        int res = activity.getResources().getIdentifier(kodeGigiList, "drawable", activity.getPackageName());
                        System.out.println("RES = " + res);
                        ivGigi.setBackgroundResource(res);

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


        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                progressDialog = new ProgressDialog(activity);
                progressDialog.setMessage("Loading...");
                progressDialog.show();

                final AlertDialog.Builder alBuilder = new AlertDialog.Builder(activity);
                LayoutInflater inflater = LayoutInflater.from(activity);
                final View dialog = (View) inflater.inflate(R.layout.dialog_tambah_perawatan, null);
                alBuilder.setView(dialog);
                alBuilder.setTitle("Data Perawatan");
                final ImageView ivFotoPerawatan = (ImageView) dialog.findViewById(R.id.ivFotoPerawatan);
                final EditText etNama = (EditText) dialog.findViewById(R.id.etTindakan);
                final EditText acObatTindakan = (EditText) dialog.findViewById(R.id.acObatTindakan);
                final TextView tvUpdate = (TextView) dialog.findViewById(R.id.tvUpdate);
                final EditText etKeterangan = (EditText) dialog.findViewById(R.id.etKeterangan);
                final CheckBox cbAtas = (CheckBox) dialog.findViewById(R.id.cbAtas);
                final CheckBox cbBawah = (CheckBox) dialog.findViewById(R.id.cbBawah);
                final CheckBox cbTengah = (CheckBox) dialog.findViewById(R.id.cbTengah);
                final CheckBox cbKanan = (CheckBox) dialog.findViewById(R.id.cbKanan);
                final CheckBox cbKiri = (CheckBox) dialog.findViewById(R.id.cbKiri);
                final ImageView ivTambalAtas = (ImageView) dialog.findViewById(R.id.ivTambalAtas);
                final ImageView ivTambalBawah = (ImageView) dialog.findViewById(R.id.ivTambalBawah);
                final ImageView ivTambalKanan = (ImageView) dialog.findViewById(R.id.ivTambalKanan);
                final ImageView ivTambalKiri = (ImageView) dialog.findViewById(R.id.ivTambalKiri);
                final ImageView ivTambalAtas2 = (ImageView) dialog.findViewById(R.id.ivTambalAtas2);
                final ImageView ivTambalBawah2 = (ImageView) dialog.findViewById(R.id.ivTambalBawah2);
                final ImageView ivTambalKanan2 = (ImageView) dialog.findViewById(R.id.ivTambalKanan2);
                final ImageView ivTambalKiri2 = (ImageView) dialog.findViewById(R.id.ivTambalKiri2);
                final ImageView ivInsisal = (ImageView) dialog.findViewById(R.id.ivInsisal);
                final ImageView ivAddTindakan = (ImageView) dialog.findViewById(R.id.ivAddTindakan);
                final RecyclerView recyclerViewPhoto = (RecyclerView) dialog.findViewById(R.id.recycleViewPhoto);
                final ImageView ivPhoto = (ImageView) dialog.findViewById(R.id.ivPhoto);
                final ImageView ivLubang = (ImageView) dialog.findViewById(R.id.ivLubang);
                recyclerViewPhoto.setVisibility(View.VISIBLE);
                tvUpdate.setVisibility(View.GONE);
                ivAddTindakan.setVisibility(View.GONE);

                mAdapter = new FirebaseRecyclerAdapter<Perawatan, MainViewPhoto>(
                        Perawatan.class, R.layout.row_list_photo, MainViewPhoto.class, mPhoto.child(perawatanKeyOri)) {
                    @Override
                    protected void populateViewHolder(MainViewPhoto viewHolder, Perawatan model, int position) {
                        System.out.println("POSITION = " + position);
                        final DatabaseReference photoRef = getRef(position);
                        String photoKey = photoRef.getKey();
                        System.out.println("Photo Key " + photoKey);
                        viewHolder.bindToPost(model, perawatanKeyOri, photoKey, activity);


                    }
                };

                recyclerViewPhoto.setAdapter(mAdapter);
                ivUpload = (ImageView) dialog.findViewById(R.id.ivUpload);
                recyclerViewPhoto.setHasFixedSize(true);
                mManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
                recyclerViewPhoto.setLayoutManager(mManager);




                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference mPerawatan = database.getReference().child("perawatan");
                final DatabaseReference mTindakanPerawatan = database.getReference().child("tindakanperawatan");
                System.out.println("PERAWATAN KEY ITEM KLIK = " + perawatanKeyOri);

                System.out.println("JADWAL KEY ITEM KLIK = " + util.getIdJadwal(activity));

                final ArrayList<String> arrayList = new ArrayList<>();



                mPerawatan.child(perawatanKeyOri).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String nama = dataSnapshot.child("namaTindakan").getValue(String.class);
                        String keterangan = dataSnapshot.child("keterangan").getValue(String.class);
                        String gigiAtas = dataSnapshot.child("gigiAtas").getValue(String.class);
                        String gigiBawah = dataSnapshot.child("gigiBawah").getValue(String.class);
                        String gigiTengah = dataSnapshot.child("berlubang").getValue(String.class);
                        String gigiKanan = dataSnapshot.child("gigiKanan").getValue(String.class);
                        String gigiKiri = dataSnapshot.child("gigiKiri").getValue(String.class);
                        String jadwalKey = dataSnapshot.child("jadwalKey").getValue(String.class);
                        kodeGigi = dataSnapshot.child("kodeGigi").getValue(String.class);
//                        String tanggal = dataSnapshot.child("tanggal").getValue(String.class);

                        mTindakanPerawatan.child(jadwalKey).orderByChild("id_perawatan").equalTo(perawatanKeyOri).addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                                arrayList.add(dataSnapshot.child("tindakan").getValue(String.class));
                                System.out.println("array list = " + arrayList);
                                String tindakanList = TextUtils.join(",", arrayList);
                                acObatTindakan.setText(tindakanList);
                                acObatTindakan.setFocusable(false);
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



                        etNama.setText(nama);
                        etNama.setKeyListener(null);
                        etKeterangan.setText(keterangan);
                        etKeterangan.setKeyListener(null);

                        cbAtas.setClickable(false);
                        cbBawah.setClickable(false);
                        cbKanan.setClickable(false);
                        cbKiri.setClickable(false);
                        cbTengah.setClickable(false);


                        if (noGigi.equalsIgnoreCase("Gigi 13") || noGigi.equalsIgnoreCase("Gigi 12") || noGigi.equalsIgnoreCase("Gigi 11") ||
                                noGigi.equalsIgnoreCase("Gigi 21") || noGigi.equalsIgnoreCase("Gigi 22") || noGigi.equalsIgnoreCase("Gigi 23") ||
                                noGigi.equalsIgnoreCase("Gigi 53") || noGigi.equalsIgnoreCase("Gigi 52") || noGigi.equalsIgnoreCase("Gigi 51") ||
                                noGigi.equalsIgnoreCase("Gigi 61") || noGigi.equalsIgnoreCase("Gigi 62") || noGigi.equalsIgnoreCase("Gigi 63") ||
                                noGigi.equalsIgnoreCase("Gigi 83") || noGigi.equalsIgnoreCase("Gigi 82") || noGigi.equalsIgnoreCase("Gigi 81") ||
                                noGigi.equalsIgnoreCase("Gigi 71") || noGigi.equalsIgnoreCase("Gigi 72") || noGigi.equalsIgnoreCase("Gigi 73") ||
                                noGigi.equalsIgnoreCase("Gigi 43") || noGigi.equalsIgnoreCase("Gigi 42") || noGigi.equalsIgnoreCase("Gigi 41") ||
                                noGigi.equalsIgnoreCase("Gigi 31") || noGigi.equalsIgnoreCase("Gigi 32") || noGigi.equalsIgnoreCase("Gigi 33")) {
                            cbTengah.setText("Insisal");
                            if (gigiAtas.equalsIgnoreCase("tambalan")) {
                                ivTambalAtas2.setVisibility(View.VISIBLE);
                                cbAtas.setChecked(true);
                                atas = "tambalan";
                            }

                            if (gigiBawah.equalsIgnoreCase("tambalan")) {
                                ivTambalBawah2.setVisibility(View.VISIBLE);
                                cbBawah.setChecked(true);
                                bawah = "tambalan";
                            }
                            if (gigiTengah.equalsIgnoreCase("insisal")) {
                                ivInsisal.setVisibility(View.VISIBLE);
                                cbTengah.setChecked(true);
                                lubang = "insisal";
                            }
                            if (gigiKanan.equalsIgnoreCase("tambalan")) {
                                ivTambalKanan2.setVisibility(View.VISIBLE);
                                cbKanan.setChecked(true);
                                kanan = "tambalan";
                            }
                            if (gigiKiri.equalsIgnoreCase("tambalan")) {
                                ivTambalKiri2.setVisibility(View.VISIBLE);
                                cbKiri.setChecked(true);
                                kiri = "tambalan";
                            }

                        } else {
                            cbTengah.setText("Oklusal");
                            if (gigiAtas.equalsIgnoreCase("tambalan")) {
                                ivTambalAtas.setVisibility(View.VISIBLE);
                                cbAtas.setChecked(true);
                                atas = "tambalan";
                            }
                            if (gigiBawah.equalsIgnoreCase("tambalan")) {
                                ivTambalBawah.setVisibility(View.VISIBLE);
                                cbBawah.setChecked(true);
                                bawah = "tambalan";
                            }
                            if (gigiTengah.equalsIgnoreCase("berlubang")) {
                                ivLubang.setVisibility(View.VISIBLE);
                                cbTengah.setChecked(true);
                                lubang = "berlubang";
                            }
                            if (gigiKanan.equalsIgnoreCase("tambalan")) {
                                ivTambalKanan.setVisibility(View.VISIBLE);
                                cbKanan.setChecked(true);
                                kanan = "tambalan";
                            }
                            if (gigiKiri.equalsIgnoreCase("tambalan")) {
                                ivTambalKiri.setVisibility(View.VISIBLE);
                                cbKiri.setChecked(true);
                                kiri = "tambalan";
                            }


                        }

                        System.out.println("noGigi listPerawatan = "+noGigi);
                        if (noGigi.equalsIgnoreCase("Gigi 18") || noGigi.equalsIgnoreCase("Gigi 17") || noGigi.equalsIgnoreCase("Gigi 16") ||
                                noGigi.equalsIgnoreCase("Gigi 15") || noGigi.equalsIgnoreCase("Gigi 14") || noGigi.equalsIgnoreCase("Gigi 55") ||
                                noGigi.equalsIgnoreCase("Gigi 54")) {
                            System.out.println("tes Gigi 18");
                            cbAtas.setText("Bukal");
                            cbBawah.setText("Palatal");
                            cbKanan.setText("Mesial");
                            cbKiri.setText("Distal");
                        } else if (noGigi.equalsIgnoreCase("Gigi 13") || noGigi.equalsIgnoreCase("Gigi 12") || noGigi.equalsIgnoreCase("Gigi 11") ||
                                noGigi.equalsIgnoreCase("Gigi 51") || noGigi.equalsIgnoreCase("Gigi 52") || noGigi.equalsIgnoreCase("Gigi 53")) {
                            cbAtas.setText("Labial");
                            cbBawah.setText("Palatal");
                            cbKanan.setText("Mesial");
                            cbKiri.setText("Distal");

                        } else if (noGigi.equalsIgnoreCase("Gigi 24") || noGigi.equalsIgnoreCase("Gigi 25") || noGigi.equalsIgnoreCase("Gigi 26") ||
                                noGigi.equalsIgnoreCase("Gigi 27") || noGigi.equalsIgnoreCase("Gigi 28") || noGigi.equalsIgnoreCase("Gigi 64") ||
                                noGigi.equalsIgnoreCase("Gigi 65")) {
                            cbAtas.setText("Bukal");
                            cbBawah.setText("Palatal");
                            cbKanan.setText("Distal");
                            cbKiri.setText("Mesial");
                        } else if (noGigi.equalsIgnoreCase("Gigi 21") || noGigi.equalsIgnoreCase("Gigi 22") || noGigi.equalsIgnoreCase("Gigi 23") ||
                                noGigi.equalsIgnoreCase("Gigi 61") || noGigi.equalsIgnoreCase("Gigi 62") || noGigi.equalsIgnoreCase("Gigi 63")) {
                            cbAtas.setText("Labial");
                            cbBawah.setText("Palatal");
                            cbKanan.setText("Distal");
                            cbKiri.setText("Mesial");
                        }
                        else if (noGigi.equalsIgnoreCase("Gigi 48") || noGigi.equalsIgnoreCase("Gigi 47") || noGigi.equalsIgnoreCase("Gigi 46") ||
                                noGigi.equalsIgnoreCase("Gigi 45") || noGigi.equalsIgnoreCase("Gigi 44") || noGigi.equalsIgnoreCase("Gigi 84") ||
                                noGigi.equalsIgnoreCase("Gigi 85")) {
                            cbAtas.setText("Lingual");
                            cbBawah.setText("Bukal");
                            cbKanan.setText("Mesial");
                            cbKiri.setText("Distal");
                        } else if (noGigi.equalsIgnoreCase("Gigi 43") || noGigi.equalsIgnoreCase("Gigi 42") || noGigi.equalsIgnoreCase("Gigi 41") ||
                                noGigi.equalsIgnoreCase("Gigi 83") || noGigi.equalsIgnoreCase("Gigi 82") || noGigi.equalsIgnoreCase("Gigi 81")) {
                            cbAtas.setText("Lingual");
                            cbBawah.setText("Labial");
                            cbKanan.setText("Mesial");
                            cbKiri.setText("Distal");
                        } else if (noGigi.equalsIgnoreCase("Gigi 34") || noGigi.equalsIgnoreCase("Gigi 35") || noGigi.equalsIgnoreCase("Gigi 36") ||
                                noGigi.equalsIgnoreCase("Gigi 37") || noGigi.equalsIgnoreCase("Gigi 38") || noGigi.equalsIgnoreCase("Gigi 74") ||
                                noGigi.equalsIgnoreCase("Gigi 75")) {
                            cbAtas.setText("Lingual");
                            cbBawah.setText("Bukal");
                            cbKanan.setText("Distal");
                            cbKiri.setText("Mesial");
                        } else if (noGigi.equalsIgnoreCase("Gigi 31") || noGigi.equalsIgnoreCase("Gigi 32") || noGigi.equalsIgnoreCase("Gigi 33") ||
                                noGigi.equalsIgnoreCase("Gigi 71") || noGigi.equalsIgnoreCase("Gigi 72") || noGigi.equalsIgnoreCase("Gigi 73")) {
                            cbAtas.setText("Lingual");
                            cbBawah.setText("Labial");
                            cbKanan.setText("Distal");
                            cbKiri.setText("Mesial");
                        }


                        String imagename = kodeGigi + "detail";
                        if (kodeGigi.equalsIgnoreCase("meb1") || kodeGigi.equalsIgnoreCase("meb2") || kodeGigi.equalsIgnoreCase("meb3") || kodeGigi.equalsIgnoreCase("meb12") || kodeGigi.equalsIgnoreCase("meb22") || kodeGigi.equalsIgnoreCase("meb32")) {
                            imagename = kodeGigi;
                        } else if (kodeGigi.equalsIgnoreCase("pob1") || kodeGigi.equalsIgnoreCase("pob2") || kodeGigi.equalsIgnoreCase("pob3") || kodeGigi.equalsIgnoreCase("pob4") || kodeGigi.equalsIgnoreCase("pob12") || kodeGigi.equalsIgnoreCase("pob22") || kodeGigi.equalsIgnoreCase("pob32") || kodeGigi.equalsIgnoreCase("pob42")) {
                            imagename = kodeGigi;
                        }

                        int res = activity.getResources().getIdentifier(imagename, "drawable", activity.getPackageName());
                        System.out.println("RES = " + res);
                        ivFotoPerawatan.setBackgroundResource(res);
                        progressDialog.dismiss();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                alBuilder.setCancelable(true).setPositiveButton("Kembali", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                    }
                });
                alBuilder.setView(dialog);
                alBuilder.create();
                final AlertDialog alertDialog = alBuilder.show();
                alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


            }

        });


    }

    private String getDate(long timeStamp) {

        try {

            SimpleDateFormat dd = new SimpleDateFormat("dd");
            SimpleDateFormat MM = new SimpleDateFormat("MM");
            SimpleDateFormat yyyy = new SimpleDateFormat("yyyy");
            Date netDate = (new Date(timeStamp));
            String day = dd.format(netDate);
            String month = MM.format(netDate);
            String nameMonth = MONTHS[Integer.parseInt(month) - 1];
            String year = yyyy.format(netDate);
            String tanggal = day + " " + nameMonth + " " + year;
            return tanggal;
        } catch (Exception ex) {
            System.out.println("Log = " + ex);
            return "xx";
        }
    }


}
