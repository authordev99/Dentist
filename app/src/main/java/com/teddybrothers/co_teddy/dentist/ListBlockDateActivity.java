package com.teddybrothers.co_teddy.dentist;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import com.teddybrothers.co_teddy.dentist.customadapter.CustomAdapterBlockDate;
import com.teddybrothers.co_teddy.dentist.entity.BlockDate;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ListBlockDateActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    FirebaseAuth mAuth;
    FirebaseDatabase databaseUtama;
    DatabaseReference mUserRef, mRoot, mBlockDate, mUser;
    BlockDate listDataBlock;
    public String dateKey, status;
    ProgressBar progressBar;
    boolean flags = false;
    final ArrayList<BlockDate> listBlockDate = new ArrayList<BlockDate>();
    ListView lv;
    Button btnDate;
    TextView tvStatus;
    String saveDateBlock = null;

    final ArrayList<String> listDate = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_date);

        if (databaseUtama == null) {
            databaseUtama = FirebaseDatabase.getInstance();
        }

        mRoot = databaseUtama.getReference();
        mBlockDate = mRoot.child("blockDate");
        mUser = mRoot.child("users");

        listBlockDate.clear();


        lv = findViewById(R.id.ListItem);
        tvStatus = findViewById(R.id.tvStatus);
        lv.setTextFilterEnabled(true);
        progressBar = findViewById(R.id.progressBar);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             blockDateDialog("blockDate","","","");
            }
        });


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println("listAdmin = " + listBlockDate);
                listDataBlock = listBlockDate.get(i);
                System.out.println("namaAdmin = " + listDataBlock.getDateBlock());
                blockDateDialog("detailBlockDate",listDataBlock.getIdBlockDate(),listDataBlock.getDateBlock(),listDataBlock.getKeterangan());
//                Toast.makeText(AdminActivity.this, "Nama Admin = " + listDataAdmin.getNama(), Toast.LENGTH_SHORT).show();


            }
        });
    }


    public void blockDateDialog(String flags, final String idBlockDate, String dateBlock, String keterangan)
    {
        final AlertDialog.Builder alBuilder = new AlertDialog.Builder(ListBlockDateActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialog = (View) inflater.inflate(R.layout.dialog_block_date, null);
        alBuilder.setView(dialog);
        btnDate = dialog.findViewById(R.id.btnDate);
        final EditText etKeterangan = dialog.findViewById(R.id.etKeterangan);

        if (flags.equalsIgnoreCase("detailBlockDate"))
        {

            btnDate.setText(dateBlock);
            etKeterangan.setText(keterangan);

            alBuilder.setCancelable(true).setNegativeButton("Hapus", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mBlockDate.child(idBlockDate).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(ListBlockDateActivity.this, "Data Tanggal Berhasil dihapus", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
        }




        if (flags.equalsIgnoreCase("blockDate"))
        {
            btnDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pickDate();
                }
            });

            alBuilder.setCancelable(true).setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String keterangan = etKeterangan.getText().toString();
                    if (btnDate.getText().toString().equalsIgnoreCase("Pilih tanggal . . .")) {
                        Toast.makeText(ListBlockDateActivity.this, "Silahkan Pilih Tanggal yang akan di block", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(keterangan)) {
                        Toast.makeText(ListBlockDateActivity.this, "Silahkan isi keterangan", Toast.LENGTH_SHORT).show();
                    } else {
                        if (saveDateBlock != null) {
                            BlockDate blockDate = new BlockDate(saveDateBlock, keterangan);
                            mBlockDate.push().setValue(blockDate);
                        }

                    }


                }
            }).setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
        }




        AlertDialog alertDialog = alBuilder.create();
        alertDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        tvStatus.setVisibility(View.GONE);

        dataBlockDate();


    }


    public void dataBlockDate() {


        mBlockDate.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listBlockDate.clear();
                listDate.clear();
                System.out.println("listAdmin = " + listBlockDate);
                for (DataSnapshot blockDate : dataSnapshot.getChildren()) {
                    final BlockDate dataBlockDate = blockDate.getValue(BlockDate.class);
                    String tanggalBlock = blockDate.child("dateBlock").getValue(String.class);
                    listDate.add(tanggalBlock);
                    dataBlockDate.idBlockDate = blockDate.getKey();

                    listBlockDate.add(dataBlockDate);
                    System.out.println("data klinik 1 = " + listBlockDate);

                }


//                Collections.sort(listBlockDate, sortirParameter);
                final CustomAdapterBlockDate arrayAdapter = new CustomAdapterBlockDate(ListBlockDateActivity.this, R.layout.card_view_log_data_medis, listBlockDate);
                lv.setEmptyView(tvStatus);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                lv.setAdapter(arrayAdapter);
                progressBar.setVisibility(View.GONE);
                arrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void pickDate() {

        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                ListBlockDateActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setMinDate(now);
        Calendar sunday;
        List<Calendar> weekends = new ArrayList<>();
        int weeks = 1000;

        for (int i = 0; i < (weeks * 7); i = i + 7) {
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
        dpd.show(getFragmentManager(), "Datepickerdialog");

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        MainActivity main = new MainActivity();
        String mon = main.MONTHS[monthOfYear];
        int month = monthOfYear + 1;
        btnDate.setText(dayOfMonth + " " + mon + " " + year);
        if (month < 10) {
            saveDateBlock = dayOfMonth + "-0" + month + "-" + year;
        } else if (month >= 10) {
            saveDateBlock = dayOfMonth + "-" + month + "-" + year;
        }

    }

    private Calendar dateToCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }
}
