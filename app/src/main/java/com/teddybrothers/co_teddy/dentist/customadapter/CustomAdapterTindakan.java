package com.teddybrothers.co_teddy.dentist.customadapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.teddybrothers.co_teddy.dentist.R;
import com.teddybrothers.co_teddy.dentist.entity.Tindakan;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by co_teddy on 3/8/2017.
 */
public class CustomAdapterTindakan extends ArrayAdapter<Tindakan> implements Filterable,SectionIndexer {
    Context context;
    int resourse,flagStatus=0;
    public String keyWord;

    private LayoutInflater mInflater;
    FirebaseAuth mAuth;
    FirebaseDatabase databaseUtama,databaseKlinik;
    DatabaseReference mUserRef, mRoot,mTindakan,mUser;

    HashMap<String,Integer> alphaIndexer;
    String[] sections;

    private ArrayList<Tindakan> originalList;
    private ArrayList<Tindakan> namaList;

    private namaFilter filter;



    public CustomAdapterTindakan(Context context, int resource, ArrayList<Tindakan> data) {
        super(context, resource,data);
        System.out.println("CUSTOMADAPTER CALLING");
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.resourse = resource;

        this.originalList = new ArrayList<Tindakan>(data);
        this.namaList= new ArrayList<Tindakan>(data);
        this.filter=new namaFilter();


        alphaIndexer = new HashMap<String, Integer>();
        int size = data.size();

        for (int x = 0; x < size; x++) {

            Tindakan object = data.get(x);
            String value = object.getNamaTindakan().toLowerCase();

            // get the first letter of the store
            String ch = value.substring(0, 1);
            // convert to uppercase otherwise lowercase a -z will be sorted
            // after upper A-Z
            ch = ch.toUpperCase();
            // put only if the key does not exist
            if (!alphaIndexer.containsKey(ch))
                alphaIndexer.put(ch, x);
        }

        Set<String> sectionLetters = alphaIndexer.keySet();
        // create a list from the set to sort
        ArrayList<String> sectionList = new ArrayList<String>(
                sectionLetters);
        Collections.sort(sectionList);
        sections = new String[sectionList.size()];
        sections = sectionList.toArray(sections);
        System.out.println("sectionsList = "+sectionList);
        System.out.println("sections = "+sections);


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

        TextView tvNamaTindakan,tvHargaTindakan;

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
        mTindakan = mRoot.child("tindakan");
        mUser = mRoot.child("users");

        if (row==null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(resourse,parent,false);
            holder = new MenuHolder();
            holder.tvNamaTindakan = (TextView) row.findViewById(R.id.tvNamaTindakan);
            holder.tvHargaTindakan = (TextView) row.findViewById(R.id.tvHargaTindakan);
            holder.ivMore = (ImageView) row.findViewById(R.id.moreTindakan);
            holder.ivMore.setVisibility(View.INVISIBLE);
            row.setTag(holder);
        }
        else
        {
            holder = (MenuHolder) row.getTag();
        }

        System.out.println("Position = "+position);

        final Tindakan menu = namaList.get(position);
        holder.tvNamaTindakan.setText(menu.getNamaTindakan());

        int hargaTindakan = Integer.parseInt(menu.getHargaTindakan());
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        DecimalFormat decimalFormat = new DecimalFormat("Rp ###,###,###,###", symbols);
        final String RpHargaTindakan = decimalFormat.format(hargaTindakan);
        holder.tvHargaTindakan.setText(RpHargaTindakan);




//        holder.progressBar.setVisibility(View.GONE);

        return row;
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


                    ArrayList<Tindakan> listNama = new ArrayList<Tindakan>(originalList);
                    result.values = listNama;
                    result.count = listNama.size();

            }
            else
            {

                ArrayList<Tindakan> listNama = new ArrayList<Tindakan>(originalList);
                ArrayList<Tindakan> nlistNama = new ArrayList<Tindakan>();
                int count = listNama.size();
                System.out.println("charsequence = "+charSequence);
//                listNama.addAll(originalList);

                System.out.println("FILTERING");
                for(int i=0; i<count; i++)
                {


                    Tindakan object = listNama.get(i);
                    String value = object.getNamaTindakan().toLowerCase();


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
            namaList = (ArrayList<Tindakan>) results.values;
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
