package com.teddybrothers.co_teddy.dentist.entity;

import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;

import java.util.Comparator;

/**
 * Created by co_teddy on 10/1/2017.
 */

public class Formulir implements Comparable<Formulir> {

    public Formulir()
    {

    }


    public Formulir(String idPasien, String idDokter, String terhadap, String tindakan, String namaLain, String umurLain, String alamatLain, String jenKelLain, String tglForm, String ttdPasien, String status, String namaPasien) {
        this.idPasien = idPasien;
        this.idDokter = idDokter;
        this.terhadap = terhadap;
        this.tindakan = tindakan;
        this.namaLain = namaLain;
        this.umurLain = umurLain;
        this.alamatLain = alamatLain;
        this.jenKelLain = jenKelLain;
        this.tglForm = tglForm;
        this.ttdPasien = ttdPasien;
        this.status = status;
        this.namaPasien = namaPasien;
    }

    public String idPasien;
    public String idDokter;
    public String terhadap;
    public String tindakan;
    public String namaLain;
    public String umurLain;
    public String alamatLain;
    public String jenKelLain;
    public String tglForm;
    public String ttdPasien;
    public String status;
    public String namaPasien;



    @Exclude
    public String idFormulir;


    public String getIdPasien() {
        return idPasien;
    }

    public String getIdDokter() {
        return idDokter;
    }


    public String getTindakan() {
        return tindakan;
    }


    public String getTglForm() {
        return tglForm;
    }


    public String getStatus() {
        return status;
    }

    public String getNamaPasien() {
        return namaPasien;
    }

    public static Comparator<Formulir> COMPARE_BY_NAME = new Comparator<Formulir>() {
        public int compare(Formulir one, Formulir other) {

            return one.getNamaPasien().compareToIgnoreCase(other.getNamaPasien());
        }
    };

    @Override
    public int compareTo(@NonNull Formulir formulir) {
        return 0;
    }
}
