package com.teddybrothers.co_teddy.dentist.customadapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
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

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by co_teddy on 3/8/2017.
 */
public class CustomAdapterHistoryJadwal extends ArrayAdapter<Jadwal> implements Filterable,SectionIndexer {
    Context context;
    int resourse,flagStatus=0;
    public String keyWord;
    public static final String[] MONTHS = {"Januari", "Febuari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
    private LayoutInflater mInflater;
    FirebaseAuth mAuth;
    FirebaseDatabase databaseUtama,databaseKlinik;
    DatabaseReference mUserRef, mRoot,mJadwal,mUser;
    Utilities util = new Utilities();
    HashMap<String,Integer> alphaIndexer;
    String[] sections;

    private ArrayList<Jadwal> originalList;
    private ArrayList<Jadwal> namaList;

    private namaFilter filter;



    public CustomAdapterHistoryJadwal(Context context, int resource, ArrayList<Jadwal> data) {
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
    public Object[] getSections() {
        return sections;
    }

    @Override
    public int getPositionForSection(int i) {
        return alphaIndexer.get(sections[i]);
    }

    @Override
    public int getSectionForPosition(int i) {
        return 0;

    }

    class MenuHolder{

        TextView tv_time,tvNamaDokter,tvTanggal,tvStatusBar,tvIDPasien,tvNama;
        CircleImageView ivPhoto;
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
        mJadwal = mRoot.child("Jadwal");
        mUser = mRoot.child("users");

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

            holder.ivMore = (ImageView) row.findViewById(R.id.ivMore);

            row.setTag(holder);
        }
        else
        {
            holder = (MenuHolder) row.getTag();
        }
        final Jadwal menu = namaList.get(position);
        System.out.println("Position = "+position);

        String tanggal = getDate(menu.getTimeStamp());
        String waktu = getTime(menu.getTimeStamp());

        String status = util.getStatus(getContext());


        holder.tvNamaDokter.setText("drg. "+menu.getNamaDokter());
        holder.tvNama.setText(menu.getNamaPasien());
        holder.tvTanggal.setText(tanggal);
        holder.tv_time.setText(waktu);
        holder.tvIDPasien.setText(menu.getIdPasien());
        holder.tvStatusBar.setText(menu.getStatus());

        if (status.equalsIgnoreCase("Pasien"))
        {
            holder.tvNama.setVisibility(View.GONE);
        }
        else if (status.equalsIgnoreCase("Dokter"))
        {
            holder.tvNamaDokter.setVisibility(View.GONE);
        }

        if (menu.getStatus().equalsIgnoreCase("Selesai")) {
            holder.tvStatusBar.setTextColor(Color.GREEN);

        } else if (menu.getStatus().equalsIgnoreCase("Terkonfirmasi")) {
            holder.tvStatusBar.setTextColor(Color.parseColor("#FFEC8E02"));

        } else if (menu.getStatus().equalsIgnoreCase("Belum Konfirmasi")) {
            holder.tvStatusBar.setTextColor(Color.BLUE);

        } else if (menu.getStatus().equalsIgnoreCase("Batal")) {
            holder.tvStatusBar.setTextColor(Color.RED);

        }


//        holder.progressBar.setVisibility(View.GONE);

        return row;
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
                    String value = object.getNamaPasien().toLowerCase();


                    if(value.contains(charSequence.toString()))
                    {
                        nlistNama.add(object);

                    }
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
