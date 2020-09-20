package com.teddybrothers.co_teddy.dentist;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.teddybrothers.co_teddy.dentist.customadapter.CustomAdapterFormulir;
import com.teddybrothers.co_teddy.dentist.customadapter.CustomAdapterPerawatanToday;
import com.teddybrothers.co_teddy.dentist.entity.DetailPerawatan;
import com.teddybrothers.co_teddy.dentist.entity.Formulir;
import com.teddybrothers.co_teddy.dentist.entity.Perawatan;
import com.teddybrothers.co_teddy.dentist.viewholder.MainViewHistoryTindakan;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class RekamMedisActivity extends AppCompatActivity {
    String gigi18, gigi17, gigi16, gigi15, gigi14, gigi13, gigi12, gigi11,
            gigi21, gigi22, gigi23, gigi24, gigi25, gigi26, gigi27, gigi28,
            gigi55, gigi54, gigi53, gigi52, gigi51,
            gigi61, gigi62, gigi63, gigi64, gigi65,
            gigi85, gigi84, gigi83, gigi82, gigi81,
            gigi71, gigi72, gigi73, gigi74, gigi75,
            gigi48, gigi47, gigi46, gigi45, gigi44, gigi43, gigi42, gigi41,
            gigi31, gigi32, gigi33, gigi34, gigi35, gigi36, gigi37, gigi38;
    public static final String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agust", "Sept", "Okt", "Nov", "Des"};
    String namaPasien, tempatLahir, tanggalLahir, noIdentitas, jenKelamin, occlusi, diastema, palatum, mandibularis, palatinus, anomali, lainnya, ketDiastema, ketAnomali;
    String kodeGigi,perawatanKey;
    LinearLayoutManager mManager;
    public static int REQUEST_PERMISSIONS = 1;
    DetailPerawatan ambil;
    boolean boolean_permission;
    FirebaseRecyclerAdapter<Perawatan, MainViewHistoryTindakan> mAdapterPerawatan;
    boolean boolean_save;
    Bitmap bitmap;
    public String idDokter, idPasien, jadwalKey,today;
    RelativeLayout rlRekamMedis;
    Button btnPdf;
    Utilities util = new Utilities();
    String status = null, statusUser;
    FirebaseDatabase mDatabase;
    FirebaseAuth mAuth;
    DatabaseReference mRoot, mUserRef, mPasien, mRekamMedis, mPerawatan, mJadwal, mTindakanPerawatan, mDokter;
    ProgressDialog progressDialog;
    final ArrayList<Perawatan> listPerawatanToday = new ArrayList<Perawatan>();
    public Calendar cal = Calendar.getInstance();
    public int day = cal.get(Calendar.DAY_OF_MONTH);
    public int month = cal.get(Calendar.MONTH);
    public int year = cal.get(Calendar.YEAR);
    Perawatan listPerawatan;

    int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rekam_medis);

        System.out.println("mDatabase = " + mDatabase);
//        progressDialog = new ProgressDialog(RekamMedisActivity.this);
//        progressDialog.setMessage("Loading . . . ");
//        progressDialog.show();

        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
        }
        mRoot = mDatabase.getReference();
        mUserRef = mRoot.child("users");
        mPasien = mRoot.child("pasien");
        mRekamMedis = mRoot.child("rekammedis");
        mTindakanPerawatan = mRoot.child("tindakanperawatan");
        mPerawatan = mRoot.child("perawatan");
        mJadwal = mRoot.child("jadwal");
        mDokter = mRoot.child("dokter");
        mAuth = FirebaseAuth.getInstance();

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            System.out.println("MASUK YEYEY");
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            status = extras.getString("status");
        }

        statusUser = util.getStatus(RekamMedisActivity.this);

        if (status.equalsIgnoreCase("detail")) {
            idPasien = util.getIdPasien(RekamMedisActivity.this);
        } else if (status.equalsIgnoreCase("profil")) {
            idPasien = extras.getString("idPasien");
        }

        idDokter = util.getIdDokter(RekamMedisActivity.this);
        jadwalKey = util.getIdJadwal(RekamMedisActivity.this);


        System.out.println(idDokter + " " + idPasien + " " + jadwalKey);
        //Fragment Rekam Medis
//        getSupportFragmentManager().beginTransaction().add(R.id.frame_layout_left, new RekamMedis()).commit();


        android.support.v4.app.FragmentManager fragmentManager = RekamMedisActivity.this.getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        RekamMedis rekamMedis = new RekamMedis();
        Bundle bundle = new Bundle();
        bundle.putString("idPasien", idPasien);
        bundle.putString("status", status);
        rekamMedis.setArguments(bundle);
        transaction.add(R.id.frame_layout_left, rekamMedis);
        transaction.commit();


        //Fragment Odontogram
//        getSupportFragmentManager().beginTransaction().add(R.id.frame_layout_right, new Odontogram()).commit();
        android.support.v4.app.FragmentManager fragmentManager1 = RekamMedisActivity.this.getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction1 = fragmentManager1.beginTransaction();
        Odontogram odontogram = new Odontogram();
        Bundle bundle2 = new Bundle();
        bundle2.putString("status", status);
        bundle2.putString("idPasien", idPasien);
        odontogram.setArguments(bundle2);
        transaction1.add(R.id.frame_layout_right, odontogram);
        transaction1.commit();


        for (int i = 11; i <= 85; i++) {
            setDetail("Gigi " + i, "detail" + i);
        }

        fn_permission();


    }

    @Override
    protected void onResume() {
        super.onResume();
        for (int i = 11; i <= 85; i++) {
            setDetail("Gigi " + i, "detail" + i);
        }

    }

    class myFooter extends PdfPageEventHelper {
        Font ffont = new Font(Font.FontFamily.UNDEFINED, 8, Font.ITALIC);
        Font judul = FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, Font.BOLD);

        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            Date date = new Date();
            String dateCreated = new SimpleDateFormat("dd" + "/" + "MM" + "/" + "yyyy").format(date);
            Phrase footer = new Phrase("Tanggal Cetak : " + dateCreated, ffont);


            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, footer, (document.right() - document.left()) / 2 + document.leftMargin(),
                    document.bottom() - 10, 0);
        }
    }




    private class AsyncTaskRunner extends AsyncTask<String, Void, Integer> {
        ProgressDialog progressDialogPdf;
        private Activity activity;
        private Context mContext;

        public AsyncTaskRunner(RekamMedisActivity activity) {

            this.activity = activity;
            this.mContext = activity;
            progressDialogPdf = new ProgressDialog(mContext);
        }

        @Override
        protected Integer doInBackground(String... strings) {

            runOnUiThread(new Runnable() {
                public void run() {
                    for (int i = 11; i <= 85; i++) {
                        setDetail("Gigi " + i, "detail" + i);
                    }

                    gigi18 = util.getGigi18(RekamMedisActivity.this);
                    System.out.println("gigi 18 = " + gigi18);
                    gigi17 = util.getGigi17(RekamMedisActivity.this);
                    gigi16 = util.getGigi16(RekamMedisActivity.this);
                    gigi15 = util.getGigi15(RekamMedisActivity.this);
                    gigi14 = util.getGigi14(RekamMedisActivity.this);
                    gigi13 = util.getGigi13(RekamMedisActivity.this);
                    gigi12 = util.getGigi12(RekamMedisActivity.this);
                    gigi11 = util.getGigi11(RekamMedisActivity.this);
                    gigi21 = util.getGigi21(RekamMedisActivity.this);
                    gigi22 = util.getGigi22(RekamMedisActivity.this);
                    gigi23 = util.getGigi23(RekamMedisActivity.this);
                    gigi24 = util.getGigi24(RekamMedisActivity.this);
                    gigi25 = util.getGigi25(RekamMedisActivity.this);
                    gigi26 = util.getGigi26(RekamMedisActivity.this);
                    gigi27 = util.getGigi27(RekamMedisActivity.this);
                    gigi28 = util.getGigi28(RekamMedisActivity.this);
                    //GIGI TENGAH ATAS
                    gigi55 = util.getGigi55(RekamMedisActivity.this);
                    gigi54 = util.getGigi54(RekamMedisActivity.this);
                    gigi53 = util.getGigi53(RekamMedisActivity.this);
                    gigi52 = util.getGigi52(RekamMedisActivity.this);
                    gigi51 = util.getGigi51(RekamMedisActivity.this);
                    gigi61 = util.getGigi61(RekamMedisActivity.this);
                    gigi62 = util.getGigi62(RekamMedisActivity.this);
                    gigi63 = util.getGigi63(RekamMedisActivity.this);
                    gigi64 = util.getGigi64(RekamMedisActivity.this);
                    gigi65 = util.getGigi65(RekamMedisActivity.this);
                    //GIGI TENGAH BAWAH
                    gigi85 = util.getGigi85(RekamMedisActivity.this);
                    gigi84 = util.getGigi84(RekamMedisActivity.this);
                    gigi83 = util.getGigi83(RekamMedisActivity.this);
                    gigi82 = util.getGigi82(RekamMedisActivity.this);
                    gigi81 = util.getGigi81(RekamMedisActivity.this);
                    gigi71 = util.getGigi71(RekamMedisActivity.this);
                    gigi72 = util.getGigi72(RekamMedisActivity.this);
                    gigi73 = util.getGigi73(RekamMedisActivity.this);
                    gigi74 = util.getGigi74(RekamMedisActivity.this);
                    gigi75 = util.getGigi75(RekamMedisActivity.this);
                    //GIGI BAWAH
                    gigi48 = util.getGigi48(RekamMedisActivity.this);
                    gigi47 = util.getGigi47(RekamMedisActivity.this);
                    gigi46 = util.getGigi46(RekamMedisActivity.this);
                    gigi45 = util.getGigi45(RekamMedisActivity.this);
                    gigi44 = util.getGigi44(RekamMedisActivity.this);
                    gigi43 = util.getGigi43(RekamMedisActivity.this);
                    gigi42 = util.getGigi42(RekamMedisActivity.this);
                    gigi41 = util.getGigi41(RekamMedisActivity.this);
                    gigi31 = util.getGigi31(RekamMedisActivity.this);
                    gigi32 = util.getGigi32(RekamMedisActivity.this);
                    gigi33 = util.getGigi33(RekamMedisActivity.this);
                    gigi34 = util.getGigi34(RekamMedisActivity.this);
                    gigi35 = util.getGigi35(RekamMedisActivity.this);
                    gigi36 = util.getGigi36(RekamMedisActivity.this);
                    gigi37 = util.getGigi37(RekamMedisActivity.this);
                    gigi38 = util.getGigi38(RekamMedisActivity.this);
                    System.out.println("idPasien bawah = " + idPasien);







                    namaPasien = util.getNamaPasien(RekamMedisActivity.this);
                    tempatLahir = util.getTempatLahir(RekamMedisActivity.this);
                    tanggalLahir = util.getTanggalLahir(RekamMedisActivity.this);
                    noIdentitas = util.getNoIdentitas(RekamMedisActivity.this);
                    jenKelamin = util.getJenKelamin(RekamMedisActivity.this);
                    occlusi = util.getOcclusi(RekamMedisActivity.this);
                    palatinus = util.getPalatinus(RekamMedisActivity.this);
                    palatum = util.getPalatum(RekamMedisActivity.this);
                    mandibularis = util.getMandibularis(RekamMedisActivity.this);
                    diastema = util.getDiastema(RekamMedisActivity.this);
                    ketDiastema = util.getKetDiastema(RekamMedisActivity.this);
                    anomali = util.getAnomali(RekamMedisActivity.this);
                    ketAnomali = util.getKetAnomali(RekamMedisActivity.this);
                    lainnya = util.getLainnya(RekamMedisActivity.this);
                    System.out.println("namaPasien = " + namaPasien);


                    try {
                        createPdf(gigi18, gigi17, gigi16, gigi15, gigi14, gigi13, gigi12, gigi11, gigi21, gigi22, gigi23, gigi24, gigi25, gigi26, gigi27, gigi28
                                , gigi55, gigi54, gigi53, gigi52, gigi51, gigi65, gigi64, gigi63, gigi62, gigi61, gigi85, gigi84, gigi83, gigi82, gigi81, gigi75, gigi74, gigi73, gigi72, gigi71
                                , gigi48, gigi47, gigi46, gigi45, gigi44, gigi43, gigi42, gigi41, gigi31, gigi32, gigi33, gigi34, gigi35, gigi36, gigi37, gigi38
                                , namaPasien, tempatLahir, tanggalLahir, noIdentitas, jenKelamin, occlusi, diastema, palatum, mandibularis, palatinus, anomali, lainnya, ketDiastema, ketAnomali);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }
                }
            });


            return null;
        }


        protected void onPostExecute(Integer s) {
            System.out.println("Progress PDF dismiss");

            if (progressDialogPdf.isShowing()) {
                progressDialogPdf.dismiss();
            }

        }

        @Override
        protected void onPreExecute() {
            System.out.println("Progress PDF");

            this.progressDialogPdf.setMessage("Harap Menunggu....");
            this.progressDialogPdf.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            this.progressDialogPdf.setProgress(0);
            this.progressDialogPdf.setMax(100);
            this.progressDialogPdf.show();

        }
    }


    private void createPdf(String gigiKode18, String gigiKode17, String gigiKode16, String gigiKode15, String gigiKode14, String gigiKode13, String gigiKode12, String gigiKode11,
                           String gigiKode21, String gigiKode22, String gigiKode23, String gigiKode24, String gigiKode25, String gigiKode26, String gigiKode27, String gigiKode28,
                           String gigiKode55, String gigiKode54, String gigiKode53, String gigiKode52, String gigiKode51, String gigiKode65, String gigiKode64, String gigiKode63, String gigiKode62, String gigiKode61,
                           String gigiKode85, String gigiKode84, String gigiKode83, String gigiKode82, String gigiKode81, String gigiKode75, String gigiKode74, String gigiKode73, String gigiKode72, String gigiKode71,
                           String gigiKode48, String gigiKode47, String gigiKode46, String gigiKode45, String gigiKode44, String gigiKode43, String gigiKode42, String gigiKode41,
                           String gigiKode31, String gigiKode32, String gigiKode33, String gigiKode34, String gigiKode35, String gigiKode36, String gigiKode37, String gigiKode38,
                           String namaPasien, String tempatLahir, String tanggalLahir, String noIdentitas, String jenKelamin, String occlusiData, String diastemaData, String palatumData, String mandibularisData, String palatinusData,
                           String anomaliData, String lainnyaData, String ketDiastemaData, String ketAnomaliData) throws IOException, DocumentException {


        File pdfFolder = new File("/sdcard", "pdfDentist");

        if (!pdfFolder.exists()) {
            pdfFolder.mkdir();

        }


        System.out.println("namaPasien method = " + namaPasien);
        BillingActivity.TableHeader.HeaderTable event = new BillingActivity.TableHeader.HeaderTable();
        //Create time stamp
        Date date = new Date();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);
        String dateCreated = new SimpleDateFormat("dd" + "/" + "MM" + "/" + "yyyy").format(date);
        File myFile = new File("/sdcard/pdfDentist/" + namaPasien + ".pdf");
        System.out.println("pdf file = " + myFile);

        FileOutputStream output = new FileOutputStream(myFile);


        //Step 1
        Document document = new Document(PageSize.A4, 36, 36, 40 + event.getTableHeight(), 36);

        //Step
        PdfWriter pdfWriter = PdfWriter.getInstance(document, output);
        pdfWriter.setPageEvent(event);
        //Step 3
        document.open();

        myFooter footer = new myFooter();
        pdfWriter.setPageEvent(footer);
//        Paragraph createdAt = new Paragraph("Tanggal Cetak : " + dateCreated, FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.BOLDITALIC));
//        createdAt.setAlignment(Paragraph.ALIGN_RIGHT);
//        createdAt.setSpacingBefore(0);
//        document.add(createdAt);


        //Step 4 Add conten
        Font judul = FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, Font.BOLD);
        Font text = FontFactory.getFont(FontFactory.TIMES_ROMAN, 11);
        Paragraph content = new Paragraph("REKAM MEDIS PASIEN", judul);
        content.setAlignment(Paragraph.ALIGN_CENTER);
        content.setSpacingAfter(10);
        document.add(content);



        final PdfPTable pdfPTable = new PdfPTable(5);
        pdfPTable.setWidthPercentage(100);
        pdfPTable.setWidths(new float[]{2, 5,1,2,5});
        pdfPTable.addCell(getCell("Nama Pasien", PdfPCell.ALIGN_LEFT,0));
        pdfPTable.addCell(getCell(": "+namaPasien, PdfPCell.ALIGN_LEFT,1));
        pdfPTable.addCell(getCell("", PdfPCell.ALIGN_LEFT,0));
        pdfPTable.addCell(getCell("Jenis Kelamin", PdfPCell.ALIGN_LEFT,0));
        pdfPTable.addCell(getCell(": "+jenKelamin, PdfPCell.ALIGN_LEFT,1));
        pdfPTable.setSpacingAfter(8);
        document.add(pdfPTable);




        final PdfPTable pdfPTable2 = new PdfPTable(5);
        pdfPTable2.setWidthPercentage(100);
        pdfPTable2.setWidths(new float[]{2, 5,1,2,5});
        pdfPTable2.addCell(getCell("NIK/No.KTP", PdfPCell.ALIGN_LEFT,0));
        pdfPTable2.addCell(getCell(": "+noIdentitas, PdfPCell.ALIGN_LEFT,1));
        pdfPTable2.addCell(getCell("", PdfPCell.ALIGN_LEFT,0));
        pdfPTable2.addCell(getCell("TTL", PdfPCell.ALIGN_LEFT,0));
        pdfPTable2.addCell(getCell(": "+tempatLahir + ", " + tanggalLahir, PdfPCell.ALIGN_LEFT,1));
        pdfPTable2.setSpacingAfter(8);
        document.add(pdfPTable2);

        //ggi1151
        PdfPTable sou1151 = new PdfPTable(4);
        sou1151.setWidthPercentage(100);
        sou1151.setWidths(new float[]{1, 2, 2, 1});
        sou1151.addCell(getCellBorder("11 [51]", PdfPCell.ALIGN_CENTER));
        String kode11 = cekKodeTabel("11", "51", gigiKode11, gigiKode51, "sou2");
        sou1151.addCell(getCellBorder(kode11, PdfPCell.ALIGN_LEFT));
        //gigi2161
        String kode21 = cekKodeTabel("21", "61", gigiKode21, gigiKode61, "sou2");
        sou1151.addCell(getCellBorder(kode21, PdfPCell.ALIGN_LEFT));
        sou1151.addCell(getCellBorder("[61] 21", PdfPCell.ALIGN_CENTER));
        document.add(sou1151);

        PdfPTable sou1252 = new PdfPTable(4);
        sou1252.setWidthPercentage(100);
        sou1252.setWidths(new float[]{1, 2, 2, 1});
        sou1252.addCell(getCellBorder("12 [52]", PdfPCell.ALIGN_CENTER));
        String kode12 = cekKodeTabel("12", "52", gigiKode12, gigiKode51, "sou2");
        sou1252.addCell(getCellBorder(kode12, PdfPCell.ALIGN_LEFT));
        String kode22 = cekKodeTabel("22", "62", gigiKode22, gigiKode62, "sou2");
        sou1252.addCell(getCellBorder(kode22, PdfPCell.ALIGN_LEFT));
        sou1252.addCell(getCellBorder("[62] 22", PdfPCell.ALIGN_CENTER));
        document.add(sou1252);

        PdfPTable sou1353 = new PdfPTable(4);
        sou1353.setWidthPercentage(100);
        sou1353.setWidths(new float[]{1, 2, 2, 1});
        sou1353.addCell(getCellBorder("13 [53]", PdfPCell.ALIGN_CENTER));
        String kode13 = cekKodeTabel("13", "53", gigiKode13, gigiKode53, "sou2");
        sou1353.addCell(getCellBorder(kode13, PdfPCell.ALIGN_LEFT));
        String kode23 = cekKodeTabel("23", "63", gigiKode23, gigiKode63, "sou2");
        sou1353.addCell(getCellBorder(kode23, PdfPCell.ALIGN_LEFT));
        sou1353.addCell(getCellBorder("[63] 23", PdfPCell.ALIGN_CENTER));
        document.add(sou1353);


        PdfPTable sou1454 = new PdfPTable(4);
        sou1454.setWidthPercentage(100);
        sou1454.setWidths(new float[]{1, 2, 2, 1});
        sou1454.addCell(getCellBorder("14 [54]", PdfPCell.ALIGN_CENTER));
        String kode14 = cekKodeTabel("14", "54", gigiKode14, gigiKode54, "sou");
        sou1454.addCell(getCellBorder(kode14, PdfPCell.ALIGN_LEFT));
        String kode24 = cekKodeTabel("24", "64", gigiKode24, gigiKode64, "sou");
        sou1454.addCell(getCellBorder(kode24, PdfPCell.ALIGN_LEFT));
        sou1454.addCell(getCellBorder("[64] 24", PdfPCell.ALIGN_CENTER));
        document.add(sou1454);

        PdfPTable sou1555 = new PdfPTable(4);
        sou1555.setWidthPercentage(100);
        sou1555.setWidths(new float[]{1, 2, 2, 1});
        sou1555.addCell(getCellBorder("15 [55]", PdfPCell.ALIGN_CENTER));
        String kode15 = cekKodeTabel("15", "55", gigiKode15, gigiKode55, "sou");
        sou1555.addCell(getCellBorder(kode15, PdfPCell.ALIGN_LEFT));
        String kode25 = cekKodeTabel("25", "65", gigiKode25, gigiKode65, "sou");
        sou1555.addCell(getCellBorder(kode25, PdfPCell.ALIGN_LEFT));
        sou1555.addCell(getCellBorder("[65] 25", PdfPCell.ALIGN_CENTER));
        document.add(sou1555);


        PdfPTable sou16 = new PdfPTable(4);
        sou16.setWidthPercentage(100);
        sou16.setWidths(new float[]{1, 2, 2, 1});
        sou16.addCell(getCellBorder("16", PdfPCell.ALIGN_CENTER));
        if (gigiKode16 == null) {
            gigiKode16 = "sou";
        }
        sou16.addCell(getCellBorder(gigiKode16, PdfPCell.ALIGN_LEFT));
        if (gigiKode26 == null) {
            gigiKode26 = "sou";
        }
        sou16.addCell(getCellBorder(gigiKode26, PdfPCell.ALIGN_LEFT));
        sou16.addCell(getCellBorder("26", PdfPCell.ALIGN_CENTER));
        document.add(sou16);


        PdfPTable sou17 = new PdfPTable(4);
        sou17.setWidthPercentage(100);
        sou17.setWidths(new float[]{1, 2, 2, 1});
        sou17.addCell(getCellBorder("17", PdfPCell.ALIGN_CENTER));
        if (gigiKode17 == null) {
            gigiKode17 = "sou";
        }
        sou17.addCell(getCellBorder(gigiKode17, PdfPCell.ALIGN_LEFT));
        if (gigiKode27 == null) {
            gigiKode27 = "sou";
        }
        sou17.addCell(getCellBorder(gigiKode27, PdfPCell.ALIGN_LEFT));
        sou17.addCell(getCellBorder("27", PdfPCell.ALIGN_CENTER));
        document.add(sou17);

        PdfPTable sou18 = new PdfPTable(4);
        sou18.setWidthPercentage(100);
        sou18.setWidths(new float[]{1, 2, 2, 1});
        sou18.addCell(getCellBorder("18", PdfPCell.ALIGN_CENTER));
        if (gigiKode18 == null) {
            gigiKode18 = "sou";
        }
        sou18.addCell(getCellBorder(gigiKode18, PdfPCell.ALIGN_LEFT));
        if (gigiKode28 == null) {
            gigiKode28 = "sou";
        }
        sou18.addCell(getCellBorder(gigiKode28, PdfPCell.ALIGN_LEFT));
        sou18.addCell(getCellBorder("28", PdfPCell.ALIGN_CENTER));
        document.add(sou18);


        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap bitmap = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.sou);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

        Image gigi18, gigi17, gigi16, gigi15, gigi14, gigi28, gigi27, gigi26, gigi25, gigi24, gigi55, gigi54, gigi64, gigi65, gigi48, gigi47, gigi46, gigi45, gigi44, gigi34, gigi35, gigi36, gigi37, gigi38, gigi85, gigi84, gigi74, gigi75;


        //STREAM GIGI ATAS
        ByteArrayOutputStream stream18 = new ByteArrayOutputStream();
        Bitmap bitmap18;
        if (gigiKode18 != null) {
            kodeGigi = checkKodeGigi(gigiKode18);
            System.out.println("Kode gigi = " + kodeGigi);
            int res18 = RekamMedisActivity.this.getResources().getIdentifier(kodeGigi, "drawable", RekamMedisActivity.this.getPackageName());
            bitmap18 = BitmapFactory.decodeResource(getBaseContext().getResources(), res18);
        } else {
            bitmap18 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.sou);
        }
        bitmap18.compress(Bitmap.CompressFormat.PNG, 100, stream18);

        ByteArrayOutputStream stream17 = new ByteArrayOutputStream();
        Bitmap bitmap17;
        if (gigiKode17 != null) {
            kodeGigi = checkKodeGigi(gigiKode17);
            System.out.println("Kode gigi = " + kodeGigi);
            int res13 = RekamMedisActivity.this.getResources().getIdentifier(kodeGigi, "drawable", RekamMedisActivity.this.getPackageName());
            bitmap17 = BitmapFactory.decodeResource(getBaseContext().getResources(), res13);
        } else {
            bitmap17 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.sou);
        }
        bitmap17.compress(Bitmap.CompressFormat.PNG, 100, stream17);

        ByteArrayOutputStream stream16 = new ByteArrayOutputStream();
        Bitmap bitmap16;
        if (gigiKode16 != null) {
            kodeGigi = checkKodeGigi(gigiKode16);
            System.out.println("Kode gigi = " + kodeGigi);
            int res13 = RekamMedisActivity.this.getResources().getIdentifier(kodeGigi, "drawable", RekamMedisActivity.this.getPackageName());
            bitmap16 = BitmapFactory.decodeResource(getBaseContext().getResources(), res13);
        } else {
            bitmap16 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.sou);
        }
        bitmap16.compress(Bitmap.CompressFormat.PNG, 100, stream16);

        ByteArrayOutputStream stream15 = new ByteArrayOutputStream();
        Bitmap bitmap15;
        if (gigiKode15 != null) {
            kodeGigi = checkKodeGigi(gigiKode15);
            System.out.println("Kode gigi = " + kodeGigi);
            int res13 = RekamMedisActivity.this.getResources().getIdentifier(kodeGigi, "drawable", RekamMedisActivity.this.getPackageName());
            bitmap15 = BitmapFactory.decodeResource(getBaseContext().getResources(), res13);
        } else {
            bitmap15 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.sou);
        }
        bitmap15.compress(Bitmap.CompressFormat.PNG, 100, stream15);

        ByteArrayOutputStream stream14 = new ByteArrayOutputStream();
        Bitmap bitmap14;
        if (gigiKode14 != null) {
            kodeGigi = checkKodeGigi(gigiKode14);
            System.out.println("Kode gigi = " + kodeGigi);
            int res13 = RekamMedisActivity.this.getResources().getIdentifier(kodeGigi, "drawable", RekamMedisActivity.this.getPackageName());
            bitmap14 = BitmapFactory.decodeResource(getBaseContext().getResources(), res13);
        } else {
            bitmap14 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.sou);
        }
        bitmap14.compress(Bitmap.CompressFormat.PNG, 100, stream14);

        ByteArrayOutputStream stream13 = new ByteArrayOutputStream();
        Bitmap bitmap13;
        if (gigiKode13 != null) {
            kodeGigi = checkKodeGigi(gigiKode13);
            System.out.println("Kode gigi = " + kodeGigi);
            int res13 = RekamMedisActivity.this.getResources().getIdentifier(kodeGigi, "drawable", RekamMedisActivity.this.getPackageName());
            bitmap13 = BitmapFactory.decodeResource(getBaseContext().getResources(), res13);
        } else {
            bitmap13 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.sou2);
        }
        bitmap13.compress(Bitmap.CompressFormat.PNG, 100, stream13);

        ByteArrayOutputStream stream12 = new ByteArrayOutputStream();
        Bitmap bitmap12;
        if (gigiKode12 != null) {
            kodeGigi = checkKodeGigi(gigiKode12);
            System.out.println("Kode gigi = " + kodeGigi);
            int res12 = RekamMedisActivity.this.getResources().getIdentifier(kodeGigi, "drawable", RekamMedisActivity.this.getPackageName());
            bitmap12 = BitmapFactory.decodeResource(getBaseContext().getResources(), res12);
        } else {
            bitmap12 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.sou2);
        }
        bitmap12.compress(Bitmap.CompressFormat.PNG, 100, stream12);

        ByteArrayOutputStream stream11 = new ByteArrayOutputStream();
        Bitmap bitmap11;
        if (gigiKode11 != null) {
            kodeGigi = checkKodeGigi(gigiKode11);
            System.out.println("Kode gigi 11 = " + kodeGigi);
            int res11 = RekamMedisActivity.this.getResources().getIdentifier(kodeGigi, "drawable", RekamMedisActivity.this.getPackageName());
            bitmap11 = BitmapFactory.decodeResource(getBaseContext().getResources(), res11);
        } else {
            bitmap11 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.sou2);
        }
        bitmap11.compress(Bitmap.CompressFormat.PNG, 100, stream11);


        ByteArrayOutputStream stream21 = new ByteArrayOutputStream();
        Bitmap bitmap21;
        if (gigiKode21 != null) {
            kodeGigi = checkKodeGigi(gigiKode21);
            System.out.println("Kode gigi = " + kodeGigi);
            int res21 = RekamMedisActivity.this.getResources().getIdentifier(kodeGigi, "drawable", RekamMedisActivity.this.getPackageName());
            bitmap21 = BitmapFactory.decodeResource(getBaseContext().getResources(), res21);
        } else {
            bitmap21 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.sou2);
        }
        bitmap21.compress(Bitmap.CompressFormat.PNG, 100, stream21);

        ByteArrayOutputStream stream22 = new ByteArrayOutputStream();
        Bitmap bitmap22;
        if (gigiKode22 != null) {
            kodeGigi = checkKodeGigi(gigiKode22);
            System.out.println("Kode gigi = " + kodeGigi);
            int res22 = RekamMedisActivity.this.getResources().getIdentifier(kodeGigi, "drawable", RekamMedisActivity.this.getPackageName());
            bitmap22 = BitmapFactory.decodeResource(getBaseContext().getResources(), res22);
        } else {
            bitmap22 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.sou2);
        }
        bitmap22.compress(Bitmap.CompressFormat.PNG, 100, stream22);


        ByteArrayOutputStream stream23 = new ByteArrayOutputStream();
        Bitmap bitmap23;
        if (gigiKode23 != null) {
            kodeGigi = checkKodeGigi(gigiKode23);
            System.out.println("Kode gigi = " + kodeGigi);
            int res13 = RekamMedisActivity.this.getResources().getIdentifier(kodeGigi, "drawable", RekamMedisActivity.this.getPackageName());
            bitmap23 = BitmapFactory.decodeResource(getBaseContext().getResources(), res13);
        } else {
            bitmap23 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.sou2);
        }
        bitmap23.compress(Bitmap.CompressFormat.PNG, 100, stream23);

        ByteArrayOutputStream stream24 = new ByteArrayOutputStream();
        Bitmap bitmap24;
        if (gigiKode24 != null) {
            kodeGigi = checkKodeGigi(gigiKode24);
            System.out.println("Kode gigi = " + kodeGigi);
            int res12 = RekamMedisActivity.this.getResources().getIdentifier(kodeGigi, "drawable", RekamMedisActivity.this.getPackageName());
            bitmap24 = BitmapFactory.decodeResource(getBaseContext().getResources(), res12);
        } else {
            bitmap24 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.sou);
        }
        bitmap24.compress(Bitmap.CompressFormat.PNG, 100, stream24);


        ByteArrayOutputStream stream25 = new ByteArrayOutputStream();
        Bitmap bitmap25;
        if (gigiKode25 != null) {
            kodeGigi = checkKodeGigi(gigiKode25);
            System.out.println("Kode gigi = " + kodeGigi);
            int res11 = RekamMedisActivity.this.getResources().getIdentifier(kodeGigi, "drawable", RekamMedisActivity.this.getPackageName());
            bitmap25 = BitmapFactory.decodeResource(getBaseContext().getResources(), res11);
        } else {
            bitmap25 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.sou);
        }
        bitmap25.compress(Bitmap.CompressFormat.PNG, 100, stream25);


        ByteArrayOutputStream stream26 = new ByteArrayOutputStream();
        Bitmap bitmap26;
        if (gigiKode26 != null) {
            kodeGigi = checkKodeGigi(gigiKode26);
            System.out.println("Kode gigi = " + kodeGigi);
            int res13 = RekamMedisActivity.this.getResources().getIdentifier(kodeGigi, "drawable", RekamMedisActivity.this.getPackageName());
            bitmap26 = BitmapFactory.decodeResource(getBaseContext().getResources(), res13);
        } else {
            bitmap26 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.sou2);
        }
        bitmap26.compress(Bitmap.CompressFormat.PNG, 100, stream26);

        ByteArrayOutputStream stream27 = new ByteArrayOutputStream();
        Bitmap bitmap27;
        if (gigiKode27 != null) {
            kodeGigi = checkKodeGigi(gigiKode27);
            System.out.println("Kode gigi = " + kodeGigi);
            int res12 = RekamMedisActivity.this.getResources().getIdentifier(kodeGigi, "drawable", RekamMedisActivity.this.getPackageName());
            bitmap27 = BitmapFactory.decodeResource(getBaseContext().getResources(), res12);
        } else {
            bitmap27 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.sou);
        }
        bitmap27.compress(Bitmap.CompressFormat.PNG, 100, stream27);


        ByteArrayOutputStream stream28 = new ByteArrayOutputStream();
        Bitmap bitmap28;
        if (gigiKode28 != null) {
            kodeGigi = checkKodeGigi(gigiKode28);
            System.out.println("Kode gigi = " + kodeGigi);
            int res11 = RekamMedisActivity.this.getResources().getIdentifier(kodeGigi, "drawable", RekamMedisActivity.this.getPackageName());
            bitmap28 = BitmapFactory.decodeResource(getBaseContext().getResources(), res11);
        } else {
            bitmap28 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.sou);
        }
        bitmap28.compress(Bitmap.CompressFormat.PNG, 100, stream28);


        //STREAM GIGI TENGAH ATAS
        ByteArrayOutputStream stream55 = new ByteArrayOutputStream();
        Bitmap bitmap55;
        if (gigiKode55 != null) {
            kodeGigi = checkKodeGigi(gigiKode55);
            System.out.println("Kode gigi = " + kodeGigi);
            int res18 = RekamMedisActivity.this.getResources().getIdentifier(kodeGigi, "drawable", RekamMedisActivity.this.getPackageName());
            bitmap55 = BitmapFactory.decodeResource(getBaseContext().getResources(), res18);
        } else {
            bitmap55 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.sou);
        }
        bitmap55.compress(Bitmap.CompressFormat.PNG, 100, stream55);

        ByteArrayOutputStream stream54 = new ByteArrayOutputStream();
        Bitmap bitmap54;
        if (gigiKode54 != null) {
            kodeGigi = checkKodeGigi(gigiKode54);
            System.out.println("Kode gigi = " + kodeGigi);
            int res13 = RekamMedisActivity.this.getResources().getIdentifier(kodeGigi, "drawable", RekamMedisActivity.this.getPackageName());
            bitmap54 = BitmapFactory.decodeResource(getBaseContext().getResources(), res13);
        } else {
            bitmap54 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.sou);
        }
        bitmap54.compress(Bitmap.CompressFormat.PNG, 100, stream54);

        ByteArrayOutputStream stream53 = new ByteArrayOutputStream();
        Bitmap bitmap53;
        if (gigiKode53 != null) {
            kodeGigi = checkKodeGigi(gigiKode53);
            System.out.println("Kode gigi = " + kodeGigi);
            int res13 = RekamMedisActivity.this.getResources().getIdentifier(kodeGigi, "drawable", RekamMedisActivity.this.getPackageName());
            bitmap53 = BitmapFactory.decodeResource(getBaseContext().getResources(), res13);
        } else {
            bitmap53 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.sou2);
        }
        bitmap53.compress(Bitmap.CompressFormat.PNG, 100, stream53);

        ByteArrayOutputStream stream52 = new ByteArrayOutputStream();
        Bitmap bitmap52;
        if (gigiKode52 != null) {
            kodeGigi = checkKodeGigi(gigiKode52);
            System.out.println("Kode gigi = " + kodeGigi);
            int res13 = RekamMedisActivity.this.getResources().getIdentifier(kodeGigi, "drawable", RekamMedisActivity.this.getPackageName());
            bitmap52 = BitmapFactory.decodeResource(getBaseContext().getResources(), res13);
        } else {
            bitmap52 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.sou2);
        }
        bitmap52.compress(Bitmap.CompressFormat.PNG, 100, stream52);

        ByteArrayOutputStream stream51 = new ByteArrayOutputStream();
        Bitmap bitmap51;
        if (gigiKode51 != null) {
            kodeGigi = checkKodeGigi(gigiKode51);
            System.out.println("Kode gigi = " + kodeGigi);
            int res13 = RekamMedisActivity.this.getResources().getIdentifier(kodeGigi, "drawable", RekamMedisActivity.this.getPackageName());
            bitmap51 = BitmapFactory.decodeResource(getBaseContext().getResources(), res13);
        } else {
            bitmap51 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.sou2);
        }
        bitmap51.compress(Bitmap.CompressFormat.PNG, 100, stream51);

        ByteArrayOutputStream stream61 = new ByteArrayOutputStream();
        Bitmap bitmap61;
        if (gigiKode61 != null) {
            kodeGigi = checkKodeGigi(gigiKode61);
            System.out.println("Kode gigi = " + kodeGigi);
            int res13 = RekamMedisActivity.this.getResources().getIdentifier(kodeGigi, "drawable", RekamMedisActivity.this.getPackageName());
            bitmap61 = BitmapFactory.decodeResource(getBaseContext().getResources(), res13);
        } else {
            bitmap61 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.sou2);
        }
        bitmap61.compress(Bitmap.CompressFormat.PNG, 100, stream61);

        ByteArrayOutputStream stream62 = new ByteArrayOutputStream();
        Bitmap bitmap62;
        if (gigiKode62 != null) {
            kodeGigi = checkKodeGigi(gigiKode62);
            System.out.println("Kode gigi = " + kodeGigi);
            int res13 = RekamMedisActivity.this.getResources().getIdentifier(kodeGigi, "drawable", RekamMedisActivity.this.getPackageName());
            bitmap62 = BitmapFactory.decodeResource(getBaseContext().getResources(), res13);
        } else {
            bitmap62 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.sou2);
        }
        bitmap62.compress(Bitmap.CompressFormat.PNG, 100, stream62);

        ByteArrayOutputStream stream63 = new ByteArrayOutputStream();
        Bitmap bitmap63;
        if (gigiKode63 != null) {
            kodeGigi = checkKodeGigi(gigiKode63);
            System.out.println("Kode gigi = " + kodeGigi);
            int res13 = RekamMedisActivity.this.getResources().getIdentifier(kodeGigi, "drawable", RekamMedisActivity.this.getPackageName());
            bitmap63 = BitmapFactory.decodeResource(getBaseContext().getResources(), res13);
        } else {
            bitmap63 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.sou2);
        }
        bitmap63.compress(Bitmap.CompressFormat.PNG, 100, stream63);


        ByteArrayOutputStream stream64 = new ByteArrayOutputStream();
        Bitmap bitmap64;
        if (gigiKode64 != null) {
            kodeGigi = checkKodeGigi(gigiKode64);
            System.out.println("Kode gigi = " + kodeGigi);
            int res21 = RekamMedisActivity.this.getResources().getIdentifier(kodeGigi, "drawable", RekamMedisActivity.this.getPackageName());
            bitmap64 = BitmapFactory.decodeResource(getBaseContext().getResources(), res21);
        } else {
            bitmap64 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.sou);
        }
        bitmap64.compress(Bitmap.CompressFormat.PNG, 100, stream64);

        ByteArrayOutputStream stream65 = new ByteArrayOutputStream();
        Bitmap bitmap65;
        if (gigiKode65 != null) {
            kodeGigi = checkKodeGigi(gigiKode65);
            System.out.println("Kode gigi = " + kodeGigi);
            int res13 = RekamMedisActivity.this.getResources().getIdentifier(kodeGigi, "drawable", RekamMedisActivity.this.getPackageName());
            bitmap65 = BitmapFactory.decodeResource(getBaseContext().getResources(), res13);
        } else {
            bitmap65 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.sou);
        }
        bitmap65.compress(Bitmap.CompressFormat.PNG, 100, stream65);


        //STREAM GIGI TENGAH BAWAH
        ByteArrayOutputStream stream85 = new ByteArrayOutputStream();
        Bitmap bitmap85;
        if (gigiKode85 != null) {
            kodeGigi = checkKodeGigi(gigiKode85);
            System.out.println("Kode gigi = " + kodeGigi);
            int res13 = RekamMedisActivity.this.getResources().getIdentifier(kodeGigi, "drawable", RekamMedisActivity.this.getPackageName());
            bitmap85 = BitmapFactory.decodeResource(getBaseContext().getResources(), res13);
        } else {
            bitmap85 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.sou);
        }
        bitmap85.compress(Bitmap.CompressFormat.PNG, 100, stream85);

        ByteArrayOutputStream stream84 = new ByteArrayOutputStream();
        Bitmap bitmap84;
        if (gigiKode84 != null) {
            kodeGigi = checkKodeGigi(gigiKode84);
            System.out.println("Kode gigi = " + kodeGigi);
            int res12 = RekamMedisActivity.this.getResources().getIdentifier(kodeGigi, "drawable", RekamMedisActivity.this.getPackageName());
            bitmap84 = BitmapFactory.decodeResource(getBaseContext().getResources(), res12);
        } else {
            bitmap84 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.sou);
        }
        bitmap84.compress(Bitmap.CompressFormat.PNG, 100, stream84);


        ByteArrayOutputStream stream83 = new ByteArrayOutputStream();
        Bitmap bitmap83;
        if (gigiKode83 != null) {
            kodeGigi = checkKodeGigi(gigiKode83);
            System.out.println("Kode gigi = " + kodeGigi);
            int res11 = RekamMedisActivity.this.getResources().getIdentifier(kodeGigi, "drawable", RekamMedisActivity.this.getPackageName());
            bitmap83 = BitmapFactory.decodeResource(getBaseContext().getResources(), res11);
        } else {
            bitmap83 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.sou2);
        }
        bitmap83.compress(Bitmap.CompressFormat.PNG, 100, stream83);


        ByteArrayOutputStream stream82 = new ByteArrayOutputStream();
        Bitmap bitmap82;
        if (gigiKode82 != null) {
            kodeGigi = checkKodeGigi(gigiKode82);
            System.out.println("Kode gigi = " + kodeGigi);
            int res13 = RekamMedisActivity.this.getResources().getIdentifier(kodeGigi, "drawable", RekamMedisActivity.this.getPackageName());
            bitmap82 = BitmapFactory.decodeResource(getBaseContext().getResources(), res13);
        } else {
            bitmap82 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.sou2);
        }
        bitmap82.compress(Bitmap.CompressFormat.PNG, 100, stream82);

        ByteArrayOutputStream stream81 = new ByteArrayOutputStream();
        Bitmap bitmap81;
        if (gigiKode81 != null) {
            kodeGigi = checkKodeGigi(gigiKode81);
            System.out.println("Kode gigi = " + kodeGigi);
            int res12 = RekamMedisActivity.this.getResources().getIdentifier(kodeGigi, "drawable", RekamMedisActivity.this.getPackageName());
            bitmap81 = BitmapFactory.decodeResource(getBaseContext().getResources(), res12);
        } else {
            bitmap81 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.sou2);
        }
        bitmap81.compress(Bitmap.CompressFormat.PNG, 100, stream81);


        ByteArrayOutputStream stream71 = new ByteArrayOutputStream();
        Bitmap bitmap71;
        if (gigiKode71 != null) {
            kodeGigi = checkKodeGigi(gigiKode71);
            System.out.println("Kode gigi = " + kodeGigi);
            int res11 = RekamMedisActivity.this.getResources().getIdentifier(kodeGigi, "drawable", RekamMedisActivity.this.getPackageName());
            bitmap71 = BitmapFactory.decodeResource(getBaseContext().getResources(), res11);
        } else {
            bitmap71 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.sou2);
        }
        bitmap71.compress(Bitmap.CompressFormat.PNG, 100, stream71);


        ByteArrayOutputStream stream72 = new ByteArrayOutputStream();
        Bitmap bitmap72;
        if (gigiKode72 != null) {
            kodeGigi = checkKodeGigi(gigiKode72);
            System.out.println("Kode gigi = " + kodeGigi);
            int res12 = RekamMedisActivity.this.getResources().getIdentifier(kodeGigi, "drawable", RekamMedisActivity.this.getPackageName());
            bitmap72 = BitmapFactory.decodeResource(getBaseContext().getResources(), res12);
        } else {
            bitmap72 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.sou2);
        }
        bitmap72.compress(Bitmap.CompressFormat.PNG, 100, stream72);


        ByteArrayOutputStream stream73 = new ByteArrayOutputStream();
        Bitmap bitmap73;
        if (gigiKode73 != null) {
            kodeGigi = checkKodeGigi(gigiKode73);
            System.out.println("Kode gigi = " + kodeGigi);
            int res11 = RekamMedisActivity.this.getResources().getIdentifier(kodeGigi, "drawable", RekamMedisActivity.this.getPackageName());
            bitmap73 = BitmapFactory.decodeResource(getBaseContext().getResources(), res11);
        } else {
            bitmap73 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.sou2);
        }
        bitmap73.compress(Bitmap.CompressFormat.PNG, 100, stream73);


        ByteArrayOutputStream stream74 = new ByteArrayOutputStream();
        Bitmap bitmap74;
        if (gigiKode74 != null) {
            kodeGigi = checkKodeGigi(gigiKode74);
            System.out.println("Kode gigi = " + kodeGigi);
            int res13 = RekamMedisActivity.this.getResources().getIdentifier(kodeGigi, "drawable", RekamMedisActivity.this.getPackageName());
            bitmap74 = BitmapFactory.decodeResource(getBaseContext().getResources(), res13);
        } else {
            bitmap74 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.sou);
        }
        bitmap74.compress(Bitmap.CompressFormat.PNG, 100, stream74);

        ByteArrayOutputStream stream75 = new ByteArrayOutputStream();
        Bitmap bitmap75;
        if (gigiKode75 != null) {
            kodeGigi = checkKodeGigi(gigiKode75);
            System.out.println("Kode gigi = " + kodeGigi);
            int res12 = RekamMedisActivity.this.getResources().getIdentifier(kodeGigi, "drawable", RekamMedisActivity.this.getPackageName());
            bitmap75 = BitmapFactory.decodeResource(getBaseContext().getResources(), res12);
        } else {
            bitmap75 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.sou);
        }
        bitmap75.compress(Bitmap.CompressFormat.PNG, 100, stream75);


        //STREAM GIGI BAWAH
        ByteArrayOutputStream stream48 = new ByteArrayOutputStream();
        Bitmap bitmap48;
        if (gigiKode48 != null) {
            kodeGigi = checkKodeGigi(gigiKode48);
            System.out.println("Kode gigi = " + kodeGigi);
            int res18 = RekamMedisActivity.this.getResources().getIdentifier(kodeGigi, "drawable", RekamMedisActivity.this.getPackageName());
            bitmap48 = BitmapFactory.decodeResource(getBaseContext().getResources(), res18);
        } else {
            bitmap48 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.sou);
        }
        bitmap48.compress(Bitmap.CompressFormat.PNG, 100, stream48);

        ByteArrayOutputStream stream47 = new ByteArrayOutputStream();
        Bitmap bitmap47;
        if (gigiKode47 != null) {
            kodeGigi = checkKodeGigi(gigiKode47);
            System.out.println("Kode gigi = " + kodeGigi);
            int res13 = RekamMedisActivity.this.getResources().getIdentifier(kodeGigi, "drawable", RekamMedisActivity.this.getPackageName());
            bitmap47 = BitmapFactory.decodeResource(getBaseContext().getResources(), res13);
        } else {
            bitmap47 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.sou);
        }
        bitmap47.compress(Bitmap.CompressFormat.PNG, 100, stream47);

        ByteArrayOutputStream stream46 = new ByteArrayOutputStream();
        Bitmap bitmap46;
        if (gigiKode46 != null) {
            kodeGigi = checkKodeGigi(gigiKode46);
            System.out.println("Kode gigi = " + kodeGigi);
            int res13 = RekamMedisActivity.this.getResources().getIdentifier(kodeGigi, "drawable", RekamMedisActivity.this.getPackageName());
            bitmap46 = BitmapFactory.decodeResource(getBaseContext().getResources(), res13);
        } else {
            bitmap46 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.sou);
        }
        bitmap46.compress(Bitmap.CompressFormat.PNG, 100, stream46);

        ByteArrayOutputStream stream45 = new ByteArrayOutputStream();
        Bitmap bitmap45;
        if (gigiKode45 != null) {
            kodeGigi = checkKodeGigi(gigiKode45);
            System.out.println("Kode gigi = " + kodeGigi);
            int res13 = RekamMedisActivity.this.getResources().getIdentifier(kodeGigi, "drawable", RekamMedisActivity.this.getPackageName());
            bitmap45 = BitmapFactory.decodeResource(getBaseContext().getResources(), res13);
        } else {
            bitmap45 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.sou);
        }
        bitmap45.compress(Bitmap.CompressFormat.PNG, 100, stream45);

        ByteArrayOutputStream stream44 = new ByteArrayOutputStream();
        Bitmap bitmap44;
        if (gigiKode44 != null) {
            kodeGigi = checkKodeGigi(gigiKode44);
            System.out.println("Kode gigi = " + kodeGigi);
            int res13 = RekamMedisActivity.this.getResources().getIdentifier(kodeGigi, "drawable", RekamMedisActivity.this.getPackageName());
            bitmap44 = BitmapFactory.decodeResource(getBaseContext().getResources(), res13);
        } else {
            bitmap44 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.sou);
        }
        bitmap44.compress(Bitmap.CompressFormat.PNG, 100, stream44);

        ByteArrayOutputStream stream43 = new ByteArrayOutputStream();
        Bitmap bitmap43;
        if (gigiKode43 != null) {
            kodeGigi = checkKodeGigi(gigiKode43);
            System.out.println("Kode gigi = " + kodeGigi);
            int res13 = RekamMedisActivity.this.getResources().getIdentifier(kodeGigi, "drawable", RekamMedisActivity.this.getPackageName());
            bitmap43 = BitmapFactory.decodeResource(getBaseContext().getResources(), res13);
        } else {
            bitmap43 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.sou2);
        }
        bitmap43.compress(Bitmap.CompressFormat.PNG, 100, stream43);

        ByteArrayOutputStream stream42 = new ByteArrayOutputStream();
        Bitmap bitmap42;
        if (gigiKode42 != null) {
            kodeGigi = checkKodeGigi(gigiKode42);
            System.out.println("Kode gigi = " + kodeGigi);
            int res13 = RekamMedisActivity.this.getResources().getIdentifier(kodeGigi, "drawable", RekamMedisActivity.this.getPackageName());
            bitmap42 = BitmapFactory.decodeResource(getBaseContext().getResources(), res13);
        } else {
            bitmap42 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.sou2);
        }
        bitmap42.compress(Bitmap.CompressFormat.PNG, 100, stream42);

        ByteArrayOutputStream stream41 = new ByteArrayOutputStream();
        Bitmap bitmap41;
        if (gigiKode41 != null) {
            kodeGigi = checkKodeGigi(gigiKode41);
            System.out.println("Kode gigi = " + kodeGigi);
            int res13 = RekamMedisActivity.this.getResources().getIdentifier(kodeGigi, "drawable", RekamMedisActivity.this.getPackageName());
            bitmap41 = BitmapFactory.decodeResource(getBaseContext().getResources(), res13);
        } else {
            bitmap41 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.sou2);
        }
        bitmap41.compress(Bitmap.CompressFormat.PNG, 100, stream41);


        ByteArrayOutputStream stream31 = new ByteArrayOutputStream();
        Bitmap bitmap31;
        if (gigiKode31 != null) {
            kodeGigi = checkKodeGigi(gigiKode31);
            System.out.println("Kode gigi = " + kodeGigi);
            int res21 = RekamMedisActivity.this.getResources().getIdentifier(kodeGigi, "drawable", RekamMedisActivity.this.getPackageName());
            bitmap31 = BitmapFactory.decodeResource(getBaseContext().getResources(), res21);
        } else {
            bitmap31 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.sou2);
        }

        bitmap31.compress(Bitmap.CompressFormat.PNG, 100, stream31);

        ByteArrayOutputStream stream32 = new ByteArrayOutputStream();
        Bitmap bitmap32;
        if (gigiKode32 != null) {
            kodeGigi = checkKodeGigi(gigiKode32);
            System.out.println("Kode gigi = " + kodeGigi);
            int res13 = RekamMedisActivity.this.getResources().getIdentifier(kodeGigi, "drawable", RekamMedisActivity.this.getPackageName());
            bitmap32 = BitmapFactory.decodeResource(getBaseContext().getResources(), res13);
        } else {
            bitmap32 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.sou2);
        }
        bitmap32.compress(Bitmap.CompressFormat.PNG, 100, stream32);


        ByteArrayOutputStream stream33 = new ByteArrayOutputStream();
        Bitmap bitmap33;
        if (gigiKode33 != null) {
            kodeGigi = checkKodeGigi(gigiKode33);
            System.out.println("Kode gigi = " + kodeGigi);
            int res13 = RekamMedisActivity.this.getResources().getIdentifier(kodeGigi, "drawable", RekamMedisActivity.this.getPackageName());
            bitmap33 = BitmapFactory.decodeResource(getBaseContext().getResources(), res13);
        } else {
            bitmap33 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.sou2);
        }
        bitmap33.compress(Bitmap.CompressFormat.PNG, 100, stream33);

        ByteArrayOutputStream stream34 = new ByteArrayOutputStream();
        Bitmap bitmap34;
        if (gigiKode34 != null) {
            kodeGigi = checkKodeGigi(gigiKode34);
            System.out.println("Kode gigi = " + kodeGigi);
            int res12 = RekamMedisActivity.this.getResources().getIdentifier(kodeGigi, "drawable", RekamMedisActivity.this.getPackageName());
            bitmap34 = BitmapFactory.decodeResource(getBaseContext().getResources(), res12);
        } else {
            bitmap34 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.sou);
        }
        bitmap34.compress(Bitmap.CompressFormat.PNG, 100, stream34);


        ByteArrayOutputStream stream35 = new ByteArrayOutputStream();
        Bitmap bitmap35;
        if (gigiKode35 != null) {
            kodeGigi = checkKodeGigi(gigiKode35);
            System.out.println("Kode gigi = " + kodeGigi);
            int res11 = RekamMedisActivity.this.getResources().getIdentifier(kodeGigi, "drawable", RekamMedisActivity.this.getPackageName());
            bitmap35 = BitmapFactory.decodeResource(getBaseContext().getResources(), res11);
        } else {
            bitmap35 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.sou);
        }
        bitmap35.compress(Bitmap.CompressFormat.PNG, 100, stream35);


        ByteArrayOutputStream stream36 = new ByteArrayOutputStream();
        Bitmap bitmap36;
        if (gigiKode36 != null) {
            kodeGigi = checkKodeGigi(gigiKode36);
            System.out.println("Kode gigi = " + kodeGigi);
            int res13 = RekamMedisActivity.this.getResources().getIdentifier(kodeGigi, "drawable", RekamMedisActivity.this.getPackageName());
            bitmap36 = BitmapFactory.decodeResource(getBaseContext().getResources(), res13);
        } else {
            bitmap36 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.sou);
        }
        bitmap36.compress(Bitmap.CompressFormat.PNG, 100, stream36);

        ByteArrayOutputStream stream37 = new ByteArrayOutputStream();
        Bitmap bitmap37;
        if (gigiKode37 != null) {
            kodeGigi = checkKodeGigi(gigiKode37);
            System.out.println("Kode gigi = " + kodeGigi);
            int res12 = RekamMedisActivity.this.getResources().getIdentifier(kodeGigi, "drawable", RekamMedisActivity.this.getPackageName());
            bitmap37 = BitmapFactory.decodeResource(getBaseContext().getResources(), res12);
        } else {
            bitmap37 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.sou);
        }
        bitmap37.compress(Bitmap.CompressFormat.PNG, 100, stream37);


        ByteArrayOutputStream stream38 = new ByteArrayOutputStream();
        Bitmap bitmap38;
        if (gigiKode38 != null) {
            kodeGigi = checkKodeGigi(gigiKode38);
            System.out.println("Kode gigi = " + kodeGigi);
            int res11 = RekamMedisActivity.this.getResources().getIdentifier(kodeGigi, "drawable", RekamMedisActivity.this.getPackageName());
            bitmap38 = BitmapFactory.decodeResource(getBaseContext().getResources(), res11);
        } else {
            bitmap38 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.sou);
        }
        bitmap38.compress(Bitmap.CompressFormat.PNG, 100, stream38);


        ByteArrayOutputStream pertama = new ByteArrayOutputStream();
        Bitmap bitmapPertama = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.pertama1);
        bitmapPertama.compress(Bitmap.CompressFormat.PNG, 100, pertama);

        ByteArrayOutputStream kedua = new ByteArrayOutputStream();
        Bitmap bitmapKedua = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.kedua1);
        bitmapKedua.compress(Bitmap.CompressFormat.PNG, 100, kedua);


        ByteArrayOutputStream mid = new ByteArrayOutputStream();
        Bitmap bitmapMid = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.mid);
        bitmapMid.compress(Bitmap.CompressFormat.PNG, 100, mid);

        ByteArrayOutputStream ketiga = new ByteArrayOutputStream();
        Bitmap bitmapKetiga = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.ketiga1);
        bitmapKetiga.compress(Bitmap.CompressFormat.PNG, 100, ketiga);

        ByteArrayOutputStream keempat = new ByteArrayOutputStream();
        Bitmap bitmapKeempat = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.keempat1);
        bitmapKeempat.compress(Bitmap.CompressFormat.PNG, 100, keempat);

        Image iPertama, iKedua, iMid, iKetiga, iKeempat;
        iPertama = Image.getInstance(pertama.toByteArray());
        iPertama.scaleToFit(340, 340);
        iKedua = Image.getInstance(kedua.toByteArray());
        iKedua.scaleToFit(210, 210);
        iMid = Image.getInstance(mid.toByteArray());
        iMid.scaleToFit(30, 30);
        iKetiga = Image.getInstance(ketiga.toByteArray());
        iKetiga.scaleToFit(210, 210);
        iKeempat = Image.getInstance(keempat.toByteArray());
        iKeempat.scaleToFit(340, 340);


        gigi18 = Image.getInstance(stream18.toByteArray());
        gigi17 = Image.getInstance(stream17.toByteArray());
        gigi16 = Image.getInstance(stream16.toByteArray());
        gigi15 = Image.getInstance(stream15.toByteArray());
        gigi14 = Image.getInstance(stream14.toByteArray());
        gigi28 = Image.getInstance(stream28.toByteArray());
        gigi27 = Image.getInstance(stream27.toByteArray());
        gigi26 = Image.getInstance(stream26.toByteArray());
        gigi25 = Image.getInstance(stream25.toByteArray());
        gigi24 = Image.getInstance(stream24.toByteArray());
        gigi55 = Image.getInstance(stream55.toByteArray());
        gigi54 = Image.getInstance(stream54.toByteArray());
        gigi64 = Image.getInstance(stream64.toByteArray());
        gigi65 = Image.getInstance(stream65.toByteArray());
        gigi48 = Image.getInstance(stream48.toByteArray());
        gigi47 = Image.getInstance(stream47.toByteArray());
        gigi46 = Image.getInstance(stream46.toByteArray());
        gigi45 = Image.getInstance(stream45.toByteArray());
        gigi44 = Image.getInstance(stream44.toByteArray());
        gigi34 = Image.getInstance(stream34.toByteArray());
        gigi35 = Image.getInstance(stream35.toByteArray());
        gigi36 = Image.getInstance(stream36.toByteArray());
        gigi37 = Image.getInstance(stream37.toByteArray());
        gigi38 = Image.getInstance(stream38.toByteArray());
        gigi75 = Image.getInstance(stream75.toByteArray());
        gigi74 = Image.getInstance(stream74.toByteArray());
        gigi85 = Image.getInstance(stream85.toByteArray());
        gigi84 = Image.getInstance(stream84.toByteArray());


        gigi18.setAlignment(Image.LEFT);
        gigi18.scaleToFit(40, 40);
        gigi17.setAlignment(Image.LEFT);
        gigi17.scaleToFit(40, 40);
        gigi16.setAlignment(Image.LEFT);
        gigi16.scaleToFit(40, 40);
        gigi15.setAlignment(Image.LEFT);
        gigi15.scaleToFit(40, 40);
        gigi14.setAlignment(Image.LEFT);
        gigi14.scaleToFit(40, 40);
        gigi28.setAlignment(Image.LEFT);
        gigi28.scaleToFit(40, 40);
        gigi27.setAlignment(Image.LEFT);
        gigi27.scaleToFit(40, 40);
        gigi26.setAlignment(Image.LEFT);
        gigi26.scaleToFit(40, 40);
        gigi25.setAlignment(Image.LEFT);
        gigi25.scaleToFit(40, 40);
        gigi24.setAlignment(Image.LEFT);
        gigi24.scaleToFit(40, 40);
        gigi55.setAlignment(Image.LEFT);
        gigi55.scaleToFit(40, 40);
        gigi54.setAlignment(Image.LEFT);
        gigi54.scaleToFit(40, 40);
        gigi64.setAlignment(Image.LEFT);
        gigi64.scaleToFit(40, 40);
        gigi65.setAlignment(Image.LEFT);
        gigi65.scaleToFit(40, 40);
        gigi48.setAlignment(Image.LEFT);
        gigi48.scaleToFit(40, 40);
        gigi47.setAlignment(Image.LEFT);
        gigi47.scaleToFit(40, 40);
        gigi46.setAlignment(Image.LEFT);
        gigi46.scaleToFit(40, 40);
        gigi45.setAlignment(Image.LEFT);
        gigi45.scaleToFit(40, 40);
        gigi44.setAlignment(Image.LEFT);
        gigi44.scaleToFit(40, 40);
        gigi34.setAlignment(Image.LEFT);
        gigi34.scaleToFit(40, 40);
        gigi35.setAlignment(Image.LEFT);
        gigi35.scaleToFit(40, 40);
        gigi36.setAlignment(Image.LEFT);
        gigi36.scaleToFit(40, 40);
        gigi37.setAlignment(Image.LEFT);
        gigi37.scaleToFit(40, 40);
        gigi38.setAlignment(Image.LEFT);
        gigi38.scaleToFit(40, 40);
        gigi75.setAlignment(Image.LEFT);
        gigi75.scaleToFit(40, 40);
        gigi74.setAlignment(Image.LEFT);
        gigi74.scaleToFit(40, 40);
        gigi85.setAlignment(Image.LEFT);
        gigi85.scaleToFit(40, 40);
        gigi84.setAlignment(Image.LEFT);
        gigi84.scaleToFit(40, 40);


        Image gigi13, gigi12, gigi11, gigi21, gigi22, gigi23, gigi53, gigi52, gigi51, gigi61, gigi62, gigi63, gigi83, gigi82, gigi81, gigi71, gigi72, gigi73, gigi43, gigi42, gigi41, gigi31, gigi32, gigi33;

        gigi13 = Image.getInstance(stream13.toByteArray());
        gigi13.setAlignment(Image.LEFT);
        gigi13.scaleToFit(40, 40);
        gigi12 = Image.getInstance(stream12.toByteArray());
        gigi12.setAlignment(Image.LEFT);
        gigi12.scaleToFit(40, 40);
        gigi11 = Image.getInstance(stream11.toByteArray());
        gigi11.setAlignment(Image.LEFT);
        gigi11.scaleToFit(40, 40);
        gigi21 = Image.getInstance(stream21.toByteArray());
        gigi21.setAlignment(Image.LEFT);
        gigi21.scaleToFit(40, 40);
        gigi22 = Image.getInstance(stream22.toByteArray());
        gigi22.setAlignment(Image.LEFT);
        gigi22.scaleToFit(40, 40);
        gigi23 = Image.getInstance(stream23.toByteArray());
        gigi23.setAlignment(Image.LEFT);
        gigi23.scaleToFit(40, 40);
        gigi53 = Image.getInstance(stream53.toByteArray());
        gigi53.setAlignment(Image.LEFT);
        gigi53.scaleToFit(40, 40);
        gigi52 = Image.getInstance(stream52.toByteArray());
        gigi52.setAlignment(Image.LEFT);
        gigi52.scaleToFit(40, 40);
        gigi51 = Image.getInstance(stream51.toByteArray());
        gigi51.setAlignment(Image.LEFT);
        gigi51.scaleToFit(40, 40);
        gigi61 = Image.getInstance(stream61.toByteArray());
        gigi61.setAlignment(Image.LEFT);
        gigi61.scaleToFit(40, 40);
        gigi62 = Image.getInstance(stream62.toByteArray());
        gigi62.setAlignment(Image.LEFT);
        gigi62.scaleToFit(40, 40);

        gigi63 = Image.getInstance(stream63.toByteArray());
        gigi63.setAlignment(Image.LEFT);
        gigi63.scaleToFit(40, 40);
        gigi83 = Image.getInstance(stream83.toByteArray());
        gigi83.setAlignment(Image.LEFT);
        gigi83.scaleToFit(40, 40);
        gigi82 = Image.getInstance(stream82.toByteArray());
        gigi82.setAlignment(Image.LEFT);
        gigi82.scaleToFit(40, 40);
        gigi81 = Image.getInstance(stream81.toByteArray());
        gigi81.setAlignment(Image.LEFT);
        gigi81.scaleToFit(40, 40);
        gigi71 = Image.getInstance(stream71.toByteArray());
        gigi71.setAlignment(Image.LEFT);
        gigi71.scaleToFit(40, 40);
        gigi72 = Image.getInstance(stream72.toByteArray());
        gigi72.setAlignment(Image.LEFT);
        gigi72.scaleToFit(40, 40);
        gigi73 = Image.getInstance(stream73.toByteArray());
        gigi73.setAlignment(Image.LEFT);
        gigi73.scaleToFit(40, 40);
        gigi43 = Image.getInstance(stream43.toByteArray());
        gigi43.setAlignment(Image.LEFT);
        gigi43.scaleToFit(40, 40);
        gigi42 = Image.getInstance(stream42.toByteArray());
        gigi42.setAlignment(Image.LEFT);
        gigi42.scaleToFit(40, 40);
        gigi41 = Image.getInstance(stream41.toByteArray());
        gigi41.setAlignment(Image.LEFT);
        gigi41.scaleToFit(40, 40);
        gigi31 = Image.getInstance(stream31.toByteArray());
        gigi31.setAlignment(Image.LEFT);
        gigi31.scaleToFit(40, 40);
        gigi32 = Image.getInstance(stream32.toByteArray());
        gigi32.setAlignment(Image.LEFT);
        gigi32.scaleToFit(40, 40);
        gigi33 = Image.getInstance(stream33.toByteArray());
        gigi33.setAlignment(Image.LEFT);
        gigi33.scaleToFit(40, 40);


        //ODONTOGRAM
        PdfPTable tablePertama = new PdfPTable(1);
        tablePertama.setWidthPercentage(100);
        Paragraph pPertama = new Paragraph();
        PdfPCell cellPertama = new PdfPCell();
        cellPertama.setBorder(Rectangle.NO_BORDER);
        pPertama.add(new Chunk(iPertama, 0, 0, true));
        pPertama.setAlignment(Element.ALIGN_CENTER);
        cellPertama.setPadding(0);
        cellPertama.addElement(pPertama);
        tablePertama.addCell(cellPertama);


        PdfPTable tableKedua = new PdfPTable(1);
        tableKedua.setWidthPercentage(100);
        Paragraph pKedua = new Paragraph();
        PdfPCell cellKedua = new PdfPCell();
        cellKedua.setPadding(0);
        cellKedua.setBorder(Rectangle.NO_BORDER);
        pKedua.add(new Chunk(iKedua, 0, 0, true));
        pKedua.setAlignment(Element.ALIGN_CENTER);

        cellKedua.addElement(pKedua);
        tableKedua.addCell(cellKedua);

        PdfPTable tableMid = new PdfPTable(1);
        tableMid.setWidthPercentage(100);
        Paragraph pMid = new Paragraph();
        PdfPCell cellMid = new PdfPCell();
        cellMid.setBorder(Rectangle.NO_BORDER);
        cellMid.setPadding(0);
        pMid.add(new Chunk(iMid, 0, 0, true));
        pMid.setAlignment(Element.ALIGN_CENTER);
        cellMid.addElement(pMid);
        tableMid.addCell(cellMid);


        PdfPTable tableKetiga = new PdfPTable(1);
        tableKetiga.setWidthPercentage(100);
        Paragraph pKetiga = new Paragraph();
        PdfPCell cellKetiga = new PdfPCell();
        cellKetiga.setBorder(Rectangle.NO_BORDER);
        cellKetiga.setPadding(0);
        pKetiga.add(new Chunk(iKetiga, 0, 0, true));
        pKetiga.setAlignment(Element.ALIGN_CENTER);
        cellKetiga.addElement(pKetiga);
        tableKetiga.addCell(cellKetiga);


        PdfPTable tableKeempat = new PdfPTable(1);
        tableKeempat.setWidthPercentage(100);
        Paragraph pKeempat = new Paragraph();
        PdfPCell cellKeempat = new PdfPCell();
        cellKeempat.setBorder(Rectangle.NO_BORDER);
        cellKeempat.setPadding(0);
        pKeempat.add(new Chunk(iKeempat, 0, 0, true));
        pKeempat.setAlignment(Element.ALIGN_CENTER);
        cellKeempat.addElement(pKeempat);
        cellKeempat.setVerticalAlignment(Element.ALIGN_TOP);
        tableKeempat.addCell(cellKeempat);


        //Gigi atas
        PdfPTable tableAtas = new PdfPTable(1);
        tableAtas.setWidthPercentage(100);
        Paragraph p = new Paragraph();
        PdfPCell cellAtas = new PdfPCell();
        cellAtas.setBorder(Rectangle.NO_BORDER);
        cellAtas.setPadding(0);

        //sou
        p.add(new Chunk(gigi18, 0, 0, true));
        p.add(new Chunk(gigi17, 0, 0, true));
        p.add(new Chunk(gigi16, 0, 0, true));
        p.add(new Chunk(gigi15, 0, 0, true));
        p.add(new Chunk(gigi14, 0, 0, true));
        //sou2
        p.add(new Chunk(gigi13, 0, 0, true));
        p.add(new Chunk(gigi12, 0, 0, true));
        p.add(new Chunk(gigi11, 0, 0, true));
        p.add(new Chunk(gigi21, 0, 0, true));
        p.add(new Chunk(gigi22, 0, 0, true));
        p.add(new Chunk(gigi23, 0, 0, true));
        //sou
        p.add(new Chunk(gigi24, 0, 0, true));
        p.add(new Chunk(gigi25, 0, 0, true));
        p.add(new Chunk(gigi26, 0, 0, true));
        p.add(new Chunk(gigi27, 0, 0, true));
        p.add(new Chunk(gigi28, 0, 0, true));
        p.setAlignment(Paragraph.ALIGN_CENTER);
        cellAtas.addElement(p);
        tableAtas.addCell(cellAtas);

        //Gigi tengah
        PdfPTable tableTengahAtas = new PdfPTable(1);
        tableTengahAtas.setWidthPercentage(100);
        Paragraph p2 = new Paragraph();
        PdfPCell cellTengahAtas = new PdfPCell();
        cellTengahAtas.setBorder(Rectangle.NO_BORDER);
        cellTengahAtas.setPadding(0);
        //sou
        p2.add(new Chunk(gigi55, 0, 0, true));
        p2.add(new Chunk(gigi54, 0, 0, true));
        //sou2
        p2.add(new Chunk(gigi53, 0, 0, true));
        p2.add(new Chunk(gigi52, 0, 0, true));
        p2.add(new Chunk(gigi51, 0, 0, true));
        p2.add(new Chunk(gigi61, 0, 0, true));
        p2.add(new Chunk(gigi62, 0, 0, true));
        p2.add(new Chunk(gigi63, 0, 0, true));
        //sou
        p2.add(new Chunk(gigi64, 0, 0, true));
        p2.add(new Chunk(gigi65, 0, 0, true));
        p2.setAlignment(Paragraph.ALIGN_CENTER);
        cellTengahAtas.addElement(p2);
        tableTengahAtas.addCell(cellTengahAtas);


        //Gigi tengah
        PdfPTable tableTengahBawah = new PdfPTable(1);
        tableTengahBawah.setWidthPercentage(100);
        Paragraph p3 = new Paragraph();
        PdfPCell cellTengahBawah = new PdfPCell();
        cellTengahBawah.setBorder(Rectangle.NO_BORDER);
        cellTengahBawah.setPadding(0);
        //sou
        p3.add(new Chunk(gigi85, 0, 0, true));
        p3.add(new Chunk(gigi84, 0, 0, true));
        //sou2
        p3.add(new Chunk(gigi83, 0, 0, true));
        p3.add(new Chunk(gigi82, 0, 0, true));
        p3.add(new Chunk(gigi81, 0, 0, true));
        p3.add(new Chunk(gigi71, 0, 0, true));
        p3.add(new Chunk(gigi72, 0, 0, true));
        p3.add(new Chunk(gigi73, 0, 0, true));
        //sou
        p3.add(new Chunk(gigi74, 0, 0, true));
        p3.add(new Chunk(gigi75, 0, 0, true));
        p3.setAlignment(Paragraph.ALIGN_CENTER);
        cellTengahBawah.addElement(p3);
        tableTengahBawah.addCell(cellTengahBawah);


        //Gigi Bawah
        PdfPTable tableBawah = new PdfPTable(1);
        tableBawah.setWidthPercentage(100);
        Paragraph p4 = new Paragraph();
        PdfPCell cellBawah = new PdfPCell();
        cellBawah.setBorder(Rectangle.NO_BORDER);
        cellBawah.setPadding(0);

        //sou
        p4.add(new Chunk(gigi48, 0, 0, true));
        p4.add(new Chunk(gigi47, 0, 0, true));
        p4.add(new Chunk(gigi46, 0, 0, true));
        p4.add(new Chunk(gigi45, 0, 0, true));
        p4.add(new Chunk(gigi44, 0, 0, true));
        //sou2
        p4.add(new Chunk(gigi43, 0, 0, true));
        p4.add(new Chunk(gigi42, 0, 0, true));
        p4.add(new Chunk(gigi41, 0, 0, true));
        p4.add(new Chunk(gigi31, 0, 0, true));
        p4.add(new Chunk(gigi32, 0, 0, true));
        p4.add(new Chunk(gigi33, 0, 0, true));
        //sou
        p4.add(new Chunk(gigi34, 0, 0, true));
        p4.add(new Chunk(gigi35, 0, 0, true));
        p4.add(new Chunk(gigi36, 0, 0, true));
        p4.add(new Chunk(gigi37, 0, 0, true));
        p4.add(new Chunk(gigi38, 0, 0, true));
        p4.setAlignment(Paragraph.ALIGN_CENTER);
        cellBawah.addElement(p4);

        tableBawah.addCell(cellBawah);

        document.add(tablePertama);
        document.add(tableAtas);
        document.add(tableKedua);
        document.add(tableTengahAtas);
        document.add(tableMid);
        document.add(tableTengahBawah);
        document.add(tableKetiga);
        document.add(tableBawah);
        document.add(tableKeempat);


        PdfPTable sou48 = new PdfPTable(4);
        sou48.setWidthPercentage(100);
        sou48.setSpacingBefore(10);
        sou48.setWidths(new float[]{1, 2, 2, 1});
        sou48.addCell(getCellBorder("48", PdfPCell.ALIGN_CENTER));
        if (gigiKode48 == null) {
            gigiKode48 = "sou";
        }
        sou48.addCell(getCellBorder(gigiKode48, PdfPCell.ALIGN_LEFT));
        if (gigiKode38 == null) {
            gigiKode38 = "sou";
        }
        sou48.addCell(getCellBorder(gigiKode38, PdfPCell.ALIGN_LEFT));
        sou48.addCell(getCellBorder("38", PdfPCell.ALIGN_CENTER));
        document.add(sou48);


        PdfPTable sou47 = new PdfPTable(4);
        sou47.setWidthPercentage(100);
        sou47.setWidths(new float[]{1, 2, 2, 1});
        sou47.addCell(getCellBorder("47", PdfPCell.ALIGN_CENTER));
        if (gigiKode47 == null) {
            gigiKode47 = "sou";
        }
        sou47.addCell(getCellBorder(gigiKode47, PdfPCell.ALIGN_LEFT));
        if (gigiKode37 == null) {
            gigiKode37 = "sou";
        }
        sou47.addCell(getCellBorder(gigiKode37, PdfPCell.ALIGN_LEFT));
        sou47.addCell(getCellBorder("37", PdfPCell.ALIGN_CENTER));
        document.add(sou47);

        PdfPTable sou46 = new PdfPTable(4);
        sou46.setWidthPercentage(100);
        sou46.setWidths(new float[]{1, 2, 2, 1});
        sou46.addCell(getCellBorder("46", PdfPCell.ALIGN_CENTER));
        if (gigiKode46 == null) {
            gigiKode46 = "sou";
        }
        sou46.addCell(getCellBorder(gigiKode46, PdfPCell.ALIGN_LEFT));
        if (gigiKode36 == null) {
            gigiKode36 = "sou";
        }
        sou46.addCell(getCellBorder(gigiKode36, PdfPCell.ALIGN_LEFT));
        sou46.addCell(getCellBorder("36", PdfPCell.ALIGN_CENTER));
        document.add(sou46);


        PdfPTable sou4585 = new PdfPTable(4);
        sou4585.setWidthPercentage(100);
        sou4585.setWidths(new float[]{1, 2, 2, 1});
        sou4585.addCell(getCellBorder("45 [85]", PdfPCell.ALIGN_CENTER));
        String kode45 = cekKodeTabel("45", "85", gigiKode45, gigiKode85, "sou");
        sou4585.addCell(getCellBorder(kode45, PdfPCell.ALIGN_LEFT));
        String kode35 = cekKodeTabel("35", "75", gigiKode35, gigiKode75, "sou");
        sou4585.addCell(getCellBorder(kode35, PdfPCell.ALIGN_LEFT));
        sou4585.addCell(getCellBorder("[75] 35", PdfPCell.ALIGN_CENTER));
        document.add(sou4585);

        PdfPTable sou4484 = new PdfPTable(4);
        sou4484.setWidthPercentage(100);
        sou4484.setWidths(new float[]{1, 2, 2, 1});
        sou4484.addCell(getCellBorder("44 [84]", PdfPCell.ALIGN_CENTER));
        String kode44 = cekKodeTabel("44", "84", gigiKode44, gigiKode84, "sou");
        sou4484.addCell(getCellBorder(kode44, PdfPCell.ALIGN_LEFT));
        String kode34 = cekKodeTabel("34", "74", gigiKode34, gigiKode74, "sou");
        sou4484.addCell(getCellBorder(kode34, PdfPCell.ALIGN_LEFT));
        sou4484.addCell(getCellBorder("[74] 34", PdfPCell.ALIGN_CENTER));
        document.add(sou4484);

        PdfPTable sou4383 = new PdfPTable(4);
        sou4383.setWidthPercentage(100);
        sou4383.setWidths(new float[]{1, 2, 2, 1});
        sou4383.addCell(getCellBorder("43 [83]", PdfPCell.ALIGN_CENTER));
        String kode43 = cekKodeTabel("43", "83", gigiKode43, gigiKode83, "sou2");
        sou4383.addCell(getCellBorder(kode43, PdfPCell.ALIGN_LEFT));
        String kode33 = cekKodeTabel("33", "73", gigiKode33, gigiKode73, "sou2");
        sou4383.addCell(getCellBorder(kode33, PdfPCell.ALIGN_LEFT));
        sou4383.addCell(getCellBorder("[73] 33", PdfPCell.ALIGN_CENTER));
        document.add(sou4383);


        PdfPTable sou4282 = new PdfPTable(4);
        sou4282.setWidthPercentage(100);
        sou4282.setWidths(new float[]{1, 2, 2, 1});
        sou4282.addCell(getCellBorder("42 [82]", PdfPCell.ALIGN_CENTER));
        String kode42 = cekKodeTabel("42", "82", gigiKode42, gigiKode82, "sou2");
        sou4282.addCell(getCellBorder(kode42, PdfPCell.ALIGN_LEFT));
        String kode32 = cekKodeTabel("32", "72", gigiKode32, gigiKode72, "sou2");
        sou4282.addCell(getCellBorder(kode32, PdfPCell.ALIGN_LEFT));
        sou4282.addCell(getCellBorder("[72] 32", PdfPCell.ALIGN_CENTER));
        document.add(sou4282);

        PdfPTable sou4151 = new PdfPTable(4);
        sou4151.setWidthPercentage(100);
        sou4151.setWidths(new float[]{1, 2, 2, 1});
        sou4151.addCell(getCellBorder("41 [81]", PdfPCell.ALIGN_CENTER));
        String kode41 = cekKodeTabel("41", "81", gigiKode41, gigiKode81, "sou2");
        sou4151.addCell(getCellBorder(kode41, PdfPCell.ALIGN_LEFT));
        String kode31 = cekKodeTabel("31", "71", gigiKode31, gigiKode71, "sou2");
        sou4151.addCell(getCellBorder(kode31, PdfPCell.ALIGN_LEFT));
        sou4151.addCell(getCellBorder("[71] 31", PdfPCell.ALIGN_CENTER));
        sou4151.setSpacingAfter(5);
        document.add(sou4151);


        //Occlusi
        final PdfPTable occlusi = new PdfPTable(4);
        occlusi.setWidthPercentage(100);
        occlusi.setWidths(new float[]{2, 1, 2, 2});
        occlusi.addCell(getCell("Occlusi", PdfPCell.ALIGN_LEFT));
        occlusi.addCell(getCell(":", PdfPCell.ALIGN_LEFT));
        occlusi.addCell(getCell(occlusiData, PdfPCell.ALIGN_LEFT));
        occlusi.addCell(getCell("", PdfPCell.ALIGN_LEFT));
        occlusi.setSpacingAfter(2);
        document.add(occlusi);


        //Torus Palatinus
        final PdfPTable palatinus = new PdfPTable(4);
        palatinus.setWidthPercentage(100);
        palatinus.setWidths(new float[]{2, 1, 2, 2});
        palatinus.addCell(getCell("Torus Palatinus", PdfPCell.ALIGN_LEFT));
        palatinus.addCell(getCell(":", PdfPCell.ALIGN_LEFT));
        palatinus.addCell(getCell(palatinusData, PdfPCell.ALIGN_LEFT));
        palatinus.addCell(getCell("", PdfPCell.ALIGN_LEFT));
        palatinus.setSpacingAfter(2);
        document.add(palatinus);


        //Torus Mandibularis
        final PdfPTable mandibularis = new PdfPTable(4);
        mandibularis.setWidthPercentage(100);
        mandibularis.setWidths(new float[]{2, 1, 2, 2});
        mandibularis.addCell(getCell("Torus Mandibularis", PdfPCell.ALIGN_LEFT));
        mandibularis.addCell(getCell(":", PdfPCell.ALIGN_LEFT));
        mandibularis.addCell(getCell(mandibularisData, PdfPCell.ALIGN_LEFT));
        mandibularis.addCell(getCell("", PdfPCell.ALIGN_LEFT));
        mandibularis.setSpacingAfter(2);
        document.add(mandibularis);

        //Palatum
        final PdfPTable palatum = new PdfPTable(4);
        palatum.setWidthPercentage(100);
        palatum.setWidths(new float[]{2, 1, 2, 2});
        palatum.addCell(getCell("Palatum", PdfPCell.ALIGN_LEFT));
        palatum.addCell(getCell(":", PdfPCell.ALIGN_LEFT));
        palatum.addCell(getCell(palatumData, PdfPCell.ALIGN_LEFT));
        palatum.addCell(getCell("", PdfPCell.ALIGN_LEFT));
        palatum.setSpacingAfter(2);
        document.add(palatum);

        //Diastema
        final PdfPTable diastema = new PdfPTable(4);
        diastema.setWidthPercentage(100);
        diastema.setWidths(new float[]{2, 1, 2, 2});
        diastema.addCell(getCell("Diastema", PdfPCell.ALIGN_LEFT));
        diastema.addCell(getCell(":", PdfPCell.ALIGN_LEFT));
        diastema.addCell(getCell(diastemaData, PdfPCell.ALIGN_LEFT));
        diastema.addCell(getCell(ketDiastemaData, PdfPCell.ALIGN_LEFT));
        diastema.setSpacingAfter(2);
        document.add(diastema);


        //Anomali
        final PdfPTable anomali = new PdfPTable(4);
        anomali.setWidthPercentage(100);
        anomali.setWidths(new float[]{2, 1, 2, 2});
        anomali.addCell(getCell("Gigi Anomali", PdfPCell.ALIGN_LEFT));
        anomali.addCell(getCell(":", PdfPCell.ALIGN_LEFT));
        anomali.addCell(getCell(anomaliData, PdfPCell.ALIGN_LEFT));
        anomali.addCell(getCell(ketAnomaliData, PdfPCell.ALIGN_LEFT));
        anomali.setSpacingAfter(2);
        document.add(anomali);


        //Lainnya
        final PdfPTable lainnya = new PdfPTable(4);
        lainnya.setWidthPercentage(100);
        lainnya.setWidths(new float[]{2, 1, 2, 2});
        lainnya.addCell(getCell("Lain-lain", PdfPCell.ALIGN_LEFT));
        lainnya.addCell(getCell(":", PdfPCell.ALIGN_LEFT));
        if (lainnyaData == null) {
            lainnyaData = "Tidak Ada";
        }
        lainnya.addCell(getCell(lainnyaData, PdfPCell.ALIGN_LEFT));
        lainnya.addCell(getCell("", PdfPCell.ALIGN_LEFT));
//        lainnya.setSpacingAfter(2);
        document.add(lainnya);


        document.newPage();
        Paragraph detail = new Paragraph("DETAIL PERAWATAN", judul);
        detail.setAlignment(Paragraph.ALIGN_CENTER);
        detail.setSpacingAfter(15);
        document.add(detail);


        final PdfPTable pdfPTableDetail = new PdfPTable(5);
        pdfPTableDetail.setWidthPercentage(100);
        pdfPTableDetail.setWidths(new float[]{2, 5,1,2,5});
        pdfPTableDetail.addCell(getCell("Nama Pasien", PdfPCell.ALIGN_LEFT,0));
        pdfPTableDetail.addCell(getCell(": "+namaPasien, PdfPCell.ALIGN_LEFT,1));
        pdfPTableDetail.addCell(getCell("", PdfPCell.ALIGN_LEFT,0));
        pdfPTableDetail.addCell(getCell("Jenis Kelamin", PdfPCell.ALIGN_LEFT,0));
        pdfPTableDetail.addCell(getCell(": "+jenKelamin, PdfPCell.ALIGN_LEFT,1));
        pdfPTableDetail.setSpacingAfter(8);
        document.add(pdfPTableDetail);




        final PdfPTable pdfPTable2Detail = new PdfPTable(5);
        pdfPTable2Detail.setWidthPercentage(100);
        pdfPTable2Detail.setWidths(new float[]{2, 5,1,2,5});
        pdfPTable2Detail.addCell(getCell("NIK/No.KTP", PdfPCell.ALIGN_LEFT,0));
        pdfPTable2Detail.addCell(getCell(": "+noIdentitas, PdfPCell.ALIGN_LEFT,1));
        pdfPTable2Detail.addCell(getCell("", PdfPCell.ALIGN_LEFT,0));
        pdfPTable2Detail.addCell(getCell("TTL", PdfPCell.ALIGN_LEFT,0));
        pdfPTable2Detail.addCell(getCell(": "+tempatLahir + ", " + tanggalLahir, PdfPCell.ALIGN_LEFT,1));
        pdfPTable2Detail.setSpacingAfter(8);
        document.add(pdfPTable2Detail);



        for (int i = 11; i <= 85; i++) {

            ArrayList<DetailPerawatan> arrayList = util.getDetail(RekamMedisActivity.this, "detail" + i);


            System.out.println("array list = " + arrayList);
            if (arrayList != null && arrayList.size() > 0) {
                Paragraph detail18 = new Paragraph("Riwayat Perawatan Gigi " + i, text);
                detail18.setAlignment(Paragraph.ALIGN_LEFT);
                detail18.setSpacingAfter(5);
                document.add(detail18);

                final PdfPTable gigi18Detail = new PdfPTable(5);
                gigi18Detail.setWidthPercentage(100);
                gigi18Detail.setWidths(new float[]{2, 2, 2, 1, 2});

                gigi18Detail.addCell(getCellBorder("Nama Tindakan", PdfPCell.ALIGN_CENTER));
                gigi18Detail.addCell(getCellBorder("Tanggal", PdfPCell.ALIGN_CENTER));
                gigi18Detail.addCell(getCellBorder("Tindakan", PdfPCell.ALIGN_CENTER));
                gigi18Detail.addCell(getCellBorder("Status", PdfPCell.ALIGN_CENTER));
                gigi18Detail.addCell(getCellBorder("Dokter", PdfPCell.ALIGN_CENTER));
//                gigi18Detail.addCell(getCellBorder("TTD", PdfPCell.ALIGN_CENTER));
                counter = counter+1;
                System.out.println("TESS = " + arrayList.get(0).getStatus());
                for (int pp = 0; pp <= arrayList.size() - 1; pp++) {
                    ambil = arrayList.get(pp);
                    gigi18Detail.addCell(getCellBorder(ambil.getNamaTindakan(), PdfPCell.ALIGN_CENTER));
                    gigi18Detail.addCell(getCellBorder(ambil.getTanggal(), PdfPCell.ALIGN_CENTER));
                    String tindakanList = TextUtils.join(", ",ambil.getListTindakan());
                    gigi18Detail.addCell(getCellBorder(tindakanList, PdfPCell.ALIGN_CENTER));
                    gigi18Detail.addCell(getCellBorder(ambil.getStatus(), PdfPCell.ALIGN_CENTER));
                    gigi18Detail.addCell(getCellBorder("drg." + ambil.getNamaDokter(), PdfPCell.ALIGN_CENTER));
//                    Image ttdDokter = Image.getInstance(new URL(ambil.getTtdDokter()));
//                    ttdDokter.scaleToFit(10, 10);
////                    ttdDokter.scalePercent(135f);
//                    PdfPCell cellTTDDokter = new PdfPCell(ttdDokter, true);
//                    gigi18Detail.addCell(cellTTDDokter);
                }
                gigi18Detail.setSpacingAfter(15);
                document.add(gigi18Detail);


            }

        }

        if (counter==0)
        {
            Paragraph textDetail = new Paragraph("TIDAK ADA DETAIL PERAWATAN", judul);
            textDetail.setAlignment(Paragraph.ALIGN_CENTER);
            textDetail.setSpacingAfter(50);
            textDetail.setSpacingBefore(50);
            document.add(textDetail);

        }



        //Step 5: Close the document
        document.close();

        System.out.println("PDF OK");
        Toast.makeText(RekamMedisActivity.this, "Laporan Berhasil Dibuat", Toast.LENGTH_SHORT).show();


        viewPdf(myFile);

    }

    public String checkKodeGigi(String kodeGigi) {
        String kode = null;
        if (kodeGigi.equalsIgnoreCase("meb1") || kodeGigi.equalsIgnoreCase("meb2") || kodeGigi.equalsIgnoreCase("meb3")) {
            kode = "meb";
        } else if (kodeGigi.equalsIgnoreCase("pob1") || kodeGigi.equalsIgnoreCase("pob2") || kodeGigi.equalsIgnoreCase("pob3") || kodeGigi.equalsIgnoreCase("pob4")) {
            kode = "pob";
        } else {
            kode = kodeGigi;
        }

        return kode;
    }

    public static PdfPCell getCell(String text, int aligment) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setPadding(0);
        cell.setHorizontalAlignment(aligment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }


    public PdfPCell getCellBorder(String text, int aligment) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setHorizontalAlignment(aligment);
        return cell;
    }

    private void viewPdf(File myFile) {
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setDataAndType(Uri.fromFile(myFile), "application/pdf");
//        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//        startActivity(intent);
        Intent intent = new Intent(RekamMedisActivity.this, PdfViewerActivity.class);
        intent.putExtra("file", myFile);
        startActivity(intent);

    }

    private void emailNote(File myFile) {
        Intent email = new Intent(Intent.ACTION_SEND);
        Uri uri = Uri.parse(myFile.getAbsolutePath());
        email.putExtra(Intent.EXTRA_STREAM, uri);
        email.setType("message/rfc822");
        startActivity(email);
    }


    private void fn_permission() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {

            if ((ActivityCompat.shouldShowRequestPermissionRationale(RekamMedisActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE))) {
            } else {
                ActivityCompat.requestPermissions(RekamMedisActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);

            }

            if ((ActivityCompat.shouldShowRequestPermissionRationale(RekamMedisActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE))) {
            } else {
                ActivityCompat.requestPermissions(RekamMedisActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);

            }
        } else {
            boolean_permission = true;


        }
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_rekam_medis, menu);
        MenuItem invoice = menu.findItem(R.id.done);
        if (jadwalKey == null) {
            invoice.setVisible(false);
        }

        if (statusUser.equalsIgnoreCase("Administrator") || status.equalsIgnoreCase("profil")) {
            invoice.setVisible(false);
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {


        if (requestCode == REQUEST_PERMISSIONS) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                boolean_permission = true;


            } else {
                Toast.makeText(getApplicationContext(), "Please allow the permission", Toast.LENGTH_LONG).show();

            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.pdf) {

            AsyncTaskRunner asyncTaskRunner = new AsyncTaskRunner(this);
            asyncTaskRunner.execute();

            System.out.println("idPasien = " + idPasien);


        } else if (id == R.id.today) {

            final AlertDialog.Builder alBuilder = new AlertDialog.Builder(RekamMedisActivity.this);
            LayoutInflater inflater = LayoutInflater.from(RekamMedisActivity.this);
            final View dialog =  inflater.inflate(R.layout.activity_list_gigi, null);
            final ListView lv =  dialog.findViewById(R.id.ListItem);
            final TextView tvStatus =  dialog.findViewById(R.id.tvStatus);
            final TextView title =  dialog.findViewById(R.id.title);
            final TextView tvAll =  dialog.findViewById(R.id.tvAll);
            final TextView tvToday =  dialog.findViewById(R.id.tvToday);
            lv.setTextFilterEnabled(true);
            lv.setEmptyView(tvStatus);
            final EditText etSearch =  dialog.findViewById(R.id.etSearch);

            int monthCurrent = month+1;
            today = day + "-" + monthCurrent + "-" + year;

            System.out.println("Filter On Start Today = " + today);
            final Long timeStampStart = convertTimeStamp(today,"00:00");
            final Long timeStampEnd = convertTimeStamp(today,"23:59");
            System.out.println("timeStamp start = "+ timeStampStart);
            System.out.println("timeStamp end = "+ timeStampEnd);


            title.setText("Riwayat Perawatan");
            title.setGravity(Gravity.LEFT);

            tvAll.setVisibility(View.VISIBLE);
            tvToday.setVisibility(View.VISIBLE);


            riwayatPerawatan(mPerawatan.orderByChild("idPasien").equalTo(idPasien),lv,etSearch,tvStatus);
            tvAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    etSearch.getText().clear();
                    riwayatPerawatan(mPerawatan.orderByChild("idPasien").equalTo(idPasien),lv,etSearch,tvStatus);
                    Toast.makeText(RekamMedisActivity.this,"Semua Perawatan",Toast.LENGTH_SHORT).show();
                }
            });

            tvToday.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    etSearch.getText().clear();
                    riwayatPerawatan(mPerawatan.orderByChild("jadwalKey").equalTo(jadwalKey),lv,etSearch,tvStatus);
                    Toast.makeText(RekamMedisActivity.this,"Perawatan Hari ini",Toast.LENGTH_SHORT).show();
                }
            });




            alBuilder.setView(dialog);
            alBuilder.create();

            final AlertDialog alertDialog1 = alBuilder.show();
            alertDialog1.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    listPerawatan = listPerawatanToday.get(i);
                    if (statusUser.equalsIgnoreCase("Pasien")||statusUser.equalsIgnoreCase("Administrator")||status.equalsIgnoreCase("profil")) {
                        dialogPerawatan(listPerawatan.getNoGigi());
                    }
                    else
                    {

                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        ListPerawatan mPerawatan = new ListPerawatan();
                        Bundle bundle = new Bundle();
                        bundle.putString("nogigi", listPerawatan.getNoGigi());
                        bundle.putString("idPasien", idPasien);
                        mPerawatan.setArguments(bundle);
                        fragmentManager.popBackStack();
                        transaction.replace(R.id.frame_layout_left, mPerawatan);
                        transaction.addToBackStack(null);
                        transaction.commit();
                        alertDialog1.dismiss();
                    }




                }
            });

        } else if (id == R.id.done) {
            startActivity(new Intent(RekamMedisActivity.this, BillingActivity.class));
        } else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    public void riwayatPerawatan(Query query, final ListView lv, final EditText etSearch, final TextView tvStatus)
    {
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                listPerawatanToday.clear();
                System.out.println("listFormulir = " + listPerawatanToday);
                for (DataSnapshot perawatan : dataSnapshot.getChildren()) {
                    final Perawatan dataFormulir = perawatan.getValue(Perawatan.class);
                    dataFormulir.idPerawatan = perawatan.getKey();
                    listPerawatanToday.add(dataFormulir);
                    System.out.println("data klinik 1 = " + listPerawatanToday);

                }

                Collections.sort(listPerawatanToday,Perawatan.COMPARE_BY_TGL);
                final CustomAdapterPerawatanToday arrayAdapter = new CustomAdapterPerawatanToday(RekamMedisActivity.this, R.layout.row_list_today, listPerawatanToday);
                lv.setFastScrollEnabled(true);
                lv.setEmptyView(tvStatus);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                lv.setAdapter(arrayAdapter);
                arrayAdapter.notifyDataSetChanged();

                etSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                        arrayAdapter.getFilter().filter(s.toString());
                        arrayAdapter.keyWord = s.toString();


                        System.out.println("arrayAdapter.keyWord = " + arrayAdapter.keyWord);
                        System.out.println("ADAPTER textchanged= " + arrayAdapter.getCount());

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void dialogPerawatan(final String noGigi) {
        final AlertDialog.Builder alBuilder = new AlertDialog.Builder(RekamMedisActivity.this);
        LayoutInflater inflater = LayoutInflater.from(RekamMedisActivity.this);
        final View dialog = (View) inflater.inflate(R.layout.activity_list_perawatan, null);
        final RecyclerView mRecyclerview = (RecyclerView) dialog.findViewById(R.id.recycleViewPerawatan);
        final ProgressBar progressBar = (ProgressBar) dialog.findViewById(R.id.progressBar);
        final TextView tvNoGigi = (TextView) dialog.findViewById(R.id.tvNoGigi);
        final TextView tvStatusData = (TextView) dialog.findViewById(R.id.tvStatusData);
        tvNoGigi.setText(noGigi);
        mRecyclerview.setHasFixedSize(true);
        mManager = new LinearLayoutManager(RekamMedisActivity.this);
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecyclerview.setLayoutManager(mManager);
        mRekamMedis = mRoot.child("rekammedis").child(idPasien).child(noGigi);
        mRekamMedis.keepSynced(true);
        mAdapterPerawatan = new FirebaseRecyclerAdapter<Perawatan, MainViewHistoryTindakan>(
                Perawatan.class, R.layout.row_list_history, MainViewHistoryTindakan.class, mRekamMedis) {
            @Override
            protected void populateViewHolder(MainViewHistoryTindakan viewHolder, Perawatan model, int position) {
                System.out.println("POSITION = " + position);
                final DatabaseReference perawatanRef = getRef(position);
                perawatanKey = perawatanRef.getKey();
                System.out.println("PerawatanKey = " + perawatanKey);
                viewHolder.bindToPost(perawatanKey, RekamMedisActivity.this, noGigi);
                progressBar.setVisibility(View.GONE);

            }
        };

        if (mRecyclerview.getChildCount() > 0) {
            tvStatusData.setVisibility(View.GONE);

        } else {
            tvStatusData.setVisibility(View.VISIBLE);

        }
        progressBar.setVisibility(View.GONE);
        mAdapterPerawatan.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                mRecyclerview.smoothScrollToPosition(mAdapterPerawatan.getItemCount());
                System.out.println("mAdapterDalem = " + mAdapterPerawatan.getItemCount());
                System.out.println("itemCount = " + itemCount);
                System.out.println("positionStart = " + positionStart);
                if (mAdapterPerawatan.getItemCount() > 0) {
                    tvStatusData.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        mRecyclerview.setAdapter(mAdapterPerawatan);

        alBuilder.setView(dialog);
        alBuilder.create();
        final AlertDialog alertDialog = alBuilder.show();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }



    public Long convertTimeStamp(String tanggal, String waktu)
    {
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Date date2= null;
        try {
            date2 = formatter.parse(tanggal+" "+waktu);
        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("Log = "+ e);
        }
        Long timeStamp = date2.getTime();
        return timeStamp;
    }

    public void setDetail(final String noGigi, final String detailGigi) {

        final ArrayList<DetailPerawatan> idPerawatanList = new ArrayList<DetailPerawatan>();
        final ArrayList<String> listTindakan = new ArrayList<>();
        System.out.println("idPasien setDetail = " + idPasien);
        mRekamMedis.child(idPasien).child(noGigi).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                idPerawatanList.clear();
                System.out.println("idPerawatanList 1 = " + idPerawatanList);
                for (DataSnapshot idSnapshot : dataSnapshot.getChildren()) {
                    final String idPerawatan = idSnapshot.child("idPerawatan").getValue(String.class);
                    final String status = idSnapshot.child("status").getValue(String.class);
                    System.out.println("idPerawatan = " + idPerawatan);
                    mPerawatan.child(idPerawatan).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final String namaTindakan = dataSnapshot.child("namaTindakan").getValue(String.class);
                            final String jadwalKey = dataSnapshot.child("jadwalKey").getValue(String.class);
                            final String keterangan = dataSnapshot.child("keterangan").getValue(String.class);

                            mJadwal.child(jadwalKey).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Long timeStamp = dataSnapshot.child("timeStamp").getValue(Long.class);
                                    final String tanggal = getDate(timeStamp);
                                    String idDokter = dataSnapshot.child("idDokter").getValue(String.class);
                                    mDokter.child(idDokter).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            final String ttdUrl = dataSnapshot.child("ttdUrl").getValue(String.class);
                                            final String namaDokter = dataSnapshot.child("nama").getValue(String.class);

                                            mTindakanPerawatan.child(jadwalKey).orderByChild("id_perawatan").equalTo(idPerawatan).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    System.out.println("datasnapshot SetDetail = "+dataSnapshot);
                                                    for (DataSnapshot tindakanPerawatan : dataSnapshot.getChildren()) {
                                                        listTindakan.add(tindakanPerawatan.child("tindakan").getValue(String.class));

                                                    }

                                                    DetailPerawatan detailPerawatan = new DetailPerawatan(namaTindakan, tanggal, keterangan, status, namaDokter, ttdUrl, listTindakan);
                                                    idPerawatanList.add(detailPerawatan);
                                                    System.out.println("idPerawatanList 2 = " + idPerawatanList);
                                                    System.out.println("noGigi = " + noGigi);
                                                    util.setDetail(RekamMedisActivity.this, idPerawatanList, detailGigi);
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });


                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }


    }

    private String getDate(long timeStamp) {

        try {

            java.text.SimpleDateFormat dd = new java.text.SimpleDateFormat("dd");
            java.text.SimpleDateFormat MM = new java.text.SimpleDateFormat("MM");
            java.text.SimpleDateFormat yyyy = new java.text.SimpleDateFormat("yyyy");
            Date netDate = (new Date(timeStamp));
            String day = dd.format(netDate);
            String month = MM.format(netDate);
            String nameMonth = MONTHS[Integer.parseInt(month) - 1];
            String year = yyyy.format(netDate);
            String tanggal = day + " " + nameMonth + " " + year;
            return tanggal;
        } catch (Exception ex) {
            System.out.println("Log = " + ex);
            return "xx";
        }
    }

    public String cekKodeTabel(String noGigi1, String noGigi2, String kodeGigi1, String kodeGigi2, String gigiDefault) {
        String finalkode = "sou";
        if (!kodeGigi1.equalsIgnoreCase(gigiDefault) && kodeGigi2.equalsIgnoreCase(gigiDefault)) {
            finalkode = noGigi1 + " : " + kodeGigi1 + " ; " + noGigi2 + " : " + gigiDefault;
        } else if (kodeGigi1.equalsIgnoreCase(gigiDefault) && !kodeGigi2.equalsIgnoreCase(gigiDefault)) {
            finalkode = noGigi1 + " : " + gigiDefault + " ; " + noGigi2 + " : " + kodeGigi2;
        } else if (!kodeGigi1.equalsIgnoreCase(gigiDefault) && !kodeGigi2.equalsIgnoreCase(gigiDefault)) {
            finalkode = noGigi1 + " : " + kodeGigi1 + " ; " + noGigi2 + " : " + kodeGigi2;
        }
        return finalkode;
    }

    public static PdfPCell getCell(String text, int aligment,int flags) throws IOException, DocumentException {

        Font bold;

        if (flags==0)
        {
            bold = FontFactory.getFont(FontFactory.TIMES, 10,Font.BOLD);
        }
        else
        {
            bold = FontFactory.getFont(FontFactory.TIMES, 10);
        }

        PdfPCell cell = new PdfPCell(new Phrase(text, bold));
        cell.setPadding(0);
        cell.setHorizontalAlignment(aligment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }
}
