package com.teddybrothers.co_teddy.dentist;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.TextView;


import com.teddybrothers.co_teddy.dentist.entity.Tindakan;

import java.util.ArrayList;

/**
 * Created by co_teddy on 3/8/2017.
 */
public class BillingAdapter extends ArrayAdapter<Tindakan> implements Filterable {
    Context context;
    int resourse;

    private LayoutInflater mInflater;
    private ArrayList<Tindakan> data;

    public BillingAdapter(Context context, int resource, ArrayList<Tindakan> data) {
        super(context, resource,data);

        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.resourse = resource;
        this.data = data;
    }


    class MenuHolder{

        TextView tvNama,tvHarga;
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

            holder.tvNama = (TextView) row.findViewById(R.id.tvNamaTindakan);
            holder.tvHarga = (TextView) row.findViewById(R.id.tvHargaTindakan);
            row.setTag(holder);
        }
        else
        {
            holder = (MenuHolder) row.getTag();
        }

        Tindakan menu = data.get(position);

        holder.tvNama.setText(menu.getNamaTindakan());
        holder.tvHarga.setText(menu.getHargaTindakan());

        return row;
    }






}
