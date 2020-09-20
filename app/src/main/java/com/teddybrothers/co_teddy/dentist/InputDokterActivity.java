package com.teddybrothers.co_teddy.dentist;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.teddybrothers.co_teddy.dentist.entity.Dokter;
import com.teddybrothers.co_teddy.dentist.entity.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class InputDokterActivity extends AppCompatActivity {
    ProgressBar pbTtd;
    ProgressDialog progressDialog;
    EditText etNama, etNoTelp, etNoSIP, etNoSTR, etEmail, etPassword;
    FirebaseDatabase mDatabase;
    DatabaseReference mRoot, mUserRef, mDokterRef;
    FirebaseAuth mAuthAdm, mAuthDokter;
    CircleImageView ivDokter;
    String idDokter;
    private SignaturePad mSignaturePad;
    private Button mClearButton;
    private Button mSaveButton;
    Utilities util = new Utilities();
    public String ttdUrl, statusUser = "Aktif";
    RelativeLayout rlSignaturePad, rlImageSignaturePad;
    public static int REQUEST_PERMISSIONS = 1;
    boolean boolean_permission;

    public static final String[] MONTHS = {"Januari", "Febuari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
    public Calendar cal1 = Calendar.getInstance();
    public int day1 = cal1.get(Calendar.DAY_OF_MONTH);
    public int month1 = cal1.get(Calendar.MONTH);
    public int year1 = cal1.get(Calendar.YEAR);

    String nama, namaSortir, noTelp, noSTR, noSIP, tanggalDaftar;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    Uri imageUri;
    String photoUrl = "null";
    public String mCurrentPhotoPath, mon1;
    String userId, userIdDokter, ttdUrlPasien;
    Button btnEdit;
    TextView tvDATAPENGGUNA;
    ProgressBar progressBar;
    private static final int GALERY_INTENT = 2;
    static final int CAMERA_PIC_REQUEST = 3;
    ArrayList<CustomObject> choice = new ArrayList<CustomObject>();
    CustomObject pilihan;
    ImageView ivTtd;

    Boolean isSigned = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_dokter);

        progressDialog = new ProgressDialog(InputDokterActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
        }
        mRoot = mDatabase.getReference();
        mUserRef = mRoot.child("users");
        mDokterRef = mRoot.child("dokter");

        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etNama = (EditText) findViewById(R.id.etNama);
        etNoTelp = (EditText) findViewById(R.id.etNoTelp);
        etNoSIP = (EditText) findViewById(R.id.etNoSIP);
        etNoSTR = (EditText) findViewById(R.id.etNoSTR);
        mAuthAdm = FirebaseAuth.getInstance();
        ivTtd = (ImageView) findViewById(R.id.ivTtd);
        pbTtd = (ProgressBar) findViewById(R.id.pbTtd);
        btnEdit = (Button) findViewById(R.id.btnEditTtd);
        tvDATAPENGGUNA = (TextView) findViewById(R.id.tvDATAPENGGUNA);
        tvDATAPENGGUNA = (TextView) findViewById(R.id.tvDATAPENGGUNA);
        mSignaturePad = (SignaturePad) findViewById(R.id.signature_pad);
        mClearButton = (Button) findViewById(R.id.clear_button);
        rlImageSignaturePad = (RelativeLayout) findViewById(R.id.rlImageSignaturePad);
        rlSignaturePad = (RelativeLayout) findViewById(R.id.rlSignaturePad);


        filterAlpabetic(etNama);

        CustomObject choice1 = new CustomObject(R.drawable.photocamera, "camera", "Camera", 0);
        choice.add(choice1);
        choice1 = new CustomObject(R.drawable.gallery, "gallery", "Gallery", 0);
        choice.add(choice1);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userIdDokter = extras.getString("userIDDokter");
            idDokter = extras.getString("idDokter");
        }
        System.out.println("ID Dokter = " + idDokter);

        if (idDokter != null) {

            getSupportActionBar().setTitle("Perbaharui Data Dokter");
            mDokterRef.child(idDokter).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String namaDokter = dataSnapshot.child("nama").getValue(String.class);
                    String noSTR = dataSnapshot.child("noSTR").getValue(String.class);
                    String noSIP = dataSnapshot.child("noSIP").getValue(String.class);
                    String noTelp = dataSnapshot.child("noTelp").getValue(String.class);
                    ttdUrlPasien = dataSnapshot.child("ttdUrl").getValue(String.class);

                    etNama.setText(namaDokter);
                    etNoSTR.setText(noSTR);
                    etNoSIP.setText(noSIP);
                    etNoTelp.setText(noTelp);
                    rlImageSignaturePad.setVisibility(View.VISIBLE);
                    Glide.with(getApplicationContext()).load(ttdUrlPasien)
                            .thumbnail(0.5f)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(ivTtd);
                    pbTtd.setVisibility(View.GONE);
                    rlSignaturePad.setVisibility(View.GONE);
                    etEmail.setVisibility(View.GONE);
                    etPassword.setVisibility(View.GONE);
                    etPassword.setTransformationMethod(null);
                    tvDATAPENGGUNA.setVisibility(View.GONE);

                    progressDialog.dismiss();

                    btnEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rlSignaturePad.setVisibility(View.VISIBLE);
                            rlImageSignaturePad.setVisibility(View.GONE);
                        }
                    });


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            progressDialog.dismiss();
            pbTtd.setVisibility(View.GONE);
            btnEdit.setVisibility(View.GONE);
            rlImageSignaturePad.setVisibility(View.GONE);

        }


        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
                System.out.println("onStartSigned");
            }

            @Override
            public void onSigned() {
                isSigned = true;
                mClearButton.setEnabled(true);
            }

            @Override
            public void onClear() {
                mClearButton.setEnabled(false);
            }
        });


        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSigned = false;
                mSignaturePad.clear();
            }
        });
        fn_permission();

    }

    public boolean addJpgSignatureToGallery(Bitmap signature, String email, String password) {
        boolean result = false;
        try {
            File photo = new File(getAlbumStorageDir("SignaturePad"), String.format("Signature_%d.jpg", System.currentTimeMillis()));
            saveBitmapToJPG(signature, photo);
            scanMediaFile(photo, email, password);
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("SignaturePad", "Directory not created");
        }
        return file;
    }

    public void saveBitmapToJPG(Bitmap bitmap, File photo) throws IOException {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        OutputStream stream = new FileOutputStream(photo);
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        stream.close();
    }

    private void scanMediaFile(File photo, final String email, final String password) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(photo);
        mediaScanIntent.setData(contentUri);

        String path = "ttdDokter/" + UUID.randomUUID() + ".jpg";
        StorageReference ttdref = storage.getReference(path);

        UploadTask uploadTask = ttdref.putFile(contentUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(InputDokterActivity.this, "Upload Gagal", Toast.LENGTH_LONG).show();

            }
        }).addOnSuccessListener(InputDokterActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(InputDokterActivity.this, "Upload Success", Toast.LENGTH_LONG).show();
                @SuppressWarnings("VisibleForTests") Uri url = taskSnapshot.getDownloadUrl();
                ttdUrl = url.toString();
                System.out.println("INI URL TTD!!!!" + ttdUrl);
                String deviceToken = FirebaseInstanceId.getInstance().getToken();
                String status = "Dokter";
                System.out.println("ttdurl =" + ttdUrl);
                if (email != null) {
                    createUser(email, password, deviceToken, status, ttdUrl, statusUser);
                } else {
                    String ttdPasien;
                    if (isSigned) {
                        ttdPasien = ttdUrl;
                    } else {
                        ttdPasien = ttdUrlPasien;
                    }
                    updateDokter(nama, noSTR, noSIP, noTelp, ttdPasien, userIdDokter);
                }


            }
        });
        InputDokterActivity.this.sendBroadcast(mediaScanIntent);
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        MenuItem update = menu.findItem(R.id.reschedule);
        MenuItem addPhoto = menu.findItem(R.id.addphoto);
        MenuItem done = menu.findItem(R.id.done);
        addPhoto.setVisible(false);
        System.out.println("idDokter = " + idDokter);
        if (idDokter != null) {

            update.setVisible(true);
            done.setVisible(false);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.done) {

            nama = etNama.getText().toString();
            namaSortir = etNama.getText().toString().toLowerCase();
            noTelp = etNoTelp.getText().toString();
            noSTR = etNoSTR.getText().toString();
            noSIP = etNoSIP.getText().toString();
            Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();
            System.out.println("ttd bitmap = " + signatureBitmap);
            final String password = etPassword.getText().toString();
            final String email = etEmail.getText().toString();

            mon1 = MONTHS[month1];
            tanggalDaftar = day1 + " " + mon1 + " " + year1;

            if (TextUtils.isEmpty(nama)) {
                etNama.setError("Nama belum terisi");
            } else if (TextUtils.isEmpty(noTelp)) {
                etNoTelp.setError("No Telp belum terisi");
            } else if (TextUtils.isEmpty(noSTR)) {
                etNoSTR.setError("No STR belum terisi");
            } else if (TextUtils.isEmpty(noSIP)) {
                etNoSIP.setError("No SIP belum terisi");
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError("Masukkan email yang valid");
            } else if (TextUtils.isEmpty(password)) {
                etPassword.setError("Password harus diisi!");
            } else if (password.length() < 6) {
                etPassword.setError("Password harus lebih dari 6 digits!");

            } else {
                progressDialog = new ProgressDialog(InputDokterActivity.this);
                progressDialog.setMessage("Loading...");
                progressDialog.show();

                addJpgSignatureToGallery(signatureBitmap, email, password);


            }


        } else if (id == R.id.reschedule)

        {
            progressDialog = new ProgressDialog(InputDokterActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();

            nama = etNama.getText().toString();
            namaSortir = etNama.getText().toString().toLowerCase();
            System.out.println("nama Sortir = " + namaSortir + " nama = " + nama.toLowerCase());
            noTelp = etNoTelp.getText().toString();
            noSTR = etNoSTR.getText().toString();
            noSIP = etNoSIP.getText().toString();
            Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();
            mon1 = MONTHS[month1];
            tanggalDaftar = day1 + " " + mon1 + " " + year1;

            if (TextUtils.isEmpty(nama)) {
                etNama.setError("Nama belum terisi");
            } else if (TextUtils.isEmpty(noTelp)) {
                etNoTelp.setError("No Telp belum terisi");
            } else if (TextUtils.isEmpty(noSTR)) {
                etNoSTR.setError("No STR belum terisi");
            } else if (TextUtils.isEmpty(noSIP)) {
                etNoSIP.setError("No SIP belum terisi");
            } else {

                addJpgSignatureToGallery(signatureBitmap, null, null);

            }

        }
        return super.onOptionsItemSelected(item);
    }

    private void createUser(final String email, final String password, final String deviceToken, final String status, final String ttdUrl, final String statusUser) {
        mAuthDokter = FirebaseAuth.getInstance();
        mAuthDokter.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    mAuthDokter.getCurrentUser().sendEmailVerification();
                    String mon = MONTHS[month1];
                    String dateCreated = day1 + " " + mon + " " + year1;
                    System.out.println("statusUser createUser = " + statusUser);
                    System.out.println("date created = " + dateCreated);
                    User user = new User(email, deviceToken, nama, status, statusUser, dateCreated);
                    mUserRef.child(mAuthDokter.getCurrentUser().getUid()).setValue(user);

                    createDokter(nama, namaSortir, noSTR, noSIP, noTelp, mAuthDokter.getCurrentUser().getUid(), tanggalDaftar, ttdUrl, statusUser);
                    mAuthDokter.signOut();
                    mAuthAdm.signInWithEmailAndPassword(util.getEmail(getApplicationContext()), util.getPassword(getApplicationContext())).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
//                            startActivity(new Intent(InputDokterActivity.this, DaftarDokterActivity.class));
                                Toast.makeText(InputDokterActivity.this, "Data Dokter Berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                                finish();
                                progressDialog.dismiss();

                            }

                        }
                    });
                }

                else {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(InputDokterActivity.this, "Email Sudah Terdaftar !", Toast.LENGTH_SHORT).show();
                        etEmail.requestFocus();
                        etEmail.setError("Email Sudah Terdaftar !");
                        progressDialog.dismiss();
                    }
                }


            }
        });


    }


    private void createDokter(String nama, String namaSortir, String noSTR, String noSIP, String notelp, String userID, String tanggalDaftar, String ttdUrl, String statusUser) {
        System.out.println("USER ID CREATE DOKTER = " + userID);
        System.out.println("nama sortir = " + namaSortir);
        Dokter dokter = new Dokter(nama, namaSortir, noSTR, noSIP, notelp, userID, tanggalDaftar, statusUser, ttdUrl, null);
        mDokterRef.push().setValue(dokter).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {


            }
        });
    }

    private void updateDokter(final String nama, String noSTR, String noSIP, String notelp, String ttdUrl, final String userId) {

        mDokterRef.child(idDokter).child("nama").setValue(nama);
        mDokterRef.child(idDokter).child("namaSortir").setValue(nama.toLowerCase());
        mDokterRef.child(idDokter).child("noSTR").setValue(noSTR);
        mDokterRef.child(idDokter).child("noSIP").setValue(noSIP);
        mDokterRef.child(idDokter).child("ttdUrl").setValue(ttdUrl);
        mDokterRef.child(idDokter).child("noTelp").setValue(notelp).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mUserRef.child(userId).child("fullname").setValue(nama);
                finish();
                progressDialog.dismiss();
            }
        });


    }


    public void filterAlpabetic(EditText editText) {
        editText.setFilters(new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence cs, int start,
                                               int end, Spanned spanned, int dStart, int dEnd) {
                        // TODO Auto-generated method stub
                        if (cs.equals("")) { // for backspace
                            return cs;
                        }
                        if (cs.toString().matches("[a-zA-Z ]+")) {
                            return cs;
                        }
                        return "";
                    }
                }
        });

    }


    public void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, CAMERA_PIC_REQUEST);
        }
    }


    public void openGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);

        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureDirectoryPath = pictureDirectory.getPath();

        Uri data = Uri.parse(pictureDirectoryPath);
        photoPickerIntent.setDataAndType(data, "image/*");
        startActivityForResult(photoPickerIntent, GALERY_INTENT);

    }


    private void uploadFoto() {

        String path = "perawatan/" + UUID.randomUUID() + ".jpg";
        StorageReference jadwalRef = storage.getReference(path);

        UploadTask uploadTask = jadwalRef.putFile(imageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(InputDokterActivity.this, "Upload Gagal", Toast.LENGTH_LONG).show();

            }
        }).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(InputDokterActivity.this, "Upload Success", Toast.LENGTH_LONG).show();
//                progressBar.setVisibility(View.GONE);
                @SuppressWarnings("VisibleForTests") Uri url = taskSnapshot.getDownloadUrl();
                photoUrl = url.toString();
                System.out.println("INI URL PHOTO!!!!" + photoUrl);

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GALERY_INTENT && resultCode == RESULT_OK) {

            //kalau foto berhasil masuk
            //alamat gambar di memori
            imageUri = data.getData();
            System.out.println("INI URI IMAGE: " + imageUri);
            mCurrentPhotoPath = imageUri.getPath();
            System.out.println("Path:" + mCurrentPhotoPath);
            //setPic();
            //memanggil stream untuk membaca data dari memori
            InputStream inputStream;


            //dapet input dari stream dari URI
            try {

                inputStream = this.getContentResolver().openInputStream(imageUri);
                //ambil bitmap dari stream
                Bitmap image = BitmapFactory.decodeStream(inputStream);
                System.out.println("INI URI IMAGE: " + imageUri);
                ivDokter.setImageBitmap(image);
                ivDokter.setVisibility(View.VISIBLE);
                System.out.println("UPLOADDD");
                imageUri = getImageUri(getApplicationContext(), image);
                File finalFile = new File(getRealPathFromUri(imageUri));
                uploadFoto();


            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Gambar gagal di buka", Toast.LENGTH_LONG).show();
            }


        } else if (requestCode == CAMERA_PIC_REQUEST && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();

            Bitmap photo = (Bitmap) extras.get("data");
            ivDokter.setImageBitmap(photo);
            ivDokter.setVisibility(View.VISIBLE);

            imageUri = getImageUri(getApplicationContext(), photo);
            File finalFile = new File(getRealPathFromUri(imageUri));
            System.out.println("TEMP URI =" + imageUri);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //COMPRESS FOTO
    public Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 80, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        System.out.println("Path =" + path);
        return Uri.parse(path);
    }

    public String getRealPathFromUri(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    public void attachPhoto() {
        final AlertDialog.Builder alBuilder = new AlertDialog.Builder(InputDokterActivity.this);
        LayoutInflater inflater = LayoutInflater.from(InputDokterActivity.this);
        final View dialog = (View) inflater.inflate(R.layout.dialog_foto, null);
        final ListView lv = (ListView) dialog.findViewById(R.id.ListItem);

        final CustomAdapter adapter = new CustomAdapter(InputDokterActivity.this, R.layout.row_list_pilihan, choice);


        alBuilder.setView(dialog);
        alBuilder.create();
        lv.setAdapter(adapter);
        final AlertDialog alertDialog = alBuilder.show();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                alertDialog.dismiss();
                pilihan = choice.get(position);

                if (pilihan.getSingkatan().equalsIgnoreCase("gallery")) {
                    openGallery();
                } else if (pilihan.getSingkatan().equalsIgnoreCase("camera")) {
                    openCamera();
                }
            }
        });
    }

    private void fn_permission() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {


            if ((ActivityCompat.shouldShowRequestPermissionRationale(InputDokterActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE))) {
            } else {
                ActivityCompat.requestPermissions(InputDokterActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);
            }
        } else {
            boolean_permission = true;


        }
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


    protected void onStart() {
        super.onStart();



    }

}
