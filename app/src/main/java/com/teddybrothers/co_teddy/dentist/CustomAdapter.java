package com.teddybrothers.co_teddy.dentist;

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
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by co_teddy on 3/8/2017.
 */
public class CustomAdapter extends ArrayAdapter<CustomObject> implements Filterable {
    Context context;
    int resourse;

    private LayoutInflater mInflater;

    CustomObject tes;
    private ArrayList<CustomObject> originalList;
    private ArrayList<CustomObject> namaList;

    private namaFilter filter;



    public CustomAdapter(Context context, int resource, ArrayList<CustomObject> data) {
        super(context, resource,data);

        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.resourse = resource;

        this.originalList = new ArrayList<CustomObject>(data);
        this.namaList= new ArrayList<CustomObject>(data);
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

    class MenuHolder{
        ImageView gambar;
        TextView tvNama;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        MenuHolder holder;



        if (row==null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(resourse,parent,false);
            holder = new MenuHolder();
            holder.gambar = (ImageView) row.findViewById(R.id.thumb);
            holder.tvNama = (TextView) row.findViewById(R.id.tvNama);

            row.setTag(holder);
        }
        else
        {
            holder = (MenuHolder) row.getTag();
        }

        CustomObject menu = namaList.get(position);
        holder.gambar.setBackgroundResource(menu.getGambar());
        holder.tvNama.setText(menu.getNama());

        return row;
    }

    private class namaFilter extends Filter
    {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            final FilterResults result = new FilterResults();
            charSequence = charSequence.toString().toLowerCase();


            if(charSequence == null && charSequence.length() == 0) {

                System.out.println("CHAR KOSONG");

                    ArrayList<CustomObject> listNama = new ArrayList<CustomObject>(originalList);
                    result.values = listNama;
                    result.count = listNama.size();

            }
            else
            {

                final ArrayList<CustomObject> listNama = new ArrayList<CustomObject>(originalList);
                final ArrayList<CustomObject> nlistNama = new ArrayList<CustomObject>();
                int count = listNama.size();

//                listNama.addAll(originalList);

                System.out.println("FILTERING");
                for(int i=0; i<count; i++)
                {
                    final CustomObject object = listNama.get(i);
                    final String value = object.getNama().toLowerCase();

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
            namaList = (ArrayList<CustomObject>) results.values;
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
