package com.teddybrothers.co_teddy.dentist.entity;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by co_teddy on 7/28/2017.
 */

public class RekamMedis {

    String dataOcclusi,dataTorusPalatinus,dataTorusMandibularis,dataPalatum,dataDiastemaAnomali;
    public static final String[] MONTHS = {"Januari", "Febuari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"};

    public RekamMedis()
    {

    }



    public String getIdDokter() {
        return idDokter;
    }


    public RekamMedis(String anamnesis, String diagnosis, String rencanaPerawatan, String ketRencanaPerawatan, String occlusi, String torusPalatinus, String torusMandibularis, String palatum, String diastema, String ketDiastema, String gigiAnomali, String ketGigiAnomali, String lainLain, String idDokter, String namaDokter, String updateTime) {
        this.anamnesis = anamnesis;
        this.diagnosis = diagnosis;
        this.rencanaPerawatan = rencanaPerawatan;
        this.ketRencanaPerawatan = ketRencanaPerawatan;
        this.occlusi = occlusi;
        this.torusPalatinus = torusPalatinus;
        this.torusMandibularis = torusMandibularis;
        this.palatum = palatum;
        this.diastema = diastema;
        this.ketDiastema = ketDiastema;
        this.gigiAnomali = gigiAnomali;
        this.ketGigiAnomali = ketGigiAnomali;
        this.lainLain = lainLain;
        this.idDokter = idDokter;
        this.namaDokter = namaDokter;
        this.updateTime = updateTime;
    }

    public String anamnesis;
    public String diagnosis;
    public String rencanaPerawatan;

    public String getRencanaPerawatan() {
        return rencanaPerawatan;
    }

    public String ketRencanaPerawatan;

    public RekamMedis(String rencanaPerawatan, String ketRencanaPerawatan, String idDokter, String namaDokter) {
        this.rencanaPerawatan = rencanaPerawatan;
        this.ketRencanaPerawatan = ketRencanaPerawatan;
        this.idDokter = idDokter;
        this.namaDokter = namaDokter;
    }

    public String occlusi;
    public String torusPalatinus;
    public String torusMandibularis;
    public String palatum;
    public String diastema;
    public String ketDiastema;
    public String gigiAnomali;
    public String ketGigiAnomali;
    public String lainLain;
    public String idDokter;
    public String namaDokter;
    public String updateTime;

    public String Occlusi() {
        return getKetOklusi(occlusi);
    }

    public String TorusPalatinus() {
        return getKetPalatinus(torusPalatinus);
    }

    public String TorusMandibularis() {
        return getKetMandibularis(torusMandibularis);
    }

    public String Palatum() {
        return getKetPalatum(palatum);
    }

    public String Diastema() {
        return getKetDiastemaAnomali(diastema);
    }

    public String GigiAnomali() {
        return getKetDiastemaAnomali(gigiAnomali);
    }

    public String getNamaDokter() {
        return namaDokter;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public String UpdateTimeConvert() {
        return getDate(Long.parseLong(updateTime),"updateTime");
    }

    public String RencanaPerawatanConvert() {
        return getDate(Long.parseLong(rencanaPerawatan),"rencanaPerawatan");
    }







    public static Comparator<RekamMedis> COMPARE_BY_DATE = new Comparator<RekamMedis>() {
        public int compare(RekamMedis one, RekamMedis other) {
            return other.getUpdateTime().compareToIgnoreCase(one.getUpdateTime());
        }
    };

    public static Comparator<RekamMedis> COMPARE_BY_TGL_RENPER = new Comparator<RekamMedis>() {
        public int compare(RekamMedis one, RekamMedis other) {
            return other.getRencanaPerawatan().compareToIgnoreCase(one.getRencanaPerawatan());
        }
    };

    public String getDate(long timeStamp,String flags){

        try{

            SimpleDateFormat ee = new SimpleDateFormat("EEEE");
            SimpleDateFormat dd = new SimpleDateFormat("dd");
            SimpleDateFormat MM = new SimpleDateFormat("MM");
            SimpleDateFormat yyyy = new SimpleDateFormat("yyyy");
            Date netDate = (new Date(timeStamp));
            String dayWeek = ee.format(netDate);
            String day = dd.format(netDate);
            String month = MM.format(netDate);
            String nameMonth = MONTHS[Integer.parseInt(month)-1];
            String year = yyyy.format(netDate);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String time = sdf.format(netDate);
            String tanggal;
            if (flags.equalsIgnoreCase("rencanaPerawatan"))
            {
                tanggal = dayWeek+", "+day+" "+nameMonth+" "+year;
            }
            else
            {
                tanggal = dayWeek+", "+day+" "+nameMonth+" "+year+" "+time;
            }
        
            return tanggal;
        }
        catch(Exception ex){
            System.out.println("Log = "+ex);
            return "xx";
        }
    }


    public String getKetOklusi(String data)
    {

        if (data.equalsIgnoreCase("0"))
        {
            dataOcclusi = "Normal Bite";
        }else if (data.equalsIgnoreCase("1"))
        {
            dataOcclusi = "Cross Bite";
        } else if (data.equalsIgnoreCase("2"))
        {
            dataOcclusi = "Steep Bite";
        }

        return dataOcclusi;
    }

    public String getKetPalatinus(String data)
    {

        if (data.equalsIgnoreCase("0"))
        {
            dataTorusPalatinus = "Tidak ada";
        }else if (data.equalsIgnoreCase("1"))
        {
            dataTorusPalatinus = "Kecil";
        } else if (data.equalsIgnoreCase("2"))
        {
            dataTorusPalatinus = "Sedang";
        }
        else if (data.equalsIgnoreCase("3"))
        {
            dataTorusPalatinus = "Besar";
        }
        else if (data.equalsIgnoreCase("4"))
        {
            dataTorusPalatinus = "Multiple";
        }

        return dataTorusPalatinus;
    }


    public String getKetMandibularis(String data)
    {

        if (data.equalsIgnoreCase("0"))
        {
            dataTorusMandibularis = "Tidak ada";
        }else if (data.equalsIgnoreCase("1"))
        {
            dataTorusMandibularis = "Sisi Kiri";
        } else if (data.equalsIgnoreCase("2"))
        {
            dataTorusMandibularis = "Sisi Kanan";
        }
        else if (data.equalsIgnoreCase("3"))
        {
            dataTorusMandibularis = "Kedua Sisi";
        }


        return dataTorusMandibularis;
    }


    public String getKetPalatum(String data)
    {

        if (data.equalsIgnoreCase("0"))
        {
            dataPalatum = "Dalam";
        }else if (data.equalsIgnoreCase("1"))
        {
            dataPalatum = "Sedang";
        } else if (data.equalsIgnoreCase("2"))
        {
            dataPalatum = "Rendah";
        }



        return dataPalatum;
    }


    public String getKetDiastemaAnomali(String data)
    {

        if (data.equalsIgnoreCase("0"))
        {
            dataDiastemaAnomali = "Tidak Ada";
        }else if (data.equalsIgnoreCase("1"))
        {
            dataDiastemaAnomali = "Ada";
        }


        return dataDiastemaAnomali;
    }










}
