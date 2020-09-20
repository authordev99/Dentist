package com.teddybrothers.co_teddy.dentist.customadapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ProgressBar;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.teddybrothers.co_teddy.dentist.DaftarPasienActivity;
import com.teddybrothers.co_teddy.dentist.R;
import com.teddybrothers.co_teddy.dentist.RekamMedisActivity;
import com.teddybrothers.co_teddy.dentist.entity.Pasien;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by co_teddy on 3/8/2017.
 */
public class CustomAdapterPasien extends ArrayAdapter<Pasien> implements Filterable {
    Context context;
    int resourse, flagStatus = 0;
    public String keyWord;

    private LayoutInflater mInflater;
    FirebaseAuth mAuth;
    FirebaseDatabase databaseUtama, databaseKlinik;
    DatabaseReference mUserRef, mRoot, mPasien, mUser;

    HashMap<String, Integer> alphaIndexer;
    String[] sections;

    private ArrayList<Pasien> originalList;
    private ArrayList<Pasien> namaList;

    private namaFilter filter;


    public CustomAdapterPasien(Context context, int resource, ArrayList<Pasien> data) {
        super(context, resource, data);
        System.out.println("CUSTOMADAPTER CALLING");
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.resourse = resource;

        this.originalList = new ArrayList<Pasien>(data);
        this.namaList = new ArrayList<Pasien>(data);
        this.filter = new namaFilter();


        alphaIndexer = new HashMap<String, Integer>();
        int size = data.size();

        for (int x = 0; x < size; x++) {

            Pasien object = data.get(x);
            String value = object.getNama().toLowerCase();

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
        System.out.println("sectionsList = " + sectionList);
        System.out.println("sections = " + sections);


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
    public int getCount() {
        return namaList.size();
    }

    class MenuHolder {

        TextView tvNamaPasien, tvUmur, tvStatusUser, tvAlamat,tvKategori;
        ProgressBar progressBar;
        CircleImageView photoPasien;
        ImageView ivMore;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final MenuHolder holder;


        if (databaseUtama == null) {
            databaseUtama = FirebaseDatabase.getInstance();
        }

        mRoot = databaseUtama.getReference();
        mPasien = mRoot.child("pasien");
        mUser = mRoot.child("users");

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(resourse, parent, false);
            holder = new MenuHolder();
            holder.tvNamaPasien = (TextView) row.findViewById(R.id.tvNamaPasien);
            holder.tvUmur = (TextView) row.findViewById(R.id.tvUmur);
            holder.tvStatusUser = (TextView) row.findViewById(R.id.tvStatusUser);
            holder.tvAlamat = (TextView) row.findViewById(R.id.tvAlamat);
            holder.tvKategori = (TextView) row.findViewById(R.id.tvKategori);
            holder.photoPasien = (CircleImageView) row.findViewById(R.id.photoPasien);
            holder.ivMore = (ImageView) row.findViewById(R.id.ivMore);
            holder.ivMore.setVisibility(View.INVISIBLE);
            row.setTag(holder);
        } else {
            holder = (MenuHolder) row.getTag();
        }

        System.out.println("Position = " + position);
        DaftarPasienActivity daftarPasienActivity = new DaftarPasienActivity();
        final Pasien menu = namaList.get(position);
        holder.tvNamaPasien.setText(menu.getNama());
        holder.tvAlamat.setText(menu.getIdPasien());
        holder.tvKategori.setText(daftarPasienActivity.Kategori[Integer.parseInt(menu.getKategori())]);
        holder.tvUmur.setText(menu.getTanggalLahir());
        holder.tvStatusUser.setText(menu.getStatusUser());

        Glide.with(getContext()).load(menu.getPhotoUrl())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.photoPasien);


        holder.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.ivMore, position);
            }
        });


//        holder.progressBar.setVisibility(View.GONE);

        return row;
    }

    private void showPopupMenu(View view, int position) {
        System.out.println("position popup = " + position);
        final Pasien menu = namaList.get(position);
        // inflate menu
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_pasien_dokter, popup.getMenu());
        popup.getMenu().findItem(R.id.rekamMedis).setVisible(true);
        popup.getMenu().findItem(R.id.delete).setVisible(false);
        if (menu.getStatusUser().equalsIgnoreCase("Aktif")) {
            popup.getMenu().findItem(R.id.aktif).setVisible(false);
        } else if (menu.getStatusUser().equalsIgnoreCase("Non Aktif")) {
            popup.getMenu().findItem(R.id.nonAktif).setVisible(false);
        }


        popup.setOnMenuItemClickListener(new CustomAdapterPasien.MenuItemClickListener(position));
        popup.show();
    }

    class MenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        private int position;

        public MenuItemClickListener(int position) {

            this.position = position;
            System.out.println("positionmenuitemclick = " + position);
        }


        @Override
        public boolean onMenuItemClick(MenuItem item) {
            System.out.println("positionmenuitemclick 1 = " + position);

            final Pasien menu = namaList.get(position);

            int id = item.getItemId();

            if (id == R.id.edit) {
                Intent intent = new Intent(getContext(), DaftarPasienActivity.class);
                intent.putExtra("idPasien", menu.getIdPasien());
                intent.putExtra("ubah", "ubah");
                intent.putExtra("userIDPasien", menu.getUserID());
                getContext().startActivity(intent);
                return true;
            } else if (id == R.id.delete) {

                AlertDialog.Builder alBuilder = new AlertDialog.Builder(getContext());
                alBuilder.setMessage("Apakah Anda yakin untuk menghapus data pasien?");
                alBuilder.setTitle("Konfirmasi");
                final TextView input = new TextView(getContext());
                alBuilder.setView(input);
                alBuilder.setCancelable(true).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

//                        progressDialog = new ProgressDialog(getContext());
//                        progressDialog.setMessage("Loading...");
//                        progressDialog.show();
//                        deletePasien();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog alertDialog = alBuilder.create();
                alertDialog.show();


            } else if (id == R.id.rekamMedis) {
                final Intent intent = new Intent(getContext(), RekamMedisActivity.class);
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference mPasien = database.getReference().child("pasien");
                System.out.println("idPasien diviewPasienRekamMedis = " + menu.getIdPasien());
                intent.putExtra("idPasien", menu.getIdPasien());
                String statusProfil = "profil";
                intent.putExtra("status", statusProfil);
                getContext().startActivity(intent);
                notifyDataSetChanged();


            } else if (id == R.id.nonAktif) {

                ubahStatusAktif("Non Aktif", menu.getIdPasien(), menu.getNama(), menu.getUserID());
            } else if (id == R.id.aktif) {

                ubahStatusAktif("Aktif", menu.getIdPasien(), menu.getNama(), menu.getUserID());
            }


            return false;
        }
    }

    public void ubahStatusAktif(final String statusUser, final String pasienKey, String nama, final String userID) {
        AlertDialog.Builder alBuilder = new AlertDialog.Builder(getContext());
        alBuilder.setMessage("Apakah Anda yakin untuk men-" + statusUser + "kan Akun " + nama + "?");
        alBuilder.setTitle("Konfirmasi");
        final TextView input = new TextView(getContext());
        alBuilder.setView(input);
        alBuilder.setCancelable(true).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

//                progressDialog = new ProgressDialog(itemView.getContext());
//                progressDialog.setMessage("Loading...");
//                progressDialog.show();
                FirebaseDatabase database;
                DatabaseReference rootRef, pasienRef, userRef;
                database = FirebaseDatabase.getInstance();
                rootRef = database.getReference();
                pasienRef = rootRef.child("pasien");
                userRef = rootRef.child("users");

                pasienRef.child(pasienKey).child("statusUser").setValue(statusUser);
                userRef.child(userID).child("statusUser").setValue(statusUser);

//                progressDialog.dismiss();
                notifyDataSetChanged();
                clear();
//                getFilter().filter(keyWord);
                flagStatus = 1;
                System.out.println("keyword 2= " + keyWord);


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

//    public void deletePasien(){
//
//        FirebaseDatabase database;
//        DatabaseReference rootRef,pasienRef,userRef;
//        database= FirebaseDatabase.getInstance();
//        rootRef= database.getReference();
//        pasienRef= rootRef.child("pasien");
//        userRef = rootRef.child("users");
//
//        userRef.child(displayedPasien.getUserID()).removeValue();
//        pasienRef.child(pasienKey).removeValue();
//
//        progressDialog.dismiss();
//        Log.d("pasienKey: ",pasienKey);
//    }

    private class namaFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults result = new FilterResults();
            charSequence = charSequence.toString().toLowerCase();
            System.out.println("charSequence sebelum = " + charSequence);
//            if (keyWord!=null)
//            {
//                charSequence = keyWord;
//            }
//            System.out.println("charSequence sekarang = "+charSequence);


            if (charSequence == null && charSequence.length() == 0) {

                System.out.println("CHAR KOSONG");


                ArrayList<Pasien> listNama = new ArrayList<Pasien>(originalList);
                result.values = listNama;
                result.count = listNama.size();

            } else {

                ArrayList<Pasien> listNama = new ArrayList<Pasien>(originalList);
                ArrayList<Pasien> nlistNama = new ArrayList<Pasien>();
                int count = listNama.size();
                System.out.println("charsequence = " + charSequence);
//                listNama.addAll(originalList);

                System.out.println("FILTERING");
                for (int i = 0; i < count; i++) {


                    Pasien object = listNama.get(i);
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
            namaList = (ArrayList<Pasien>) results.values;
            notifyDataSetChanged();
            System.out.println("result = " + namaList);
            System.out.println("count = " + namaList.size());
            clear();
            for (int i = 0; i < namaList.size(); i++)
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
