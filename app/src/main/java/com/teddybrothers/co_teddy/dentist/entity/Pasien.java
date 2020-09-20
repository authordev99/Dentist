package com.teddybrothers.co_teddy.dentist.entity;

import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;

/**
 * Created by co_teddy on 7/28/2017.
 */

public class Pasien implements Comparable<Pasien> {

    public Pasien()
    {

    }


    public String nama;
    public String namaSortir;
    public String tempatLahir;
    public String tanggalLahir;
    public String umur;
    public String noIdentitas;
    public String jenisKelamin;
    public String alamat;
    public String suku;
    public String pekerjaan;
    public String teleponRumah;
    public String teleponSeluler;
    public String kategori;
    public String golonganDarah;
    public String tekananDarah;
    public String penyakitJantung;
    public String ketPenyakitJantung;
    public String diabetes;
    public String ketDiabetes;
    public String haemopilia;
    public String ketHaemopilia;
    public String hepatitis;
    public String ketHepatitis;
    public String gastring;
    public String ketGastring;
    public String penyakitLainnya;
    public String ketPenyakitLainnnya;
    public String alergiObat;
    public String ketAlergiObat;
    public String alergiMakanan;
    public String ketAlergiMakanan;
    public String tanggalCatat;
    public String statusUser;
    public String userID;
    public String photoUrl;

    public Pasien(String nama, String namaSortir, String tempatLahir, String tanggalLahir, String umur, String noIdentitas, String jenisKelamin, String alamat, String suku, String pekerjaan, String teleponRumah, String teleponSeluler, String kategori, String golonganDarah, String tekananDarah, String penyakitJantung, String ketPenyakitJantung, String diabetes, String ketDiabetes, String haemopilia, String ketHaemopilia, String hepatitis, String ketHepatitis, String gastring, String ketGastring, String penyakitLainnya, String ketPenyakitLainnnya, String alergiObat, String ketAlergiObat, String alergiMakanan, String ketAlergiMakanan, String tanggalCatat, String statusUser, String userID, String photoUrl) {
        this.nama = nama;
        this.namaSortir = namaSortir;
        this.tempatLahir = tempatLahir;
        this.tanggalLahir = tanggalLahir;
        this.umur = umur;
        this.noIdentitas = noIdentitas;
        this.jenisKelamin = jenisKelamin;
        this.alamat = alamat;
        this.suku = suku;
        this.pekerjaan = pekerjaan;
        this.teleponRumah = teleponRumah;
        this.teleponSeluler = teleponSeluler;
        this.kategori = kategori;
        this.golonganDarah = golonganDarah;
        this.tekananDarah = tekananDarah;
        this.penyakitJantung = penyakitJantung;
        this.ketPenyakitJantung = ketPenyakitJantung;
        this.diabetes = diabetes;
        this.ketDiabetes = ketDiabetes;
        this.haemopilia = haemopilia;
        this.ketHaemopilia = ketHaemopilia;
        this.hepatitis = hepatitis;
        this.ketHepatitis = ketHepatitis;
        this.gastring = gastring;
        this.ketGastring = ketGastring;
        this.penyakitLainnya = penyakitLainnya;
        this.ketPenyakitLainnnya = ketPenyakitLainnnya;
        this.alergiObat = alergiObat;
        this.ketAlergiObat = ketAlergiObat;
        this.alergiMakanan = alergiMakanan;
        this.ketAlergiMakanan = ketAlergiMakanan;
        this.tanggalCatat = tanggalCatat;
        this.statusUser = statusUser;
        this.userID = userID;
        this.photoUrl = photoUrl;
    }

    @Exclude
    public String idPasien;

    public String getNama() {
        return nama;
    }

    public String getNamaSortir() {
        return namaSortir;
    }

    public String getTempatLahir() {
        return tempatLahir;
    }

    public String getTanggalLahir() {
        return tanggalLahir;
    }

    public String getUmur() {
        return umur;
    }

    public String getNoIdentitas() {
        return noIdentitas;
    }

    public String getJenisKelamin() {
        return jenisKelamin;
    }

    public String getAlamat() {
        return alamat;
    }

    public String getSuku() {
        return suku;
    }

    public String getPekerjaan() {
        return pekerjaan;
    }

    public String getTeleponRumah() {
        return teleponRumah;
    }

    public String getTeleponSeluler() {
        return teleponSeluler;
    }

    public String getKategori() {
        return kategori;
    }

    public String getGolonganDarah() {
        return golonganDarah;
    }

    public String getTekananDarah() {
        return tekananDarah;
    }

    public String getPenyakitJantung() {
        return penyakitJantung;
    }

    public String getKetPenyakitJantung() {
        return ketPenyakitJantung;
    }

    public String getDiabetes() {
        return diabetes;
    }

    public String getKetDiabetes() {
        return ketDiabetes;
    }

    public String getHaemopilia() {
        return haemopilia;
    }

    public String getKetHaemopilia() {
        return ketHaemopilia;
    }

    public String getHepatitis() {
        return hepatitis;
    }

    public String getKetHepatitis() {
        return ketHepatitis;
    }

    public String getGastring() {
        return gastring;
    }

    public String getKetGastring() {
        return ketGastring;
    }

    public String getPenyakitLainnya() {
        return penyakitLainnya;
    }

    public String getKetPenyakitLainnnya() {
        return ketPenyakitLainnnya;
    }

    public String getAlergiObat() {
        return alergiObat;
    }

    public String getKetAlergiObat() {
        return ketAlergiObat;
    }

    public String getAlergiMakanan() {
        return alergiMakanan;
    }

    public String getKetAlergiMakanan() {
        return ketAlergiMakanan;
    }

    public String getTanggalCatat() {
        return tanggalCatat;
    }

    public String getStatusUser() {
        return statusUser;
    }

    public String getUserID() {
        return userID;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getIdPasien() {
        return idPasien;
    }

    public static Comparator<Pasien> getCompareByName() {
        return COMPARE_BY_NAME;
    }

    public static Comparator<Pasien> getCompareByDate() {
        return COMPARE_BY_DATE;
    }

    public int compareTo(Pasien other) {
        return getNama().compareToIgnoreCase(other.getNama());
    }

    public static Comparator<Pasien> COMPARE_BY_NAME = new Comparator<Pasien>() {
        public int compare(Pasien one, Pasien other) {

            return one.getNama().compareToIgnoreCase(other.getNama());
        }
    };

    public static Comparator<Pasien> COMPARE_BY_DATE = new Comparator<Pasien>() {
        public int compare(Pasien one, Pasien other) {

            return one.getTanggalCatat().compareToIgnoreCase(other.getTanggalCatat());
        }
    };

    @Override
    public String toString() {

        return nama+" "+idPasien;
    }

}
