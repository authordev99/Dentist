package com.teddybrothers.co_teddy.dentist.entity;

/**
 * Created by co_teddy on 3/17/2018.
 */

public class DetailTindakan {

    public DetailTindakan(String hargaTindakan, String noGigi, String tindakan) {
        this.hargaTindakan = hargaTindakan;
        this.noGigi = noGigi;
        this.tindakan = tindakan;
    }

    public String getHargaTindakan() {
        return hargaTindakan;
    }

    public String getNoGigi() {
        return noGigi;
    }


    public String getTindakan() {
        return tindakan;
    }

    String hargaTindakan;
    String noGigi;

    String tindakan;
}
