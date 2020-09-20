package com.teddybrothers.co_teddy.dentist.entity;

import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;

import java.util.Comparator;

/**
 * Created by co_teddy on 5/11/2017.
 */

public class Jadwal implements Comparable{

    public Jadwal()
    {

    }


    public Jadwal(String foto, String keluhan, Long timeStamp, String waktu, String idPasien, String idDokter, String status, String namaPasien, String namaDokter) {
        this.foto = foto;
        this.keluhan = keluhan;
        this.timeStamp = timeStamp;
        this.waktu = waktu;
        this.idPasien = idPasien;
        this.idDokter = idDokter;
        this.status = status;
        this.namaPasien = namaPasien;
        this.namaDokter = namaDokter;
    }

    public String getFoto() {
        return foto;
    }

    public String getKeluhan() {
        return keluhan;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public String getWaktu() {
        return waktu;
    }

    public String getIdPasien() {
        return idPasien;
    }

    public String getIdDokter() {
        return idDokter;
    }

    public String getStatus() {
        return status;
    }

    public String getNamaPasien() {
        return namaPasien;
    }

    public String getNamaDokter() {
        return namaDokter;
    }

    public String getIdJadwal() {
        return idJadwal;
    }

    public String foto;
    public String keluhan;
    public Long timeStamp;
    public String waktu;
    public String idPasien;
    public String idDokter;
    public String status;
    public String namaPasien;
    public String namaDokter;



    @Exclude
    public String idJadwal;



    public static Comparator<Jadwal> COMPARE_BY_TIMESTAMP_ASC = new Comparator<Jadwal>() {
        public int compare(Jadwal one, Jadwal other) {
            return one.getTimeStamp().compareTo(other.getTimeStamp());
        }
    };

    public static Comparator<Jadwal> COMPARE_BY_TIMESTAMP_DES = new Comparator<Jadwal>() {
        public int compare(Jadwal one, Jadwal other) {
            return other.getTimeStamp().compareTo(one.getTimeStamp());
        }
    };

    @Override
    public int compareTo(@NonNull Object o) {
        return 0;
    }

    @Override
    public String toString() {
        return namaPasien+" "+idPasien;
    }
}
