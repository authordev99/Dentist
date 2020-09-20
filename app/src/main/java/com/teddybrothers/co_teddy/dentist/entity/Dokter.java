package com.teddybrothers.co_teddy.dentist.entity;

import com.google.firebase.database.Exclude;

import java.util.Comparator;

/**
 * Created by co_teddy on 7/27/2017.
 */

public class Dokter {


    public Dokter(String nama, String namaSortir, String noSTR, String noSIP, String noTelp, String userID, String tanggalDaftar, String statusUser, String ttdUrl, String photoUrl) {
        this.nama = nama;
        this.namaSortir = namaSortir;
        this.noSTR = noSTR;
        this.noSIP = noSIP;
        this.noTelp = noTelp;
        this.userID = userID;
        this.tanggalDaftar = tanggalDaftar;
        this.statusUser = statusUser;
        this.ttdUrl = ttdUrl;
        this.photoUrl = photoUrl;
    }


    public Dokter()
    {

    }




    public String getNama() {
        return nama;
    }

    public String getNamaSortir() {
        return namaSortir;
    }

    public String getNoSTR() {
        return noSTR;
    }

    public String getNoSIP() {
        return noSIP;
    }

    public String getNoTelp() {
        return noTelp;
    }

    public String getUserID() {
        return userID;
    }

    public String getTanggalDaftar() {
        return tanggalDaftar;
    }

    public String getStatusUser() {
        return statusUser;
    }

    public String getTtdUrl() {
        return ttdUrl;
    }



    public String nama;
    public String namaSortir;
    public String noSTR;
    public String noSIP;
    public String noTelp;
    public String userID;
    public String tanggalDaftar;
    public String statusUser;
    public String ttdUrl;
    public String photoUrl;

    public String getIdDokter() {
        return idDokter;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }
    @Exclude
    public String idDokter;






    public static Comparator<Dokter> COMPARE_BY_NAME_ASC = new Comparator<Dokter>() {
        public int compare(Dokter one, Dokter other) {
            return one.getNama().compareToIgnoreCase(other.getNama());
        }
    };

    public static Comparator<Dokter> COMPARE_BY_NAME_DESC = new Comparator<Dokter>() {
        public int compare(Dokter one, Dokter other) {
            return other.getNama().compareToIgnoreCase(one.getNama());
        }
    };


}
