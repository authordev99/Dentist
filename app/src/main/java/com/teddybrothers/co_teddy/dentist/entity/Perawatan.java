package com.teddybrothers.co_teddy.dentist.entity;

import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;

import java.util.Comparator;

/**
 * Created by co_teddy on 7/28/2017.
 */

public class Perawatan implements Comparable<Perawatan>{



    public Perawatan()
    {

    }


    public String getNoGigi() {
        return noGigi;
    }

    public String getKodeGigi() {
        return kodeGigi;
    }

    public String getNamaTindakan() {
        return namaTindakan;
    }

    public String getObat() {
        return obat;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public String getGigiAtas() {
        return gigiAtas;
    }

    public String getGigiBawah() {
        return gigiBawah;
    }

    public String getGigiKanan() {
        return gigiKanan;
    }

    public String getGigiKiri() {
        return gigiKiri;
    }

    public String getBerlubang() {
        return berlubang;
    }

    public String getJadwalKey() {
        return jadwalKey;
    }



    public Long getTanggal() {
        return tanggal;
    }

    public Perawatan(String noGigi, String kodeGigi, String namaTindakan, String obat, String keterangan, String gigiAtas, String gigiBawah, String gigiKanan, String gigiKiri, String berlubang, String jadwalKey, String idPasien, Long tanggal) {
        this.noGigi = noGigi;
        this.kodeGigi = kodeGigi;
        this.namaTindakan = namaTindakan;
        this.obat = obat;
        this.keterangan = keterangan;
        this.gigiAtas = gigiAtas;
        this.gigiBawah = gigiBawah;
        this.gigiKanan = gigiKanan;
        this.gigiKiri = gigiKiri;
        this.berlubang = berlubang;
        this.jadwalKey = jadwalKey;
        this.idPasien = idPasien;
        this.tanggal = tanggal;
    }

    public String noGigi;
    public String kodeGigi;
    public String namaTindakan;
    public String obat;
    public String keterangan;
    public String gigiAtas;
    public String gigiBawah;
    public String gigiKanan;
    public String gigiKiri;
    public String berlubang;
    public String jadwalKey;
    public String idPasien;
    public Long tanggal;


    public String getIdPerawatan() {
        return idPerawatan;
    }

    @Exclude
    public String idPerawatan;

    public static Comparator<Perawatan> COMPARE_BY_NOGIGI = new Comparator<Perawatan>() {
        public int compare(Perawatan one, Perawatan other) {
            return one.getNoGigi().compareToIgnoreCase(other.getNoGigi());
        }
    };

    public static Comparator<Perawatan> COMPARE_BY_TGL = new Comparator<Perawatan>() {
        public int compare(Perawatan one, Perawatan other) {
            return other.getTanggal().compareTo(one.getTanggal());
        }
    };


    @Override
    public int compareTo(@NonNull Perawatan perawatan) {
        return 0;
    }

    @Override
    public String toString() {
        return noGigi+" "+namaTindakan;
    }
}
