package com.teddybrothers.co_teddy.dentist;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.teddybrothers.co_teddy.dentist.entity.Perawatan;
import com.teddybrothers.co_teddy.dentist.viewholder.MainViewHistoryTindakan;
import com.teddybrothers.co_teddy.dentist.viewholder.MainViewPhoto;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 */

public class ListPerawatan extends Fragment {

    public RekamMedisActivity mActivity;
    int textLenght = 0;
    ImageView ivUpload;
    EditText acObatTindakan;
    //GalleryPhotoStorage
    private Context context;
    boolean isClick = false;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    Uri imageUri;
    String photoUrl = "null";
    public static final String TAG = "ListPerawatan";
    private static final int GALERY_INTENT = 2;
    public String mCurrentPhotoPath, hargaTindakan, perawatanKeyOri, obat, update, perawatanKeyOri1;
    String perawatanKeyOri12, keyPushTindakanPerawatan;
    public String statusPerawatan;
    RecyclerView mRecyclerview;
    FirebaseRecyclerAdapter<Perawatan, MainViewHistoryTindakan> mAdapterPerawatan;
    LinearLayoutManager mManager;
    FirebaseRecyclerAdapter<Perawatan, PerawatanHolder> mAdapter;
    FirebaseRecyclerAdapter<Perawatan, MainViewPhoto> mAdapterPhoto;
    ProgressBar progressBarPerawatan;
    AlertDialog alertDialogUpdate;

    FirebaseDatabase mDatabase;
    ImageView ivGigiPerawatan, ivInsisal, ivLubang, ivTambalAtas, ivTambalBawah, ivTambalKanan, ivTambalKiri, ivTambalAtas2, ivTambalBawah2, ivTambalKanan2, ivTambalKiri2;
    public String perawatanKey, gigiAtas, gigiBawah, gigiKanan, gigiKiri, nama, keterangan, berlubang, save;

    ArrayList<CustomObject> listSimbolGigi = new ArrayList<CustomObject>();
    ArrayList<CustomObject> simbolDepan = new ArrayList<CustomObject>();
    ArrayList<CustomObject> mebDepan = new ArrayList<CustomObject>();
    ArrayList<CustomObject> pobDepan = new ArrayList<CustomObject>();
    ArrayList<CustomObject> meb = new ArrayList<CustomObject>();
    ArrayList<CustomObject> pob = new ArrayList<CustomObject>();
    ArrayList<CustomObject> tambal = new ArrayList<CustomObject>();

    final ArrayList<String> hargaTags = new ArrayList<String>();

    ArrayList<CustomObject> choice = new ArrayList<CustomObject>();

    CustomAdapter adapter;
    CustomObject listGigi, listMeb, listPob, listMeb2, listPob2;
    DatabaseReference mRoot, mUserRef, mRekamMedis, mPerawatan, mTindakan, mPhoto, mTindakanPerawatan, mJadwal, mInvoice;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    Button btnDate;
    TextView tvNoGigi, tvStatusData;
    public String atas = "null";
    public String bawah = "null";
    public String kanan = "null";
    public String kiri = "null";
    public String lubang = "null";

    CustomObject pilihan;
    public String mon1, noGigi, keyPerawatan, keyTindakan, kodeGigi, idPasien, jadwalKey, idDokter, today, currentTime;
    static final int CAMERA_PIC_REQUEST = 3;
    static final int TINDAKAN_PIC_REQUERST = 1;
    static final String TINDAKAN_KEY = "keyTindakan";
    static final String TINDAKAN_HARGA = "hargaTindakan";
    static final String TINDAKAN_NAMA = "namaTindakan";
    public static final String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agust", "Sept", "Okt", "Nov", "Des"};
    public Calendar cal1 = Calendar.getInstance();
    public int day1 = cal1.get(Calendar.DAY_OF_MONTH);
    public int month1 = cal1.get(Calendar.MONTH);
    public int year1 = cal1.get(Calendar.YEAR);
    Utilities util = new Utilities();
    public Calendar cal = Calendar.getInstance();
    public int day = cal.get(Calendar.DAY_OF_MONTH);
    public int month = cal.get(Calendar.MONTH);
    public int year = cal.get(Calendar.YEAR);
    public int hour = cal.get(Calendar.HOUR_OF_DAY);
    public int minute = cal.get(Calendar.MINUTE);
    ArrayAdapter<String> namaAdapter;
    String perawatanKey1, statusUser, statusIntent, nameTindakan, priceTindakan, idTindakan;
    String status = "Aktif";
    Long timeStamp, tanggalPerawatan;

    int previousLength;
    boolean backSpace,flags=true;

    public ListPerawatan() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.out.println("ON CREATE");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        System.out.println("ON CREATE VIEW");
        View root = inflater.inflate(R.layout.fragment_list_perawatan, container, false);
        idPasien = util.getIdPasien(mActivity);
        idDokter = util.getIdDokter(mActivity);
        jadwalKey = util.getIdJadwal(mActivity);
        Bundle bundle = getArguments();
        if (bundle != null) {
            noGigi = bundle.getString("nogigi");
            idPasien = bundle.getString("idPasien");
            statusIntent = bundle.getString("statusIntent");
        }


        int month = month1 + 1;
        today = day1 + "-" + month + "-" + year1;
        currentTime = hour + ":" + minute;

        System.out.println("Filter On Start Today = " + today);
        timeStamp = convertTimeStamp(today, currentTime);
        System.out.println("timeStamp current = " + timeStamp);

        statusUser = util.getStatus(mActivity);
        System.out.println("Bundle = " + noGigi + " " + idPasien);

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


        tvNoGigi = (TextView) root.findViewById(R.id.tvNoGigi);
        mRecyclerview = (RecyclerView) root.findViewById(R.id.recycleViewPerawatan);
        progressBarPerawatan = (ProgressBar) root.findViewById(R.id.progressBar);
        tvStatusData = (TextView) root.findViewById(R.id.tvStatusData);
        mRecyclerview.setHasFixedSize(true);
//        mRecyclerview.setNestedScrollingEnabled(true);
        mManager = new LinearLayoutManager(mActivity);
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecyclerview.setLayoutManager(mManager);

        final FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                final AlertDialog.Builder alBuilder = new AlertDialog.Builder(mActivity);
                LayoutInflater inflater = LayoutInflater.from(mActivity);
                final View dialog = (View) inflater.inflate(R.layout.dialog_tambah_perawatan, null);

                alBuilder.setView(dialog);
                alBuilder.setTitle("Tambah Perawatan");
                final EditText etNama = (EditText) dialog.findViewById(R.id.etTindakan);
                final RecyclerView recyclerViewPhoto = (RecyclerView) dialog.findViewById(R.id.recycleViewPhoto);
                final ImageView ivPhoto = (ImageView) dialog.findViewById(R.id.ivPhoto);
                ivUpload = (ImageView) dialog.findViewById(R.id.ivUpload);
                ivPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        attachPhoto();
                    }
                });
                recyclerViewPhoto.setHasFixedSize(true);
                mManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
                recyclerViewPhoto.setLayoutManager(mManager);
                acObatTindakan = (EditText) dialog.findViewById(R.id.acObatTindakan);
                final TextView tvUpdate = (TextView) dialog.findViewById(R.id.tvUpdate);
                final ImageView ivAddTindakan = (ImageView) dialog.findViewById(R.id.ivAddTindakan);
                final EditText etKeterangan = (EditText) dialog.findViewById(R.id.etKeterangan);
                final ArrayAdapter<String> autoComplete = new ArrayAdapter<String>(mActivity, android.R.layout.simple_list_item_1);

                ivAddTindakan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mActivity, TindakanActivity.class);
                        String status = "pilihTindakan";
                        intent.putExtra("pilihTindakan", status);
                        startActivityForResult(intent, TINDAKAN_PIC_REQUERST);
                    }
                });


                tvUpdate.setVisibility(View.GONE);
                mon1 = MONTHS[month1];
//                btnDate = (Button) dialog.findViewById(R.id.btnDate);
//                btnDate.setText(day1 + " " + mon1 + " " + year1);


                ivGigiPerawatan = (ImageView) dialog.findViewById(R.id.ivFotoPerawatan);
                final CheckBox cbAtas = (CheckBox) dialog.findViewById(R.id.cbAtas);
                final CheckBox cbBawah = (CheckBox) dialog.findViewById(R.id.cbBawah);
                final CheckBox cbTengah = (CheckBox) dialog.findViewById(R.id.cbTengah);
                final CheckBox cbKanan = (CheckBox) dialog.findViewById(R.id.cbKanan);
                final CheckBox cbKiri = (CheckBox) dialog.findViewById(R.id.cbKiri);
                final TextView tvTambalan = (TextView) dialog.findViewById(R.id.tvTambalan);
                ivTambalAtas = (ImageView) dialog.findViewById(R.id.ivTambalAtas);
                ivTambalBawah = (ImageView) dialog.findViewById(R.id.ivTambalBawah);
                ivTambalKanan = (ImageView) dialog.findViewById(R.id.ivTambalKanan);
                ivTambalKiri = (ImageView) dialog.findViewById(R.id.ivTambalKiri);
                //Arsir GigiDepan
                ivTambalAtas2 = (ImageView) dialog.findViewById(R.id.ivTambalAtas2);
                ivTambalBawah2 = (ImageView) dialog.findViewById(R.id.ivTambalBawah2);
                ivTambalKanan2 = (ImageView) dialog.findViewById(R.id.ivTambalKanan2);
                ivTambalKiri2 = (ImageView) dialog.findViewById(R.id.ivTambalKiri2);
                ivInsisal = (ImageView) dialog.findViewById(R.id.ivInsisal);
                ivLubang = (ImageView) dialog.findViewById(R.id.ivLubang);


                if (noGigi.equalsIgnoreCase("Gigi 13") || noGigi.equalsIgnoreCase("Gigi 12") || noGigi.equalsIgnoreCase("Gigi 11") ||
                        noGigi.equalsIgnoreCase("Gigi 21") || noGigi.equalsIgnoreCase("Gigi 22") || noGigi.equalsIgnoreCase("Gigi 23") ||
                        noGigi.equalsIgnoreCase("Gigi 53") || noGigi.equalsIgnoreCase("Gigi 52") || noGigi.equalsIgnoreCase("Gigi 51") ||
                        noGigi.equalsIgnoreCase("Gigi 61") || noGigi.equalsIgnoreCase("Gigi 62") || noGigi.equalsIgnoreCase("Gigi 63") ||
                        noGigi.equalsIgnoreCase("Gigi 83") || noGigi.equalsIgnoreCase("Gigi 82") || noGigi.equalsIgnoreCase("Gigi 81") ||
                        noGigi.equalsIgnoreCase("Gigi 71") || noGigi.equalsIgnoreCase("Gigi 72") || noGigi.equalsIgnoreCase("Gigi 73") ||
                        noGigi.equalsIgnoreCase("Gigi 43") || noGigi.equalsIgnoreCase("Gigi 42") || noGigi.equalsIgnoreCase("Gigi 41") ||
                        noGigi.equalsIgnoreCase("Gigi 31") || noGigi.equalsIgnoreCase("Gigi 32") || noGigi.equalsIgnoreCase("Gigi 33")) {
                    ivGigiPerawatan.setBackgroundResource(R.drawable.soudetaildepan);
                    cbTengah.setText("Insisal");

                    cbAtas.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                ivTambalAtas2.setVisibility(View.VISIBLE);
                                atas = "tambalan";
                            } else {
                                ivTambalAtas2.setVisibility(View.INVISIBLE);
                                atas = "null";
                            }
                        }
                    });

                    cbBawah.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                ivTambalBawah2.setVisibility(View.VISIBLE);
                                bawah = "tambalan";
                            } else {
                                ivTambalBawah2.setVisibility(View.INVISIBLE);
                                bawah = "null";
                            }
                        }
                    });

                    cbTengah.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                ivInsisal.setVisibility(View.VISIBLE);
                                lubang = "insisal";
                            } else {
                                ivInsisal.setVisibility(View.INVISIBLE);
                                lubang = "null";
                            }
                        }
                    });

                    cbKanan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                ivTambalKanan2.setVisibility(View.VISIBLE);
                                kanan = "tambalan";
                            } else {
                                ivTambalKanan2.setVisibility(View.INVISIBLE);
                                kanan = "null";
                            }
                        }
                    });

                    cbKiri.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                ivTambalKiri2.setVisibility(View.VISIBLE);
                                kiri = "tambalan";
                            } else {
                                ivTambalKiri2.setVisibility(View.INVISIBLE);
                                kiri = "null";
                            }
                        }
                    });
                } else {
                    ivGigiPerawatan.setBackgroundResource(R.drawable.soudetailbelakang);
                    cbTengah.setText("Oklusal");
                    cbAtas.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                ivTambalAtas.setVisibility(View.VISIBLE);
                                atas = "tambalan";
                            } else {
                                ivTambalAtas.setVisibility(View.INVISIBLE);
                                atas = "null";
                            }
                        }
                    });

                    cbBawah.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                ivTambalBawah.setVisibility(View.VISIBLE);
                                bawah = "tambalan";
                            } else {
                                ivTambalBawah.setVisibility(View.INVISIBLE);
                                bawah = "null";
                            }
                        }
                    });

                    cbTengah.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                ivLubang.setVisibility(View.VISIBLE);
                                lubang = "berlubang";
                            } else {
                                ivLubang.setVisibility(View.INVISIBLE);
                                lubang = "null";
                            }
                        }
                    });

                    cbKanan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                ivTambalKanan.setVisibility(View.VISIBLE);
                                kanan = "tambalan";
                            } else {
                                ivTambalKanan.setVisibility(View.INVISIBLE);
                                kanan = "null";
                            }
                        }
                    });

                    cbKiri.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                ivTambalKiri.setVisibility(View.VISIBLE);
                                kiri = "tambalan";
                            } else {
                                ivTambalKiri.setVisibility(View.INVISIBLE);
                                kiri = "null";
                            }
                        }
                    });
                }
//SET CB TEXT
                System.out.println("noGigi listPerawatan = " + noGigi);
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
                } else if (noGigi.equalsIgnoreCase("Gigi 48") || noGigi.equalsIgnoreCase("Gigi 47") || noGigi.equalsIgnoreCase("Gigi 46") ||
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


                ivGigiPerawatan.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        final ArrayList<CustomObject> listSimbolGigi2 = listGigiBelakang();
                        final ArrayList<CustomObject> simbolDepan = listGigiDepan();


                        final AlertDialog.Builder alBuilder = new AlertDialog.Builder(mActivity);
                        LayoutInflater inflater = LayoutInflater.from(mActivity);
                        final View dialog = (View) inflater.inflate(R.layout.activity_list_gigi, null);
                        final ListView lv = (ListView) dialog.findViewById(R.id.ListItem);
                        final TextView tvStatus = (TextView) dialog.findViewById(R.id.tvStatus);
                        lv.setTextFilterEnabled(true);
                        lv.setEmptyView(tvStatus);
                        final EditText etSearch = (EditText) dialog.findViewById(R.id.etSearch);
                        final ArrayList<String> listNama = new ArrayList<String>();

                        if (noGigi.equalsIgnoreCase("Gigi 13") || noGigi.equalsIgnoreCase("Gigi 12") || noGigi.equalsIgnoreCase("Gigi 11") ||
                                noGigi.equalsIgnoreCase("Gigi 21") || noGigi.equalsIgnoreCase("Gigi 22") || noGigi.equalsIgnoreCase("Gigi 23") ||
                                noGigi.equalsIgnoreCase("Gigi 53") || noGigi.equalsIgnoreCase("Gigi 52") || noGigi.equalsIgnoreCase("Gigi 51") ||
                                noGigi.equalsIgnoreCase("Gigi 61") || noGigi.equalsIgnoreCase("Gigi 62") || noGigi.equalsIgnoreCase("Gigi 63") ||
                                noGigi.equalsIgnoreCase("Gigi 83") || noGigi.equalsIgnoreCase("Gigi 82") || noGigi.equalsIgnoreCase("Gigi 81") ||
                                noGigi.equalsIgnoreCase("Gigi 71") || noGigi.equalsIgnoreCase("Gigi 72") || noGigi.equalsIgnoreCase("Gigi 73") ||
                                noGigi.equalsIgnoreCase("Gigi 43") || noGigi.equalsIgnoreCase("Gigi 42") || noGigi.equalsIgnoreCase("Gigi 41") ||
                                noGigi.equalsIgnoreCase("Gigi 31") || noGigi.equalsIgnoreCase("Gigi 32") || noGigi.equalsIgnoreCase("Gigi 33")) {
                            final CustomAdapter adapterNewDepan = new CustomAdapter(mActivity, R.layout.row_list_gigi, simbolDepan);
                            etSearch.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {

                                    adapterNewDepan.getFilter().filter(s.toString());
                                    System.out.println("ADAPTER textchanged= " + adapterNewDepan.getCount());

                                }

                                @Override
                                public void afterTextChanged(Editable s) {

                                }
                            });
                            lv.setAdapter(adapterNewDepan);

                        } else {
                            final CustomAdapter adapterNew = new CustomAdapter(mActivity, R.layout.row_list_gigi, listSimbolGigi2);
                            System.out.println("ADAPTER TERPILIH");
                            etSearch.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {

                                    adapterNew.getFilter().filter(s.toString());
                                    System.out.println("ADAPTER textchanged= " + adapterNew.getCount());

                                }

                                @Override
                                public void afterTextChanged(Editable s) {

                                }
                            });
                            lv.setAdapter(adapterNew);
                        }


                        System.out.println("LIST SIMBOL = " + listSimbolGigi2);
                        alBuilder.setView(dialog);
                        alBuilder.create();

                        final AlertDialog alertDialog1 = alBuilder.show();
                        alertDialog1.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        //List View Simbol GIGI
                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                alertDialog1.dismiss();
                                if (noGigi.equalsIgnoreCase("Gigi 13") || noGigi.equalsIgnoreCase("Gigi 12") || noGigi.equalsIgnoreCase("Gigi 11") ||
                                        noGigi.equalsIgnoreCase("Gigi 21") || noGigi.equalsIgnoreCase("Gigi 22") || noGigi.equalsIgnoreCase("Gigi 23") ||
                                        noGigi.equalsIgnoreCase("Gigi 53") || noGigi.equalsIgnoreCase("Gigi 52") || noGigi.equalsIgnoreCase("Gigi 51") ||
                                        noGigi.equalsIgnoreCase("Gigi 61") || noGigi.equalsIgnoreCase("Gigi 62") || noGigi.equalsIgnoreCase("Gigi 63") ||
                                        noGigi.equalsIgnoreCase("Gigi 83") || noGigi.equalsIgnoreCase("Gigi 82") || noGigi.equalsIgnoreCase("Gigi 81") ||
                                        noGigi.equalsIgnoreCase("Gigi 71") || noGigi.equalsIgnoreCase("Gigi 72") || noGigi.equalsIgnoreCase("Gigi 73") ||
                                        noGigi.equalsIgnoreCase("Gigi 43") || noGigi.equalsIgnoreCase("Gigi 42") || noGigi.equalsIgnoreCase("Gigi 41") ||
                                        noGigi.equalsIgnoreCase("Gigi 31") || noGigi.equalsIgnoreCase("Gigi 32") || noGigi.equalsIgnoreCase("Gigi 33")) {
                                    //GIGI DEPAN
                                    listGigi = simbolDepan.get(position);
                                    if (listGigi.getSingkatan().equalsIgnoreCase("meb2")) {
                                        tvTambalan.setVisibility(View.GONE);
                                        cbAtas.setVisibility(View.GONE);
                                        cbTengah.setVisibility(View.GONE);
                                        cbBawah.setVisibility(View.GONE);
                                        cbKanan.setVisibility(View.GONE);
                                        cbKiri.setVisibility(View.GONE);
                                        final AlertDialog.Builder alBuilder = new AlertDialog.Builder(mActivity);
                                        LayoutInflater inflater = LayoutInflater.from(mActivity);
                                        final View dialog = (View) inflater.inflate(R.layout.activity_list_gigi, null);
                                        final ListView lvMeb = (ListView) dialog.findViewById(R.id.ListItem);
                                        final CustomAdapter adapter = new CustomAdapter(mActivity, R.layout.row_list_gigi, mebDepan);

                                        alBuilder.setView(dialog);
                                        alBuilder.create();
                                        lvMeb.setAdapter(adapter);
                                        final AlertDialog alertDialog = alBuilder.show();
                                        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                                        lvMeb.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                alertDialog.dismiss();
                                                listMeb2 = mebDepan.get(position);
                                                ivGigiPerawatan.setBackgroundResource(listMeb2.getDetail());
                                                etNama.setText(listMeb2.getNama());
                                            }
                                        });

                                    } else if (listGigi.getSingkatan().equalsIgnoreCase("pob2")) {
                                        tvTambalan.setVisibility(View.GONE);
                                        cbAtas.setVisibility(View.GONE);
                                        cbBawah.setVisibility(View.GONE);
                                        cbTengah.setVisibility(View.GONE);
                                        cbKanan.setVisibility(View.GONE);
                                        cbKiri.setVisibility(View.GONE);
                                        final AlertDialog.Builder alBuilder = new AlertDialog.Builder(mActivity);
                                        LayoutInflater inflater = LayoutInflater.from(mActivity);
                                        final View dialog = (View) inflater.inflate(R.layout.activity_list_gigi, null);
                                        final ListView lvPob = (ListView) dialog.findViewById(R.id.ListItem);
                                        final CustomAdapter adapter = new CustomAdapter(mActivity, R.layout.row_list_gigi, pobDepan);


                                        alBuilder.setView(dialog);
                                        alBuilder.create();
                                        lvPob.setAdapter(adapter);
                                        final AlertDialog alertDialog = alBuilder.show();
                                        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                                        lvPob.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                alertDialog.dismiss();
                                                listPob2 = pobDepan.get(position);
                                                ivGigiPerawatan.setBackgroundResource(listPob2.getDetail());
                                                etNama.setText(listPob2.getNama());
                                            }
                                        });
                                    } else {
                                        ivGigiPerawatan.setBackgroundResource(listGigi.getDetail());
                                        etNama.setText(listGigi.getNama());
                                    }
                                } else {
                                    listGigi = listSimbolGigi2.get(position);


                                    if (listGigi.getSingkatan().equalsIgnoreCase("meb")) {
                                        tvTambalan.setVisibility(View.GONE);
                                        cbAtas.setVisibility(View.GONE);
                                        cbBawah.setVisibility(View.GONE);
                                        cbTengah.setVisibility(View.GONE);
                                        cbKanan.setVisibility(View.GONE);
                                        cbKiri.setVisibility(View.GONE);
                                        final AlertDialog.Builder alBuilder = new AlertDialog.Builder(mActivity);
                                        LayoutInflater inflater = LayoutInflater.from(mActivity);
                                        final View dialog = (View) inflater.inflate(R.layout.activity_list_gigi, null);
                                        final ListView lvMeb = (ListView) dialog.findViewById(R.id.ListItem);
                                        final CustomAdapter adapter = new CustomAdapter(mActivity, R.layout.row_list_gigi, meb);

                                        alBuilder.setView(dialog);
                                        alBuilder.create();
                                        lvMeb.setAdapter(adapter);
                                        final AlertDialog alertDialog = alBuilder.show();
                                        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                                        lvMeb.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                alertDialog.dismiss();
                                                listMeb = meb.get(position);
                                                ivGigiPerawatan.setBackgroundResource(listMeb.getDetail());
                                                etNama.setText(listMeb.getNama());
                                            }
                                        });

                                    } else if (listGigi.getSingkatan().equalsIgnoreCase("pob")) {
                                        tvTambalan.setVisibility(View.GONE);
                                        cbAtas.setVisibility(View.GONE);
                                        cbBawah.setVisibility(View.GONE);
                                        cbTengah.setVisibility(View.GONE);
                                        cbKanan.setVisibility(View.GONE);
                                        cbKiri.setVisibility(View.GONE);
                                        final AlertDialog.Builder alBuilder = new AlertDialog.Builder(mActivity);
                                        LayoutInflater inflater = LayoutInflater.from(mActivity);
                                        final View dialog = (View) inflater.inflate(R.layout.activity_list_gigi, null);
                                        final ListView lvPob = (ListView) dialog.findViewById(R.id.ListItem);
                                        final CustomAdapter adapter = new CustomAdapter(mActivity, R.layout.row_list_gigi, pob);


                                        alBuilder.setView(dialog);
                                        alBuilder.create();
                                        lvPob.setAdapter(adapter);
                                        final AlertDialog alertDialog = alBuilder.show();
                                        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                                        lvPob.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                alertDialog.dismiss();
                                                listPob = pob.get(position);
                                                ivGigiPerawatan.setBackgroundResource(listPob.getDetail());
                                                etNama.setText(listPob.getNama());
                                            }
                                        });
                                    } else {
                                        ivGigiPerawatan.setBackgroundResource(listGigi.getDetail());
                                        etNama.setText(listGigi.getNama());

                                    }
                                }


                            }
                        });
                        return false;
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
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                alertDialog.show();


                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mInvoice.child(jadwalKey).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() == null) {


                                    final String obatTindakan = acObatTindakan.getText().toString();
//                                    if (TextUtils.isEmpty(obatTindakan)) {
//                                        acObatTindakan.setError("Obat/Tindakan harus diisi!");
//                                    } else {


                                        gigiAtas = atas;
                                        gigiBawah = bawah;
                                        gigiKanan = kanan;
                                        gigiKiri = kiri;
                                        berlubang = lubang;

                                        try {
                                            if (listGigi.getSingkatan() != null) {
                                                kodeGigi = listGigi.getSingkatan();
                                                if (kodeGigi.equalsIgnoreCase("meb")) {
                                                    kodeGigi = listMeb.getSingkatan();
                                                } else if (kodeGigi.equalsIgnoreCase("pob")) {
                                                    kodeGigi = listPob.getSingkatan();
                                                } else if (kodeGigi.equalsIgnoreCase("meb2")) {
                                                    kodeGigi = listMeb2.getSingkatan();
                                                } else if (kodeGigi.equalsIgnoreCase("pob2")) {
                                                    kodeGigi = listPob2.getSingkatan();
                                                }
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        System.out.println("KODE GIGI = " + kodeGigi);

                                        if (noGigi.equalsIgnoreCase("Gigi 13") || noGigi.equalsIgnoreCase("Gigi 12") || noGigi.equalsIgnoreCase("Gigi 11") ||
                                                noGigi.equalsIgnoreCase("Gigi 21") || noGigi.equalsIgnoreCase("Gigi 22") || noGigi.equalsIgnoreCase("Gigi 23") ||
                                                noGigi.equalsIgnoreCase("Gigi 53") || noGigi.equalsIgnoreCase("Gigi 52") || noGigi.equalsIgnoreCase("Gigi 51") ||
                                                noGigi.equalsIgnoreCase("Gigi 61") || noGigi.equalsIgnoreCase("Gigi 62") || noGigi.equalsIgnoreCase("Gigi 63") ||
                                                noGigi.equalsIgnoreCase("Gigi 83") || noGigi.equalsIgnoreCase("Gigi 82") || noGigi.equalsIgnoreCase("Gigi 81") ||
                                                noGigi.equalsIgnoreCase("Gigi 71") || noGigi.equalsIgnoreCase("Gigi 72") || noGigi.equalsIgnoreCase("Gigi 73") ||
                                                noGigi.equalsIgnoreCase("Gigi 43") || noGigi.equalsIgnoreCase("Gigi 42") || noGigi.equalsIgnoreCase("Gigi 41") ||
                                                noGigi.equalsIgnoreCase("Gigi 31") || noGigi.equalsIgnoreCase("Gigi 32") || noGigi.equalsIgnoreCase("Gigi 33")) {

                                            if (kodeGigi == null) {
                                                kodeGigi = "sou2";
                                            }
                                        } else {

                                            if (kodeGigi == null) {
                                                kodeGigi = "sou";
                                            }

                                        }


                                        if (kodeGigi == null) {
                                            kodeGigi = "sou";
                                        }
                                        System.out.println("KODE GIGI 1 = " + kodeGigi);
                                        nama = etNama.getText().toString();
                                        keterangan = etKeterangan.getText().toString();
                                        String tindakan = acObatTindakan.getText().toString();
//                                        if (TextUtils.isEmpty(tindakan)) {
//                                            acObatTindakan.setError("Tindakan tidak boleh kosong");
//                                        } else
                                            if (TextUtils.isEmpty(nama)) {
                                            etNama.setError("Nama Tindakan tidak boleh kosong");
                                        } else {

                                            final List tindakanTags = new ArrayList<String>(Arrays.asList(tindakan.split(", ")));
                                            System.out.println("substring = " + tindakanTags);
                                            isClick = true;

                                            if (ivUpload.getDrawable() != null) {
                                                save = "ok";

                                                progressDialog = new ProgressDialog(mActivity);
                                                progressDialog.setMessage("Loading...");
                                                progressDialog.show();
                                                uploadFoto(tindakanTags);
                                                alertDialog.dismiss();
                                                System.out.println("CREATE PERAWATAN FOTO");
                                            } else {
                                                progressDialog = new ProgressDialog(mActivity);
                                                progressDialog.setMessage("Loading...");
                                                progressDialog.show();
                                                createPerawatan(noGigi, kodeGigi, nama, keyTindakan, keterangan, gigiAtas, gigiBawah, gigiKanan, gigiKiri, berlubang, jadwalKey, idPasien, tindakanTags);
                                                alertDialog.dismiss();
                                                System.out.println("CREATE PERAWATAN TANPA FOTO");
                                            }
                                        }




//                        final String obatTindakanFinal = obatTindakan.substring(obatTindakan.lastIndexOf(",")+1);


                                } else {
                                    Toast.makeText(mActivity, "Maaf invoice anda telah dibuat", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                });
            }


        });

        mRecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0)
                    fab.hide();
                else if (dy < 0)
                    fab.show();
            }
        });

        return root;
    }


    public void saveTindakanPerawatan(final List tindakanTags, final String keyPerawatan) {
        System.out.println("size tagssss 1 = " + tindakanTags.size());
        for (int i = 0; i <= tindakanTags.size() - 1; i++) {
            System.out.println("tindakantags 1 = " + String.valueOf(tindakanTags.get(i)));
            String nama = String.valueOf(tindakanTags.get(i));
            final int finalI = i;
            mTindakan.orderByChild("namaTindakan").equalTo(nama).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot tindakan : dataSnapshot.getChildren()) {
                        hargaTindakan = tindakan.child("hargaTindakan").getValue(String.class);
                        System.out.println("substring 2= " + tindakan.getValue());
                        hargaTags.add(hargaTindakan);
                        System.out.println("harga 2= " + hargaTindakan);
                        System.out.println("hargaTags  2= " + hargaTags);

                    }
                    System.out.println("hargaTags luar = " + hargaTags);
//                    util.setHarga(mActivity,hargaTags,"tindakan");
                    System.out.println("isClick luar = " + isClick);
                    if (isClick) {
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("tindakan", String.valueOf(tindakanTags.get(finalI)));
                        hashMap.put("id_perawatan", keyPerawatan);
                        hashMap.put("noGigi", noGigi);
                        if (hargaTags.size()>0)
                        {
                            hashMap.put("hargaTindakan", String.valueOf(hargaTags.get(finalI)));
                            mTindakanPerawatan.child(jadwalKey).push().setValue(hashMap);
                            if (update!=null)
                            {
                                alertDialogUpdate.dismiss();
                                Toast.makeText(mActivity, "Data perawatan berhasil diperbaharui", Toast.LENGTH_SHORT).show();
                            }

                            callRefreshFragment();
                            if (finalI==tindakanTags.size()-1)
                            {
                                System.out.println("isClick posisi terakhir= " + finalI);
                                isClick = false;
                            }
                        }
                        else
                        {
//                            Toast.makeText(mActivity,"Periksa kembali Obat/Tindakan",Toast.LENGTH_SHORT).show();
                            acObatTindakan.setError("Periksa Kembali");

                        }



                    }





                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        System.out.println("size tagssss 22 = " + tindakanTags.size());


    }

    private void createPerawatanPhoto(String noGigi, String kodeGigi, String nama, String obat, String keterangan, String gigiAtas, String gigiBawah, String gigiKanan, String gigiKiri, String berlubang, String jadwalKey, String idPasien, List tindakanTags) {


        Perawatan perawatan = new Perawatan(noGigi, kodeGigi, nama, obat, keterangan, gigiAtas, gigiBawah, gigiKanan, gigiKiri, berlubang, jadwalKey, idPasien, timeStamp);
        keyPerawatan = mPerawatan.push().getKey();
        System.out.println("key Perawatan create tindakan= " + keyPerawatan);
        HashMap<String, String> rekamMedis = new HashMap<>();
        rekamMedis.put("idPerawatan", keyPerawatan);
        rekamMedis.put("status", status);
        mRekamMedis.push().setValue(rekamMedis);
        mPerawatan.child(keyPerawatan).setValue(perawatan);
        mPhoto.child(keyPerawatan).push().child("url").setValue(photoUrl);


        saveTindakanPerawatan(tindakanTags, keyPerawatan);
        progressDialog.dismiss();
        callRefreshFragment();


    }

    private void createPerawatan(String noGigi, String kodeGigi, String nama, String obat, String keterangan, String gigiAtas, String gigiBawah, String gigiKanan, String gigiKiri, String berlubang, String jadwalKey, String idPasien, List tindakanTags) {

        Perawatan perawatan = new Perawatan(noGigi, kodeGigi, nama, obat, keterangan, gigiAtas, gigiBawah, gigiKanan, gigiKiri, berlubang, jadwalKey, idPasien, timeStamp);
        keyPerawatan = mPerawatan.push().getKey();
        System.out.println("key Perawatan create tindakan= " + keyPerawatan);
        System.out.println("harga tindakan= " + hargaTindakan);
        HashMap<String, String> rekamMedis = new HashMap<>();
        rekamMedis.put("idPerawatan", keyPerawatan);
        rekamMedis.put("status", status);
        mRekamMedis.push().setValue(rekamMedis);
        mPerawatan.child(keyPerawatan).setValue(perawatan);
        System.out.println("size = " + tindakanTags.size());


        saveTindakanPerawatan(tindakanTags, keyPerawatan);


        progressDialog.dismiss();
        callRefreshFragment();


    }

    private void updatePerawatan(String noGigi, String kodeGigi, String nama, String obat, String keterangan, String gigiAtas, String gigiBawah, String gigiKanan, String gigiKiri, String berlubang, String idPasien, List tindakanTags, Long tanggalPerawatan) {

        Perawatan perawatan = new Perawatan(noGigi, kodeGigi, nama, obat, keterangan, gigiAtas, gigiBawah, gigiKanan, gigiKiri, berlubang, jadwalKey, idPasien, tanggalPerawatan);
        System.out.println("key Perawatan perawatanKeyOri updatePerawatan= " + perawatanKeyOri12);
        System.out.println("harga tindakan updatePerawatan= " + hargaTindakan);
        mPerawatan.child(perawatanKeyOri12).setValue(perawatan);
        if (imageUri != null) {
            mPhoto.child(perawatanKeyOri12).push().child("url").setValue(photoUrl);
        }

        System.out.println("tindakanTags update = " + tindakanTags);
        saveTindakanPerawatan(tindakanTags, perawatanKeyOri12);
        progressDialog.dismiss();


    }


    public void onStart() {
        super.onStart();
        System.out.println("ON START CREATED");
        tvNoGigi.setText(noGigi);
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
        }
        mRoot = mDatabase.getReference();
        mUserRef = mRoot.child("users");
        mTindakan = mRoot.child("tindakan");
        mJadwal = mRoot.child("jadwal");
        mInvoice = mRoot.child("invoice");
        mPerawatan = mRoot.child("perawatan");
        mTindakanPerawatan = mRoot.child("tindakanperawatan");
        mRekamMedis = mRoot.child("rekammedis").child(idPasien).child(noGigi);
        mPhoto = mRoot.child("photo");
        mAuth = FirebaseAuth.getInstance();


        mAdapter = new FirebaseRecyclerAdapter<Perawatan, PerawatanHolder>(
                Perawatan.class, R.layout.row_list_history, PerawatanHolder.class, mRekamMedis) {
            @Override
            protected void populateViewHolder(final PerawatanHolder viewHolder, Perawatan model, final int position) {
                System.out.println("POSITION = " + position);
                final DatabaseReference perawatanRef = getRef(position);
                perawatanKey = perawatanRef.getKey();
                System.out.println("perawatanKey = " + perawatanKey);
                progressBarPerawatan.setVisibility(View.GONE);
                System.out.println("NO GIGI = " + noGigi);


                mRekamMedis.child(perawatanKey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        perawatanKeyOri = dataSnapshot.child("idPerawatan").getValue(String.class);
                        statusPerawatan = dataSnapshot.child("status").getValue(String.class);
                        viewHolder.tvStatusPerawatan.setText(statusPerawatan);
                        if (perawatanKeyOri != null) {
                            mPerawatan.child(perawatanKeyOri).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    String namaList = dataSnapshot.child("namaTindakan").getValue(String.class);
                                    String kodeGigiList = dataSnapshot.child("kodeGigi").getValue(String.class);
                                    String jadwalKeyPerawatan = dataSnapshot.child("jadwalKey").getValue(String.class);

                                    Long timeStamp = dataSnapshot.child("tanggal").getValue(Long.class);
                                    System.out.println("ori = " + perawatanKeyOri);

                                    viewHolder.tvTanggal.setText(getDate(timeStamp));
                                    System.out.println("jadwal KEY PERAWATAN = " + jadwalKeyPerawatan);

                                    viewHolder.tvDeskripsi.setText(namaList);

                                    System.out.println("KodeGigiList = " + kodeGigiList);
                                    if (kodeGigiList != null) {
                                        if (kodeGigiList.equalsIgnoreCase("meb1") || kodeGigiList.equalsIgnoreCase("meb2") || kodeGigiList.equalsIgnoreCase("meb3")) {
                                            kodeGigiList = "meb";
                                        } else if (kodeGigiList.equalsIgnoreCase("pob1") || kodeGigiList.equalsIgnoreCase("pob2") || kodeGigiList.equalsIgnoreCase("pob3") || kodeGigiList.equalsIgnoreCase("pob4")) {
                                            kodeGigiList = "pob";
                                        } else if (kodeGigiList.equalsIgnoreCase("meb12") || kodeGigiList.equalsIgnoreCase("meb22") || kodeGigiList.equalsIgnoreCase("meb32")) {
                                            kodeGigiList = "mebdepan";
                                        } else if (kodeGigiList.equalsIgnoreCase("pob12") || kodeGigiList.equalsIgnoreCase("pob22") || kodeGigiList.equalsIgnoreCase("pob32") || kodeGigiList.equalsIgnoreCase("pob42")) {
                                            kodeGigiList = "pobdepan";
                                        }


                                        Activity activity = mActivity;
                                        System.out.println("getActivity = " + activity);
                                        if (activity != null) {
                                            int res = activity.getResources().getIdentifier(kodeGigiList, "drawable", activity.getPackageName());
                                            System.out.println("RES = " + res);
                                            viewHolder.ivGigi.setBackgroundResource(res);
                                        }

                                    }


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
                rekamMedis(perawatanKey);


                System.out.println("POSITION LUAR = " + viewHolder.getAdapterPosition());

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {


                        final AlertDialog.Builder alBuilder = new AlertDialog.Builder(mActivity);
                        LayoutInflater inflater = LayoutInflater.from(mActivity);
                        final View dialog = (View) inflater.inflate(R.layout.dialog_tambah_perawatan, null);
                        alBuilder.setCancelable(false);

                        alBuilder.setView(dialog);
                        alBuilder.setTitle("Data Perawatan");
                        final ImageView ivFotoPerawatan = (ImageView) dialog.findViewById(R.id.ivFotoPerawatan);
                        final EditText etNama = (EditText) dialog.findViewById(R.id.etTindakan);
                        acObatTindakan = (EditText) dialog.findViewById(R.id.acObatTindakan);
                        final EditText etKeterangan = (EditText) dialog.findViewById(R.id.etKeterangan);
                        final CheckBox cbAtas = (CheckBox) dialog.findViewById(R.id.cbAtas);
                        final CheckBox cbBawah = (CheckBox) dialog.findViewById(R.id.cbBawah);
                        final CheckBox cbTengah = (CheckBox) dialog.findViewById(R.id.cbTengah);
                        final CheckBox cbKanan = (CheckBox) dialog.findViewById(R.id.cbKanan);
                        final CheckBox cbKiri = (CheckBox) dialog.findViewById(R.id.cbKiri);
                        final TextView tvUpdate = (TextView) dialog.findViewById(R.id.tvUpdate);
                        final TextView tvTambalan = (TextView) dialog.findViewById(R.id.tvTambalan);
                        final ImageView ivTambalAtas = (ImageView) dialog.findViewById(R.id.ivTambalAtas);
                        final ImageView ivTambalBawah = (ImageView) dialog.findViewById(R.id.ivTambalBawah);
                        final ImageView ivTambalKanan = (ImageView) dialog.findViewById(R.id.ivTambalKanan);
                        final ImageView ivTambalKiri = (ImageView) dialog.findViewById(R.id.ivTambalKiri);
                        final ImageView ivTambalAtas2 = (ImageView) dialog.findViewById(R.id.ivTambalAtas2);
                        final ImageView ivTambalBawah2 = (ImageView) dialog.findViewById(R.id.ivTambalBawah2);
                        final ImageView ivTambalKanan2 = (ImageView) dialog.findViewById(R.id.ivTambalKanan2);
                        final ImageView ivTambalKiri2 = (ImageView) dialog.findViewById(R.id.ivTambalKiri2);
                        final RecyclerView recyclerViewPhoto = (RecyclerView) dialog.findViewById(R.id.recycleViewPhoto);
                        final ImageView ivPhoto = (ImageView) dialog.findViewById(R.id.ivPhoto);
                        final ImageView ivAddTindakan = (ImageView) dialog.findViewById(R.id.ivAddTindakan);
                        recyclerViewPhoto.setVisibility(View.VISIBLE);
                        tvUpdate.setVisibility(View.VISIBLE);
                        ivAddTindakan.setVisibility(View.GONE);
                        ivUpload = (ImageView) dialog.findViewById(R.id.ivUpload);
//                        acObatTindakan.setOnKeyListener(new View.OnKeyListener() {
//                            @Override
//                            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
//
//                                if (keyEvent.getAction() == keyEvent.ACTION_DOWN)
//                                {
//                                    if(keyCode == KeyEvent.KEYCODE_DEL) {
//                                        String tindakan = acObatTindakan.getText().toString();
//                                        String result;
//                                        int lastIndex = tindakan.lastIndexOf(',');
//                                        if (lastIndex >= 0) {
//                                            result = tindakan.substring(0, tindakan.lastIndexOf(','));
//                                            System.out.println("cek tindakan last index = "+tindakan.lastIndexOf(','));
//                                        }
//                                        else
//                                        {
//                                            result = "";
//                                        }
//
//
//                                        acObatTindakan.setText(result);
//
//                                    }
//                                    return true;
//                                }
//
//
//                             return false;
//                            }
//                        });



                        acObatTindakan.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                previousLength = charSequence.length();
                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void afterTextChanged(Editable editable) {
                                backSpace = previousLength > editable.length();
                                System.out.println("cek backspace  = "+backSpace);

                                if (flags)
                                {
                                    if (backSpace && flags)
                                    {
                                        String tindakan = acObatTindakan.getText().toString();
                                        String result;
                                        int lastIndex = tindakan.lastIndexOf(',');
                                        System.out.println("cek backspace last index = "+lastIndex+" tindakan = "+tindakan);
                                        if (lastIndex >= 0) {
                                            result = tindakan.substring(0, tindakan.lastIndexOf(','));
                                            System.out.println("cek tindakan last index = "+tindakan.lastIndexOf(','));
                                            flags = false;
                                        }
                                        else
                                        {
                                            result = "";
                                        }


                                        acObatTindakan.setText(result);

                                    }
                                }
                                else
                                {
                                    if(backSpace)
                                    {
                                        flags=true;
                                    }
                                }



                            }
                        });



                        ivPhoto.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                attachPhoto();

                            }
                        });

                        final ImageView ivLubang = (ImageView) dialog.findViewById(R.id.ivLubang);
                        final ImageView ivInsisal = (ImageView) dialog.findViewById(R.id.ivInsisal);
//                        btnDate = (Button) dialog.findViewById(R.id.btnDate);


                        ivFotoPerawatan.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                final ArrayList<CustomObject> listSimbolGigi2 = listGigiBelakang();
                                final ArrayList<CustomObject> simbolDepan = listGigiDepan();


                                final AlertDialog.Builder alBuilder = new AlertDialog.Builder(mActivity);
                                LayoutInflater inflater = LayoutInflater.from(mActivity);
                                final View dialog = (View) inflater.inflate(R.layout.activity_list_gigi, null);
                                final ListView lv = (ListView) dialog.findViewById(R.id.ListItem);
                                final TextView tvStatus = (TextView) dialog.findViewById(R.id.tvStatus);
                                lv.setTextFilterEnabled(true);
                                lv.setEmptyView(tvStatus);
                                final EditText etSearch = (EditText) dialog.findViewById(R.id.etSearch);
                                final ArrayList<String> listNama = new ArrayList<String>();


                                if (noGigi.equalsIgnoreCase("Gigi 13") || noGigi.equalsIgnoreCase("Gigi 12") || noGigi.equalsIgnoreCase("Gigi 11") ||
                                        noGigi.equalsIgnoreCase("Gigi 21") || noGigi.equalsIgnoreCase("Gigi 22") || noGigi.equalsIgnoreCase("Gigi 23") ||
                                        noGigi.equalsIgnoreCase("Gigi 53") || noGigi.equalsIgnoreCase("Gigi 52") || noGigi.equalsIgnoreCase("Gigi 51") ||
                                        noGigi.equalsIgnoreCase("Gigi 61") || noGigi.equalsIgnoreCase("Gigi 62") || noGigi.equalsIgnoreCase("Gigi 63") ||
                                        noGigi.equalsIgnoreCase("Gigi 83") || noGigi.equalsIgnoreCase("Gigi 82") || noGigi.equalsIgnoreCase("Gigi 81") ||
                                        noGigi.equalsIgnoreCase("Gigi 71") || noGigi.equalsIgnoreCase("Gigi 72") || noGigi.equalsIgnoreCase("Gigi 73") ||
                                        noGigi.equalsIgnoreCase("Gigi 43") || noGigi.equalsIgnoreCase("Gigi 42") || noGigi.equalsIgnoreCase("Gigi 41") ||
                                        noGigi.equalsIgnoreCase("Gigi 31") || noGigi.equalsIgnoreCase("Gigi 32") || noGigi.equalsIgnoreCase("Gigi 33")) {
                                    final CustomAdapter adapterNewDepan = new CustomAdapter(mActivity, R.layout.row_list_gigi, simbolDepan);

                                    etSearch.addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                        }

                                        @Override
                                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                                            adapterNewDepan.getFilter().filter(s.toString());
                                            System.out.println("ADAPTER textchanged= " + adapterNewDepan.getCount());

                                        }

                                        @Override
                                        public void afterTextChanged(Editable s) {

                                        }
                                    });
                                    lv.setAdapter(adapterNewDepan);

                                } else {
                                    final CustomAdapter adapterNew = new CustomAdapter(mActivity, R.layout.row_list_gigi, listSimbolGigi2);
                                    System.out.println("ADAPTER TERPILIH");
                                    etSearch.addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                        }

                                        @Override
                                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                                            adapterNew.getFilter().filter(s.toString());
                                            System.out.println("ADAPTER textchanged= " + adapterNew.getCount());

                                        }

                                        @Override
                                        public void afterTextChanged(Editable s) {

                                        }
                                    });
                                    lv.setAdapter(adapterNew);
                                }


                                System.out.println("LIST SIMBOL = " + listSimbolGigi2);
                                alBuilder.setView(dialog);
                                alBuilder.create();

                                final AlertDialog alertDialog1 = alBuilder.show();
                                alertDialog1.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                                //List View Simbol GIGI
                                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                        alertDialog1.dismiss();
                                        if (noGigi.equalsIgnoreCase("Gigi 13") || noGigi.equalsIgnoreCase("Gigi 12") || noGigi.equalsIgnoreCase("Gigi 11") ||
                                                noGigi.equalsIgnoreCase("Gigi 21") || noGigi.equalsIgnoreCase("Gigi 22") || noGigi.equalsIgnoreCase("Gigi 23") ||
                                                noGigi.equalsIgnoreCase("Gigi 53") || noGigi.equalsIgnoreCase("Gigi 52") || noGigi.equalsIgnoreCase("Gigi 51") ||
                                                noGigi.equalsIgnoreCase("Gigi 61") || noGigi.equalsIgnoreCase("Gigi 62") || noGigi.equalsIgnoreCase("Gigi 63") ||
                                                noGigi.equalsIgnoreCase("Gigi 83") || noGigi.equalsIgnoreCase("Gigi 82") || noGigi.equalsIgnoreCase("Gigi 81") ||
                                                noGigi.equalsIgnoreCase("Gigi 71") || noGigi.equalsIgnoreCase("Gigi 72") || noGigi.equalsIgnoreCase("Gigi 73") ||
                                                noGigi.equalsIgnoreCase("Gigi 43") || noGigi.equalsIgnoreCase("Gigi 42") || noGigi.equalsIgnoreCase("Gigi 41") ||
                                                noGigi.equalsIgnoreCase("Gigi 31") || noGigi.equalsIgnoreCase("Gigi 32") || noGigi.equalsIgnoreCase("Gigi 33")) {
                                            //GIGI DEPAN
                                            listGigi = simbolDepan.get(position);
                                            if (listGigi.getSingkatan().equalsIgnoreCase("meb2")) {
                                                tvTambalan.setVisibility(View.GONE);
                                                cbAtas.setVisibility(View.GONE);
                                                cbBawah.setVisibility(View.GONE);
                                                cbTengah.setVisibility(View.GONE);
                                                cbKanan.setVisibility(View.GONE);
                                                cbKiri.setVisibility(View.GONE);
                                                final AlertDialog.Builder alBuilder = new AlertDialog.Builder(mActivity);
                                                LayoutInflater inflater = LayoutInflater.from(mActivity);
                                                final View dialog = (View) inflater.inflate(R.layout.activity_list_gigi, null);
                                                final ListView lvMeb = (ListView) dialog.findViewById(R.id.ListItem);
                                                final CustomAdapter adapter = new CustomAdapter(mActivity, R.layout.row_list_gigi, mebDepan);

                                                alBuilder.setView(dialog);
                                                alBuilder.create();
                                                lvMeb.setAdapter(adapter);
                                                final AlertDialog alertDialog = alBuilder.show();
                                                alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                                                lvMeb.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                    @Override
                                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                        alertDialog.dismiss();
                                                        listMeb = mebDepan.get(position);
                                                        ivFotoPerawatan.setBackgroundResource(listMeb.getDetail());
                                                        etNama.setText(listMeb.getNama());
                                                    }
                                                });

                                            } else if (listGigi.getSingkatan().equalsIgnoreCase("pob2")) {
                                                tvTambalan.setVisibility(View.GONE);
                                                cbAtas.setVisibility(View.GONE);
                                                cbBawah.setVisibility(View.GONE);
                                                cbTengah.setVisibility(View.GONE);
                                                cbKanan.setVisibility(View.GONE);
                                                cbKiri.setVisibility(View.GONE);
                                                final AlertDialog.Builder alBuilder = new AlertDialog.Builder(mActivity);
                                                LayoutInflater inflater = LayoutInflater.from(mActivity);
                                                final View dialog = (View) inflater.inflate(R.layout.activity_list_gigi, null);
                                                final ListView lvPob = (ListView) dialog.findViewById(R.id.ListItem);
                                                final CustomAdapter adapter = new CustomAdapter(mActivity, R.layout.row_list_gigi, pobDepan);


                                                alBuilder.setView(dialog);
                                                alBuilder.create();
                                                lvPob.setAdapter(adapter);
                                                final AlertDialog alertDialog = alBuilder.show();
                                                alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                                                lvPob.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                    @Override
                                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                        alertDialog.dismiss();
                                                        listPob = pobDepan.get(position);
                                                        ivFotoPerawatan.setBackgroundResource(listPob.getDetail());
                                                        etNama.setText(listPob.getNama());
                                                    }
                                                });
                                            } else {
                                                ivFotoPerawatan.setBackgroundResource(listGigi.getDetail());
                                                etNama.setText(listGigi.getNama());
                                            }
                                        } else {
                                            listGigi = listSimbolGigi2.get(position);
                                            if (listGigi.getSingkatan().equalsIgnoreCase("meb")) {
                                                tvTambalan.setVisibility(View.GONE);
                                                cbAtas.setVisibility(View.GONE);
                                                cbBawah.setVisibility(View.GONE);
                                                cbTengah.setVisibility(View.GONE);
                                                cbKanan.setVisibility(View.GONE);
                                                cbKiri.setVisibility(View.GONE);
                                                final AlertDialog.Builder alBuilder = new AlertDialog.Builder(mActivity);
                                                LayoutInflater inflater = LayoutInflater.from(mActivity);
                                                final View dialog = (View) inflater.inflate(R.layout.activity_list_gigi, null);
                                                final ListView lvMeb = (ListView) dialog.findViewById(R.id.ListItem);
                                                final CustomAdapter adapter = new CustomAdapter(mActivity, R.layout.row_list_gigi, meb);

                                                alBuilder.setView(dialog);
                                                alBuilder.create();
                                                lvMeb.setAdapter(adapter);
                                                final AlertDialog alertDialog = alBuilder.show();
                                                alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                                                lvMeb.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                    @Override
                                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                        alertDialog.dismiss();
                                                        listMeb = meb.get(position);
                                                        ivFotoPerawatan.setBackgroundResource(listMeb.getDetail());
                                                        etNama.setText(listMeb.getNama());
                                                    }
                                                });

                                            } else if (listGigi.getSingkatan().equalsIgnoreCase("pob")) {
                                                tvTambalan.setVisibility(View.GONE);
                                                cbAtas.setVisibility(View.GONE);
                                                cbBawah.setVisibility(View.GONE);
                                                cbTengah.setVisibility(View.GONE);
                                                cbKanan.setVisibility(View.GONE);
                                                cbKiri.setVisibility(View.GONE);
                                                final AlertDialog.Builder alBuilder = new AlertDialog.Builder(mActivity);
                                                LayoutInflater inflater = LayoutInflater.from(mActivity);
                                                final View dialog = (View) inflater.inflate(R.layout.activity_list_gigi, null);
                                                final ListView lvPob = (ListView) dialog.findViewById(R.id.ListItem);
                                                final CustomAdapter adapter = new CustomAdapter(mActivity, R.layout.row_list_gigi, pob);


                                                alBuilder.setView(dialog);
                                                alBuilder.create();
                                                lvPob.setAdapter(adapter);
                                                final AlertDialog alertDialog = alBuilder.show();
                                                alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                                                lvPob.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                    @Override
                                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                        alertDialog.dismiss();
                                                        listPob = pob.get(position);
                                                        ivFotoPerawatan.setBackgroundResource(listPob.getDetail());
                                                        etNama.setText(listPob.getNama());
                                                    }
                                                });
                                            } else {
                                                ivFotoPerawatan.setBackgroundResource(listGigi.getDetail());
                                                etNama.setText(listGigi.getNama());

                                            }
                                        }


                                    }
                                });
                                return false;
                            }


                        });

                        ivAddTindakan.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(mActivity, TindakanActivity.class);
                                String status = "pilihTindakan";
                                intent.putExtra("pilihTindakan", status);
                                startActivityForResult(intent, TINDAKAN_PIC_REQUERST);
                            }
                        });

                        System.out.println("POSITION VIEWHOLDER = " + position);
                        final DatabaseReference perawatanRef1 = getRef(position);
                        perawatanKey1 = perawatanRef1.getKey();
                        System.out.println("PERAWATAN KEY 1 = " + perawatanKey1);


                        final ArrayList<String> arrayList = new ArrayList<>();

                        mRekamMedis.child(perawatanKey1).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                perawatanKeyOri12 = dataSnapshot.child("idPerawatan").getValue(String.class);
                                System.out.println("PERAWATAN KEY ID PERAWATAN 1 = " + perawatanKeyOri12);
                                System.out.println("datasnapshot on change = " + dataSnapshot);

                                mPerawatan.child(perawatanKeyOri12).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String jadwalKeyPerawatan = dataSnapshot.child("jadwalKey").getValue(String.class);

                                        if (!jadwalKeyPerawatan.equalsIgnoreCase(jadwalKey)) {
                                            tvUpdate.setVisibility(View.GONE);
                                        }

                                        mTindakanPerawatan.child(jadwalKeyPerawatan).orderByChild("id_perawatan").equalTo(perawatanKeyOri12).addChildEventListener(new ChildEventListener() {
                                            @Override
                                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                                                arrayList.add(dataSnapshot.child("tindakan").getValue(String.class));
                                                keyPushTindakanPerawatan = dataSnapshot.getKey();
                                                System.out.println("keyPushTindakanPerawatan = " + keyPushTindakanPerawatan);
                                                System.out.println("array list = " + arrayList);
                                                String tindakanList = TextUtils.join(", ",arrayList);
                                                acObatTindakan.setText(tindakanList);
                                                acObatTindakan.setEnabled(false);


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

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });


                                tvUpdate.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        mInvoice.child(jadwalKey).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.getValue() == null) {
                                                    isClick = true;
                                                    acObatTindakan.setEnabled(true);
//                                                    acObatTindakan.setText("");


                                                    mTindakanPerawatan.child(jadwalKey).orderByChild("id_perawatan").equalTo(perawatanKeyOri12).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                                                System.out.println("datasnapshot value = " + data.getValue());
                                                                System.out.println("datasnapshot value2 = " + data.getKey());
                                                                mTindakanPerawatan.child(jadwalKey).child(data.getKey()).removeValue();
                                                            }
                                                            System.out.println("datasnapshot getref = " + dataSnapshot.getRef());
                                                            System.out.println("datasnapshot acobat = " + dataSnapshot);
                                                            System.out.println("datasnapshot acobatKey = " + dataSnapshot.getKey());
                                                            tvUpdate.setVisibility(View.GONE);
                                                            ivAddTindakan.setVisibility(View.VISIBLE);
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });
                                                } else {
                                                    Toast.makeText(mActivity, "Maaf invoice anda telah dibuat", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                                    }
                                });

                                mPerawatan.child(perawatanKeyOri12).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String nama = dataSnapshot.child("namaTindakan").getValue(String.class);

                                        String keterangan = dataSnapshot.child("keterangan").getValue(String.class);
                                        String gigiAtas = dataSnapshot.child("gigiAtas").getValue(String.class);
                                        String gigiBawah = dataSnapshot.child("gigiBawah").getValue(String.class);
                                        String gigiTengah = dataSnapshot.child("berlubang").getValue(String.class);
                                        String gigiKanan = dataSnapshot.child("gigiKanan").getValue(String.class);
                                        String gigiKiri = dataSnapshot.child("gigiKiri").getValue(String.class);
                                        tanggalPerawatan = dataSnapshot.child("tanggal").getValue(Long.class);
                                        kodeGigi = dataSnapshot.child("kodeGigi").getValue(String.class);


                                        etNama.setText(nama);
                                        etKeterangan.setText(keterangan);
//                                        btnDate.setText(tanggal);

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

                                            cbAtas.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                @Override
                                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                    if (isChecked) {
                                                        ivTambalAtas2.setVisibility(View.VISIBLE);
                                                        atas = "tambalan";
                                                    } else {
                                                        ivTambalAtas2.setVisibility(View.INVISIBLE);
                                                        atas = "null";
                                                    }
                                                }
                                            });

                                            cbBawah.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                @Override
                                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                    if (isChecked) {
                                                        ivTambalBawah2.setVisibility(View.VISIBLE);
                                                        bawah = "tambalan";
                                                    } else {
                                                        ivTambalBawah2.setVisibility(View.INVISIBLE);
                                                        bawah = "null";
                                                    }
                                                }
                                            });


                                            cbTengah.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                @Override
                                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                    if (isChecked) {
                                                        ivInsisal.setVisibility(View.VISIBLE);
                                                        lubang = "insisal";
                                                    } else {
                                                        ivInsisal.setVisibility(View.INVISIBLE);
                                                        lubang = "insisal";
                                                    }
                                                }
                                            });

                                            cbKanan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                @Override
                                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                    if (isChecked) {
                                                        ivTambalKanan2.setVisibility(View.VISIBLE);
                                                        kanan = "tambalan";
                                                    } else {
                                                        ivTambalKanan2.setVisibility(View.INVISIBLE);
                                                        kanan = "null";
                                                    }
                                                }
                                            });

                                            cbKiri.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                @Override
                                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                    if (isChecked) {
                                                        ivTambalKiri2.setVisibility(View.VISIBLE);
                                                        kiri = "tambalan";
                                                    } else {
                                                        ivTambalKiri2.setVisibility(View.INVISIBLE);
                                                        kiri = "null";
                                                    }
                                                }
                                            });
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

                                            cbAtas.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                @Override
                                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                    if (isChecked) {
                                                        ivTambalAtas.setVisibility(View.VISIBLE);
                                                        atas = "tambalan";
                                                    } else {
                                                        ivTambalAtas.setVisibility(View.INVISIBLE);
                                                        atas = "null";
                                                    }
                                                }
                                            });

                                            cbBawah.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                @Override
                                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                    if (isChecked) {
                                                        ivTambalBawah.setVisibility(View.VISIBLE);
                                                        bawah = "tambalan";
                                                    } else {
                                                        ivTambalBawah.setVisibility(View.INVISIBLE);
                                                        bawah = "null";
                                                    }
                                                }
                                            });

                                            cbTengah.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                @Override
                                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                    if (isChecked) {
                                                        ivLubang.setVisibility(View.VISIBLE);
                                                        lubang = "berlubang";
                                                    } else {
                                                        ivLubang.setVisibility(View.INVISIBLE);
                                                        lubang = "null";
                                                    }
                                                }
                                            });

                                            cbKanan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                @Override
                                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                    if (isChecked) {
                                                        ivTambalKanan.setVisibility(View.VISIBLE);
                                                        kanan = "tambalan";
                                                    } else {
                                                        ivTambalKanan.setVisibility(View.INVISIBLE);
                                                        kanan = "null";
                                                    }
                                                }
                                            });

                                            cbKiri.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                @Override
                                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                    if (isChecked) {
                                                        ivTambalKiri.setVisibility(View.VISIBLE);
                                                        kiri = "tambalan";
                                                    } else {
                                                        ivTambalKiri.setVisibility(View.INVISIBLE);
                                                        kiri = "null";
                                                    }
                                                }
                                            });
                                        }

                                        System.out.println("noGigi listPerawatan = " + noGigi);
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
                                        } else if (noGigi.equalsIgnoreCase("Gigi 21") || noGigi.equalsIgnoreCase("gigi22") || noGigi.equalsIgnoreCase("gigi23") ||
                                                noGigi.equalsIgnoreCase("Gigi 61") || noGigi.equalsIgnoreCase("gigi62") || noGigi.equalsIgnoreCase("gigi63")) {
                                            cbAtas.setText("Labial");
                                            cbBawah.setText("Palatal");
                                            cbKanan.setText("Distal");
                                            cbKiri.setText("Mesial");
                                        } else if (noGigi.equalsIgnoreCase("Gigi 48") || noGigi.equalsIgnoreCase("Gigi 47") || noGigi.equalsIgnoreCase("Gigi 46") ||
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

                                        int res = v.getContext().getResources().getIdentifier(imagename, "drawable", v.getContext().getPackageName());
                                        System.out.println("RES = " + res);
                                        ivFotoPerawatan.setBackgroundResource(res);


                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                                recyclerViewPhoto.setHasFixedSize(true);
                                mManager = new LinearLayoutManager(v.getContext(), LinearLayoutManager.HORIZONTAL, false);
                                recyclerViewPhoto.setLayoutManager(mManager);

                                mAdapterPhoto = new FirebaseRecyclerAdapter<Perawatan, MainViewPhoto>(
                                        Perawatan.class, R.layout.row_list_photo, MainViewPhoto.class, mPhoto.child(perawatanKeyOri12)) {
                                    @Override
                                    protected void populateViewHolder(MainViewPhoto viewHolder, Perawatan model, int position) {
                                        System.out.println("POSITION = " + position);
                                        final DatabaseReference photoRef = getRef(position);
                                        String photoKey = photoRef.getKey();
                                        System.out.println("Photo Key " + photoKey);
                                        viewHolder.bindToPost(model, perawatanKeyOri12, photoKey, mActivity);


                                    }
                                };
                                recyclerViewPhoto.setAdapter(mAdapterPhoto);


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                System.out.println("datasnapshot = " + databaseError);
                            }
                        });


                        alBuilder.setCancelable(true).setPositiveButton("Update", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).setNeutralButton("Inform Consent", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

                        alertDialogUpdate = alBuilder.create();
                        alertDialogUpdate.setCanceledOnTouchOutside(false);
                        alertDialogUpdate.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        alertDialogUpdate.show();
                        alertDialogUpdate.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {


                                mRekamMedis.child(perawatanKey1).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        perawatanKeyOri12 = dataSnapshot.child("idPerawatan").getValue(String.class);

                                        mPerawatan.child(perawatanKeyOri12).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                String jadwalKeyPerawatan = dataSnapshot.child("jadwalKey").getValue(String.class);
                                                if (jadwalKeyPerawatan.equalsIgnoreCase(jadwalKey)) {
                                                    mInvoice.child(jadwalKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            if (dataSnapshot.getValue() == null) {


                                                                gigiAtas = atas;
                                                                gigiBawah = bawah;
                                                                gigiKanan = kanan;
                                                                gigiKiri = kiri;
                                                                berlubang = lubang;


                                                                try {
                                                                    if (listGigi.getSingkatan() != null) {
                                                                        kodeGigi = listGigi.getSingkatan();
                                                                    }
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }


                                                                System.out.println("LIST GIGI LUAR = " + kodeGigi);


                                                                obat = acObatTindakan.getText().toString();
                                                                System.out.println("obat = " + obat);
                                                                if (TextUtils.isEmpty(obat)) {
                                                                    acObatTindakan.setError("Tindakan tidak boleh kosong");
                                                                } else {
                                                                    final List tindakanTags = new ArrayList<String>(Arrays.asList(obat.split(", ")));
                                                                    System.out.println("substring = " + tindakanTags);


                                                                    nama = etNama.getText().toString();
                                                                    keterangan = etKeterangan.getText().toString();
                                                                    System.out.println("KEY TINDAKAN sebelum upload = " + keyTindakan);
                                                                    if (imageUri != null) {
                                                                        progressDialog = new ProgressDialog(mActivity);
                                                                        progressDialog.setMessage("Loading...");
                                                                        progressDialog.show();
                                                                        save = "no";
                                                                        update = "Foto";
                                                                        uploadFoto(tindakanTags);
                                                                        alertDialogUpdate.dismiss();
                                                                        Toast.makeText(mActivity, "Data perawatan berhasil diperbaharui", Toast.LENGTH_SHORT).show();
                                                                    } else {
                                                                        progressDialog = new ProgressDialog(mActivity);
                                                                        progressDialog.setMessage("Loading...");
                                                                        progressDialog.show();
                                                                        update = "OK";
                                                                        updatePerawatan(noGigi, kodeGigi, nama, keyTindakan, keterangan, gigiAtas, gigiBawah, gigiKanan, gigiKiri, berlubang, idPasien, tindakanTags, tanggalPerawatan);
                                                                        System.out.println("LIST GIGI SINGKATAN = " + kodeGigi);
                                                                        System.out.println("Update PERAWATAN TANPA FOTO");

                                                                    }


                                                                }

                                                            } else {
                                                                Toast.makeText(mActivity, "Maaf invoice anda telah dibuat", Toast.LENGTH_SHORT).show();
                                                            }

                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });
                                                } else {
                                                    Toast.makeText(mActivity, "Maaf tidak dapat diperbaharui", Toast.LENGTH_SHORT).show();
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
                        });

                        alertDialogUpdate.getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                obat = acObatTindakan.getText().toString();
                                Intent intent = new Intent(mActivity,FormActivity.class);
                                intent.putExtra("idPasien",idPasien);
                                intent.putExtra("namaTindakan",obat);
                                System.out.println(" namaTindakan = "+obat+" idPasien = "+idPasien);
                                startActivity(intent);
                            }
                        });

                    }

                });


                if (statusUser.equalsIgnoreCase("Pasien") || statusUser.equalsIgnoreCase("Administrator")) {
                    viewHolder.ivMore.setVisibility(View.GONE);
                }

                viewHolder.ivMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println("POSITION ivMore = " + position);
                        final DatabaseReference perawatanRef1 = getRef(position);
                        final String perawatanKeyIvMore = perawatanRef1.getKey();


                        showPopupMenu(viewHolder.ivMore, position, perawatanKeyIvMore);

                    }
                });


            }
        };
        System.out.println("mRecyclerViewCount = " + mRecyclerview.getChildCount() + mAdapter.getItemCount());
        System.out.println("mAdapter = " + mRecyclerview.getChildCount());
        if (mRecyclerview.getChildCount() > 0) {
            tvStatusData.setVisibility(View.GONE);

        } else {
            tvStatusData.setVisibility(View.VISIBLE);

        }
        progressBarPerawatan.setVisibility(View.GONE);
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                mRecyclerview.smoothScrollToPosition(mAdapter.getItemCount());
                System.out.println("mAdapterDalem = " + mAdapter.getItemCount());
                System.out.println("itemCount = " + itemCount);
                System.out.println("positionStart = " + positionStart);
                if (mAdapter.getItemCount() > 0) {
                    tvStatusData.setVisibility(View.GONE);
                    progressBarPerawatan.setVisibility(View.GONE);
                }
            }
        });


        mRecyclerview.setAdapter(mAdapter);

        System.out.println("getAdapter = " + mRecyclerview.getAdapter().getItemCount());


    }


    public static class PerawatanHolder extends RecyclerView.ViewHolder {


        ImageView ivGigi, ivMore;
        TextView tvDeskripsi, tvTanggal, tvStatusPerawatan;


        public PerawatanHolder(final View itemView) {
            super(itemView);
            tvDeskripsi = (TextView) itemView.findViewById(R.id.tvNama);
            tvTanggal = (TextView) itemView.findViewById(R.id.tvTanggal);
            tvStatusPerawatan = (TextView) itemView.findViewById(R.id.tvStatusPerawatan);
            ivGigi = (ImageView) itemView.findViewById(R.id.thumb);
            ivMore = (ImageView) itemView.findViewById(R.id.ivMore);


        }


    }

    private void showPopupMenu(View view, int position, final String perawatanKeyIvMore) {
        // inflate menu

        final PopupMenu popup = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_list_perawatan, popup.getMenu());

        mRekamMedis.child(perawatanKeyIvMore).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("perawatanKeyIvMore = " + perawatanKeyIvMore);


                String status = dataSnapshot.child("status").getValue(String.class);

                if (status.equalsIgnoreCase("Aktif")) {
                    popup.getMenu().findItem(R.id.aktif).setVisible(false);
                } else if (status.equalsIgnoreCase("Non Aktif")) {
                    popup.getMenu().findItem(R.id.nonAktif).setVisible(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        popup.setOnMenuItemClickListener(new ListPerawatan.MenuItemClickListener(position, perawatanKeyIvMore));
        popup.show();

    }

    class MenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        private int position;
        private String perawatanKeyIvMore, perawatanKeyOri;

        public MenuItemClickListener(int position, String perawatanKeyIvMore) {
            this.position = position;
            this.perawatanKeyIvMore = perawatanKeyIvMore;


        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int id = item.getItemId();
            System.out.println("Position = " + position);
            System.out.println("perawatanKeyIvMore = " + perawatanKeyIvMore);


            if (id == R.id.aktif) {
                ubahStatus("Aktif", perawatanKeyIvMore);
            } else if (id == R.id.nonAktif) {
                ubahStatus("Non Aktif", perawatanKeyIvMore);
            }


            return false;
        }
    }

    public void ubahStatus(final String status, final String perawatanKeyIvMore) {
        AlertDialog.Builder alBuilder = new AlertDialog.Builder(mActivity);
        alBuilder.setMessage("Apakah Anda yakin untuk mengubah status perawatan menjadi " + status + "?");
        alBuilder.setTitle("Konfirmasi");
        final TextView input = new TextView(mActivity);
        alBuilder.setView(input);
        alBuilder.setCancelable(true).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                progressDialog = new ProgressDialog(mActivity);
                progressDialog.setMessage("Loading...");
                progressDialog.show();
                System.out.println("perawatanKey delete = " + perawatanKeyIvMore);
                mRekamMedis.child(perawatanKeyIvMore).child("status").setValue(status);
                progressDialog.dismiss();

                callRefreshFragment();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alBuilder.create();
        alertDialog.show();
    }


    public void callRefreshFragment() {
        FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        ListPerawatan mListPerawatan = new ListPerawatan();
//

        Bundle bundle = new Bundle();
        bundle.putString("nogigi", noGigi);
        bundle.putString("idPasien", idPasien);
        mListPerawatan.setArguments(bundle);
        fragmentManager.popBackStack();
        transaction.replace(R.id.frame_layout_left, mListPerawatan);
        transaction.addToBackStack(null);
        transaction.commit();

        progressDialog.dismiss();
    }


    public void openGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);

        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureDirectoryPath = pictureDirectory.getPath();

        Uri data = Uri.parse(pictureDirectoryPath);
        photoPickerIntent.setDataAndType(data, "image/*");
        startActivityForResult(photoPickerIntent, GALERY_INTENT);


    }

    public void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(mActivity.getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, CAMERA_PIC_REQUEST);
        }
    }

    void saveState(List tindakanTags) {


        System.out.println("update di savestate = " + update);
        System.out.println("keyTindakan = " + keyTindakan);

        String userId = mAuth.getCurrentUser().getUid();

        if (save.equalsIgnoreCase("ok")) {
            createPerawatanPhoto(noGigi, kodeGigi, nama, keyTindakan, keterangan, gigiAtas, gigiBawah, gigiKanan, gigiKiri, berlubang, jadwalKey, userId, tindakanTags);
            System.out.println("CREATE PERAWATAN FOTO");
        } else if (update.equalsIgnoreCase("Foto")) {
            System.out.println("UPDATE PERAWATAN FOTO");
            updatePerawatan(noGigi, kodeGigi, nama, keyTindakan, keterangan, gigiAtas, gigiBawah, gigiKanan, gigiKiri, berlubang, idPasien, tindakanTags, tanggalPerawatan);


            System.out.println("perawatanKeyOri di update = " + perawatanKeyOri12);


        }

    }

    private void uploadFoto(final List tindakanTags) {


        String path = "perawatan/" + UUID.randomUUID() + ".jpg";
        StorageReference eventref = storage.getReference(path);

        UploadTask uploadTask = eventref.putFile(imageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mActivity, "Upload Gagal", Toast.LENGTH_LONG).show();

            }
        }).addOnSuccessListener(mActivity, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(mActivity, "Foto berhasil di upload", Toast.LENGTH_LONG).show();

                @SuppressWarnings("VisibleForTests") Uri url = taskSnapshot.getDownloadUrl();
                photoUrl = url.toString();
                System.out.println("INI URL PHOTO!!!!" + photoUrl);
                saveState(tindakanTags);
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        System.out.println("RESULT CODE = " + resultCode + " " + "RequestCode = " + requestCode + " " + "RESULT OK = " + mActivity.RESULT_OK);

        if (requestCode == GALERY_INTENT && resultCode == mActivity.RESULT_OK) {
            //kalau foto berhasil masuk
            //alamat gambar di memori
            imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(getContext(), this);
            System.out.println("INI URI IMAGE: " + imageUri);
            mCurrentPhotoPath = imageUri.getPath();
            System.out.println("Path:" + mCurrentPhotoPath);


        } else if (requestCode == CAMERA_PIC_REQUEST && resultCode == mActivity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap image = (Bitmap) extras.get("data");
            imageUri = getImageUri(mActivity.getApplicationContext(), image);
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(getContext(), this);
            File finalFile = new File(getRealPathFromUri(imageUri));
            System.out.println("TEMP URI =" + imageUri);
        } else if (requestCode == TINDAKAN_PIC_REQUERST && data != null) {
            nameTindakan = data.getStringExtra(TINDAKAN_NAMA);
            priceTindakan = data.getStringExtra(TINDAKAN_HARGA);
            idTindakan = data.getStringExtra(TINDAKAN_KEY);

            if (TextUtils.isEmpty(acObatTindakan.getText().toString())) {
                acObatTindakan.setText(nameTindakan);
            } else {
                acObatTindakan.setText(acObatTindakan.getText().toString() + ", " + nameTindakan);
            }


//            btnPasien.setText(pasienName);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == mActivity.RESULT_OK) {
                imageUri = result.getUri();
                ivUpload.setImageURI(imageUri);
                ivUpload.setVisibility(View.VISIBLE);

                System.out.println("INI URI IMAGE CROP : " + imageUri);


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }


    }

    public Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        System.out.println("Path =" + path);
        return Uri.parse(path);
    }

    public String getRealPathFromUri(Uri uri) {
        Cursor cursor = mActivity.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);

    }

    public void rekamMedis(String key) {
        System.out.println("TEs = " + key);
        System.out.println("mRekamMedis = " + mRekamMedis);
        mRekamMedis.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                perawatanKeyOri1 = dataSnapshot.child("idPerawatan").getValue(String.class);
                System.out.println("datasnapshot in method RM = " + dataSnapshot);
                System.out.println("ori1 = " + perawatanKeyOri1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("datasnapshot = " + databaseError);
            }
        });
        System.out.println("TESI");
    }

    public void attachPhoto() {
        final AlertDialog.Builder alBuilder = new AlertDialog.Builder(mActivity);
        LayoutInflater inflater = LayoutInflater.from(mActivity);
        final View dialog = (View) inflater.inflate(R.layout.dialog_foto, null);
        final ListView lv = (ListView) dialog.findViewById(R.id.ListItem);

        final CustomAdapter adapter = new CustomAdapter(mActivity, R.layout.row_list_pilihan, choice);


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


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (mActivity == null && context instanceof RekamMedisActivity) {
            System.out.println("ON ATTACH LIST PERAWATAN");
            mActivity = (RekamMedisActivity) context;
        }

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


    public ArrayList<CustomObject> listGigiBelakang()
    {
        final ArrayList<CustomObject> listSimbolGigi2 = new ArrayList<CustomObject>();
        CustomObject simbol = new CustomObject(R.drawable.car, "car", "Tambalan Sementara / Caries (Car)", R.drawable.cardetail);
        listSimbolGigi2.add(simbol);
        simbol = new CustomObject(R.drawable.rrx, "rrx", "Sisa Akar (rrx)", R.drawable.rrxdetail);
        listSimbolGigi2.add(simbol);
        simbol = new CustomObject(R.drawable.non, "non", "Missing Tooth", R.drawable.nondetail);
        listSimbolGigi2.add(simbol);
        simbol = new CustomObject(R.drawable.amf, "amf", "Tambalan Amalgam (AMF)", R.drawable.amfdetail);
        listSimbolGigi2.add(simbol);
        simbol = new CustomObject(R.drawable.cof, "cof", "Tambalan Composite (Cof)", R.drawable.cofdetail);
        listSimbolGigi2.add(simbol);
        simbol = new CustomObject(R.drawable.fis, "fis", "Pit dan Fissure Sealant (Fis)", R.drawable.fisdetail);
        listSimbolGigi2.add(simbol);
        simbol = new CustomObject(R.drawable.nvt, "nvt", "Gigi non-vital", R.drawable.nvtdetail);
        listSimbolGigi2.add(simbol);
        simbol = new CustomObject(R.drawable.rct, "rct", "Perawatan Saluran Akar (rct)", R.drawable.rctdetail);
        listSimbolGigi2.add(simbol);
        simbol = new CustomObject(R.drawable.une, "une", "Un-Erupted (une)", R.drawable.unedetail);
        listSimbolGigi2.add(simbol);
        simbol = new CustomObject(R.drawable.pre, "pre", "Partial Erupt (Pre)", R.drawable.predetail);
        listSimbolGigi2.add(simbol);
        simbol = new CustomObject(R.drawable.sou, "sou", "Normal / Baik (Sou)", R.drawable.soudetail);
        listSimbolGigi2.add(simbol);
        simbol = new CustomObject(R.drawable.ano, "ano", "Anomali (Ano)", R.drawable.anodetail);
        listSimbolGigi2.add(simbol);
        simbol = new CustomObject(R.drawable.cfr, "cfr", "Fracture", R.drawable.cfrdetail);
        listSimbolGigi2.add(simbol);
        simbol = new CustomObject(R.drawable.amfrct, "amfrct", "Anomali (Ano)", R.drawable.amfrctdetail);
        listSimbolGigi2.add(simbol);
        simbol = new CustomObject(R.drawable.fmc, "fmc", "Full Metal Crown pada gigi vital (fmc)", R.drawable.fmcdetail);
        listSimbolGigi2.add(simbol);
        simbol = new CustomObject(R.drawable.fmcrct, "fmcrct", "Full Metal Crown pada gigi non-vital (fmc-rct)", R.drawable.fmcrctdetail);
        listSimbolGigi2.add(simbol);
        simbol = new CustomObject(R.drawable.poc, "poc", "Porcleain crown pada gigi vital (poc)", R.drawable.pocdetail);
        listSimbolGigi2.add(simbol);
        simbol = new CustomObject(R.drawable.pocrct, "pocrct", "Porcleain crown pada gigi non vital (poc-rct)", R.drawable.pocrctdetail);
        listSimbolGigi2.add(simbol);
        simbol = new CustomObject(R.drawable.mis, "mis", "Gigi Hilang (mis)", R.drawable.misdetail);
        listSimbolGigi2.add(simbol);
        simbol = new CustomObject(R.drawable.ipx, "ipx", "Implant + Porcleain crown (ipx-poc)", R.drawable.ipxdetail);
        listSimbolGigi2.add(simbol);
        simbol = new CustomObject(R.drawable.ipx, "ipx", "Implant + Porcleain crown (ipx-poc)", R.drawable.ipxdetail);
        listSimbolGigi2.add(simbol);
        simbol = new CustomObject(R.drawable.mebfull, "meb", "Full metal Brigde 3 Units", 0);
        listSimbolGigi2.add(simbol);
        simbol = new CustomObject(R.drawable.pobfull, "pob", "Porcelain bridge 4 units", 0);
        listSimbolGigi2.add(simbol);
        simbol = new CustomObject(R.drawable.frmacr, "frmacr", "Partial Denture / Full Denture (frm-acr)", R.drawable.frmacrdetail);
        listSimbolGigi2.add(simbol);
        simbol = new CustomObject(R.drawable.migrasikanan, "migrasikanan", "Migrasi / Version / Rotasi (Kanan)", R.drawable.migrasikanandetail);
        listSimbolGigi2.add(simbol);
        simbol = new CustomObject(R.drawable.migrasikiri, "migrasikiri", "Migrasi / Version / Rotasi (Kiri)", R.drawable.migrasikiridetail);
        listSimbolGigi2.add(simbol);

        return listSimbolGigi2;
    }

    private ArrayList<CustomObject> listGigiDepan()
    {
        final ArrayList<CustomObject> simbolDepan = new ArrayList<CustomObject>();
        CustomObject simbolDepanGigi = new CustomObject(R.drawable.rrx2, "rrx2", "Sisa Akar (rrx)", R.drawable.rrx2detail);
        simbolDepan.add(simbolDepanGigi);
        simbolDepanGigi = new CustomObject(R.drawable.car2, "car2", "Tambalan Sementara / Caries", R.drawable.cardetail2);
        simbolDepan.add(simbolDepanGigi);
        simbolDepanGigi = new CustomObject(R.drawable.non2, "non2", "Missing Tooth", R.drawable.non2detail);
        simbolDepan.add(simbolDepanGigi);
        simbolDepanGigi = new CustomObject(R.drawable.mis2, "mis2", "Gigi Hilang (mis)", R.drawable.mis2detail);
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
        simbolDepanGigi = new CustomObject(R.drawable.ipx2, "ipx2", "Implant + Porcleain crown (ipx-poc)", R.drawable.ipx2detail);
        simbolDepan.add(simbolDepanGigi);
        simbolDepanGigi = new CustomObject(R.drawable.mebfull2, "meb2", "Full metal Brigde 3 Units", 0);
        simbolDepan.add(simbolDepanGigi);
        simbolDepanGigi = new CustomObject(R.drawable.pob2full, "pob2", "Porcelain bridge 4 units", 0);
        simbolDepan.add(simbolDepanGigi);
        simbolDepanGigi = new CustomObject(R.drawable.frmacr2, "frmacr2", "Partial Denture / Full Denture (frm-acr)", R.drawable.frmacr2detail);
        simbolDepan.add(simbolDepanGigi);
        simbolDepanGigi = new CustomObject(R.drawable.migrasikanan2, "migrasikanan2", "Migrasi / Version / Rotasi (Kanan)", R.drawable.migrasikanan2detail);
        simbolDepan.add(simbolDepanGigi);
        simbolDepanGigi = new CustomObject(R.drawable.migrasikiri2, "migrasikiri2", "Migrasi / Version / Rotasi (Kiri)", R.drawable.migrasikiri2detail);
        simbolDepan.add(simbolDepanGigi);

        return simbolDepan;
    }
}
