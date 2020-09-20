package com.teddybrothers.co_teddy.dentist;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.teddybrothers.co_teddy.dentist.customadapter.CustomAdapterFormulir;
import com.teddybrothers.co_teddy.dentist.entity.Formulir;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class FormulirActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseDatabase databaseUtama, databaseKlinik;
    DatabaseReference mUserRef, mRoot, mFormulir, mUser,mPasien,mDokter;
    Formulir listDataFormulir;
    public String statusUser, status;
    ProgressDialog progressDialogPdf;
    Utilities util = new Utilities();

    final ArrayList<Formulir> listFormulir = new ArrayList<Formulir>();
    ListView lv;
    ImageView ivCancel;
    TextView tvStatus;
    EditText etSearch;
    String idPasien,idDokter;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulir);

        System.out.println("ONCREATE Formulir ACTIVITY");

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            System.out.println("MASUK YEYEY");
        }

        statusUser = util.getStatus(FormulirActivity.this);

        if (databaseUtama == null) {
            databaseUtama = FirebaseDatabase.getInstance();
        }

        mRoot = databaseUtama.getReference();
        mFormulir = mRoot.child("formulir");
        mPasien = mRoot.child("pasien");
        mDokter = mRoot.child("dokter");
        mUser = mRoot.child("users");

        listFormulir.clear();
        etSearch = findViewById(R.id.etSearch);
        ivCancel = findViewById(R.id.ivCancel);
        lv = findViewById(R.id.ListItem);
        tvStatus = findViewById(R.id.tvStatus);
        lv.setTextFilterEnabled(true);
        progressBar = findViewById(R.id.progressBar);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FormulirActivity.this, FormActivity.class));
            }
        });


        if (statusUser.equalsIgnoreCase("Administrator")||statusUser.equalsIgnoreCase("Pasien"))
        {
            fab.setVisibility(View.GONE);
        }



        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println("listFormulir = " + listFormulir);
                listDataFormulir = listFormulir.get(i);

                progressDialogPdf = new ProgressDialog(FormulirActivity.this);
                progressDialogPdf.setMessage("Loading...");
                progressDialogPdf.show();
                System.out.println("status = " + status);

                mPasien.child(listDataFormulir.getIdPasien()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String nama = dataSnapshot.child("nama").getValue(String.class);
                        String jenKel = dataSnapshot.child("jenisKelamin").getValue(String.class);
                        final String alamat = dataSnapshot.child("alamat").getValue(String.class);
                        final String umurPasien = dataSnapshot.child("umur").getValue(String.class);
                        if (jenKel.equalsIgnoreCase("0")) {
                            jenKel = "Laki-laki";
                        } else if (jenKel.equalsIgnoreCase("1")) {
                            jenKel = "Perempuan";
                        }

                        final String finalJenKel = jenKel;
                        mDokter.child(listDataFormulir.getIdDokter()).addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String namaDokter = dataSnapshot.child("nama").getValue(String.class);
                                String ttdDokter = dataSnapshot.child("ttdUrl").getValue(String.class);

                                try {
                                    createPdf(nama, umurPasien, finalJenKel, alamat, listDataFormulir.status, listDataFormulir.getTindakan(), listDataFormulir.terhadap, listDataFormulir.namaLain,
                                            listDataFormulir.umurLain, listDataFormulir.jenKelLain, listDataFormulir.alamatLain, listDataFormulir.tglForm, namaDokter, listDataFormulir.ttdPasien, ttdDokter);
                                } catch (DocumentException e) {
                                    e.printStackTrace();
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


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
        });


    }

    @Override
    protected void onStart() {

        System.out.println("ONSTART Formulir ACTIVITY");
        etSearch.setText("");

        tvStatus.setVisibility(View.GONE);
        Query query = null;
        if (statusUser.equalsIgnoreCase("Pasien"))
        {
            idPasien = util.getIdPasien(FormulirActivity.this);
            query = mFormulir.orderByChild("idPasien").equalTo(idPasien);
        }
        else if (statusUser.equalsIgnoreCase("Dokter"))
        {
            idDokter = util.getIdDokter(FormulirActivity.this);
            query = mFormulir.orderByChild("idDokter").equalTo(idDokter);
        }
        else if (statusUser.equalsIgnoreCase("Administrator"))
        {
            query = mFormulir;
        }

        dataFormulir(Formulir.COMPARE_BY_NAME,query);


        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etSearch.getText().clear();
                ivCancel.setVisibility(View.GONE);
            }
        });

        super.onStart();

    }


    public void dataFormulir(final Comparator<Formulir> sortirParamater,Query query)
    {

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getSupportActionBar().setSubtitle(dataSnapshot.getChildrenCount() + " Formulir");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listFormulir.clear();
                System.out.println("listFormulir = " + listFormulir);
                for (DataSnapshot Formulir : dataSnapshot.getChildren()) {
                    final Formulir dataFormulir = Formulir.getValue(Formulir.class);
                    dataFormulir.idFormulir = Formulir.getKey();

                    listFormulir.add(dataFormulir);
                    System.out.println("data klinik 1 = " + listFormulir);

                }


                Collections.sort(listFormulir,sortirParamater);
                final CustomAdapterFormulir arrayAdapter = new CustomAdapterFormulir(FormulirActivity.this, R.layout.card_view_form, listFormulir);
                lv.setFastScrollEnabled(true);
                lv.setFastScrollAlwaysVisible(true);
                lv.setEmptyView(tvStatus);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                lv.setAdapter(arrayAdapter);
                progressBar.setVisibility(View.GONE);
                arrayAdapter.notifyDataSetChanged();

                etSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int i, int i1, int count) {
                        arrayAdapter.getFilter().filter(s.toString());
                        arrayAdapter.keyWord = s.toString();

                        if (s.length()>0) {
                            ivCancel.setVisibility(View.VISIBLE);
                        } else if (s.length()==0) {
                            ivCancel.setVisibility(View.GONE);
                        }

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

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Glide.get(this).clearMemory();
    }

    @Override
    protected void onPause() {
        System.out.println("onPause");
        listFormulir.clear();
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

       if (id==android.R.id.home)
        {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void viewPdf(File myFile) {

        Intent intent = new Intent(FormulirActivity.this, PdfViewerActivity.class);
        intent.putExtra("file", myFile);
        intent.putExtra("title", "Formulir");
        startActivity(intent);


    }

    class myFooter extends PdfPageEventHelper {
        Font ffont = new Font(Font.FontFamily.UNDEFINED, 5, Font.ITALIC);
        Font judul = FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, Font.BOLD);


        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            Date date = new Date();
            String dateCreated = new SimpleDateFormat("dd" + "/" + "MM" + "/" + "yyyy").format(date);
            Phrase footer = new Phrase("Tanggal Cetak : " + dateCreated, ffont);


            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, footer, (document.right() - document.left()) / 2 + document.leftMargin(),
                    document.bottom() - 10, 0);
        }
    }




    private void createPdf(String namaPasien, String umur, String jenKel, String pasienAlamat, String status, String tindakan, String terhadap, String namaLain, String umurLain,
                           String jenKelLain, String alamatLain, String tanggal, String namaDokter, String ttdPasien, String ttdDokter) throws DocumentException, MalformedURLException, IOException {


        File pdfFolder = new File("/sdcard", "pdfDentistFormulir");

        if (!pdfFolder.exists()) {
            pdfFolder.mkdir();

        }

        System.out.println("namaPasien method = " + namaPasien);
        BillingActivity.TableHeader.HeaderTable event = new BillingActivity.TableHeader.HeaderTable();

        //Create time stamp
        Date date = new Date();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);
        String dateCreated = new SimpleDateFormat("dd" + "/" + "MM" + "/" + "yyyy").format(date);
        File myFile = new File("/sdcard/pdfDentistFormulir/" + namaPasien + ".pdf");
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




        Font text = FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, Font.BOLD);
        //Step 4 Add conten
        Font judul = FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, Font.BOLD);
        Paragraph content = new Paragraph(status + " TINDAKAN KEDOKTERAN", judul);
        content.setAlignment(Paragraph.ALIGN_CENTER);
        content.setSpacingAfter(10);
        document.add(content);
        Paragraph paragraph11 = null;
        String object = null;
        if (terhadap.equalsIgnoreCase("0")) {
            object = "Saya";
        } else {
            object = terhadap + " Saya";
        }
        //Step 4 Add conten

        Paragraph paragraph1 = new Paragraph("Yang bertandatangan di bawah ini, saya, nama " + namaPasien + " , umur " + umur + " Tahun, " + jenKel + ", alamat " +
                pasienAlamat + " ,dengan ini menyatakan " + status + " untuk dilakukannya tindakan " + tindakan + " terhadap " + object, text);
        paragraph1.setAlignment(Paragraph.ALIGN_LEFT);
        paragraph1.setSpacingAfter(10);
        document.add(paragraph1);


        if (terhadap.equalsIgnoreCase("0"))
        {

        }else
        {
            String jenisKelaminLain = null;
            if (jenKelLain.equalsIgnoreCase("0")) {
                jenisKelaminLain = "Laki-laki";
            } else if (jenKelLain.equalsIgnoreCase("1")) {
                jenisKelaminLain = "Perempuan";
            }

            paragraph11 = new Paragraph("bernama " + namaLain + " , umur " + umurLain + " Tahun, " + jenisKelaminLain + ", alamat " +
                    alamatLain ,text);
            paragraph11.setAlignment(Paragraph.ALIGN_LEFT);
            paragraph11.setSpacingAfter(10);
            document.add(paragraph11);
        }


        //Step 4 Add conten

        Paragraph paragraph2 = new Paragraph("Saya memahami perlunya dan manfaat tindakan tersebut sebagaimana telah dijelaskan seperti diatas kepada saya, termasuk risiko dan komplikasi yang mungkin timbul. " +
                "Saya juga menyadari bahwa oleh karena ilmu kedokteran bukanlah ilmu pasti, maka keberhasilan tindakan kedokteran bukanlah keniscayaan, melainkan sangat bergantung kepada izin Tuhan Yang Maha Esa. ", text);
        paragraph2.setAlignment(Paragraph.ALIGN_LEFT);
        paragraph2.setSpacingAfter(20);
        document.add(paragraph2);

//        final PdfPTable pdf = new PdfPTable(2);
//        Paragraph paragraph3 = new Paragraph("Palembang, " + tanggal, text);
//        paragraph3.setAlignment(Paragraph.ALIGN_RIGHT);
//        paragraph3.setSpacingAfter(10);
//        document.add(paragraph3);

        final PdfPTable pdfPTable2 = new PdfPTable(2);
        pdfPTable2.setWidthPercentage(100);
        pdfPTable2.setWidths(new float[]{1, 1});
        pdfPTable2.addCell(getCell("", PdfPCell.ALIGN_CENTER));
        pdfPTable2.addCell(getCell("Palembang, " + tanggal, PdfPCell.ALIGN_CENTER));
        pdfPTable2.setSpacingAfter(5);
        document.add(pdfPTable2);


        final PdfPTable pdfPTable = new PdfPTable(2);
        pdfPTable.setWidthPercentage(100);
        pdfPTable.setWidths(new float[]{1, 1});
        pdfPTable.addCell(getCell("Yang menyatakan", PdfPCell.ALIGN_CENTER));
        pdfPTable.addCell(getCell("Dokter Gigi", PdfPCell.ALIGN_CENTER));
        pdfPTable.setSpacingAfter(5);
        document.add(pdfPTable);


        Image ttdpasien = Image.getInstance(new URL(ttdPasien));
        ttdpasien.scalePercent(350f);

        Image ttdokter = Image.getInstance(new URL(ttdDokter));
        ttdokter.scalePercent(350f);

        final PdfPTable ttd = new PdfPTable(2);
        ttd.setWidthPercentage(100);
        ttd.setWidths(new float[]{1, 1});
        PdfPCell cellTTDPasien = new PdfPCell(ttdpasien, true);
        cellTTDPasien.setBorder(0);
        ttd.addCell(cellTTDPasien);

        PdfPCell cellTTDDokter = new PdfPCell(ttdokter, true);
        cellTTDDokter.setBorder(0);
        ttd.addCell(cellTTDDokter);

        document.add(ttd);

        System.out.println("ttdPasien = " + ttdPasien);


        final PdfPTable namaTTD = new PdfPTable(2);
        namaTTD.setWidthPercentage(100);
        namaTTD.setWidths(new float[]{1, 1});
        namaTTD.addCell(getCell(namaPasien, PdfPCell.ALIGN_CENTER));
        namaTTD.addCell(getCell("drg. " + namaDokter, PdfPCell.ALIGN_CENTER));
        namaTTD.setSpacingAfter(5);
        document.add(namaTTD);

        document.close();
        progressDialogPdf.dismiss();

        System.out.println("PDF OK");
        viewPdf(myFile);

    }

    public static PdfPCell getCell(String text, int aligment) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setPadding(0);
        cell.setHorizontalAlignment(aligment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }


}


