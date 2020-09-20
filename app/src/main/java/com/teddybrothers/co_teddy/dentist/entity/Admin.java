package com.teddybrothers.co_teddy.dentist.entity;

import com.google.firebase.database.Exclude;

import java.util.Comparator;

/**
 * Created by co_teddy on 12/17/2017.
 */

public class Admin {

    RekamMedis rekamMedis = new RekamMedis();


    public Admin()
    {

    }


    public Admin(String nama, String namaSortir, String dateCreated, String alamat, String jenisKelamin, String statusUser, String photoUrl) {
        this.nama = nama;
        this.namaSortir = namaSortir;
        this.dateCreated = dateCreated;
        this.alamat = alamat;
        this.jenisKelamin = jenisKelamin;
        this.statusUser = statusUser;
        this.photoUrl = photoUrl;
    }

    public String nama;
    public String namaSortir;
    public String dateCreated;
    public String alamat;
    public String jenisKelamin;
    public String statusUser;
    public String photoUrl;

    public String getNama() {
        return nama;
    }

    public String getNamaSortir() {
        return namaSortir;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public String getAlamat() {
        return alamat;
    }

    public String getJenisKelamin() {
        return jenisKelamin;
    }

    public String getStatusUser() {
        return statusUser;
    }
    public String getIdAdmin() {
        return idAdmin;
    }
    public String getPhotoUrl() {
        return photoUrl;
    }


    @Exclude
    public String idAdmin;





    public int compareTo(Admin other) {
        return getNama().compareToIgnoreCase(other.getNama());
    }

    public static Comparator<Admin> COMPARE_BY_NAME_ASC = new Comparator<Admin>() {
        public int compare(Admin one, Admin other) {
            return one.getNama().compareToIgnoreCase(other.getNama());
        }
    };

    public static Comparator<Admin> COMPARE_BY_NAME_DES = new Comparator<Admin>() {
        public int compare(Admin one, Admin other) {
            return other.getNama().compareToIgnoreCase(one.getNama());
        }
    };
}
