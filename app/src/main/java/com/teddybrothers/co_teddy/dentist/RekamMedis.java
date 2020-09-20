package com.teddybrothers.co_teddy.dentist;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.teddybrothers.co_teddy.dentist.entity.BlockDate;
import com.teddybrothers.co_teddy.dentist.entity.Pasien;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class RekamMedis extends Fragment implements com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public RekamMedisActivity mActivity;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ProgressDialog progressDialog;
    public static final String[] MONTHS = {"Januari", "Febuari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
    public Calendar cal = Calendar.getInstance();
    public int day = cal.get(Calendar.DAY_OF_MONTH);
    public int month = cal.get(Calendar.MONTH);
    public int year = cal.get(Calendar.YEAR);
    Long timeStamp;
    final ArrayList<String> listDate = new ArrayList<>();

    public String idDokter,idPasien,jadwalKey,statusIntent;
    TextView tvNamaPasien,tvNo,tvTglLahir,tvJK;
    EditText etAnamnesis,etDiagnosis,etKetDiastema,etKetAnomali,etLainnya,etKeteranganRenPer;
    RadioGroup rgOcclusi,rgPalatinus,rgMandibularis,rgPalatum,rgDiastema,rgAnomali;
    RadioButton rbOcclusi,rbPalatinus,rbMandibularis,rbPalatum,rbDiastema,rbAnomali;
    int idOcclusi,idPalatinus,idMandibularis,idPalatum,idDiastema,idAnomali;
    Button btnSimpan,btnLog,btnDate;
    FirebaseDatabase mDatabase;
    DatabaseReference mRoot, mUserRef, mPasien, mPerawatan,mTindakan,mPhoto,mDokter,mRekamMedis,mLogRencanaPerawatan,mBlockDate;
    FirebaseAuth mAuth;
    String status;
    Utilities util = new Utilities();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_rekam_medis, container, false);
        Toolbar toolbar = (Toolbar) root.findViewById(R.id.toolbar);

        if (mDatabase == null) {

            mDatabase = FirebaseDatabase.getInstance();
            System.out.println("ON START CREATED");
        }
        mRoot = mDatabase.getReference();
        mUserRef = mRoot.child("users");
        mTindakan = mRoot.child("tindakan");
        mPerawatan = mRoot.child("perawatan");
        mPasien = mRoot.child("pasien");
        mDokter = mRoot.child("dokter");
        mPhoto = mRoot.child("photo");
        mLogRencanaPerawatan = mRoot.child("logRenPer");
        mBlockDate =mRoot.child("blockDate");
        mAuth = FirebaseAuth.getInstance();

        etKeteranganRenPer = (EditText) root.findViewById(R.id.etKeteranganRenPer);
        btnSimpan = (Button) root.findViewById(R.id.btnSimpan);
        btnLog = (Button) root.findViewById(R.id.btnLog);
        btnDate = (Button) root.findViewById(R.id.btnDate);
        etAnamnesis = (EditText) root.findViewById(R.id.etAnamnesis);
        etDiagnosis = (EditText) root.findViewById(R.id.etDiagnosis);

        etKetDiastema = (EditText) root.findViewById(R.id.etKetDiastema);
        etKetAnomali = (EditText) root.findViewById(R.id.etKetAnomali);
        etLainnya = (EditText) root.findViewById(R.id.etLainnya);

        tvNamaPasien = (TextView) root.findViewById(R.id.tvNamaPasien);
        tvNo = (TextView) root.findViewById(R.id.tvNo);
        tvTglLahir = (TextView) root.findViewById(R.id.tvLahir);
        tvJK = (TextView) root.findViewById(R.id.tvJenKel);

        rgOcclusi = (RadioGroup) root.findViewById(R.id.rgOcclusi);
        rgPalatinus = (RadioGroup) root.findViewById(R.id.rgPalatinus);
        rgMandibularis = (RadioGroup) root.findViewById(R.id.rgMandibularis);
        rgPalatum = (RadioGroup) root.findViewById(R.id.rgPalatum);
        rgDiastema = (RadioGroup) root.findViewById(R.id.rgDiastema);
        rgAnomali = (RadioGroup) root.findViewById(R.id.rgAnomali);

        idOcclusi = rgOcclusi.getCheckedRadioButtonId();
        idPalatinus = rgPalatinus.getCheckedRadioButtonId();
        idMandibularis = rgMandibularis.getCheckedRadioButtonId();
        idPalatum = rgPalatum.getCheckedRadioButtonId();
        idDiastema = rgDiastema.getCheckedRadioButtonId();
        idAnomali = rgAnomali.getCheckedRadioButtonId();
        System.out.println("ID OCCLUSI = "+idOcclusi);

        rbOcclusi = (RadioButton) root.findViewById(idOcclusi);
        rbPalatinus = (RadioButton) root.findViewById(idPalatinus);
        rbMandibularis = (RadioButton) root.findViewById(idMandibularis);
        rbPalatum = (RadioButton) root.findViewById(idPalatum);
        rbDiastema = (RadioButton) root.findViewById(idDiastema);
        rbAnomali = (RadioButton) root.findViewById(idAnomali);



        rgOcclusi.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                rbOcclusi = (RadioButton) root.findViewById(checkedId);
                System.out.println("Radio BUtton = "+rbOcclusi.getText().toString());

            }
        });

        rgPalatinus.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                rbPalatinus = (RadioButton) root.findViewById(checkedId);
                System.out.println("Radio BUtton = "+rbPalatinus.getText().toString());
            }
        });

        rgMandibularis.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                rbMandibularis = (RadioButton) root.findViewById(checkedId);
                System.out.println("Radio BUtton = "+rbMandibularis.getText().toString());
            }
        });

        rgPalatum.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                rbPalatum = (RadioButton) root.findViewById(checkedId);
                System.out.println("Radio BUtton = "+rbPalatum.getText().toString());
            }
        });

        rgDiastema.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                rbDiastema = (RadioButton) root.findViewById(checkedId);
                System.out.println("Radio BUtton = "+rbDiastema.getText().toString());
                if (rbDiastema.getText().toString().equalsIgnoreCase("Ada"))
                {
                    etKetDiastema.setEnabled(true);
                }
                else
                {
                    etKetDiastema.setEnabled(false);
                }
            }
        });

        rgAnomali.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                rbAnomali = (RadioButton) root.findViewById(checkedId);
                System.out.println("Radio BUtton = "+rbAnomali.getText().toString());
                if (rbAnomali.getText().toString().equalsIgnoreCase("Ada"))
                {
                    etKetAnomali.setEnabled(true);
                }
                else
                {
                    etKetAnomali.setEnabled(false);
                }
            }
        });


        idPasien = util.getIdPasien(mActivity);
        Bundle extras = this.getArguments();
        if (extras != null) {
            idPasien = extras.getString("idPasien");
            statusIntent = extras.getString("status");
        }

        idDokter = util.getIdDokter(mActivity);
        jadwalKey= util.getIdJadwal(mActivity);

        btnDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                pickDate();
            }
        });


        mBlockDate.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listDate.clear();
                for (DataSnapshot blockDate : dataSnapshot.getChildren()) {
                    final BlockDate dataBlockDate = blockDate.getValue(BlockDate.class);
                    String tanggalBlock = blockDate.child("dateBlock").getValue(String.class);
                    listDate.add(tanggalBlock);
                    dataBlockDate.idBlockDate = blockDate.getKey();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mPasien.child(idPasien).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Pasien pasien = dataSnapshot.getValue(Pasien.class);
                 tvNamaPasien.setText(pasien.getNama());
                tvNo.setText(pasien.getNoIdentitas());
                tvTglLahir.setText(pasien.getTempatLahir()+", "+pasien.getTanggalLahir());
                String jenKel;
                if (pasien.getJenisKelamin().toString().equals("0"))
                {
                    jenKel = "Laki - laki";
                }
                else
                {
                    jenKel = "Perempuan";
                }
                tvJK.setText(jenKel);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                RiwayatMedis mMedis = new RiwayatMedis();
                Bundle bundle = new Bundle();
                System.out.println("idPasien btnLog = "+idPasien);
                bundle.putString("idPasien", idPasien);
                mMedis.setArguments(bundle);
                fragmentManager.popBackStack();
                transaction.replace(R.id.frame_layout_right, mMedis);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog = new ProgressDialog(getContext());
                if (btnSimpan.getText().equals("UPDATE"))
                {
                    progressDialog.setMessage("Update Data...");
                }
                else
                {
                    progressDialog.setMessage("Saving Data...");
                }
                progressDialog.show();

                if (btnDate.getText().toString().equalsIgnoreCase("Pilih Tanggal Rencana Perawatan . . ."))
                {
                    Toast.makeText(mActivity, "Silahkan pilih tanggal rencana perawatan ", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    final String anamnesis = etAnamnesis.getText().toString();
                    final String diagnosis = etDiagnosis.getText().toString();
                    final String rencanaPerawatan = String.valueOf(timeStamp);
                    final String ketRencanaPerawatan = etKeteranganRenPer.getText().toString();
                    final String occlusi = String.valueOf(rgOcclusi.indexOfChild(rbOcclusi));
                    final String palatinus = String.valueOf(rgPalatinus.indexOfChild(rbPalatinus));
                    final String mandibularis = String.valueOf(rgMandibularis.indexOfChild(rbMandibularis));
                    final String palatum = String.valueOf(rgPalatum.indexOfChild(rbPalatum));
                    final String diastema = String.valueOf(rgDiastema.indexOfChild(rbDiastema));
                    final String anomali = String.valueOf(rgAnomali.indexOfChild(rbAnomali));
                    final String ketDiastema = etKetDiastema.getText().toString();
                    final String ketAnomali = etKetAnomali.getText().toString();
                    final String lainnya = etLainnya.getText().toString();
                    System.out.println("SIMPAN = "+occlusi + palatinus + mandibularis + palatum + diastema);
                    Long timeStamp = System.currentTimeMillis();
                    final String timestamp = timeStamp.toString();
                    mDokter.child(idDokter).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String namaDokter = dataSnapshot.child("nama").getValue(String.class);
                            rekamMedis(anamnesis,diagnosis,rencanaPerawatan,ketRencanaPerawatan,occlusi,palatinus,mandibularis,palatum,diastema,ketDiastema,anomali,ketAnomali,lainnya,idDokter,timestamp,namaDokter);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }




            }
        });


        return root;
    }


    public void pickDate() {



        Calendar now = Calendar.getInstance();
        int day = 0;
        if (now.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY)
        {
            day = now.get(Calendar.DAY_OF_MONTH)+1;
        }
        else
        {
            day = now.get(Calendar.DAY_OF_MONTH);
        }
        com.wdullaer.materialdatetimepicker.date.DatePickerDialog dpd = com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance(
                RekamMedis.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                day
        );

        now.set(now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                day);
        dpd.setMinDate(now);



        Calendar sunday;
        List<Calendar> weekends = new ArrayList<>();
        int weeks = 1000;

        for (int i = 0; i < (weeks * 7) ; i = i + 7) {
            sunday = Calendar.getInstance();

            sunday.add(Calendar.DAY_OF_YEAR, (Calendar.SUNDAY - sunday.get(Calendar.DAY_OF_WEEK) + 7 + i));
            // saturday = Calendar.getInstance();
            // saturday.add(Calendar.DAY_OF_YEAR, (Calendar.SATURDAY - saturday.get(Calendar.DAY_OF_WEEK) + i));
            // weekends.add(saturday);
            weekends.add(sunday);
        }
        Calendar[] disabledDays = weekends.toArray(new Calendar[weekends.size()]);
        dpd.setDisabledDays(disabledDays);





        for (int i = 0; i < listDate.size(); i++) {

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            java.util.Date date = null;
            try {
                date = sdf.parse(listDate.get(i));
                System.out.println("date convert = " + date);
                now = dateToCalendar(date);
                System.out.println(now.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            List<Calendar> dates = new ArrayList<>();
            dates.add(now);
            Calendar[] disabledDays1 = dates.toArray(new Calendar[dates.size()]);
            dpd.setDisabledDays(disabledDays1);

        }
//
        dpd.show(mActivity.getFragmentManager(), "Datepickerdialog");




    }

    private Calendar dateToCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    private void rekamMedis(String anamnesis, String diagnosis, final String rencanaPerawatan, final String ketRencanaPerawatan, String occlusi, String palatinus, String mandibularis, String palatum, String diastema,
                            String ketDiastema, String anomali, String ketAnomali, String lain, final String idDokter,String timestamp,String namaDokter) {

        final com.teddybrothers.co_teddy.dentist.entity.RekamMedis rekamMedis = new com.teddybrothers.co_teddy.dentist.entity.RekamMedis(anamnesis,diagnosis,rencanaPerawatan,ketRencanaPerawatan,occlusi,palatinus,mandibularis,
                palatum,diastema,ketDiastema,anomali,ketAnomali,lain,idDokter,namaDokter,timestamp);

        final com.teddybrothers.co_teddy.dentist.entity.RekamMedis renPerawatan = new com.teddybrothers.co_teddy.dentist.entity.RekamMedis(rencanaPerawatan,ketRencanaPerawatan,idDokter,namaDokter);

        mRekamMedis.child(idPasien).child("renPerawatan").push().setValue(renPerawatan);

        mRekamMedis.child(idPasien).child("datamedis").setValue(rekamMedis).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {


                if (btnSimpan.getText().equals("UPDATE"))
                {
                    Toast.makeText(mActivity,"Data Berhasil Diupdate",Toast.LENGTH_SHORT).show();

                    mLogRencanaPerawatan.child(idPasien).push().setValue(rekamMedis);
                    progressDialog.dismiss();
                }
                else
                {
                    Toast.makeText(mActivity,"Data Berhasil Disimpan",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
                btnSimpan.setText("UPDATE");

            }
        });

    }



    public void onStart() {
        super.onStart();


        if (mDatabase == null) {

            mDatabase = FirebaseDatabase.getInstance();
            System.out.println("ON START CREATED");
        }

        mRoot = mDatabase.getReference();
        mUserRef = mRoot.child("users");
        mTindakan = mRoot.child("tindakan");
        mPerawatan = mRoot.child("perawatan");
        mPasien = mRoot.child("pasien");
        mRekamMedis = mRoot.child("rekammedis");
//        mRekamMedis.keepSynced(true);
        mDokter = mRoot.child("dokter");
        mPhoto = mRoot.child("photo");
        mAuth = FirebaseAuth.getInstance();
        status = util.getStatus(mActivity);
        System.out.println("ID PASIEN = "+idPasien);

        if (status.equalsIgnoreCase("Dokter")&&statusIntent.equalsIgnoreCase("profil"))
        {
            btnSimpan.setVisibility(View.GONE);
        }

        if (status.equalsIgnoreCase("Pasien")||status.equalsIgnoreCase("Administrator"))
        {
            btnSimpan.setVisibility(View.GONE);

            for (int i = 0; i < rgAnomali.getChildCount(); i++) {
                rgAnomali.getChildAt(i).setEnabled(false);
            }

            for (int i = 0; i < rgDiastema.getChildCount(); i++) {
                rgDiastema.getChildAt(i).setEnabled(false);
            }
            for (int i = 0; i < rgMandibularis.getChildCount(); i++) {
                rgMandibularis.getChildAt(i).setEnabled(false);
            }
            for (int i = 0; i < rgOcclusi.getChildCount(); i++) {
                rgOcclusi.getChildAt(i).setEnabled(false);
            }
            for (int i = 0; i < rgPalatinus.getChildCount(); i++) {
                rgPalatinus.getChildAt(i).setEnabled(false);
            }
            for (int i = 0; i < rgPalatum.getChildCount(); i++) {
                rgPalatum.getChildAt(i).setEnabled(false);
            }


            etLainnya.setEnabled(false);
            etKetAnomali.setEnabled(false);
            etKetDiastema.setEnabled(false);
            etKeteranganRenPer.setEnabled(false);
            etAnamnesis.setEnabled(false);
            etDiagnosis.setEnabled(false);
            btnDate.setEnabled(false);

        }






        if (idPasien!=null)
        {

            mPasien.child(idPasien).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    System.out.println("jenkel hahahaha");
                    String userIDPasien = dataSnapshot.child("userID").getValue(String.class);
                    String jenKel = dataSnapshot.child("jenisKelamin").getValue(String.class);
                    String noIdentitas = dataSnapshot.child("noIdentitas").getValue(String.class);
                    util.setNoIdentitas(mActivity, noIdentitas);
                    String tempatLahir = dataSnapshot.child("tempatLahir").getValue(String.class);
                    util.setTempatLahir(mActivity, tempatLahir);
                    String tanggalLahir = dataSnapshot.child("tanggalLahir").getValue(String.class);
                    util.setTanggalLahir(mActivity, tanggalLahir);
                    String namaPasien = dataSnapshot.child("nama").getValue(String.class);
                    util.setNamaPasien(mActivity, namaPasien);
                    System.out.println("jenkel = " + jenKel);

                    if (jenKel.equalsIgnoreCase("0")) {
                        jenKel = "Laki-laki";
                    } else if (jenKel.equalsIgnoreCase("1")) {
                        jenKel = "Perempuan";
                    }

                    util.setJenKelamin(mActivity, jenKel);

                    System.out.println("jenkel = " + jenKel);
                    System.out.println("nama = " + namaPasien);


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });





            mRekamMedis.child(idPasien).child("datamedis").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue()!=null)
                    {

                        btnSimpan.setText("UPDATE");
                        String anamnesis = dataSnapshot.child("anamnesis").getValue(String.class);
                        String diagnosis = dataSnapshot.child("diagnosis").getValue(String.class);
                        String rencanaPerawatan = dataSnapshot.child("rencanaPerawatan").getValue(String.class);
                        String ketRencanaPerawatan = dataSnapshot.child("ketRencanaPerawatan").getValue(String.class);
                        String occlusi = dataSnapshot.child("occlusi").getValue(String.class);
                        String palatinus = dataSnapshot.child("torusPalatinus").getValue(String.class);
                        String mandibularis = dataSnapshot.child("torusMandibularis").getValue(String.class);
                        String palatum = dataSnapshot.child("palatum").getValue(String.class);
                        String diastema = dataSnapshot.child("diastema").getValue(String.class);
                        String ketDiastema = dataSnapshot.child("ketDiastema").getValue(String.class);
                        String anomali = dataSnapshot.child("gigiAnomali").getValue(String.class);
                        String ketAnomali = dataSnapshot.child("ketGigiAnomali").getValue(String.class);
                        String lainnya = dataSnapshot.child("lainLain").getValue(String.class);



                        etAnamnesis.setText(anamnesis);
                        etDiagnosis.setText(diagnosis);
                        System.out.println("Rencana perawatan = "+rencanaPerawatan);
                        if (!rencanaPerawatan.equalsIgnoreCase("null"))
                        {
                            btnDate.setText(getDate(Long.parseLong(rencanaPerawatan)));
                        }

                        etKeteranganRenPer.setText(ketRencanaPerawatan);
                        etKetDiastema.setText(ketDiastema);
                        etKetAnomali.setText(ketAnomali);
                        etLainnya.setText(lainnya);
                        if (occlusi != null && palatinus!=null&& mandibularis!=null&& palatum!=null&& diastema!=null&& anomali!=null)
                        {
                            rgOcclusi.check(rgOcclusi.getChildAt(Integer.parseInt(occlusi)).getId());
                            rgPalatinus.check(rgPalatinus.getChildAt(Integer.parseInt(palatinus)).getId());
                            rgMandibularis.check(rgMandibularis.getChildAt(Integer.parseInt(mandibularis)).getId());
                            rgPalatum.check(rgPalatum.getChildAt(Integer.parseInt(palatum)).getId());
                            rgDiastema.check(rgDiastema.getChildAt(Integer.parseInt(diastema)).getId());
                            rgAnomali.check(rgAnomali.getChildAt(Integer.parseInt(anomali)).getId());
                        }

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


        mRekamMedis.child(idPasien).child("datamedis").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                String occlusiData = dataSnapshot.child("occlusi").getValue(String.class);
                System.out.println("occlusi = " + occlusiData);
                if (occlusiData != null) {
                    if (occlusiData.equalsIgnoreCase("0")) {
                        occlusiData = "Normal Bite";
                    } else if (occlusiData.equalsIgnoreCase("1")) {
                        occlusiData = "Cross Bite";
                    } else if (occlusiData.equalsIgnoreCase("2")) {
                        occlusiData = "Steep Bite";
                    }
                    util.setOcclusi(mActivity, occlusiData);

                } else {
                    util.setOcclusi(mActivity, "Tidak Ada");
                }

                String palatinusData = dataSnapshot.child("torusPalatinus").getValue(String.class);
                if (palatinusData != null) {
                    if (palatinusData.equalsIgnoreCase("0")) {
                        palatinusData = "Tidak Ada";
                    } else if (palatinusData.equalsIgnoreCase("1")) {
                        palatinusData = "Kecil";
                    } else if (palatinusData.equalsIgnoreCase("2")) {
                        palatinusData = "Sedang";
                    } else if (palatinusData.equalsIgnoreCase("3")) {
                        palatinusData = "Besar";
                    } else if (palatinusData.equalsIgnoreCase("4")) {
                        palatinusData = "Multiple";
                    }
                    util.setPalatinus(mActivity, palatinusData);

                } else {
                    util.setPalatinus(mActivity, "Tidak Ada");
                }


                String mandibularisData = dataSnapshot.child("torusMandibularis").getValue(String.class);
                if (mandibularisData != null) {
                    if (mandibularisData.equalsIgnoreCase("0")) {
                        mandibularisData = "Tidak Ada";
                    } else if (mandibularisData.equalsIgnoreCase("1")) {
                        mandibularisData = "Sisi Kiri";
                    } else if (mandibularisData.equalsIgnoreCase("2")) {
                        mandibularisData = "Sisi Kanan";
                    } else if (mandibularisData.equalsIgnoreCase("3")) {
                        mandibularisData = "Kedua Sisi";
                    }
                    util.setMandibularis(mActivity, mandibularisData);
                } else {
                    util.setMandibularis(mActivity, "Tidak Ada");
                }

                String palatumData = dataSnapshot.child("palatum").getValue(String.class);
                if (palatumData != null) {
                    if (palatumData.equalsIgnoreCase("0")) {
                        palatumData = "Dalam";
                    } else if (palatumData.equalsIgnoreCase("1")) {
                        palatumData = "Sedang";
                    } else if (palatumData.equalsIgnoreCase("2")) {
                        palatumData = "Rendah";
                    }
                    util.setPalatum(mActivity, palatumData);
                } else {
                    util.setPalatum(mActivity, "Tidak Ada");
                }


                String diastemaData = dataSnapshot.child("diastema").getValue(String.class);
                if (diastemaData != null) {
                    if (diastemaData.equalsIgnoreCase("0")) {
                        diastemaData = "Tidak Ada";
                    } else if (diastemaData.equalsIgnoreCase("1")) {
                        diastemaData = "Ada";
                    }
                    util.setDiastema(mActivity, diastemaData);
                } else {
                    util.setDiastema(mActivity, "Tidak Ada");
                }


                String ketDiastema = dataSnapshot.child("ketDiastema").getValue(String.class);
                if (ketDiastema != null) {
                    util.setKetDiastema(mActivity, ketDiastema);
                } else {
                    util.setKetDiastema(mActivity, "");
                }


                String anomaliData = dataSnapshot.child("gigiAnomali").getValue(String.class);
                if (anomaliData != null) {
                    if (anomaliData.equalsIgnoreCase("0")) {
                        anomaliData = "Tidak Ada";
                    } else if (anomaliData.equalsIgnoreCase("1")) {
                        anomaliData = "Ada";
                    }
                    util.setAnomali(mActivity, anomaliData);
                } else {
                    util.setAnomali(mActivity, "Tidak Ada");
                }


                String ketAnomali = dataSnapshot.child("ketGigiAnomali").getValue(String.class);
                if (ketAnomali != null) {
                    util.setKetAnomali(mActivity, ketAnomali);
                } else {
                    util.setKetAnomali(mActivity, "");

                }


                String lainnyaData = dataSnapshot.child("lainLain").getValue(String.class);
                if (lainnyaData != null) {
                    util.setLainnya(mActivity, lainnyaData);
                } else {
                    util.setLainnya(mActivity, "Tidak Ada");
                }


            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public Long convertTimeStamp(String tanggal)
    {
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date date2= null;
        try {
            date2 = formatter.parse(tanggal);
        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("Log = "+ e);
        }
        Long timeStamp = date2.getTime();
        return timeStamp;
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (mActivity==null&& context instanceof RekamMedisActivity)
        {
            System.out.println("ON ATTACH LIST PERAWATAN");
            mActivity = (RekamMedisActivity) context;
        }

    }




    @Override
    public void onDateSet(com.wdullaer.materialdatetimepicker.date.DatePickerDialog view, int myear, int monthOfYear, int dayOfMonth) {
        month = monthOfYear + 1;
        String mon = MONTHS[month - 1];
        day = dayOfMonth;
        year = myear;

        SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE");
        Date date = new Date(year, monthOfYear, dayOfMonth-1);
        String dayOfWeek = simpledateformat.format(date);
        System.out.println("dayOfWeek = "+dayOfWeek);

        btnDate.setText(dayOfWeek+", "+day + " " + mon + " " + year);
        String selectedDate = day+"-"+month+"-"+year;
        System.out.println("selectedDate = "+selectedDate);
        timeStamp = convertTimeStamp(selectedDate);

    }
}
