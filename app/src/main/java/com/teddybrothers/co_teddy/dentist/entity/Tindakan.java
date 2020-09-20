package com.teddybrothers.co_teddy.dentist.entity;

import com.google.firebase.database.Exclude;

import java.util.Comparator;

/**
 * Created by co_teddy on 7/22/2017.
 */

public class Tindakan {

    public Tindakan(String namaTindakan, String deskripsiTindakan, String hargaTindakan) {
        this.namaTindakan = namaTindakan;
        this.deskripsiTindakan = deskripsiTindakan;
        this.hargaTindakan = hargaTindakan;
    }

    public Tindakan()
    {

    }

    public String namaTindakan;
    public String deskripsiTindakan;
    public String hargaTindakan;

    public String getIdTindakan() {
        return idTindakan;
    }

    @Exclude
    public String idTindakan;

    public String getNamaTindakan() {
        return namaTindakan;
    }

    public String getDeskripsiTindakan() {
        return deskripsiTindakan;
    }

    public String getHargaTindakan() {
        return hargaTindakan;
    }

    public int compareTo(Tindakan other) {
        return getNamaTindakan().compareToIgnoreCase(other.getNamaTindakan());
    }

    public static Comparator<Tindakan> COMPARE_BY_NAME = new Comparator<Tindakan>() {
        public int compare(Tindakan one, Tindakan other) {
            return one.getNamaTindakan().compareToIgnoreCase(other.getNamaTindakan());
        }
    };
}
