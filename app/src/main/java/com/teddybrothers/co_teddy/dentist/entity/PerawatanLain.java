package com.teddybrothers.co_teddy.dentist.entity;

/**
 * Created by co_teddy on 7/22/2017.
 */

public class PerawatanLain {



    public PerawatanLain()
    {

    }

    public PerawatanLain(String foto, String regio, String diagnosis, String perawatan, String obat, String keluhanLain) {
        this.foto = foto;
        this.regio = regio;
        this.diagnosis = diagnosis;
        this.perawatan = perawatan;
        this.obat = obat;
        this.keluhanLain = keluhanLain;
    }

    public String foto;
    public String regio;
    public String diagnosis;
    public String perawatan;
    public String obat;
    public String keluhanLain;

}
