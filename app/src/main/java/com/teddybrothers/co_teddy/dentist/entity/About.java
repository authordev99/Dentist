package com.teddybrothers.co_teddy.dentist.entity;

/**
 * Created by co_teddy on 7/22/2017.
 */

public class About {



    public About()
    {

    }


    public String getNamaKlinik() {
        return namaKlinik;
    }

    public String getAlamat() {
        return alamat;
    }

    public String getNoTelp() {
        return noTelp;
    }

    public String getTelpSeluler() {
        return telpSeluler;
    }

    public String getEmail() {
        return email;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public int getInterval() {
        return interval;
    }

    public int getJamBuka() {
        return jamBuka;
    }

    public int getMenitBuka() {
        return menitBuka;
    }

    public int getJamTutup() {
        return jamTutup;
    }

    public int getMenitTutup() {
        return menitTutup;
    }

    public String namaKlinik;
    public String alamat;
    public String noTelp;
    public String telpSeluler;
    public String email;
    public String latitude;
    public String longitude;
    public int interval;
    public int jamBuka;
    public int menitBuka;
    public int jamTutup;
    public int menitTutup;

    public About(String namaKlinik, String alamat, String noTelp, String telpSeluler, String email, String latitude, String longitude, int interval, int jamBuka, int menitBuka, int jamTutup, int menitTutup) {
        this.namaKlinik = namaKlinik;
        this.alamat = alamat;
        this.noTelp = noTelp;
        this.telpSeluler = telpSeluler;
        this.email = email;
        this.latitude = latitude;
        this.longitude = longitude;
        this.interval = interval;
        this.jamBuka = jamBuka;
        this.menitBuka = menitBuka;
        this.jamTutup = jamTutup;
        this.menitTutup = menitTutup;
    }
}
