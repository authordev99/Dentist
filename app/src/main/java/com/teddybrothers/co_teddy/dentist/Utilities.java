package com.teddybrothers.co_teddy.dentist;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.teddybrothers.co_teddy.dentist.entity.DetailPerawatan;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class Utilities {

    SharedPreferences sp;

    SharedPreferences.Editor editor;
    String value, email, password,idPasienLogin,idPasien,idJadwal,idDokter,status,namaPasien,tempatLahir,tanggalLahir,noIdentitas,jenKelamin
            ,occlusi,diastema,palatum,mandibularis,palatinus,anomali,lainnya,ketDiastema,ketAnomali,ttdPasien,ttdDokter,keyPushTindakanPerawatan,keyPushPerawatan;
    String gigi18, gigi17,gigi16, gigi15,gigi14, gigi13,gigi12, gigi11,
            gigi21, gigi22,gigi23, gigi24,gigi25, gigi26,gigi27, gigi28,
            gigi55, gigi54,gigi53, gigi52,gigi51,
            gigi61, gigi62,gigi63, gigi64,gigi65,
            gigi85, gigi84,gigi83, gigi82,gigi81,
            gigi71, gigi72,gigi73, gigi74,gigi75,
            gigi48, gigi47,gigi46, gigi45,gigi44, gigi43,gigi42, gigi41,
            gigi31, gigi32,gigi33, gigi34,gigi35, gigi36,gigi37, gigi38;




    public void setEmail(Context context, String email){
        sp = context.getSharedPreferences("email", Context.MODE_PRIVATE);
        editor = sp.edit();
        editor.putString("email", email);
        editor.commit();

    }

    public void setDetail(Context context, ArrayList<DetailPerawatan> detailPerawatan, String detailGigi){
        SharedPreferences pref = context.getSharedPreferences("detailPreference", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();

        Gson gson = new Gson();
        Set<DetailPerawatan> set = new HashSet<DetailPerawatan>();
        set.addAll(detailPerawatan);
        String json = gson.toJson(set);
        System.out.println("json = "+json);
        edit.putString(detailGigi, json);
        edit.commit();

    }





    public ArrayList<DetailPerawatan> getDetail(Context context,String detailGigi){
        SharedPreferences pref = context.getSharedPreferences("detailPreference", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = pref.getString(detailGigi, "");
//        Set<String> set = sp.getStringSet("detail18", null);
        ArrayList<DetailPerawatan> list = gson.fromJson(json, new TypeToken<ArrayList<DetailPerawatan>>(){}.getType());
        return list;
    }

    public String getEmail(Context context){
        sp = context.getSharedPreferences("email", Context.MODE_PRIVATE);
        email = sp.getString("email", value);
        System.out.println("email = "+email);
        return email;
    }

    public void setPassword(Context context, String password){
        sp = context.getSharedPreferences("password", Context.MODE_PRIVATE);
        editor = sp.edit();
        editor.putString("password", password);
        editor.commit();
    }

    public String getPassword(Context context){
        sp = context.getSharedPreferences("password", Context.MODE_PRIVATE);
        value = null;
        password = sp.getString("password", value);
        return password;
    }


    public void setIdPasien(Context context, String idPasien){
        sp = context.getSharedPreferences("idPasien", Context.MODE_PRIVATE);
        editor = sp.edit();
        editor.putString("idPasien", idPasien);
        editor.commit();
    }

    public void setIdPasienLogin(Context context, String idPasienLogin){
        sp = context.getSharedPreferences("idPasienLogin", Context.MODE_PRIVATE);
        editor = sp.edit();
        editor.putString("idPasienLogin", idPasienLogin);
        editor.commit();
    }

    public String getIdPasien(Context context){
        sp = context.getSharedPreferences("idPasien", Context.MODE_PRIVATE);
        value = null;
        idPasien = sp.getString("idPasien", value);
        return idPasien;
    }

    public String getIdPasienLogin(Context context){
        sp = context.getSharedPreferences("idPasienLogin", Context.MODE_PRIVATE);
        value = null;
        idPasienLogin = sp.getString("idPasienLogin", value);
        return idPasienLogin;
    }

    public void setIdJadwal(Context context, String idJadwal){
        sp = context.getSharedPreferences("idJadwal", Context.MODE_PRIVATE);
        editor = sp.edit();
        editor.putString("idJadwal", idJadwal);
        editor.commit();
    }

    public String getIdJadwal(Context context){
        sp = context.getSharedPreferences("idJadwal", Context.MODE_PRIVATE);
        value = null;
        idJadwal = sp.getString("idJadwal", idJadwal);
        return idJadwal;
    }

    public void setIdDokter(Context context, String idDokter){
        sp = context.getSharedPreferences("idDokter", Context.MODE_PRIVATE);
        editor = sp.edit();
        editor.putString("idDokter", idDokter);
        editor.commit();
    }

    public String getIdDokter(Context context){
        sp = context.getSharedPreferences("idDokter", Context.MODE_PRIVATE);
        value = null;
        idDokter = sp.getString("idDokter", idDokter);
        return idDokter;
    }
    //ODONTOGRAM
    //GIGI ATAS


    public String getGigi28(Context context){
        sp = context.getSharedPreferences("Gigi 28", Context.MODE_PRIVATE);
        value = null;
        gigi28 = sp.getString("Gigi 28", gigi28);
        return gigi28;
    }



    public String getGigi27(Context context){
        sp = context.getSharedPreferences("Gigi 27", Context.MODE_PRIVATE);
        value = null;
        gigi27 = sp.getString("Gigi 27", gigi27);
        return gigi27;
    }



    public String getGigi26(Context context){
        sp = context.getSharedPreferences("Gigi 26", Context.MODE_PRIVATE);
        value = null;
        gigi26 = sp.getString("Gigi 26", gigi26);
        return gigi26;
    }



    public String getGigi25(Context context){
        sp = context.getSharedPreferences("Gigi 25", Context.MODE_PRIVATE);
        value = null;
        gigi25 = sp.getString("Gigi 25", gigi25);
        return gigi25;
    }



    public String getGigi24(Context context){
        sp = context.getSharedPreferences("Gigi 24", Context.MODE_PRIVATE);
        value = null;
        gigi24 = sp.getString("Gigi 24", gigi24);
        return gigi24;
    }



    public String getGigi23(Context context){
        sp = context.getSharedPreferences("Gigi 23", Context.MODE_PRIVATE);
        value = null;
        gigi23 = sp.getString("Gigi 23", gigi23);
        return gigi23;
    }



    public String getGigi22(Context context){
        sp = context.getSharedPreferences("Gigi 22", Context.MODE_PRIVATE);
        value = null;
        gigi22 = sp.getString("Gigi 22", gigi22);
        return gigi22;
    }


    public String getGigi21(Context context){
        sp = context.getSharedPreferences("Gigi 21", Context.MODE_PRIVATE);
        value = null;
        gigi21 = sp.getString("Gigi 21", gigi21);
        return gigi21;
    }

    public void setGigi(String noGigi,Context context, String kode){
        sp = context.getSharedPreferences(noGigi, Context.MODE_PRIVATE);
        editor = sp.edit();
        editor.putString(noGigi, kode);
        editor.commit();
    }

    public String getGigi(Context context,String noGigi){
        sp = context.getSharedPreferences("noGigi", Context.MODE_PRIVATE);
        value = null;
        noGigi = sp.getString(noGigi, noGigi);
        return noGigi;
    }



    public String getGigi18(Context context){
        sp = context.getSharedPreferences("Gigi 18", Context.MODE_PRIVATE);
        value = null;
        gigi18 = sp.getString("Gigi 18", gigi18);
        return gigi18;
    }


    public String getGigi17(Context context){
        sp = context.getSharedPreferences("Gigi 17", Context.MODE_PRIVATE);
        value = null;
        gigi17 = sp.getString("Gigi 17", gigi17);
        return gigi17;
    }



    public String getGigi16(Context context){
        sp = context.getSharedPreferences("Gigi 16", Context.MODE_PRIVATE);
        value = null;
        gigi16 = sp.getString("Gigi 16", gigi16);
        return gigi16;
    }


    public String getGigi15(Context context){
        sp = context.getSharedPreferences("Gigi 15", Context.MODE_PRIVATE);
        value = null;
        gigi15 = sp.getString("Gigi 15", gigi15);
        return gigi15;
    }



    public String getGigi14(Context context){
        sp = context.getSharedPreferences("Gigi 14", Context.MODE_PRIVATE);
        value = null;
        gigi14 = sp.getString("Gigi 14", gigi14);
        return gigi14;
    }




    public String getGigi13(Context context){
        sp = context.getSharedPreferences("Gigi 13", Context.MODE_PRIVATE);
        value = null;
        gigi13 = sp.getString("Gigi 13", gigi13);
        return gigi13;
    }



    public String getGigi12(Context context){
        sp = context.getSharedPreferences("Gigi 12", Context.MODE_PRIVATE);
        value = null;
        gigi12 = sp.getString("Gigi 12", gigi12);
        return gigi12;
    }



    public String getGigi11(Context context){
        sp = context.getSharedPreferences("Gigi 11", Context.MODE_PRIVATE);
        value = null;
        gigi11 = sp.getString("Gigi 11", gigi11);
        return gigi11;
    }


    //GIGI BAWAH


    public String getGigi48(Context context){
        sp = context.getSharedPreferences("Gigi 48", Context.MODE_PRIVATE);
        value = null;
        gigi48 = sp.getString("Gigi 48", gigi48);
        return gigi48;
    }


    public String getGigi47(Context context){
        sp = context.getSharedPreferences("Gigi 47", Context.MODE_PRIVATE);
        value = null;
        gigi47 = sp.getString("Gigi 47", gigi47);
        return gigi47;
    }



    public String getGigi46(Context context){
        sp = context.getSharedPreferences("Gigi 46", Context.MODE_PRIVATE);
        value = null;
        gigi46 = sp.getString("Gigi 46", gigi46);
        return gigi46;
    }



    public String getGigi45(Context context){
        sp = context.getSharedPreferences("Gigi 45", Context.MODE_PRIVATE);
        value = null;
        gigi45 = sp.getString("Gigi 45", gigi45);
        return gigi45;
    }


    public String getGigi44(Context context){
        sp = context.getSharedPreferences("Gigi 44", Context.MODE_PRIVATE);
        value = null;
        gigi44 = sp.getString("Gigi 44", gigi44);
        return gigi44;
    }




    public String getGigi43(Context context){
        sp = context.getSharedPreferences("Gigi 43", Context.MODE_PRIVATE);
        value = null;
        gigi43 = sp.getString("Gigi 43", gigi43);
        return gigi43;
    }



    public String getGigi42(Context context){
        sp = context.getSharedPreferences("Gigi 42", Context.MODE_PRIVATE);
        value = null;
        gigi42 = sp.getString("Gigi 42", gigi42);
        return gigi42;
    }



    public String getGigi41(Context context){
        sp = context.getSharedPreferences("Gigi 41", Context.MODE_PRIVATE);
        value = null;
        gigi41 = sp.getString("Gigi 41", gigi41);
        return gigi41;
    }


    public String getGigi38(Context context){
        sp = context.getSharedPreferences("Gigi 38", Context.MODE_PRIVATE);
        value = null;
        gigi38 = sp.getString("Gigi 38", gigi38);
        return gigi38;
    }



    public String getGigi37(Context context){
        sp = context.getSharedPreferences("Gigi 37", Context.MODE_PRIVATE);
        value = null;
        gigi37 = sp.getString("Gigi 37", gigi37);
        return gigi37;
    }


    public String getGigi36(Context context){
        sp = context.getSharedPreferences("Gigi 36", Context.MODE_PRIVATE);
        value = null;
        gigi36 = sp.getString("Gigi 36", gigi36);
        return gigi36;
    }



    public String getGigi35(Context context){
        sp = context.getSharedPreferences("Gigi 35", Context.MODE_PRIVATE);
        value = null;
        gigi35 = sp.getString("Gigi 35", gigi35);
        return gigi35;
    }



    public String getGigi34(Context context){
        sp = context.getSharedPreferences("Gigi 34", Context.MODE_PRIVATE);
        value = null;
        gigi34 = sp.getString("Gigi 34", gigi34);
        return gigi34;
    }



    public String getGigi33(Context context){
        sp = context.getSharedPreferences("Gigi 33", Context.MODE_PRIVATE);
        value = null;
        gigi33 = sp.getString("Gigi 33", gigi33);
        return gigi33;
    }



    public String getGigi32(Context context){
        sp = context.getSharedPreferences("Gigi 32", Context.MODE_PRIVATE);
        value = null;
        gigi32 = sp.getString("Gigi 32", gigi32);
        return gigi32;
    }



    public String getGigi31(Context context){
        sp = context.getSharedPreferences("Gigi 31", Context.MODE_PRIVATE);
        value = null;
        gigi31 = sp.getString("Gigi 31", gigi31);
        return gigi31;
    }



    public String getGigi85(Context context){
        sp = context.getSharedPreferences("Gigi 85", Context.MODE_PRIVATE);
        value = null;
        gigi85 = sp.getString("Gigi 85", gigi85);
        return gigi85;
    }


    public String getGigi84(Context context){
        sp = context.getSharedPreferences("Gigi 84", Context.MODE_PRIVATE);
        value = null;
        gigi84 = sp.getString("Gigi 84", gigi84);
        return gigi84;
    }




    public String getGigi83(Context context){
        sp = context.getSharedPreferences("Gigi 83", Context.MODE_PRIVATE);
        value = null;
        gigi83 = sp.getString("Gigi 83", gigi83);
        return gigi83;
    }



    public String getGigi82(Context context){
        sp = context.getSharedPreferences("Gigi 82", Context.MODE_PRIVATE);
        value = null;
        gigi82 = sp.getString("Gigi 82", gigi82);
        return gigi82;
    }



    public String getGigi81(Context context){
        sp = context.getSharedPreferences("Gigi 81", Context.MODE_PRIVATE);
        value = null;
        gigi81 = sp.getString("Gigi 81", gigi81);
        return gigi81;
    }


    public String getGigi75(Context context){
        sp = context.getSharedPreferences("Gigi 75", Context.MODE_PRIVATE);
        value = null;
        gigi75 = sp.getString("Gigi 75", gigi75);
        return gigi75;
    }



    public String getGigi74(Context context){
        sp = context.getSharedPreferences("Gigi 74", Context.MODE_PRIVATE);
        value = null;
        gigi74 = sp.getString("Gigi 74", gigi74);
        return gigi74;
    }




    public String getGigi73(Context context){
        sp = context.getSharedPreferences("Gigi 73", Context.MODE_PRIVATE);
        value = null;
        gigi73 = sp.getString("Gigi 73", gigi73);
        return gigi73;
    }



    public String getGigi72(Context context){
        sp = context.getSharedPreferences("Gigi 72", Context.MODE_PRIVATE);
        value = null;
        gigi72 = sp.getString("Gigi 72", gigi72);
        return gigi72;
    }


    public String getGigi71(Context context){
        sp = context.getSharedPreferences("Gigi 71", Context.MODE_PRIVATE);
        value = null;
        gigi71 = sp.getString("Gigi 71", gigi71);
        return gigi71;
    }




    public String getGigi55(Context context){
        sp = context.getSharedPreferences("Gigi 55", Context.MODE_PRIVATE);
        value = null;
        gigi55 = sp.getString("Gigi 55", gigi55);
        return gigi55;
    }



    public String getGigi54(Context context){
        sp = context.getSharedPreferences("Gigi 54", Context.MODE_PRIVATE);
        value = null;
        gigi54 = sp.getString("Gigi 54", gigi54);
        return gigi54;
    }




    public String getGigi53(Context context){
        sp = context.getSharedPreferences("Gigi 53", Context.MODE_PRIVATE);
        value = null;
        gigi53 = sp.getString("Gigi 53", gigi53);
        return gigi53;
    }



    public String getGigi52(Context context){
        sp = context.getSharedPreferences("Gigi 52", Context.MODE_PRIVATE);
        value = null;
        gigi52 = sp.getString("Gigi 52", gigi52);
        return gigi52;
    }



    public String getGigi51(Context context){
        sp = context.getSharedPreferences("Gigi 51", Context.MODE_PRIVATE);
        value = null;
        gigi51 = sp.getString("Gigi 51", gigi51);
        return gigi51;
    }



    public String getGigi65(Context context){
        sp = context.getSharedPreferences("Gigi 65", Context.MODE_PRIVATE);
        value = null;
        gigi65 = sp.getString("Gigi 65", gigi65);
        return gigi65;
    }


    public String getGigi64(Context context){
        sp = context.getSharedPreferences("Gigi 64", Context.MODE_PRIVATE);
        value = null;
        gigi64 = sp.getString("Gigi 64", gigi64);
        return gigi64;
    }




    public String getGigi63(Context context){
        sp = context.getSharedPreferences("Gigi 63", Context.MODE_PRIVATE);
        value = null;
        gigi63 = sp.getString("Gigi 63", gigi63);
        return gigi63;
    }



    public String getGigi62(Context context){
        sp = context.getSharedPreferences("Gigi 62", Context.MODE_PRIVATE);
        value = null;
        gigi62 = sp.getString("Gigi 62", gigi62);
        return gigi62;
    }



    public String getGigi61(Context context){
        sp = context.getSharedPreferences("Gigi 61", Context.MODE_PRIVATE);
        value = null;
        gigi61 = sp.getString("Gigi 61", gigi61);
        return gigi61;
    }

    public void setNamaPasien(Context context, String namaPasien){
        sp = context.getSharedPreferences("namaPasien", Context.MODE_PRIVATE);
        editor = sp.edit();
        editor.clear();
        editor.putString("namaPasien", namaPasien);
        editor.commit();
    }

    public String getNamaPasien(Context context){
        sp = context.getSharedPreferences("namaPasien", Context.MODE_PRIVATE);
        value = null;
        namaPasien = sp.getString("namaPasien", namaPasien);
        return namaPasien;
    }

    public void setTempatLahir(Context context, String tempatLahir){
        sp = context.getSharedPreferences("tempatLahir", Context.MODE_PRIVATE);
        editor = sp.edit();
        editor.putString("tempatLahir", tempatLahir);
        editor.commit();
    }

    public String getTempatLahir(Context context){
        sp = context.getSharedPreferences("tempatLahir", Context.MODE_PRIVATE);
        value = null;
        tempatLahir = sp.getString("tempatLahir", tempatLahir);
        return tempatLahir;
    }
    public void setTanggalLahir(Context context, String tanggalLahir){
        sp = context.getSharedPreferences("tanggalLahir", Context.MODE_PRIVATE);
        editor = sp.edit();
        editor.putString("tanggalLahir", tanggalLahir);
        editor.commit();
    }

    public String getTanggalLahir(Context context){
        sp = context.getSharedPreferences("tanggalLahir", Context.MODE_PRIVATE);
        value = null;
        tanggalLahir = sp.getString("tanggalLahir", tanggalLahir);
        return tanggalLahir;
    }


    public void setNoIdentitas(Context context, String noIdentitas){
        sp = context.getSharedPreferences("noIdentitas", Context.MODE_PRIVATE);
        editor = sp.edit();
        editor.putString("noIdentitas", noIdentitas);
        editor.commit();
    }

    public String getNoIdentitas(Context context){
        sp = context.getSharedPreferences("noIdentitas", Context.MODE_PRIVATE);
        value = null;
        noIdentitas = sp.getString("noIdentitas", noIdentitas);
        return noIdentitas;
    }

    public void setJenKelamin(Context context, String jenKelamin){
        sp = context.getSharedPreferences("jenKelamin", Context.MODE_PRIVATE);
        editor = sp.edit();
        editor.putString("jenKelamin", jenKelamin);
        editor.commit();
    }

    public String getJenKelamin(Context context){
        sp = context.getSharedPreferences("jenKelamin", Context.MODE_PRIVATE);
        value = null;
        jenKelamin = sp.getString("jenKelamin", jenKelamin);
        return jenKelamin;
    }

    public void setOcclusi(Context context, String occlusi){
        sp = context.getSharedPreferences("occlusi", Context.MODE_PRIVATE);
        editor = sp.edit();
        editor.putString("occlusi", occlusi);
        editor.commit();
    }

    public String getOcclusi(Context context){
        sp = context.getSharedPreferences("occlusi", Context.MODE_PRIVATE);
        value = null;
        occlusi = sp.getString("occlusi", occlusi);
        return occlusi;
    }

    public void setPalatinus(Context context, String palatinus){
        sp = context.getSharedPreferences("palatinus", Context.MODE_PRIVATE);
        editor = sp.edit();
        editor.putString("palatinus", palatinus);
        editor.commit();
    }

    public String getPalatinus(Context context){
        sp = context.getSharedPreferences("palatinus", Context.MODE_PRIVATE);
        value = null;
        palatinus = sp.getString("palatinus", palatinus);
        return palatinus;
    }

    public void setMandibularis(Context context, String mandibularis){
        sp = context.getSharedPreferences("mandibularis", Context.MODE_PRIVATE);
        editor = sp.edit();
        editor.putString("mandibularis", mandibularis);
        editor.commit();
    }

    public String getMandibularis(Context context){
        sp = context.getSharedPreferences("mandibularis", Context.MODE_PRIVATE);
        value = null;
        mandibularis = sp.getString("mandibularis", mandibularis);
        return mandibularis;
    }

    public void setPalatum(Context context, String palatum){
        sp = context.getSharedPreferences("palatum", Context.MODE_PRIVATE);
        editor = sp.edit();
        editor.putString("palatum", palatum);
        editor.commit();
    }

    public String getPalatum(Context context){
        sp = context.getSharedPreferences("palatum", Context.MODE_PRIVATE);
        value = null;
        palatum = sp.getString("palatum", palatum);
        return palatum;
    }

    public void setDiastema(Context context, String diastema){
        sp = context.getSharedPreferences("diastema", Context.MODE_PRIVATE);
        editor = sp.edit();
        editor.putString("diastema", diastema);
        editor.commit();
    }

    public String getDiastema(Context context){
        sp = context.getSharedPreferences("diastema", Context.MODE_PRIVATE);
        value = null;
        diastema = sp.getString("diastema", diastema);
        return diastema;
    }

    public void setKetDiastema(Context context, String ketDiastema){
        sp = context.getSharedPreferences("ketDiastema", Context.MODE_PRIVATE);
        editor = sp.edit();
        editor.putString("ketDiastema", ketDiastema);
        editor.commit();
    }

    public String getKetDiastema(Context context){
        sp = context.getSharedPreferences("ketDiastema", Context.MODE_PRIVATE);
        value = null;
        ketDiastema = sp.getString("ketDiastema", ketDiastema);
        return ketDiastema;
    }

    public void setAnomali(Context context, String anomali){
        sp = context.getSharedPreferences("anomali", Context.MODE_PRIVATE);
        editor = sp.edit();
        editor.putString("anomali", anomali);
        editor.commit();
    }

    public String getAnomali(Context context){
        sp = context.getSharedPreferences("anomali", Context.MODE_PRIVATE);
        value = null;
        anomali = sp.getString("anomali", anomali);
        return anomali;
    }

    public void setKetAnomali(Context context, String ketAnomali){
        sp = context.getSharedPreferences("ketAnomali", Context.MODE_PRIVATE);
        editor = sp.edit();
        editor.putString("ketAnomali", ketAnomali);
        editor.commit();
    }

    public String getKetAnomali(Context context){
        sp = context.getSharedPreferences("ketAnomali", Context.MODE_PRIVATE);
        value = null;
        ketAnomali = sp.getString("ketAnomali", ketAnomali);
        return ketAnomali;
    }

    public void setLainnya(Context context, String lainnya){
        sp = context.getSharedPreferences("lainnya", Context.MODE_PRIVATE);
        editor = sp.edit();
        editor.putString("lainnya", lainnya);
        editor.commit();
    }

    public String getLainnya(Context context){
        sp = context.getSharedPreferences("lainnya", Context.MODE_PRIVATE);
        value = null;
        lainnya = sp.getString("lainnya", lainnya);
        return lainnya;
    }


    public void setTtdPasien(Context context, String ttdPasien){
        sp = context.getSharedPreferences("ttdPasien", Context.MODE_PRIVATE);
        editor = sp.edit();
        editor.putString("ttdPasien", ttdPasien);
        editor.commit();
    }

    public String getTtdPasien(Context context){
        sp = context.getSharedPreferences("ttdPasien", Context.MODE_PRIVATE);
        value = null;
        ttdPasien = sp.getString("ttdPasien", ttdPasien);
        return ttdPasien;
    }

    public void setTtdDokter(Context context, String ttdDokter){
        sp = context.getSharedPreferences("ttdDokter", Context.MODE_PRIVATE);
        editor = sp.edit();
        editor.putString("ttdDokter", ttdDokter);
        editor.commit();
    }

    public String getTtdDokter(Context context){
        sp = context.getSharedPreferences("ttdDokter", Context.MODE_PRIVATE);
        value = null;
        ttdDokter = sp.getString("ttdDokter", ttdDokter);
        return ttdDokter;
    }

    public void setStatus(Context context, String status){
        sp = context.getSharedPreferences("status", Context.MODE_PRIVATE);
        editor = sp.edit();
        editor.putString("status", status);
        editor.commit();
    }

    public String getStatus(Context context){
        sp = context.getSharedPreferences("status", Context.MODE_PRIVATE);
        value = null;
        status = sp.getString("status", status);
        return status;
    }


    public void setKeyPushTindakanPerawatan(Context context, String keyPushTindakanPerawatan){
        sp = context.getSharedPreferences("detailPreference", Context.MODE_PRIVATE);
        editor = sp.edit();
        editor.putString("keyPushTindakanPerawatan", keyPushTindakanPerawatan);
        editor.commit();
    }



}
