package com.teddybrothers.co_teddy.dentist.entity;

import com.google.firebase.database.Exclude;

/**
 * Created by co_teddy on 3/30/2018.
 */

public class BlockDate {


    public BlockDate()
    {

    }

    public BlockDate(String dateBlock, String keterangan) {
        this.dateBlock = dateBlock;
        this.keterangan = keterangan;
    }

    public String getDateBlock() {
        return dateBlock;
    }

    public void setDateBlock(String dateBlock) {
        this.dateBlock = dateBlock;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    String dateBlock;
    String keterangan;


    @Exclude
    public String idBlockDate;

    public String getIdBlockDate() {
        return idBlockDate;
    }

    public void setIdBlockDate(String idBlockDate) {
        this.idBlockDate = idBlockDate;
    }
}
