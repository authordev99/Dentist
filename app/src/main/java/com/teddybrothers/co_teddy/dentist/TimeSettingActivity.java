package com.teddybrothers.co_teddy.dentist;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.HashMap;

public class TimeSettingActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    RelativeLayout rlInterval, rlJamBuka,rlJamTutup;
    int idInterval;
    RadioButton rbInterval;
    FirebaseDatabase databaseUtama;
    DatabaseReference mRoot,mAbout;
    int intervalTime;
    TextView tvInterval;
    Boolean flags = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_setting);

        rlInterval = findViewById(R.id.rlInterval);
        tvInterval  =findViewById(R.id.tvInterval);


        if (databaseUtama == null) {

            databaseUtama = FirebaseDatabase.getInstance();
            System.out.println("ON START CREATED");
        }
        mRoot = databaseUtama.getReference();
        mAbout = mRoot.child("about");

        mAbout.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null)
                {
                    intervalTime = dataSnapshot.child("interval").getValue(Integer.class);
                    if (intervalTime>1)
                    {
                        tvInterval.setText(intervalTime+" Menit");
                    }else if (intervalTime==1)
                    {
                        tvInterval.setText("Tidak Ada");
                    }
                }else
                {
                    tvInterval.setText("Tidak Ada");
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        rlInterval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog();
            }
        });


    }





    void dialog() {
        final CharSequence[] interval = {"Tidak ada", "30 Menit", "45 Menit", "60 Menit"};
        final AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
        alt_bld.setTitle("Pilih Interval Waktu");
        mAbout.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("interval").getValue()!=null)
                {
                    intervalTime = dataSnapshot.child("interval").getValue(Integer.class);
                    int posisi = 0;
                    if (intervalTime==1)
                    {
                        posisi = intervalTime;
                    }else if (intervalTime==30)
                    {
                        posisi = 1;
                    }else if (intervalTime==45)
                    {
                        posisi = 2;
                    }else if (intervalTime==60)
                    {
                        posisi = 3;
                    }

                    alt_bld.setSingleChoiceItems(interval, posisi, new DialogInterface
                            .OnClickListener() {
                        public void onClick(final DialogInterface dialog, int item) {
                            Toast.makeText(TimeSettingActivity.this,
                                    "Interval Waktu = " + interval[item], Toast.LENGTH_SHORT).show();
                            int intervalWaktu = 1;
                            if (interval[item].equals("30 Menit")) {
                                intervalWaktu = 30;
                            }else if (interval[item].equals("45 Menit")) {
                                intervalWaktu = 45;
                            }else if (interval[item].equals("60 Menit")) {
                                intervalWaktu = 60;
                            }
                            mAbout.child("interval").setValue(intervalWaktu).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        dialog.dismiss();
                                    }
                                }
                            });
                            // dismiss the alertbox after chose option

                        }
                    });
                    AlertDialog alert = alt_bld.create();
                    alert.show();
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {

    }
}
