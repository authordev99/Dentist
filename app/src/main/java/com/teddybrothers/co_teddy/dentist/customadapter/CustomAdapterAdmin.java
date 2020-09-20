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
import com.teddybrothers.co_teddy.dentist.RegisterAdminActivity;
import com.teddybrothers.co_teddy.dentist.R;
import com.teddybrothers.co_teddy.dentist.RegisterAdminActivity;
import com.teddybrothers.co_teddy.dentist.entity.Admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by co_teddy on 3/8/2017.
 */
public class CustomAdapterAdmin extends ArrayAdapter<Admin> implements Filterable,SectionIndexer {
    Context context;
    int resourse,flagStatus=0;
    public String keyWord;

    private LayoutInflater mInflater;
    FirebaseAuth mAuth;
    FirebaseDatabase databaseUtama,databaseKlinik;
    DatabaseReference mUserRef, mRoot,mAdmin,mUser;

    HashMap<String,Integer> alphaIndexer;
    String[] sections;

    private ArrayList<Admin> originalList;
    private ArrayList<Admin> namaList;

    private namaFilter filter;



    public CustomAdapterAdmin(Context context, int resource, ArrayList<Admin> data) {
        super(context, resource,data);
        System.out.println("CUSTOMADAPTER CALLING");
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.resourse = resource;

        this.originalList = new ArrayList<Admin>(data);
        this.namaList= new ArrayList<Admin>(data);
        this.filter=new namaFilter();


        alphaIndexer = new HashMap<String, Integer>();
        int size = data.size();

        for (int x = 0; x < size; x++) {

            Admin object = data.get(x);
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

        TextView tvNamaAdmin,tvStatusUser,tvDateCreated;
        CircleImageView photoAdmin;
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
        mAdmin = mRoot.child("Admin");
        mUser = mRoot.child("users");

        if (row==null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(resourse,parent,false);
            holder = new MenuHolder();
            holder.tvNamaAdmin = (TextView) row.findViewById(R.id.tvNamaAdmin);
            holder.tvStatusUser = (TextView) row.findViewById(R.id.tvStatusUser);
            holder.tvDateCreated = (TextView) row.findViewById(R.id.tvDateCreated);
            holder.photoAdmin = (CircleImageView) row.findViewById(R.id.photoAdmin);
            holder.ivMore = (ImageView) row.findViewById(R.id.ivMore);
            holder.ivMore.setVisibility(View.INVISIBLE);


            row.setTag(holder);
        }
        else
        {
            holder = (MenuHolder) row.getTag();
        }

        System.out.println("Position = "+position);

        final Admin menu = namaList.get(position);
        holder.tvNamaAdmin.setText(menu.getNama());
        holder.tvDateCreated.setText(menu.getDateCreated());
        holder.tvStatusUser.setText(menu.getStatusUser());

        Glide.with(getContext()).load(menu.getPhotoUrl())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.photoAdmin);




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
        System.out.println("position popup = "+position);
        final Admin menu = namaList.get(position);
        // inflate menu
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_pasien_dokter, popup.getMenu());

        popup.getMenu().findItem(R.id.delete).setVisible(false);
        if (menu.getStatusUser().equalsIgnoreCase("Aktif"))
        {
            popup.getMenu().findItem(R.id.aktif).setVisible(false);
        }
        else if (menu.getStatusUser().equalsIgnoreCase("Non Aktif"))
        {
            popup.getMenu().findItem(R.id.nonAktif).setVisible(false);
        }


        popup.setOnMenuItemClickListener(new CustomAdapterAdmin.MenuItemClickListener(position));
        popup.show();
    }

    class MenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        private int position;

        public MenuItemClickListener(int position) {

            this.position = position;
            System.out.println("positionmenuitemclick = "+position);
        }





        @Override
        public boolean onMenuItemClick(MenuItem item) {
            System.out.println("positionmenuitemclick 1 = "+position);

            final Admin menu = namaList.get(position);

            int id = item.getItemId();



            if (id==R.id.nonAktif) {

                ubahStatusAktif("Non Aktif",menu.getIdAdmin(),menu.getNama(),menu.getIdAdmin());
            }
            else if (id==R.id.aktif) {

                ubahStatusAktif("Aktif",menu.getIdAdmin(),menu.getNama(),menu.getIdAdmin());
            }


            return false;
        }
    }

    public void ubahStatusAktif(final String statusUser, final String AdminKey, String nama, final String userID)
    {
        AlertDialog.Builder alBuilder = new AlertDialog.Builder(getContext());
        alBuilder.setMessage("Apakah Anda yakin untuk men-"+statusUser+"kan Akun "+nama+"?");
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
                DatabaseReference rootRef,AdminRef,userRef;
                database= FirebaseDatabase.getInstance();
                rootRef= database.getReference();
                AdminRef= rootRef.child("admin");
                userRef = rootRef.child("users");

                AdminRef.child(AdminKey).child("statusUser").setValue(statusUser);
                userRef.child(userID).child("statusUser").setValue(statusUser);

//                progressDialog.dismiss();
                notifyDataSetChanged();
                clear();
//                getFilter().filter(keyWord);
                flagStatus = 1;
                System.out.println("keyword 2= "+keyWord);


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

//    public void deleteAdmin(){
//
//        FirebaseDatabase database;
//        DatabaseReference rootRef,AdminRef,userRef;
//        database= FirebaseDatabase.getInstance();
//        rootRef= database.getReference();
//        AdminRef= rootRef.child("Admin");
//        userRef = rootRef.child("users");
//
//        userRef.child(displayedAdmin.getUserID()).removeValue();
//        AdminRef.child(AdminKey).removeValue();
//
//        progressDialog.dismiss();
//        Log.d("AdminKey: ",AdminKey);
//    }

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


                    ArrayList<Admin> listNama = new ArrayList<Admin>(originalList);
                    result.values = listNama;
                    result.count = listNama.size();

            }
            else
            {

                ArrayList<Admin> listNama = new ArrayList<Admin>(originalList);
                ArrayList<Admin> nlistNama = new ArrayList<Admin>();
                int count = listNama.size();
                System.out.println("charsequence = "+charSequence);
//                listNama.addAll(originalList);

                System.out.println("FILTERING");
                for(int i=0; i<count; i++)
                {


                    Admin object = listNama.get(i);
                    String value = object.getNama().toLowerCase();


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
            namaList = (ArrayList<Admin>) results.values;
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
