package com.teddybrothers.co_teddy.dentist;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.teddybrothers.co_teddy.dentist.entity.Dokter;
import com.teddybrothers.co_teddy.dentist.entity.Perawatan;
import com.teddybrothers.co_teddy.dentist.viewholder.MainViewHistoryTindakan;


public class Odontogram extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public RekamMedisActivity mActivity;
    ProgressBar pbTtd;
    String perawatanKey;
    RecyclerView mRecyclerview;
    FirebaseRecyclerAdapter<Perawatan, MainViewHistoryTindakan> mAdapterPerawatan;
    LinearLayoutManager mManager;
    Bitmap bitmap;
    FrameLayout flOdontogram;
    Boolean isClick = true;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    String ttdUrl = "null";


    TextView tvPerawatanLain, tvNamaDokter, tvTanggalPemeriksaan;
    FirebaseDatabase mDatabase;
    DatabaseReference mRoot, mUserRef, mRekamMedis, mPerawatan, mDokter, mJadwal,mPasien;
    FirebaseAuth mAuth;
    public String noGigi, statusPerawatan, idPerawatan;
    public int id;
    EditText etSearch;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private SignaturePad mSignaturePad;
    ImageView ivSignaturePad;
    CustomObject data[];
    public String idDokter, idPasien, jadwalKey;


    private Button mClearButton;
    private Button mSaveButton;
    ProgressDialog progressDialog;
    Utilities util = new Utilities();

    CustomObject object;
    int textLenght = 0;
    String status = "profil";

    public Odontogram() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_odontogram, container, false);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.show();



        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
        }



        mRoot = mDatabase.getReference();
        mUserRef = mRoot.child("users");
        mJadwal = mRoot.child("jadwal");
        mRekamMedis = mRoot.child("rekammedis");
        mPerawatan = mRoot.child("perawatan");
        mDokter = mRoot.child("dokter");
        mAuth = FirebaseAuth.getInstance();


        ivSignaturePad = (ImageView) root.findViewById(R.id.ivSignaturePad);
        flOdontogram = (FrameLayout) root.findViewById(R.id.flOdontogram);
        pbTtd = (ProgressBar) root.findViewById(R.id.pbTtd);
        tvNamaDokter = (TextView) root.findViewById(R.id.tvNamaDokter);
        tvTanggalPemeriksaan = (TextView) root.findViewById(R.id.tvTanggalPemeriksaan);

        idPasien = util.getIdPasien(getActivity());
        idDokter = util.getIdDokter(getActivity());
        jadwalKey = util.getIdJadwal(getActivity());

        Bundle bundle = getArguments();
        idPasien = bundle.getString("idPasien");
        status = bundle.getString("status");

        System.out.println("status odontogram = " + status);
        System.out.println("idPasien odontogram 2 = " + idPasien);

        SharedPreferences detailGigi = getActivity().getSharedPreferences("detailPreference", Context.MODE_PRIVATE);
        detailGigi.edit().clear().commit();

        if (status.equalsIgnoreCase("profil")) {
            ivSignaturePad.setVisibility(View.GONE);
            tvNamaDokter.setVisibility(View.GONE);
            tvTanggalPemeriksaan.setVisibility(View.GONE);
            pbTtd.setVisibility(View.GONE);
        } else if (status.equalsIgnoreCase("detail")) {
            mDokter.child(idDokter).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Dokter dokter = dataSnapshot.getValue(Dokter.class);
                    tvNamaDokter.setText("drg. " + dokter.getNama());
                    String ttdDokter = dokter.getTtdUrl();
                    System.out.println("ttdDokter= " + ttdDokter);

                    Glide.with(getActivity()).load(ttdDokter)
                            .thumbnail(0.5f)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(ivSignaturePad);
                    pbTtd.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            mJadwal.child(jadwalKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String tanggal = dataSnapshot.child("tanggal").getValue(String.class);
                    tvTanggalPemeriksaan.setText(tanggal);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


        for (int i = 11; i<=18; i++)
        {
            int btnId = getResources().getIdentifier("gigi"+i,"id",getActivity().getPackageName());
            final ImageButton btnGigi = root.findViewById(btnId);
            odontogram(btnGigi,"Gigi "+i);
            final int finalI = i;
            btnGigi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PerawatanGigi("Gigi "+ finalI);



                }
            });

        }

        for (int i = 21; i<=28; i++)
        {
            int btnId = getResources().getIdentifier("gigi"+i,"id",getActivity().getPackageName());
            ImageButton btnGigi = root.findViewById(btnId);
            odontogram(btnGigi,"Gigi "+i);
            final int finalI = i;
            btnGigi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PerawatanGigi("Gigi "+ finalI);
                }
            });

        }


        for (int i = 51; i<=55; i++)
        {
            int btnId = getResources().getIdentifier("gigi"+i,"id",getActivity().getPackageName());
            ImageButton btnGigi = root.findViewById(btnId);
            odontogram(btnGigi,"Gigi "+i);
            final int finalI = i;
            btnGigi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PerawatanGigi("Gigi "+ finalI);
                }
            });

        }

        for (int i = 61; i<=65; i++)
        {
            int btnId = getResources().getIdentifier("gigi"+i,"id",getActivity().getPackageName());
            ImageButton btnGigi = root.findViewById(btnId);
            odontogram(btnGigi,"Gigi "+i);
            final int finalI = i;
            btnGigi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PerawatanGigi("Gigi "+ finalI);
                }
            });

        }


        for (int i = 81; i<=85; i++)
        {
            int btnId = getResources().getIdentifier("gigi"+i,"id",getActivity().getPackageName());
            ImageButton btnGigi = root.findViewById(btnId);
            odontogram(btnGigi,"Gigi "+i);
            final int finalI = i;
            btnGigi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PerawatanGigi("Gigi "+ finalI);
                }
            });

        }

        for (int i = 71; i<=75; i++)
        {
            int btnId = getResources().getIdentifier("gigi"+i,"id",getActivity().getPackageName());
            ImageButton btnGigi = root.findViewById(btnId);
            odontogram(btnGigi,"Gigi "+i);
            final int finalI = i;
            btnGigi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PerawatanGigi("Gigi "+ finalI);
                }
            });

        }


        for (int i = 41; i<=48; i++)
        {
            int btnId = getResources().getIdentifier("gigi"+i,"id",getActivity().getPackageName());
            ImageButton btnGigi = root.findViewById(btnId);
            odontogram(btnGigi,"Gigi "+i);
            final int finalI = i;
            btnGigi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PerawatanGigi("Gigi "+ finalI);
                }
            });

        }

        for (int i = 31; i<=38; i++)
        {
            int btnId = getResources().getIdentifier("gigi"+i,"id",getActivity().getPackageName());
            final ImageButton btnGigi = root.findViewById(btnId);
            odontogram(btnGigi,"Gigi "+i);
            final int finalI = i;
            btnGigi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PerawatanGigi("Gigi "+ finalI);
                }
            });





        }

//        Button gigi = root.findViewById(R.id.gigi);
//
//        gigi.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//                    // change color
//                    view.setPressed(true);
//                    view.setSelected(true);
//
//                    System.out.println("btn clicked");
//                }
//                else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
//                    // set to normal color
//                    view.setPressed(false);
//
//                    System.out.println("btn clicked");
//                }
//
//                return true;
//            }
//        });



        tvPerawatanLain = (TextView) root.findViewById(R.id.tvPerawatanLain);
        tvPerawatanLain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PerawatanLainActivity.class));
            }
        });


        return root;
    }




    public void PerawatanGigi(String noGigi) {
        String statusUser = util.getStatus(getActivity());
        System.out.println("Status = " + statusUser);
        if (statusUser.equalsIgnoreCase("Dokter") && !status.equalsIgnoreCase("profil")) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            ListPerawatan mPerawatan = new ListPerawatan();
            Bundle bundle = new Bundle();
            bundle.putString("nogigi", noGigi);
            bundle.putString("idPasien", idPasien);
            mPerawatan.setArguments(bundle);
            fragmentManager.popBackStack();
            transaction.replace(R.id.frame_layout_left, mPerawatan);
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (statusUser.equalsIgnoreCase("Pasien") || statusUser.equalsIgnoreCase("Administrator") )  {
            dialogPerawatan(noGigi);
        }
        else if (statusUser.equalsIgnoreCase("Dokter") && status.equalsIgnoreCase("profil"))
        {
            dialogPerawatan(noGigi);
        }



    }

    public void dialogPerawatan(final String noGigi) {
        final AlertDialog.Builder alBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View dialog = (View) inflater.inflate(R.layout.activity_list_perawatan, null);
        final RecyclerView mRecyclerview = (RecyclerView) dialog.findViewById(R.id.recycleViewPerawatan);
        final ProgressBar progressBar = (ProgressBar) dialog.findViewById(R.id.progressBar);
        final TextView tvNoGigi = (TextView) dialog.findViewById(R.id.tvNoGigi);
        final TextView tvStatusData = (TextView) dialog.findViewById(R.id.tvStatusData);
        tvNoGigi.setText(noGigi);
        mRecyclerview.setHasFixedSize(true);
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecyclerview.setLayoutManager(mManager);
        mRekamMedis = mRoot.child("rekammedis").child(idPasien).child(noGigi);
        mRekamMedis.keepSynced(true);
        mAdapterPerawatan = new FirebaseRecyclerAdapter<Perawatan, MainViewHistoryTindakan>(
                Perawatan.class, R.layout.row_list_history, MainViewHistoryTindakan.class, mRekamMedis) {
            @Override
            protected void populateViewHolder(MainViewHistoryTindakan viewHolder, Perawatan model, int position) {
                System.out.println("POSITION = " + position);
                final DatabaseReference perawatanRef = getRef(position);
                perawatanKey = perawatanRef.getKey();
                System.out.println("PerawatanKey = " + perawatanKey);
                viewHolder.bindToPost(perawatanKey, mActivity, noGigi);
                progressBar.setVisibility(View.GONE);

            }
        };

        if (mRecyclerview.getChildCount() > 0) {
            tvStatusData.setVisibility(View.GONE);

        } else {
            tvStatusData.setVisibility(View.VISIBLE);

        }
        progressBar.setVisibility(View.GONE);
        mAdapterPerawatan.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                mRecyclerview.smoothScrollToPosition(mAdapterPerawatan.getItemCount());
                System.out.println("mAdapterDalem = " + mAdapterPerawatan.getItemCount());
                System.out.println("itemCount = " + itemCount);
                System.out.println("positionStart = " + positionStart);
                if (mAdapterPerawatan.getItemCount() > 0) {
                    tvStatusData.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        mRecyclerview.setAdapter(mAdapterPerawatan);

        alBuilder.setView(dialog);
        alBuilder.create();
        final AlertDialog alertDialog = alBuilder.show();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    public void onStart() {
        super.onStart();



    }


    public void odontogram(final ImageButton btnGigi, final String noGigi)
    {
        mRekamMedis.child(idPasien).child(noGigi).orderByChild("status").equalTo("Aktif").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("Datasnapshot gigi38 = " + dataSnapshot.getValue());

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    idPerawatan = child.child("idPerawatan").getValue(String.class);
                    statusPerawatan = child.child("status").getValue(String.class);
                    System.out.println("ID PERAWATAN ODONTOGRAM = " + idPerawatan);
                    System.out.println("Status Perawatan = " + statusPerawatan);
                }

                System.out.println("Status Perawatan luar = " + statusPerawatan);
                if (dataSnapshot.getChildrenCount() > 1) {
                    if (noGigi.equalsIgnoreCase("Gigi 13") || noGigi.equalsIgnoreCase("Gigi 12") || noGigi.equalsIgnoreCase("Gigi 11") ||
                            noGigi.equalsIgnoreCase("Gigi 21") || noGigi.equalsIgnoreCase("Gigi 22") || noGigi.equalsIgnoreCase("Gigi 23") ||
                            noGigi.equalsIgnoreCase("Gigi 53") || noGigi.equalsIgnoreCase("Gigi 52") || noGigi.equalsIgnoreCase("Gigi 51") ||
                            noGigi.equalsIgnoreCase("Gigi 61") || noGigi.equalsIgnoreCase("Gigi 62") || noGigi.equalsIgnoreCase("Gigi 63") ||
                            noGigi.equalsIgnoreCase("Gigi 83") || noGigi.equalsIgnoreCase("Gigi 82") || noGigi.equalsIgnoreCase("Gigi 81") ||
                            noGigi.equalsIgnoreCase("Gigi 71") || noGigi.equalsIgnoreCase("Gigi 72") || noGigi.equalsIgnoreCase("Gigi 73") ||
                            noGigi.equalsIgnoreCase("Gigi 43") || noGigi.equalsIgnoreCase("Gigi 42") || noGigi.equalsIgnoreCase("Gigi 41") ||
                            noGigi.equalsIgnoreCase("Gigi 31") || noGigi.equalsIgnoreCase("Gigi 32") || noGigi.equalsIgnoreCase("Gigi 33")) {
                        btnGigi.setBackgroundResource(R.drawable.khusus2);
                        util.setGigi(noGigi,mActivity, "khusus2");
                    }
                    else
                    {
                        btnGigi.setBackgroundResource(R.drawable.khusus1);
                        util.setGigi(noGigi,mActivity, "khusus1");
                    }

                } else if (dataSnapshot.getChildrenCount() == 1) {
                    mPerawatan.child(idPerawatan).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String kode = dataSnapshot.child("kodeGigi").getValue(String.class);
                            if (kode != null) {
                                if (kode.equalsIgnoreCase("meb1") || kode.equalsIgnoreCase("meb2") || kode.equalsIgnoreCase("meb3")) {
                                    kode = "meb";
                                } else if (kode.equalsIgnoreCase("pob1") || kode.equalsIgnoreCase("pob2") || kode.equalsIgnoreCase("pob3") || kode.equalsIgnoreCase("pob4")) {
                                    kode = "pob";
                                }else if (kode.equalsIgnoreCase("meb12") || kode.equalsIgnoreCase("meb22") || kode.equalsIgnoreCase("meb32")) {
                                    kode = "mebdepan";
                                } else if (kode.equalsIgnoreCase("pob12") || kode.equalsIgnoreCase("pob22") || kode.equalsIgnoreCase("pob32") || kode.equalsIgnoreCase("pob42")) {
                                    kode = "pobdepan";
                                }
                            } else {
                                kode = "sou";
                            }

                            util.setGigi(noGigi,mActivity, kode);
                            int res = mActivity.getResources().getIdentifier(kode, "drawable", mActivity.getPackageName());
                            btnGigi.setBackgroundResource(res);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else if (dataSnapshot.getChildrenCount() == 0) {
                    if (noGigi.equalsIgnoreCase("Gigi 13") || noGigi.equalsIgnoreCase("Gigi 12") || noGigi.equalsIgnoreCase("Gigi 11") ||
                            noGigi.equalsIgnoreCase("Gigi 21") || noGigi.equalsIgnoreCase("Gigi 22") || noGigi.equalsIgnoreCase("Gigi 23") ||
                            noGigi.equalsIgnoreCase("Gigi 53") || noGigi.equalsIgnoreCase("Gigi 52") || noGigi.equalsIgnoreCase("Gigi 51") ||
                            noGigi.equalsIgnoreCase("Gigi 61") || noGigi.equalsIgnoreCase("Gigi 62") || noGigi.equalsIgnoreCase("Gigi 63") ||
                            noGigi.equalsIgnoreCase("Gigi 83") || noGigi.equalsIgnoreCase("Gigi 82") || noGigi.equalsIgnoreCase("Gigi 81") ||
                            noGigi.equalsIgnoreCase("Gigi 71") || noGigi.equalsIgnoreCase("Gigi 72") || noGigi.equalsIgnoreCase("Gigi 73") ||
                            noGigi.equalsIgnoreCase("Gigi 43") || noGigi.equalsIgnoreCase("Gigi 42") || noGigi.equalsIgnoreCase("Gigi 41") ||
                            noGigi.equalsIgnoreCase("Gigi 31") || noGigi.equalsIgnoreCase("Gigi 32") || noGigi.equalsIgnoreCase("Gigi 33")) {
                        btnGigi.setBackgroundResource(R.drawable.sou2);
                        util.setGigi(noGigi,mActivity, "sou2");
                    }
                    else {
                        btnGigi.setBackgroundResource(R.drawable.sou);
                        util.setGigi(noGigi,mActivity, "sou");
                    }


                }





            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        progressDialog.dismiss();



    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (mActivity==null&& context instanceof RekamMedisActivity)
        {
            System.out.println("ON ATTACH ODONTOGRAM");
            mActivity = (RekamMedisActivity) context;
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("PAUSE ODONTOGRAM");
        util.sp.unregisterOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

    }
}
