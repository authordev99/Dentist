package com.teddybrothers.co_teddy.dentist.customadapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;
import com.teddybrothers.co_teddy.dentist.InputJadwalActivity;
import com.teddybrothers.co_teddy.dentist.R;
import com.teddybrothers.co_teddy.dentist.Utilities;
import com.teddybrothers.co_teddy.dentist.entity.Jadwal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by co_teddy on 3/8/2017.
 */
public class CustomAdapterJadwal extends ArrayAdapter<Jadwal> implements Filterable {
    Context context;
    int resourse,flagStatus=0;
    public String keyWord;
    public static final String[] MONTHS = {"Januari", "Febuari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
    private LayoutInflater mInflater;
    FirebaseAuth mAuth;
    FirebaseDatabase databaseUtama,databaseKlinik;

    DatabaseReference mUserRef, mRoot,mJadwal,mUser,mDokter,mPasien;
    Utilities util = new Utilities();


    private ArrayList<Jadwal> originalList;
    private ArrayList<Jadwal> namaList;

    private namaFilter filter;



    public CustomAdapterJadwal(Context context, int resource, ArrayList<Jadwal> data) {
        super(context, resource,data);
        System.out.println("CUSTOMADAPTER CALLING");

        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.resourse = resource;

        this.originalList = new ArrayList<Jadwal>(data);
        this.namaList= new ArrayList<Jadwal>(data);
        this.filter=new namaFilter();


    }





    @NonNull
    @Override
    public Filter getFilter() {
        if (filter == null){
            filter  = new namaFilter();
        }
        return filter;
    }

    @Override
    public int getCount() {
        return namaList.size();
    }

    class MenuHolder{

        TextView tv_time,tvNamaDokter,tvTanggal,tvStatusBar,tvIDPasien,tvNama;
        View circle_timeline,statusBar;
        ImageView ivMore;

    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final MenuHolder holder;


        if (databaseUtama==null)
        {
            databaseUtama = FirebaseDatabase.getInstance();
        }

        mRoot = databaseUtama.getReference();
        mAuth = FirebaseAuth.getInstance();
        mJadwal = mRoot.child("Jadwal");
        mUser = mRoot.child("users");
        mPasien = mRoot.child("pasien");
        mDokter = mRoot.child("dokter");

        String userId = mAuth.getCurrentUser().getUid();

        if (row==null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(resourse,parent,false);
            holder = new MenuHolder();
            holder.tv_time = (TextView) row.findViewById(R.id.tv_time);
            holder.tvNamaDokter = (TextView) row.findViewById(R.id.tvNamaDokter);
            holder.tvTanggal = (TextView) row.findViewById(R.id.tvTanggal);
            holder.tvStatusBar = (TextView) row.findViewById(R.id.tvStatusBar);
            holder.tvIDPasien = (TextView) row.findViewById(R.id.tvIDPasien);
            holder.tvNama = (TextView) row.findViewById(R.id.tvNama);
            holder.circle_timeline =  row.findViewById(R.id.circle);
            holder.statusBar =  row.findViewById(R.id.StatusBar);
            holder.tvStatusBar =  row.findViewById(R.id.tvStatusBar);
            holder.ivMore =  row.findViewById(R.id.ivMore);

            row.setTag(holder);
        }
        else
        {
            holder = (MenuHolder) row.getTag();
        }
        System.out.println("namaList = "+namaList);
        final Jadwal menu = namaList.get(position);
        System.out.println("Position = "+position);

        String tanggal = getDate(menu.getTimeStamp());
        String waktu = getTime(menu.getTimeStamp());


        holder.tvNamaDokter.setText("drg. "+menu.getNamaDokter());
        holder.tvNama.setText(menu.getNamaPasien());
        holder.tvTanggal.setText(tanggal);
        holder.tv_time.setText(waktu);
        holder.tvIDPasien.setText(menu.getIdPasien());
        holder.tvStatusBar.setText(menu.getStatus());

        mDokter.orderByChild("userID").equalTo(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String idDokter = dataSnapshot.getKey();
                if (idDokter!=null)
                {
                    if (!idDokter.equalsIgnoreCase(menu.idDokter)||menu.status.equalsIgnoreCase("Selesai")||menu.status.equalsIgnoreCase("Batal"))
                    {
                        holder.ivMore.setVisibility(View.GONE);
                    }
                    else
                    {
                        holder.ivMore.setVisibility(View.VISIBLE);
                    }
                }

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



        mPasien.orderByChild("userID").equalTo(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String idPasien = dataSnapshot.getKey();
                if (idPasien!=null)
                {
                    if (!idPasien.equalsIgnoreCase(menu.idPasien)||menu.status.equalsIgnoreCase("Selesai")||menu.status.equalsIgnoreCase("Batal"))
                    {
                        holder.ivMore.setVisibility(View.GONE);
                    }
                    else
                    {
                        holder.ivMore.setVisibility(View.VISIBLE);
                    }
                }


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




        if (menu.status.equalsIgnoreCase("Belum Konfirmasi")) {
            holder.statusBar.setBackground(context.getResources().getDrawable(R.drawable.status_belum_konfirmasi));
            holder.circle_timeline.setBackground(context.getResources().getDrawable(R.drawable.belum_konfirmasi));
        } else if (menu.status.equalsIgnoreCase("Selesai")) {
            holder.statusBar.setBackground(context.getResources().getDrawable(R.drawable.status_selesai));
            holder.circle_timeline.setBackground(context.getResources().getDrawable(R.drawable.selesai));
        } else if (menu.status.equalsIgnoreCase("Terkonfirmasi")) {
            holder.statusBar.setBackground(context.getResources().getDrawable(R.drawable.status_terkonfirmasi));
            holder.circle_timeline.setBackground(context.getResources().getDrawable(R.drawable.terkonfirmasi));
        } else if (menu.status.equalsIgnoreCase("Jadwal Ulang")) {
            holder.statusBar.setBackground(context.getResources().getDrawable(R.drawable.status_reschedule));
            holder.circle_timeline.setBackground(context.getResources().getDrawable(R.drawable.reschedule_color));
        }else if (menu.status.equalsIgnoreCase("Batal")) {
            holder.statusBar.setBackground(context.getResources().getDrawable(R.drawable.status_batal));
            holder.circle_timeline.setBackground(context.getResources().getDrawable(R.drawable.batal));

        }






        holder.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.ivMore,position);
            }
        });







//        holder.progressBar.setVisibility(View.GONE);

        return row;
    }

    private void showPopupMenu(View view, int position) {
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_jadwal, popup.getMenu());
        String status = util.getStatus(getContext());
        if (status.equalsIgnoreCase("Pasien"))
        {
            popup.getMenu().findItem(R.id.selesai).setVisible(false);
            popup.getMenu().findItem(R.id.konfirmasi).setVisible(false);

        }
        popup.setOnMenuItemClickListener(new CustomAdapterJadwal.MenuItemClickListener(position));
        popup.show();
    }

    class MenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        private int position;

        public MenuItemClickListener(int position) {
            this.position = position;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int id = item.getItemId();

            final Jadwal menu = namaList.get(position);

            if (id == R.id.jadwalUlang) {
                String tanggal = getDateTimestampFormat(menu.timeStamp);
                String waktu = getTime(menu.timeStamp);

                Intent intent = new Intent(getContext(), InputJadwalActivity.class);
                intent.putExtra("jadwalKey", menu.idJadwal);
                intent.putExtra("tanggal", tanggal);
                intent.putExtra("waktu", waktu);
                intent.putExtra("IDdokter", menu.idDokter);
                intent.putExtra("keluhan", menu.keluhan);
                intent.putExtra("IDpasien", menu.idPasien);
                intent.putExtra("namaDokter", menu.namaDokter);
                intent.putExtra("photoUrl",menu.getFoto());
                getContext().startActivity(intent);
                return true;
            } else if (id == R.id.batal) {

                AlertDialog.Builder alBuilder = new AlertDialog.Builder(getContext());
                alBuilder.setMessage("Apakah Anda yakin untuk membatalkan jadwal?");
                alBuilder.setTitle("Konfirmasi");
                final TextView input = new TextView(getContext());
                alBuilder.setView(input);
                alBuilder.setCancelable(true).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


//                        statusBar.setBackground(getContext().getResources().getDrawable(R.drawable.status_batal));
                        updateStatus("Batal", menu.idPasien,menu.idDokter,menu.idJadwal);


                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog alertDialog = alBuilder.create();
                alertDialog.show();

            } else if (id == R.id.selesai) {

                AlertDialog.Builder alBuilder = new AlertDialog.Builder(getContext());
                alBuilder.setMessage("Apakah Anda yakin untuk mengubah status jadwal menjadi 'Selesai'?");
                alBuilder.setTitle("Konfirmasi");
                final TextView input = new TextView(getContext());
                alBuilder.setView(input);
                alBuilder.setCancelable(true).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


//                        statusBar.setBackground(getContext().getResources().getDrawable(R.drawable.status_selesai));
                        updateStatus("Selesai", menu.idPasien,menu.idDokter,menu.idJadwal);


                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog alertDialog = alBuilder.create();
                alertDialog.show();

            } else if (id == R.id.konfirmasi) {

                AlertDialog.Builder alBuilder = new AlertDialog.Builder(getContext());
                alBuilder.setMessage("Apakah Anda yakin untuk mengubah status jadwal menjadi 'Konfirmasi'?");
                alBuilder.setTitle("Konfirmasi");
                final TextView input = new TextView(getContext());
                alBuilder.setView(input);
                alBuilder.setCancelable(true).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

//
//                        statusBar.setBackground(getContext().getResources().getDrawable(R.drawable.status_terkonfirmasi));
                        updateStatus("Terkonfirmasi", menu.idPasien,menu.idDokter,menu.idJadwal);


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


            return false;
        }
    }

    private String getDateTimestampFormat(long timeStamp){

        try{


            SimpleDateFormat dd = new SimpleDateFormat("dd-MM-yyyy");

            Date netDate = (new Date(timeStamp));
            String day = dd.format(netDate);
            return day;
        }
        catch(Exception ex){
            System.out.println("Log = "+ex);
            return "xx";
        }
    }


    public void updateStatus(final String status, final String idPasien, final String idDokter, final String jadwalKey) {
        FirebaseDatabase database;
        final FirebaseAuth mAuth;
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        final DatabaseReference rootRef, jadwalRef, notifRef, pasienRef,dokterRef;
        database = FirebaseDatabase.getInstance();
        rootRef = database.getReference();
        notifRef = rootRef.child("notification");
        dokterRef = rootRef.child("dokter");
        jadwalRef = rootRef.child("jadwal");
        pasienRef = rootRef.child("pasien");
        System.out.println("Jadwal:" + jadwalRef);
        System.out.println("jadwalKey: " + jadwalKey);
        pasienRef.child(idPasien).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String userIDPASIEN = dataSnapshot.child("userID").getValue(String.class);
                System.out.println("USER ID = " + userIDPASIEN);
                jadwalRef.child(jadwalKey).child("status").setValue(status);
                dokterRef.child(idDokter).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String userIDDOKTER = dataSnapshot.child("userID").getValue(String.class);
                        HashMap map = new HashMap();
                        map.put("idPasien", idPasien);
                        map.put("idJadwal", jadwalKey);
                        map.put("idDokterUser",userIDDOKTER);
                        map.put("idUser", userIDPASIEN);
                        System.out.println("MAP = " + map);
                        notifRef.push().setValue(map);
                        Log.d("Jadwal KEY UPDATE: ", jadwalKey);
                        System.out.println("idPasien, idJadwal, idDokterUser, idUserPasien = "+idPasien+" "+jadwalKey+" "+userIDDOKTER);
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

    private String getTime(long timeStamp){

        try{
            Date d=new Date(new Date().getTime()+28800000);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Date netDate = (new Date(timeStamp));
            return sdf.format(netDate);
        }
        catch(Exception ex){
            return "xx";
        }
    }

    private class namaFilter extends Filter
    {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults result = new FilterResults();
            charSequence = charSequence.toString().toLowerCase();
            System.out.println("charSequence sebelum = "+charSequence);
//            if (keyWord!=null)
//            {
//                charSequence = keyWord;
//            }
//            System.out.println("charSequence sekarang = "+charSequence);


            if(charSequence == null && charSequence.length() == 0) {

                System.out.println("CHAR KOSONG");
                    ArrayList<Jadwal> listNama = new ArrayList<Jadwal>(originalList);
                    result.values = listNama;
                    result.count = listNama.size();

            }
            else
            {

                ArrayList<Jadwal> listNama = new ArrayList<Jadwal>(originalList);
                ArrayList<Jadwal> nlistNama = new ArrayList<Jadwal>();
                int count = listNama.size();
                System.out.println("charsequence = "+charSequence);
//                listNama.addAll(originalList);

                System.out.println("FILTERING");
                for(int i=0; i<count; i++)
                {


                    Jadwal object = listNama.get(i);
                    System.out.println("object = "+object.toString().toLowerCase());
                    if (object.toString().toLowerCase().contains(charSequence.toString()))
                    {
                        nlistNama.add(object);

                    }

//                    String value = null;
//                    if (flags.equalsIgnoreCase("id"))
//                    {
//                        value = object.getIdPasien().toLowerCase();
//                    }
//                    else if (flags.equalsIgnoreCase("nama"))
//                    {
//                        value = object.getNamaPasien().toLowerCase();
//                    }
//
//
//
//                    if(value.contains(charSequence.toString()))
//                    {
//
//                    }
                }

                result.values = nlistNama;
                result.count = nlistNama.size();


            }


            return result;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            System.out.println("PUBLISH RESULT");
            namaList = (ArrayList<Jadwal>) results.values;
            notifyDataSetChanged();
            System.out.println("result = "+namaList);
            System.out.println("count = "+namaList.size());
            clear();
            for(int i = 0; i<namaList.size(); i++)
            {
                add(namaList.get(i));
                notifyDataSetInvalidated();
            }
        }
    }

    @Override
    public void notifyDataSetChanged() {

        super.notifyDataSetChanged();
    }
}
