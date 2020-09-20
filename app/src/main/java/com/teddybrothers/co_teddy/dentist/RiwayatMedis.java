package com.teddybrothers.co_teddy.dentist;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.teddybrothers.co_teddy.dentist.customadapter.CustomAdapterLogMedis;
import com.teddybrothers.co_teddy.dentist.customadapter.CustomAdapterTindakan;
import com.teddybrothers.co_teddy.dentist.entity.*;
import com.teddybrothers.co_teddy.dentist.entity.RekamMedis;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;


public class RiwayatMedis extends Fragment {

    public RekamMedisActivity mActivity;
    String idPasien;
    ListView lvLog;
    TextView tvStatus;
    FirebaseDatabase mDatabase;
    ProgressBar progressBar;
    DatabaseReference mRoot,mRekamMedis,mPerawatan,mLogRencanaPerawatan;
    com.teddybrothers.co_teddy.dentist.entity.RekamMedis rekamMedis;
    final ArrayList<RekamMedis> listLogRekamMedis = new ArrayList<RekamMedis>();

    public RiwayatMedis() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_riwayat_medis, container, false);

        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
        }
        mRoot = mDatabase.getReference();
        mRekamMedis = mRoot.child("rekammedis");
        mPerawatan = mRoot.child("perawatan");
        mLogRencanaPerawatan = mRoot.child("logRenPer");

        lvLog = root.findViewById(R.id.lvLog);
        progressBar = root.findViewById(R.id.progressBar);
        tvStatus = root.findViewById(R.id.tvStatusData);

        Bundle bundle = getArguments();
        if (bundle != null) {
            idPasien = bundle.getString("idPasien");
        }
        System.out.println("idPasien riwayat medis = " + idPasien);


        mLogRencanaPerawatan.child(idPasien).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listLogRekamMedis.clear();
                System.out.println("listLogRekamMedis = " + listLogRekamMedis);
                for (DataSnapshot rekamMedis : dataSnapshot.getChildren()) {
                    RekamMedis dataTindakan = rekamMedis.getValue(RekamMedis.class);

                    listLogRekamMedis.add(dataTindakan);
                    System.out.println("data klinik 1 = " + listLogRekamMedis);

                }


                Collections.sort(listLogRekamMedis, RekamMedis.COMPARE_BY_DATE);
                final CustomAdapterLogMedis arrayAdapter = new CustomAdapterLogMedis(mActivity, R.layout.card_view_log_data_medis, listLogRekamMedis,"riwayatMedis");
                lvLog.setFastScrollEnabled(true);
                lvLog.setFastScrollAlwaysVisible(true);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                lvLog.setEmptyView(tvStatus);
                lvLog.setAdapter(arrayAdapter);
                progressBar.setVisibility(View.GONE);
                arrayAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        lvLog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                rekamMedis = listLogRekamMedis.get(i);
                final AlertDialog.Builder alBuilder = new AlertDialog.Builder(mActivity);
                LayoutInflater inflater = LayoutInflater.from(mActivity);
                final View dialog = (View) inflater.inflate(R.layout.dialog_log_data_medis, null);
                final TextView tvAnamnesis = (TextView) dialog.findViewById(R.id.tvAnamnesis);
                final TextView tvDiagnosis = (TextView) dialog.findViewById(R.id.tvDiagnosis);
                final TextView tvRencanaPerawatan = (TextView) dialog.findViewById(R.id.tvRenPer);
                final TextView tvKetRencanaPerawatan = (TextView) dialog.findViewById(R.id.ketRenPer);
                final TextView tvOklusi = (TextView) dialog.findViewById(R.id.tvOklusi);
                final TextView tvTorusPalatinus = (TextView) dialog.findViewById(R.id.tvTorusPalatinus);
                final TextView tvTorusMandibularis = (TextView) dialog.findViewById(R.id.tvTorusMandibularis);
                final TextView tvPalatum = (TextView) dialog.findViewById(R.id.tvPalatum);
                final TextView tvDiastema = (TextView) dialog.findViewById(R.id.tvDiastema);
                final TextView tvKetDiastema = (TextView) dialog.findViewById(R.id.tvKetDiastema);
                final TextView tvGigiAnomali = (TextView) dialog.findViewById(R.id.tvGigiAnomali);
                final TextView tvKetGigiAnomali = (TextView) dialog.findViewById(R.id.tvKetGigiAnomali);
                final TextView tvLainnya = (TextView) dialog.findViewById(R.id.tvLainnya);
                final TextView tvNamaDokter = (TextView) dialog.findViewById(R.id.tvNamaDokter);
                final TextView tvUpdateAt = (TextView) dialog.findViewById(R.id.tvUpdateAt);

                tvAnamnesis.setText(rekamMedis.anamnesis);
                tvDiagnosis.setText(rekamMedis.diagnosis);
                tvRencanaPerawatan.setText(rekamMedis.RencanaPerawatanConvert());
                tvKetRencanaPerawatan.setText(rekamMedis.ketRencanaPerawatan);
                tvOklusi.setText(rekamMedis.Occlusi());
                tvTorusPalatinus.setText(rekamMedis.TorusPalatinus());
                tvTorusMandibularis.setText(rekamMedis.TorusMandibularis());
                tvPalatum.setText(rekamMedis.Palatum());
                tvDiastema.setText(rekamMedis.Diastema());
                tvKetDiastema.setText(rekamMedis.ketDiastema);
                tvGigiAnomali.setText(rekamMedis.GigiAnomali());
                tvKetGigiAnomali.setText(rekamMedis.ketGigiAnomali);
                tvLainnya.setText(rekamMedis.lainLain);
                tvNamaDokter.setText("drg. "+rekamMedis.namaDokter);
                tvUpdateAt.setText(rekamMedis.UpdateTimeConvert());




                alBuilder.setView(dialog);
                alBuilder.create();
                alBuilder.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                final AlertDialog alertDialog = alBuilder.show();
                alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);





            }
        });


        return root;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (mActivity == null && context instanceof RekamMedisActivity) {
            System.out.println("ON ATTACH ODONTOGRAM");
            mActivity = (RekamMedisActivity) context;
        }

    }






}
