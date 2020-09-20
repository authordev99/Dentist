package com.teddybrothers.co_teddy.dentist;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.teddybrothers.co_teddy.dentist.customadapter.CustomAdapterBilling;
import com.teddybrothers.co_teddy.dentist.entity.Invoice;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class HistoryBillingActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    FirebaseAuth mAuth;
    FirebaseDatabase databaseUtama, databaseKlinik;
    DatabaseReference mUserRef, mRoot, mInvoice, mUser, mDokter, mPasien, mJadwal;
    Invoice listDataInvoice;
    public String statusUser, status;
    ProgressDialog progressDialog;
    public static final String[] MONTHS = {"Januari", "Febuari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
    public Calendar cal1 = Calendar.getInstance();
    Long timeStampEnd, timeStampStart;
    Invoice ambil;
    Boolean flagsDate = false;
    TextView tvDariTanggal, tvKeTanggal;

    String selectedDateFrom, selectedDateTo;
    int dayFrom, monthFrom, yearFrom, dayTo, monthTo, yearTo;

    public Calendar cal = Calendar.getInstance();
    public int day = cal.get(Calendar.DAY_OF_MONTH);
    public int month = cal.get(Calendar.MONTH);
    public int year = cal.get(Calendar.YEAR);


    final ArrayList<Invoice> listInvoice = new ArrayList<Invoice>();
    ListView lv;
    ImageView ivCancel;
    TextView tvStatus;
    EditText etSearch;
    Utilities util = new Utilities();
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_billing);


        System.out.println("ONCREATE Invoice ACTIVITY");

        if (databaseUtama == null) {
            databaseUtama = FirebaseDatabase.getInstance();
        }

        mRoot = databaseUtama.getReference();
        mInvoice = mRoot.child("invoice");
        mUser = mRoot.child("users");
        mJadwal = mRoot.child("jadwal");
        mDokter = mRoot.child("dokter");
        mPasien = mRoot.child("pasien");

        listInvoice.clear();
        etSearch = findViewById(R.id.etSearch);
        ivCancel = findViewById(R.id.ivCancel);
        lv = findViewById(R.id.ListItem);
        tvStatus = findViewById(R.id.tvStatus);
        lv.setTextFilterEnabled(true);
        progressBar = findViewById(R.id.progressBar);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        statusUser = util.getStatus(HistoryBillingActivity.this);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                listDataInvoice = listInvoice.get(i);
                Intent intent = new Intent(HistoryBillingActivity.this, BillingActivity.class);
                intent.putExtra("idJadwalHistory", listDataInvoice.getIdInvoice());
                intent.putExtra("idPasien", listDataInvoice.getIdPasien());
                intent.putExtra("idDokter", listDataInvoice.getIdDokter());
                intent.putExtra("namaPasien", listDataInvoice.getNamaPasien());
                intent.putExtra("statusIntent", "dariListBilling");
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onStart() {

        listInvoice.clear();

        System.out.println("ONSTART Invoice ACTIVITY");
        etSearch.setText("");


        if (statusUser.equalsIgnoreCase("Pasien")) {
            String idPasien = util.getIdPasienLogin(HistoryBillingActivity.this);
            dataHistory(Invoice.COMPARE_BY_TANGGAL_ASC, mInvoice.orderByChild("idPasien").equalTo(idPasien));
        } else {
            dataHistory(Invoice.COMPARE_BY_TANGGAL_ASC, mInvoice);
        }


        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etSearch.getText().clear();
                ivCancel.setVisibility(View.GONE);
            }
        });

        super.onStart();

    }


    public void dataHistory(final Comparator<Invoice> sortirParameter, Query query) {

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getSupportActionBar().setSubtitle(dataSnapshot.getChildrenCount() + " Billing");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listInvoice.clear();
                System.out.println("listInvoice = " + listInvoice);
                for (DataSnapshot Invoice : dataSnapshot.getChildren()) {
                    final Invoice dataInvoice = Invoice.getValue(Invoice.class);
                    dataInvoice.idInvoice = Invoice.getKey();

                    listInvoice.add(dataInvoice);
                    System.out.println("data klinik 1 = " + listInvoice);

                }


                Collections.sort(listInvoice, sortirParameter);
                final CustomAdapterBilling arrayAdapter = new CustomAdapterBilling(HistoryBillingActivity.this, R.layout.card_view_history_billing, listInvoice);

                lv.setEmptyView(tvStatus);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                lv.setAdapter(arrayAdapter);
                progressBar.setVisibility(View.GONE);
                arrayAdapter.notifyDataSetChanged();

                etSearch.addTextChangedListener(new TextWatcher() {
                    String textSearch;

                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        textSearch = charSequence.toString();
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int i, int i1, int count) {
                        arrayAdapter.getFilter().filter(s.toString());
                        arrayAdapter.keyWord = s.toString();

                        if (s.length() > 0) {
                            ivCancel.setVisibility(View.VISIBLE);
                        } else if (s.length() == 0) {
                            ivCancel.setVisibility(View.GONE);
                        }

                        System.out.println("textSearch.lenght = " + textSearch.length());
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
        System.out.println("onResume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("onDestroy");
        Glide.get(this).clearMemory();
    }

    @Override
    protected void onPause() {
        System.out.println("onPause");
        listInvoice.clear();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sortir, menu);
        final MenuItem sortir = menu.findItem(R.id.sortir).setVisible(false);
        final MenuItem filter = menu.findItem(R.id.filter).setVisible(false);
        final MenuItem dateRange = menu.findItem(R.id.daterange);
        final MenuItem pdfReport = menu.findItem(R.id.pdfReport);

        if (!statusUser.equalsIgnoreCase("Pasien"))
        {
            dateRange.setVisible(true);
            pdfReport.setVisible(true);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.daterange) {
//           pickRangeDate();
//             pickDate();
            rangeDateDialog();
        } else if (id == R.id.pdfReport) {
            try {
                createPdf();
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return super.onOptionsItemSelected(item);
    }


    public void pickDate(Boolean flagsDate) {

        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd;

        if (flagsDate && selectedDateFrom != null) {
            dpd = DatePickerDialog.newInstance(
                    HistoryBillingActivity.this,
                    yearFrom,
                    monthFrom - 1,
                    dayFrom
            );
        } else if (!flagsDate && selectedDateTo != null) {
            dpd = DatePickerDialog.newInstance(
                    HistoryBillingActivity.this,
                    yearTo,
                    monthTo - 1,
                    dayTo
            );
        } else {


            if (selectedDateFrom!=null)
            {
                dpd = DatePickerDialog.newInstance(
                        HistoryBillingActivity.this,
                        yearFrom,
                        monthFrom - 1,
                        dayFrom
                );

                Calendar calFrom = Calendar.getInstance();
                calFrom.set(yearFrom,monthFrom-1,dayFrom);
                dpd.setMinDate(calFrom);
            }else {
                dpd = DatePickerDialog.newInstance(
                        HistoryBillingActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
            }
        }


        Calendar sunday;
        List<Calendar> weekends = new ArrayList<>();
        int weeks = 50;

        for (int i = 0; i < (weeks * 7); i = i + 7) {
            sunday = Calendar.getInstance();
            sunday.add(Calendar.DAY_OF_YEAR, (Calendar.SUNDAY - sunday.get(Calendar.DAY_OF_WEEK) + 7 + i));
            // saturday = Calendar.getInstance();
            // saturday.add(Calendar.DAY_OF_YEAR, (Calendar.SATURDAY - saturday.get(Calendar.DAY_OF_WEEK) + i));
            // weekends.add(saturday);
            weekends.add(sunday);
        }
        Calendar[] disabledDays = weekends.toArray(new Calendar[weekends.size()]);
        dpd.setDisabledDays(disabledDays);


//                for (int i = 0;i < holidays.length; i++) {
//                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
//                    String a = "26-07-2018";
//                    java.util.Date date = null;
//                    try {
//                        date = sdf.parse(holidays[i]);
//                        DateActivity obj = new DateActivity();
//                        now = obj.dateToCalendar(date);
//                        System.out.println(now.getTime());
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//
//                    List<Calendar> dates = new ArrayList<>();
//                    dates.add(now);
//                    Calendar[] disabledDays1 = dates.toArray(new Calendar[dates.size()]);
//                    dpd.setDisabledDays(disabledDays1);
//
//                }
        dpd.show(getFragmentManager(), "Datepickerdialog");

    }


//    public void pickRangeDate()
//    {
//        Calendar now = Calendar.getInstance();
//        DatePickerDialog dpd = DatePickerDialog.newInstance(
//                HistoryBillingActivity.this,
//                now.get(Calendar.YEAR),
//                now.get(Calendar.MONTH),
//                now.get(Calendar.DAY_OF_MONTH)
//        );
//        dpd.show(getFragmentManager(), "Datepickerdialog");
//
//    }


    public Long convertTimeStamp(String tanggal, String waktu) {
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Date date2 = null;
        try {
            date2 = formatter.parse(tanggal + " " + waktu);
        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("Log = " + e);
        }
        Long timeStamp = date2.getTime();
        return timeStamp;
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        int monthSelected = monthOfYear + 1;


        if (flagsDate) {
            dayFrom = dayOfMonth;
            monthFrom = monthSelected;
            yearFrom = year;
            selectedDateFrom = dayFrom + "-" + monthFrom + "-" + yearFrom;
            tvDariTanggal.setText(selectedDateFrom);
        } else {
            dayTo = dayOfMonth;
            monthTo = monthSelected;
            yearTo = year;
            selectedDateTo = dayTo + "-" + monthTo + "-" + yearTo;
            tvKeTanggal.setText(selectedDateTo);
        }
        System.out.println("selectedFrom = " + selectedDateFrom + " " + "selectedDateTo = " + selectedDateTo);


    }

//    @Override
//    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth, int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {
//
//        int monSelectedFrom = monthOfYear+1;
//        int monSelectedTo = monthOfYearEnd+1;
//        String selectedDateFrom = dayOfMonth+"-"+monSelectedFrom+"-"+year;
//        System.out.println("selectedFrom = "+selectedDateFrom);
//        timeStampStart = convertTimeStamp(selectedDateFrom,"00:00");
//        System.out.println("timeStamp start = "+ timeStampStart);
//
//        String selectedDateTo = dayOfMonthEnd+"-"+monSelectedTo+"-"+yearEnd;
//        System.out.println("selectedDate = "+selectedDateTo);
//        timeStampEnd = convertTimeStamp(selectedDateTo,"23:59");
//        System.out.println("timeStamp end = "+ timeStampEnd);
//
//        if (timeStampStart!=null && timeStampEnd!=null)
//        {
//            Query query = mInvoice.orderByChild("tanggalInvoice").startAt(timeStampStart).endAt(timeStampEnd);
//            dataHistory(Invoice.COMPARE_BY_TANGGAL_ASC, query);
//        }
//
////
//
//
//    }

    private void createPdf() throws DocumentException, IOException {


        File pdfFolder = new File("/sdcard", "pdfDentistReportBilling");

        if (!pdfFolder.exists()) {
            pdfFolder.mkdir();

        }

        BillingActivity.TableHeader.HeaderTable event = new BillingActivity.TableHeader.HeaderTable();

        //Create time stamp
        Date date = new Date();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);
        String dateCreated = new SimpleDateFormat("dd" + "/" + "MM" + "/" + "yyyy").format(date);
        File myFile = new File("/sdcard/pdfDentistReportBilling/" + "_" + timeStamp + ".pdf");


        System.out.println("pdf file = " + myFile);

        FileOutputStream output = new FileOutputStream(myFile);


        //Step 1
        Document document = new Document(PageSize.A4, 36, 36, 40 + event.getTableHeight(), 36);

        //Step
        PdfWriter pdfWriter = PdfWriter.getInstance(document, output);
        pdfWriter.setPageEvent(event);
        //Step 3
        document.open();

        BillingActivity.myFooter footer = new BillingActivity.myFooter();
        pdfWriter.setPageEvent(footer);


        Font text = FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, Font.BOLD);
        Font textTitle = FontFactory.getFont(FontFactory.TIMES_ROMAN, 20, Font.BOLD);


        Paragraph title = new Paragraph("LAPORAN INVOICE", textTitle);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        title.setSpacingAfter(15);
        document.add(title);

        BillingActivity billingActivity = new BillingActivity();



        final PdfPTable pdfPTable = new PdfPTable(5);
        pdfPTable.setWidthPercentage(100);
        pdfPTable.setWidths(new float[]{2, 5, 1, 2, 5});
        pdfPTable.addCell(billingActivity.getCell("Periode", PdfPCell.ALIGN_LEFT, 0));
        String periode = null;
        if (selectedDateFrom == null || selectedDateTo==null) {
            periode = "Semua";
        } else if (selectedDateFrom!=null && selectedDateTo!=null) {
            String monFrom = MONTHS[monthFrom-1];
            String monTo = MONTHS[monthTo-1];
            String dateFrom = dayFrom+" "+monFrom+" "+yearFrom;
            String dateTo = dayTo+" "+monTo+" "+yearTo;
            periode = dateFrom+" - "+dateTo;
        }
        pdfPTable.addCell(billingActivity.getCell(": " + periode, PdfPCell.ALIGN_LEFT, 1));
        pdfPTable.addCell(billingActivity.getCell("", PdfPCell.ALIGN_LEFT, 0));
        pdfPTable.addCell(billingActivity.getCell("", PdfPCell.ALIGN_LEFT, 0));
        pdfPTable.addCell(billingActivity.getCell("", PdfPCell.ALIGN_LEFT, 1));
        pdfPTable.setSpacingAfter(4);
        document.add(pdfPTable);


        Paragraph tindakan = new Paragraph("LAPORAN INVOICE", text);
        tindakan.setAlignment(Paragraph.ALIGN_LEFT);
        tindakan.setSpacingAfter(5);
        document.add(tindakan);

        if (listInvoice != null && listInvoice.size() > 0) {

            int totalHarga = 0;
            final PdfPTable tindakanDetail = new PdfPTable(5);
            tindakanDetail.setWidthPercentage(100);
            tindakanDetail.setWidths(new float[]{1, 3, 4, 3, 3});
            tindakanDetail.addCell(billingActivity.getCellBorderBold("NO", PdfPCell.ALIGN_CENTER));
            tindakanDetail.addCell(billingActivity.getCellBorderBold("PASIEN", PdfPCell.ALIGN_CENTER));
            tindakanDetail.addCell(billingActivity.getCellBorderBold("TANGGAL", PdfPCell.ALIGN_CENTER));
            tindakanDetail.addCell(billingActivity.getCellBorderBold("DOKTER", PdfPCell.ALIGN_CENTER));
            tindakanDetail.addCell(billingActivity.getCellBorderBold("HARGA", PdfPCell.ALIGN_CENTER));

            System.out.println("TESS = " + listInvoice.get(0).getTotalHarga());

            for (int pp = 0; pp <= listInvoice.size() - 1; pp++) {
                ambil = listInvoice.get(pp);
                tindakanDetail.addCell(billingActivity.getCellBorder(String.valueOf(pp + 1), PdfPCell.ALIGN_CENTER, 1));
                tindakanDetail.addCell(billingActivity.getCellBorder(ambil.getNamaPasien(), PdfPCell.ALIGN_CENTER, 1));
                tindakanDetail.addCell(billingActivity.getCellBorder(ambil.tanggalConvert(), PdfPCell.ALIGN_LEFT, 1));
                tindakanDetail.addCell(billingActivity.getCellBorder("drg. " + ambil.getNamaDokter(), PdfPCell.ALIGN_CENTER, 1));
                int hargaPerInvoice = (int) rupiahToDouble(ambil.getTotalHarga());
                int diskon = Integer.parseInt(ambil.getDiskon());

                tindakanDetail.addCell(billingActivity.getCellBorder(billingActivity.rupiahFormat(String.valueOf(hargaPerInvoice - diskon)), PdfPCell.ALIGN_CENTER, 1));
                totalHarga = (int) ((rupiahToDouble(ambil.getTotalHarga()) - Double.valueOf(ambil.getDiskon())) + totalHarga);

            }

            document.add(tindakanDetail);

            final PdfPTable total = new PdfPTable(5);
            total.setWidthPercentage(100);
            total.setWidths(new float[]{1, 3, 4, 3, 3});
            total.addCell(billingActivity.getCell("", PdfPCell.ALIGN_CENTER, 1));
            total.addCell(billingActivity.getCell("", PdfPCell.ALIGN_CENTER, 1));
            total.addCell(billingActivity.getCell("", PdfPCell.ALIGN_CENTER, 1));
            total.addCell(billingActivity.getCellBorderBold("GRAND TOTAL", PdfPCell.ALIGN_CENTER));
            total.addCell(billingActivity.getCellBorder(billingActivity.rupiahFormat(String.valueOf(totalHarga)), PdfPCell.ALIGN_CENTER, 1));
            document.add(total);
        }


        document.close();


        System.out.println("PDF OK");

        viewPdf(myFile);
//        sendEmail(myFile);


    }

    private void viewPdf(File myFile) {

        Intent intent = new Intent(HistoryBillingActivity.this, PdfViewerActivity.class);
        intent.putExtra("file", myFile);
        intent.putExtra("title", "Laporan Invoice");
        startActivity(intent);


    }


    private double rupiahToDouble(String angka) {

// remove the £ and ,
        String s2 = angka.replaceAll("[Rp,]", "");
// then turn into a double
        double d = Double.parseDouble(s2);
// and round up to two decimal places.
        double value = (long) (d * 100 + 0.5) / 100.0;

        System.out.printf("%.0f%n", value);

        return value;
    }


    private void rangeDateDialog() {
        final AlertDialog.Builder alBuilder = new AlertDialog.Builder(HistoryBillingActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        alBuilder.setTitle("Filter Tanggal");
        final View dialog = (View) inflater.inflate(R.layout.dialog_range_date, null);
        alBuilder.setView(dialog);

        tvDariTanggal = dialog.findViewById(R.id.tvDariTanggal);
        tvKeTanggal = dialog.findViewById(R.id.tvKeTanggal);
        final RelativeLayout rlDariTanggal = dialog.findViewById(R.id.rlDariTanggal);
        final RelativeLayout rlKeTanggal = dialog.findViewById(R.id.rlKeTanggal);

        if (selectedDateFrom != null || selectedDateTo != null) {
            tvDariTanggal.setText(selectedDateFrom);
            tvKeTanggal.setText(selectedDateTo);
        }


        rlDariTanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flagsDate = true;
                pickDate(flagsDate);


            }
        });

        rlKeTanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flagsDate = false;
                pickDate(flagsDate);

            }
        });


        alBuilder.setCancelable(true).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        }).setNeutralButton("Clear", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });


        final AlertDialog alertDialog = alBuilder.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                System.out.println("selectedDateFrom = " + selectedDateFrom);
                System.out.println("selectedDateTo = " + selectedDateTo);
                if (selectedDateFrom != null && selectedDateTo != null)
                {
                    timeStampStart = convertTimeStamp(selectedDateFrom, "00:00");
                    timeStampEnd = convertTimeStamp(selectedDateTo, "00:00");
                }


                System.out.println("timeStampStart = " + timeStampStart + " " + timeStampEnd);
                if (tvDariTanggal.getText().toString().equalsIgnoreCase("Semua") && !tvKeTanggal.getText().toString().equalsIgnoreCase("Semua")) {
                    Toast.makeText(HistoryBillingActivity.this, "Silahkan pilih tanggal awal", Toast.LENGTH_SHORT).show();
                } else if (!tvDariTanggal.getText().toString().equalsIgnoreCase("Semua") && tvKeTanggal.getText().toString().equalsIgnoreCase("Semua")) {
                    Toast.makeText(HistoryBillingActivity.this, "Silahkan pilih tanggal akhir", Toast.LENGTH_SHORT).show();
                } else if (timeStampStart != null && timeStampEnd != null) {
                    Query query = mInvoice.orderByChild("tanggalInvoice").startAt(timeStampStart).endAt(timeStampEnd);
                    dataHistory(Invoice.COMPARE_BY_TANGGAL_ASC, query);
                    alertDialog.dismiss();
                } else if (tvDariTanggal.getText().toString().equalsIgnoreCase("Semua") && tvKeTanggal.getText().toString().equalsIgnoreCase("Semua")) {

                    dataHistory(Invoice.COMPARE_BY_TANGGAL_ASC, mInvoice);
                    alertDialog.dismiss();
                }

            }
        });

        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();

            }
        });

        alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tvKeTanggal.setText("Semua");
                tvDariTanggal.setText("Semua");
                selectedDateFrom = null;
                selectedDateTo = null;
                timeStampStart = null;
                timeStampEnd = null;
            }
        });
    }
}
