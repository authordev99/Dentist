package com.teddybrothers.co_teddy.dentist.customadapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.teddybrothers.co_teddy.dentist.R;
import com.teddybrothers.co_teddy.dentist.entity.RekamMedis;
import com.teddybrothers.co_teddy.dentist.entity.Tindakan;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by co_teddy on 3/8/2017.
 */
public class CustomAdapterLogMedis extends ArrayAdapter<RekamMedis> {
    Context context;
    int resourse;
    public String keyWord,flagsStatus;
    public static final String[] MONTHS = {"Januari", "Febuari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
    private LayoutInflater mInflater;
    FirebaseAuth mAuth;
    FirebaseDatabase databaseUtama,databaseKlinik;
    DatabaseReference mUserRef, mRoot,mTindakan,mUser;

    HashMap<String,Integer> alphaIndexer;
    String[] sections;

    private ArrayList<RekamMedis> originalList;
    private ArrayList<RekamMedis> namaList;






    public CustomAdapterLogMedis(Context context, int resource, ArrayList<RekamMedis> data,String flags) {
        super(context, resource,data);
        System.out.println("CUSTOMADAPTER CALLING");
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.resourse = resource;

        this.originalList = new ArrayList<RekamMedis>(data);
        this.namaList= new ArrayList<RekamMedis>(data);
        this.flagsStatus = flags;


    }



    class MenuHolder{

        TextView tvNamaDokter,tvUpdateAt;

        ImageView ivMore;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final MenuHolder holder;

        if (row==null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(resourse,parent,false);
            holder = new MenuHolder();
            holder.tvNamaDokter = (TextView) row.findViewById(R.id.tvNamaDokter);
            holder.tvUpdateAt = (TextView) row.findViewById(R.id.tvUpdateAt);

            row.setTag(holder);
        }
        else
        {
            holder = (MenuHolder) row.getTag();
        }

        System.out.println("Position = "+position);

        final RekamMedis menu = namaList.get(position);
        holder.tvNamaDokter.setText("drg. "+menu.getNamaDokter());
        String updateAt = null;
        if (flagsStatus.equalsIgnoreCase("rencanaPerawatan"))
        {
            updateAt = menu.RencanaPerawatanConvert();
        }
        else if (flagsStatus.equalsIgnoreCase("riwayatMedis"))
        {
            updateAt = getDate(Long.parseLong(menu.getUpdateTime()));
        }
        holder.tvUpdateAt.setText(updateAt);


        return row;
    }



    public String getDate(long timeStamp){

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
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String time = sdf.format(netDate);

            String tanggal = dayWeek+", "+day+" "+nameMonth+" "+year+" "+time;
            return tanggal;
        }
        catch(Exception ex){
            System.out.println("Log = "+ex);
            return "xx";
        }
    }
}
