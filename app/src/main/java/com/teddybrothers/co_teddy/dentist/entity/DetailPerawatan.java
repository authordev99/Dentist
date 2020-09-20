package com.teddybrothers.co_teddy.dentist.entity;

import java.util.ArrayList;

/**
 * Created by co_teddy on 12/9/2017.
 */

public class DetailPerawatan {

    public DetailPerawatan(){

    }


    public DetailPerawatan(String namaTindakan, String tanggal, String keterangan, String status, String namaDokter, String ttdDokter, ArrayList<String> listTindakan) {
        this.namaTindakan = namaTindakan;
        this.tanggal = tanggal;
        this.keterangan = keterangan;
        this.status = status;
        this.namaDokter = namaDokter;
        this.ttdDokter = ttdDokter;
        this.listTindakan = listTindakan;
    }

    public String getNamaTindakan() {
        return namaTindakan;
    }

    public String getTanggal() {
        return tanggal;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public String getStatus() {
        return status;
    }

    public String getNamaDokter() {
        return namaDokter;
    }

    public String getTtdDokter() {
        return ttdDokter;
    }

    public ArrayList<String> getListTindakan() {
        return listTindakan;
    }

    public String namaTindakan;
    public String tanggal;
    public String keterangan;
    public String status;
    public String namaDokter;
    public String ttdDokter;
    public ArrayList<String> listTindakan;


}
