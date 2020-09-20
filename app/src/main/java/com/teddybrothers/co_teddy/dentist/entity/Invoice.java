package com.teddybrothers.co_teddy.dentist.entity;

import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by co_teddy on 11/29/2017.
 */

public class Invoice implements Comparable<Invoice>{

    public static final String[] MONTHS = {"Januari", "Febuari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"};


    public Invoice()
    {

    }


    public Invoice(String noInvoice, Long tanggalInvoice, String totalHarga, String diskon, String jamInvoice, String metodePembayaran, String idPasien, String namaPasien, String idDokter, String namaDokter) {
        this.noInvoice = noInvoice;
        this.tanggalInvoice = tanggalInvoice;
        this.totalHarga = totalHarga;
        this.diskon = diskon;
        this.jamInvoice = jamInvoice;
        this.metodePembayaran = metodePembayaran;
        this.idPasien = idPasien;
        this.namaPasien = namaPasien;
        this.idDokter = idDokter;
        this.namaDokter = namaDokter;
    }

    public String getNoInvoice() {
        return noInvoice;
    }

    public Long getTanggalInvoice() {
        return tanggalInvoice;
    }

    public String getTotalHarga() {
        return totalHarga;
    }

    public String getDiskon() {
        return diskon;
    }

    public String getJamInvoice() {
        return jamInvoice;
    }

    public String getMetodePembayaran() {
        return metodePembayaran;
    }

    public String getIdPasien() {
        return idPasien;
    }

    public String getNamaPasien() {
        return namaPasien;
    }

    public String getIdDokter() {
        return idDokter;
    }

    public String getNamaDokter() {
        return namaDokter;
    }

    public String getIdInvoice() {
        return idInvoice;
    }

    public String tanggalConvert() {
        return getDate(tanggalInvoice);
    }

    public String noInvoice;
    public Long tanggalInvoice;
    public String totalHarga;
    public String diskon;
    public String jamInvoice;
    public String metodePembayaran;
    public String idPasien;
    public String namaPasien;
    public String idDokter;
    public String namaDokter;

    @Exclude
    public String idInvoice;


    public static Comparator<Invoice> COMPARE_BY_TANGGAL = new Comparator<Invoice>() {
        public int compare(Invoice one, Invoice other) {
            return one.getTanggalInvoice().compareTo(other.getTanggalInvoice());
        }
    };

    public static Comparator<Invoice> COMPARE_BY_TANGGAL_ASC = new Comparator<Invoice>() {
        public int compare(Invoice one, Invoice other) {
            return other.getTanggalInvoice().compareTo(one.getTanggalInvoice());
        }
    };

    @Override
    public int compareTo(@NonNull Invoice invoice) {
        return 0;
    }

    @Override
    public String toString() {
        return namaPasien+" "+namaDokter;
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
