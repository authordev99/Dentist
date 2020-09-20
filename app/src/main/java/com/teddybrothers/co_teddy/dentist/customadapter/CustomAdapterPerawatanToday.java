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
import com.teddybrothers.co_teddy.dentist.entity.Invoice;
import com.teddybrothers.co_teddy.dentist.entity.Perawatan;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by co_teddy on 3/8/2017.
 */
public class CustomAdapterPerawatanToday extends ArrayAdapter<Perawatan> implements Filterable {
    Context context;
    int resourse,flagStatus=0;
    public String keyWord;

    private LayoutInflater mInflater;
    FirebaseAuth mAuth;
    FirebaseDatabase databaseUtama,databaseKlinik;
    DatabaseReference mUserRef, mRoot,mPerawatan,mUser;

    HashMap<String,Integer> alphaIndexer;
    String[] sections;

    private ArrayList<Perawatan> originalList;
    private ArrayList<Perawatan> namaList;

    private namaFilter filter;



    public CustomAdapterPerawatanToday(Context context, int resource, ArrayList<Perawatan> data) {
        super(context, resource,data);
        System.out.println("CUSTOMADAPTER CALLING");
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.resourse = resource;

        this.originalList = new ArrayList<Perawatan>(data);
        this.namaList= new ArrayList<Perawatan>(data);
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

        TextView tvNoGigi,tvNama,tvTanggal,tvStatusPerawatan;

        ImageView ivMore,thumb;

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
        mPerawatan = mRoot.child("Perawatan");
        mUser = mRoot.child("users");

        if (row==null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(resourse,parent,false);
            holder = new MenuHolder();
            holder.tvNoGigi = (TextView) row.findViewById(R.id.tvNoGigi);
            holder.tvNama = (TextView) row.findViewById(R.id.tvNama);
            holder.thumb = (ImageView) row.findViewById(R.id.thumb);
//            holder.tvStatusPerawatan = (TextView) row.findViewById(R.id.tvStatusPerawatan);
            holder.tvTanggal = (TextView) row.findViewById(R.id.tvTanggal);
            holder.ivMore = (ImageView) row.findViewById(R.id.ivMore);
            row.setTag(holder);
        }
        else
        {
            holder = (MenuHolder) row.getTag();
        }

        System.out.println("Position = "+position);

        final Perawatan menu = namaList.get(position);
        holder.tvNoGigi.setText(menu.getNoGigi());
        holder.tvNama.setText(menu.getNamaTindakan());
//        holder.tvStatusPerawatan.setText(menu.getStatus());
        holder.tvTanggal.setText(getDateTimestampFormat(menu.getTanggal()));


        String kodeGigiList = menu.getKodeGigi();

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

                int res = getContext().getResources().getIdentifier(kodeGigiList, "drawable", getContext().getPackageName());
                System.out.println("RES = " + res);
                holder.thumb.setBackgroundResource(res);
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


                    ArrayList<Perawatan> listNama = new ArrayList<Perawatan>(originalList);
                    result.values = listNama;
                    result.count = listNama.size();

            }
            else
            {

                ArrayList<Perawatan> listNama = new ArrayList<Perawatan>(originalList);
                ArrayList<Perawatan> nlistNama = new ArrayList<Perawatan>();
                int count = listNama.size();
                System.out.println("charsequence = "+charSequence);
//                listNama.addAll(originalList);

                System.out.println("FILTERING");
                for(int i=0; i<count; i++)
                {


                    Perawatan object = listNama.get(i);
                    System.out.println("object = "+object.toString().toLowerCase());
                    if (object.toString().toLowerCase().contains(charSequence.toString()))
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
            namaList = (ArrayList<Perawatan>) results.values;
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
