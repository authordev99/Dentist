package com.teddybrothers.co_teddy.dentist;

/**
 * Created by co_teddy on 3/8/2017.
 */
public class CustomObject {


    public CustomObject(int gambar, String singkatan,String nama, int detail) {
        this.gambar = gambar;
        this.detail = detail;
        this.nama = nama;
        this.singkatan = singkatan;
    }

    public CustomObject() {

    }

    public int getGambar() {
        return gambar;
    }



    public String getNama() {
        return nama;
    }
    public String getSingkatan() {
        return singkatan;
    }

    int gambar; //karena id int

    public int getDetail() {
        return detail;
    }

    int detail;
    String nama;
    String singkatan;


}
