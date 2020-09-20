package com.teddybrothers.co_teddy.dentist;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.teddybrothers.co_teddy.dentist.entity.About;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class AboutDentalActivity extends AppCompatActivity {

    ImageView ivCall, ivEmail, ivLocation, ivCallSeluler, ivSms, ivWA;
    TextView tvNama, tvAlamat, tvTelp, tvEmail, tvTelpSeluler,tvAppVersion,tvJamPraktik;
    public static int REQUEST_PERMISSIONS = 1;
    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 100;
    boolean boolean_permission;
    int PLACE_PICKER_REQUEST = 1;
    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    DatabaseReference mUserRef, mRoot, mAbout;

    ProgressDialog progressDialog;
    Utilities util = new Utilities();
    GPSTracker gps;
    String alamat, latitude, longitude,status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_dental);

        tvJamPraktik = (TextView) findViewById(R.id.tvJamPraktik);
        tvAppVersion = (TextView) findViewById(R.id.tvAppVersion);
        tvNama = (TextView) findViewById(R.id.tvNama);
        tvAlamat = (TextView) findViewById(R.id.tvAlamat);
        tvTelp = (TextView) findViewById(R.id.tvTelp);
        tvTelpSeluler = (TextView) findViewById(R.id.tvTelpSeluler2);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        ivWA = (ImageView) findViewById(R.id.ivWA);
        ivCall = (ImageView) findViewById(R.id.ivTelp);
        ivSms = (ImageView) findViewById(R.id.ivSms);
        ivCallSeluler = (ImageView) findViewById(R.id.ivTelpSeluler);
        ivEmail = (ImageView) findViewById(R.id.ivEmail);
        ivLocation = (ImageView) findViewById(R.id.ivAlamat);

        tvAppVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW ,Uri.parse("market://details?id=com.teddybrothers.co_teddy.dentist"));
                startActivity(intent);
            }
        });

        status = util.getStatus(AboutDentalActivity.this);
        ivWA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneSeluler = tvTelpSeluler.getText().toString();
                String message = "Halo Dokter!!";
                try {
                    PackageManager packageManager = getPackageManager();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    String url = "https://api.whatsapp.com/send?phone=" + phoneSeluler + "&text=" + URLEncoder.encode(message, "UTF-8");
                    i.setPackage("com.whatsapp");
                    i.setData(Uri.parse(url));
                    if (i.resolveActivity(packageManager) != null) {
                        startActivity(i);
                    }
                    else
                    {
                        Toast.makeText(AboutDentalActivity.this,"Aplikasi Whatapps tidak ditemukan",Toast.LENGTH_SHORT).show();
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
//                Uri uri = Uri.parse("smsto:" + phoneSeluler);
//                Intent i = new Intent(Intent.ACTION_SENDTO, uri);
//                i.setPackage("com.whatsapp");
//                startActivity(Intent.createChooser(i, ""));
            }
        });

        ivCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = tvTelp.getText().toString();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));

                if (ActivityCompat.checkSelfPermission(AboutDentalActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                startActivity(intent);

            }
        });

        ivCallSeluler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneSeluler = tvTelpSeluler.getText().toString();
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.fromParts("tel", phoneSeluler, null));
                if (ActivityCompat.checkSelfPermission(AboutDentalActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                startActivity(intent);

            }
        });

        ivEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = tvEmail.getText().toString();
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", email, null));
                startActivity(intent.createChooser(intent, "Email via..."));

            }
        });

        ivSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = tvTelpSeluler.getText().toString();
                ContentValues values = new ContentValues();
                String name = tvNama.getText().toString();
                values.put(Contacts.People.NUMBER, phone);
                values.put(Contacts.People.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM);
                values.put(Contacts.People.LABEL, name);
                values.put(Contacts.People.NAME, name);
                Uri dataUri = getContentResolver().insert(Contacts.People.CONTENT_URI, values);
                Uri updateUri = Uri.withAppendedPath(dataUri, Contacts.People.Phones.CONTENT_DIRECTORY);
                values.clear();
                values.put(Contacts.People.Phones.TYPE, Contacts.People.TYPE_MOBILE);
                values.put(Contacts.People.NUMBER, phone);
                updateUri = getContentResolver().insert(updateUri, values);
                Toast.makeText(AboutDentalActivity.this,"Kontak telah ditambahkan",Toast.LENGTH_SHORT).show();
                contactExists(AboutDentalActivity.this,phone);
            }
        });

        ivLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startingMaps();
            }
        });






    }

    public  boolean contactExists(Activity _activity, String number){
        Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,Uri.encode(number));

        String[] mPhoneNumberProjection = { ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME };

        Cursor cur = _activity.getContentResolver().query(lookupUri,mPhoneNumberProjection, null, null, null);
        try {
            if (cur.moveToFirst()) {
                // if contact are in contact list it will return true

                ivSms.setVisibility(View.GONE);
                return true;
            }} finally {
            if (cur != null)
                cur.close();
        }

        //if contact are not match that means contact are not added
        return false;
    }

    public void startingMaps() {
        gps = new GPSTracker(AboutDentalActivity.this);

//        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
//        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        double longitude = location.getLongitude();
//        double latitude = location.getLatitude();

        // check if GPS enabled
        if (gps.canGetLocation()) {
            double latitudeMe = gps.getLatitude();
            double longitudeMe = gps.getLongitude();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr=" + latitudeMe + "," + longitudeMe + "&daddr=" + latitude + "," + longitude + ""));
            startActivity(intent);

            // \n is for new line
            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();


        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {


        if (requestCode == REQUEST_PERMISSIONS) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                boolean_permission = true;


            } else {


            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        final MenuItem edit = menu.findItem(R.id.edit);
        String status = util.getStatus(AboutDentalActivity.this);
        System.out.println("status = " + status);
        if (status.equalsIgnoreCase("Pasien")||status.equalsIgnoreCase("Dokter")) {
            edit.setVisible(false);
        }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.edit) {

            startActivity(new Intent(AboutDentalActivity.this,InputAboutDental.class));


        }
        else if (id==android.R.id.home)
        {
            onBackPressed();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }







    @Override
    protected void onStart() {
        super.onStart();

        progressDialog = new ProgressDialog(AboutDentalActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
        }
        mAuth = FirebaseAuth.getInstance();
        mRoot = mDatabase.getReference();
        mUserRef = mRoot.child("users");
        mAbout = mRoot.child("about");


        String userId = mAuth.getCurrentUser().getUid();

        mAbout.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("datasnapshot about = " + dataSnapshot);
                if (dataSnapshot.getValue() == null) {
                    if (status.equalsIgnoreCase("Administrator")||status.equalsIgnoreCase("Dokter"))
                    {
                        startActivity(new Intent(AboutDentalActivity.this,InputAboutDental.class));
                        finish();
                        progressDialog.dismiss();
                    }
                    else
                    {
                        Toast.makeText(AboutDentalActivity.this,"Data Profil klinik tidak ditemukan",Toast.LENGTH_SHORT).show();
                        finish();
                    }
//                    ivCall.setVisibility(View.GONE);
//                    ivEmail.setVisibility(View.GONE);
//                    ivLocation.setVisibility(View.GONE);
//                    ivCallSeluler.setVisibility(View.GONE);
//                    ivWA.setVisibility(View.GONE);
//                    ivSms.setVisibility(View.GONE);


                } else {

                    About about = dataSnapshot.getValue(About.class);
//                    String nama = dataSnapshot.child("namaKlinik").getValue(String.class);
//                    String alamat = dataSnapshot.child("alamat").getValue(String.class);
//                    String noTelp = dataSnapshot.child("noTelp").getValue(String.class);
//                    String noTelpSeluler = dataSnapshot.child("telpSeluler").getValue(String.class);
//                    String email = dataSnapshot.child("email").getValue(String.class);
//                    latitude = dataSnapshot.child("latitude").getValue(String.class);
//                    longitude = dataSnapshot.child("longitude").getValue(String.class);

                    tvNama.setText(about.getNamaKlinik());





                    tvJamPraktik.setText(cekJam(about.getJamBuka(),about.getMenitBuka())+" - "+cekJam(about.getJamTutup(),about.getMenitTutup()));
                    tvAlamat.setText(about.getAlamat());
                    tvTelp.setText(about.getNoTelp());
                    tvEmail.setText(about.getEmail());
                    tvTelpSeluler.setText(about.getTelpSeluler());
                    progressDialog.dismiss();
                    contactExists(AboutDentalActivity.this,about.getTelpSeluler());
                    getVersionInfo();
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getVersionInfo() {
        String versionName = "";
        int versionCode = -1;
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = packageInfo.versionName;
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

//
//        tvAppVersion.setText(String.format("Version name = %s \nVersion code = %d", versionName, versionCode));
        tvAppVersion.setText("v"+versionName);
    }

    public String cekJam(int jam,int menit)
    {
        String jamPraktik = null, menitPraktik = null;
        if (jam < 10) {
            jamPraktik = "0" + jam;
        } else {
            jamPraktik = String.valueOf(jam);
        }
        if (menit < 10) {
            menitPraktik = "0" + menit;
        } else {
            menitPraktik = String.valueOf(menit);
        }

        return jamPraktik+":"+menitPraktik;

    }



}
