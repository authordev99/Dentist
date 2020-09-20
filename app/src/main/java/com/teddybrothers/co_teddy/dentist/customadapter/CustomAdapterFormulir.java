package com.teddybrothers.co_teddy.dentist.customadapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.teddybrothers.co_teddy.dentist.FormActivity;
import com.teddybrothers.co_teddy.dentist.R;
import com.teddybrothers.co_teddy.dentist.entity.Formulir;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by co_teddy on 3/8/2017.
 */
public class CustomAdapterFormulir extends ArrayAdapter<Formulir> implements Filterable,SectionIndexer {
    Context context;
    int resourse,flagStatus=0;
    public String keyWord;

    private LayoutInflater mInflater;
    FirebaseAuth mAuth;
    FirebaseDatabase databaseUtama,databaseKlinik;
    DatabaseReference mUserRef, mRoot,mFormulir,mUser;

    HashMap<String,Integer> alphaIndexer;
    String[] sections;

    private ArrayList<Formulir> originalList;
    private ArrayList<Formulir> namaList;

    private namaFilter filter;



    public CustomAdapterFormulir(Context context, int resource, ArrayList<Formulir> data) {
        super(context, resource,data);
        System.out.println("CUSTOMADAPTER CALLING");
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.resourse = resource;

        this.originalList = new ArrayList<Formulir>(data);
        this.namaList= new ArrayList<Formulir>(data);
        this.filter=new namaFilter();


        alphaIndexer = new HashMap<String, Integer>();
        int size = data.size();

        for (int x = 0; x < size; x++) {

            Formulir object = data.get(x);
            String value = object.getNamaPasien().toLowerCase();

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

        TextView tvNamaPasien,tvTindakan,tvTglForm,tvStatus;
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
        mFormulir = mRoot.child("formulir");
        mUser = mRoot.child("users");

        if (row==null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(resourse,parent,false);
            holder = new MenuHolder();
            holder.tvNamaPasien = (TextView) row.findViewById(R.id.tvNamaPasien);
            holder.tvTindakan = (TextView) row.findViewById(R.id.tvTindakan);
            holder.tvTglForm = (TextView) row.findViewById(R.id.tvTglForm);
            holder.tvStatus = (TextView) row.findViewById(R.id.tvStatus);
            holder.ivMore = (ImageView) row.findViewById(R.id.ivMore);



            row.setTag(holder);
        }
        else
        {
            holder = (MenuHolder) row.getTag();
        }

        System.out.println("Position = "+position);

        final Formulir menu = namaList.get(position);
        holder.tvNamaPasien.setText(menu.getNamaPasien()+" | "+menu.getIdPasien());
        holder.tvTindakan.setText(menu.getTindakan());
        holder.tvTglForm.setText(menu.getTglForm());
        holder.tvStatus.setText(menu.getStatus());




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
        // inflate menu
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_pasien_dokter, popup.getMenu());
        popup.getMenu().findItem(R.id.aktif).setVisible(false);
        popup.getMenu().findItem(R.id.delete).setVisible(false);
        popup.getMenu().findItem(R.id.nonAktif).setVisible(false);
        popup.getMenu().findItem(R.id.rekamMedis).setVisible(false);
        popup.setOnMenuItemClickListener(new CustomAdapterFormulir.MenuItemClickListener(position));
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
            final Formulir menu = namaList.get(position);

            if (id == R.id.edit) {
                Intent intent = new Intent(getContext(), FormActivity.class);
                intent.putExtra("formkey", menu.idFormulir);
                getContext().startActivity(intent);
                return true;
            }

            return false;
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


                    ArrayList<Formulir> listNama = new ArrayList<Formulir>(originalList);
                    result.values = listNama;
                    result.count = listNama.size();

            }
            else
            {

                ArrayList<Formulir> listNama = new ArrayList<Formulir>(originalList);
                ArrayList<Formulir> nlistNama = new ArrayList<Formulir>();
                int count = listNama.size();
                System.out.println("charsequence = "+charSequence);
//                listNama.addAll(originalList);

                System.out.println("FILTERING");
                for(int i=0; i<count; i++)
                {


                    Formulir object = listNama.get(i);
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
            namaList = (ArrayList<Formulir>) results.values;
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
