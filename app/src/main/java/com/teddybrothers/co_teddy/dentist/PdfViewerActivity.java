package com.teddybrothers.co_teddy.dentist;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PdfViewerActivity extends AppCompatActivity {
    DatabaseReference mRoot, mUserRef, mRekamMedis, mPerawatan, mJadwal;
    FirebaseAuth mAuth;
    PDFView pdfView;
    File pdfFile;
    Context context;
    String title;
    public static int REQUEST_PERMISSIONS = 1;
    boolean boolean_permission;
    Utilities util = new Utilities();
    String statusUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);




        statusUser = util.getStatus(PdfViewerActivity.this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            pdfFile = (File) extras.get("file");
            title = (String) extras.get("title");
        }

        if (title!=null)
        {
            getSupportActionBar().setTitle(title);
        }
        System.out.println("pdfFile = "+pdfFile);
        pdfView = (PDFView) findViewById(R.id.pdfView);
        pdfView.fromFile(pdfFile).load();

        fn_permission();

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_rekam_medis_view, menu);
        MenuItem cetak = menu.findItem(R.id.cetak);
        MenuItem share = menu.findItem(R.id.share);
        if (statusUser.equalsIgnoreCase("Pasien")||statusUser.equalsIgnoreCase("Administrator"))
        {
            cetak.setVisible(false);
            share.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.share) {


            share(pdfFile);

        } else if (id == R.id.cetak) {

            print(pdfFile);
        }

        else if (id==android.R.id.home)
        {
            onBackPressed();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void print(final File myFile)
    {
        PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);

        PrintDocumentAdapter printDocumentAdapter = new PrintDocumentAdapter() {
            @Override
            public void onLayout(PrintAttributes printAttributes, PrintAttributes printAttributes1, CancellationSignal cancellationSignal, LayoutResultCallback layoutResultCallback, Bundle bundle) {
                if (cancellationSignal.isCanceled()) {
                    layoutResultCallback.onLayoutCancelled();
                    return;
                }


                PrintDocumentInfo pdi = new PrintDocumentInfo.Builder("Name of file").setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).build();

                layoutResultCallback.onLayoutFinished(pdi, true);
            }

            @Override
            public void onWrite(PageRange[] pageRanges, ParcelFileDescriptor parcelFileDescriptor, CancellationSignal cancellationSignal, WriteResultCallback writeResultCallback) {
                InputStream input = null;
                OutputStream output = null;

                try {

                    input = new FileInputStream(myFile);
                    output = new FileOutputStream(parcelFileDescriptor.getFileDescriptor());

                    byte[] buf = new byte[1024];
                    int bytesRead;

                    while ((bytesRead = input.read(buf)) > 0) {
                        output.write(buf, 0, bytesRead);
                    }

                    writeResultCallback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});

                } catch (FileNotFoundException ee){
                    //Catch exception
                } catch (Exception e) {
                    //Catch exception
                } finally {
                    try {
                        input.close();
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        String jobName = this.getString(R.string.app_name) + " Document";
        printManager.print(jobName, printDocumentAdapter, null);
    }
    private void share(File myFile) {

        Uri uriShare = FileProvider.getUriForFile(PdfViewerActivity.this,"com.teddybrothers.co_teddy.dentist",myFile);
//        Uri.fromFile(myFile);
        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        share.setType("application/pdf");
        share.putExtra(Intent.EXTRA_STREAM, uriShare);
        share.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        this.revokeUriPermission(uriShare, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(share);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {


        if (requestCode == REQUEST_PERMISSIONS) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                boolean_permission = true;


            } else {
                Toast.makeText(getApplicationContext(), "Please allow the permission", Toast.LENGTH_LONG).show();

            }
        }
    }

    private void fn_permission() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)||
                (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {


            if (ActivityCompat.checkSelfPermission(PdfViewerActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if ((ActivityCompat.shouldShowRequestPermissionRationale(PdfViewerActivity.this, Manifest.permission.CALL_PHONE))) {
                } else {
                    ActivityCompat.requestPermissions(PdfViewerActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_PERMISSIONS);
                }
                if ((ActivityCompat.shouldShowRequestPermissionRationale(PdfViewerActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE))) {
                } else {
                    ActivityCompat.requestPermissions(PdfViewerActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_PERMISSIONS);

                }

            }
        } else {
            boolean_permission = true;


        }
    }

}
