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
import android.widget.ProgressBar;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.teddybrothers.co_teddy.dentist.R;
import com.teddybrothers.co_teddy.dentist.entity.Invoice;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by co_teddy on 3/8/2017.
 */
public class CustomAdapterBilling extends ArrayAdapter<Invoice> implements Filterable, SectionIndexer {
    Context context;
    int resourse, flagStatus = 0;
    public String keyWord;

    private LayoutInflater mInflater;
    FirebaseAuth mAuth;
    FirebaseDatabase databaseUtama, databaseKlinik;
    DatabaseReference mUserRef, mRoot, mInvoice, mUser;
    public static final String[] MONTHS = {"Januari", "Febuari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
    HashMap<String, Integer> alphaIndexer;
    String[] sections;

    private ArrayList<Invoice> originalList;
    private ArrayList<Invoice> namaList;

    private namaFilter filter;


    public CustomAdapterBilling(Context context, int resource, ArrayList<Invoice> data) {
        super(context, resource, data);
        System.out.println("CUSTOMADAPTER CALLING");
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.resourse = resource;

        this.originalList = new ArrayList<Invoice>(data);
        this.namaList = new ArrayList<Invoice>(data);
        this.filter = new namaFilter();


//        alphaIndexer = new HashMap<String, Integer>();
//        int size = data.size();
//
//        for (int x = 0; x < size; x++) {
//
//            Invoice object = data.get(x);
//            String value = object.getNama().toLowerCase();
//
//            // get the first letter of the store
//            String ch = value.substring(0, 1);
//            // convert to uppercase otherwise lowercase a -z will be sorted
//            // after upper A-Z
//            ch = ch.toUpperCase();
//            // put only if the key does not exist
//            if (!alphaIndexer.containsKey(ch))
//                alphaIndexer.put(ch, x);
//        }
//
//        Set<String> sectionLetters = alphaIndexer.keySet();
//        // create a list from the set to sort
//        ArrayList<String> sectionList = new ArrayList<String>(
//                sectionLetters);
//        Collections.sort(sectionList);
//        sections = new String[sectionList.size()];
//        sections = sectionList.toArray(sections);
//        System.out.println("sectionsList = "+sectionList);
//        System.out.println("sections = "+sections);


    }


    @NonNull
    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new namaFilter();
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

    class MenuHolder {

        TextView tvNamaPasien, tvIdInvoice, tvTotalHarga, tvTanggal, tvNamaDokter,tvIDPasien;




    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final MenuHolder holder;


        if (databaseUtama == null) {
            databaseUtama = FirebaseDatabase.getInstance();
        }

        mRoot = databaseUtama.getReference();
        mInvoice = mRoot.child("invoice");
        mUser = mRoot.child("users");

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(resourse, parent, false);
            holder = new MenuHolder();
            holder.tvNamaPasien = row.findViewById(R.id.tvNamaPasien);
            holder.tvIDPasien = row.findViewById(R.id.tvIDPasien);
            holder.tvIdInvoice = row.findViewById(R.id.tvIdInvoice);
            holder.tvTotalHarga = row.findViewById(R.id.tvTotalHarga);
            holder.tvTanggal = row.findViewById(R.id.tvTanggal);
            holder.tvNamaDokter = row.findViewById(R.id.tvNamaDokter);

//
            row.setTag(holder);
        } else {
            holder = (MenuHolder) row.getTag();
        }

        System.out.println("Position = " + position);

        final Invoice menu = namaList.get(position);

        System.out.println("check = " + menu.getTanggalInvoice() + " " + menu.getNamaDokter() + " " + menu.getNamaPasien() + " " + menu.getTotalHarga());
        holder.tvNamaPasien.setText(menu.getNamaPasien());
        holder.tvIDPasien.setText(menu.getIdPasien());
        holder.tvIdInvoice.setText("INV-" + menu.getNoInvoice());
        holder.tvTanggal.setText(getDate(menu.getTanggalInvoice()));
        holder.tvNamaDokter.setText("drg. " + menu.getNamaDokter());
        int finalTotalHarga = 0;
        String ttlHarga = menu.getTotalHarga().replace("Rp ", "");
        if (ttlHarga.contains(",")) {
            finalTotalHarga = Integer.parseInt(ttlHarga.replace(",", ""));
        } else {
            finalTotalHarga = Integer.parseInt(ttlHarga);
        }

        System.out.println("finalTotalHarga= " + finalTotalHarga);

        int diskonHarga = Integer.parseInt(menu.getDiskon());
        int grandTotal = finalTotalHarga - diskonHarga;


        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        DecimalFormat decimalFormat = new DecimalFormat("Rp ###,###,###,###", symbols);
        final String RpGrandTotal = decimalFormat.format(grandTotal);

        holder.tvTotalHarga.setText(RpGrandTotal);


        return row;
    }

    private String getDate(long timeStamp) {

        try {

            SimpleDateFormat ee = new SimpleDateFormat("EEEE");
            SimpleDateFormat dd = new SimpleDateFormat("dd");
            SimpleDateFormat MM = new SimpleDateFormat("MM");
            SimpleDateFormat yyyy = new SimpleDateFormat("yyyy");
            Date netDate = (new Date(timeStamp));
            String dayWeek = ee.format(netDate);
            String day = dd.format(netDate);
            String month = MM.format(netDate);
            String nameMonth = MONTHS[Integer.parseInt(month) - 1];
            String year = yyyy.format(netDate);
            String tanggal = dayWeek + ", " + day + " " + nameMonth + " " + year;
            return tanggal;
        } catch (Exception ex) {
            System.out.println("Log = " + ex);
            return "xx";
        }
    }


    private class namaFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults result = new FilterResults();
            charSequence = charSequence.toString().toLowerCase();
            System.out.println("charSequence sebelum = " + charSequence);


            if (charSequence == null && charSequence.length() == 0) {

                System.out.println("CHAR KOSONG");


                ArrayList<Invoice> listNama = new ArrayList<Invoice>(originalList);
                result.values = listNama;
                result.count = listNama.size();

            } else {

                ArrayList<Invoice> listNama = new ArrayList<Invoice>(originalList);
                ArrayList<Invoice> nlistNama = new ArrayList<Invoice>();
                int count = listNama.size();
                System.out.println("charsequence = " + charSequence);
//                listNama.addAll(originalList);

                System.out.println("FILTERING");
                for (int i = 0; i < count; i++) {


                    Invoice object = listNama.get(i);
                    System.out.println("object = " + object.toString().toLowerCase());
                    if (object.toString().toLowerCase().contains(charSequence.toString())) {
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
            namaList = (ArrayList<Invoice>) results.values;
            notifyDataSetChanged();
            System.out.println("result = " + namaList);
            System.out.println("count = " + namaList.size());

            clear();


            for (int i = 0; i < namaList.size(); i++) {
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
