package com.teddybrothers.co_teddy.dentist;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.teddybrothers.co_teddy.dentist.entity.PerawatanLain;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

public class PerawatanLainActivity extends AppCompatActivity {

    ImageView ivPhotoUpload;
    EditText etRegio, etDiagnosis, etPerawatan, etObat, etKeluhan;
    private FirebaseStorage storage = com.google.firebase.storage.FirebaseStorage.getInstance();

    private static final int GALERY_INTENT = 1;
    Uri imageUri;
    String photoUrl = "null";
    ProgressDialog progressDialog;
    ProgressBar pbFoto;
    FirebaseDatabase mDatabase;
    FirebaseAuth mAuth;
    DatabaseReference mRoot, mUserRef, mPasien, mRekamMedis;
    String idPasien;
    public String mCurrentPhotoPath, urlPhoto;
    static final int CAMERA_PIC_REQUEST = 3;
    ArrayList<CustomObject> choice = new ArrayList<CustomObject>();
    CustomObject pilihan;
    Utilities util = new Utilities();
    String statusUser,foto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perawatan_lain);

        pbFoto = (ProgressBar) findViewById(R.id.pbFoto);
        ivPhotoUpload = (ImageView) findViewById(R.id.ivPhotoUpload);
        etRegio = (EditText) findViewById(R.id.etRegio);
        etDiagnosis = (EditText) findViewById(R.id.etDiagnosis);
        etPerawatan = (EditText) findViewById(R.id.etPerawatan);
        etObat = (EditText) findViewById(R.id.etObat);
        etKeluhan = (EditText) findViewById(R.id.etKeluhan);


        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
        }
        mRoot = mDatabase.getReference();
        mUserRef = mRoot.child("users");
        mPasien = mRoot.child("pasien");
        mRekamMedis = mRoot.child("rekammedis");
        mAuth = FirebaseAuth.getInstance();
        idPasien = util.getIdPasien(PerawatanLainActivity.this);
        statusUser = util.getStatus(PerawatanLainActivity.this);

        mRekamMedis.child(idPasien).child("perawatanLain").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String regio = dataSnapshot.child("regio").getValue(String.class);
                String diagnosis = dataSnapshot.child("diagnosis").getValue(String.class);
                String perawatan = dataSnapshot.child("perawatan").getValue(String.class);
                String obat = dataSnapshot.child("obat").getValue(String.class);
                String keluhanLain = dataSnapshot.child("keluhanLain").getValue(String.class);
                foto = dataSnapshot.child("foto").getValue(String.class);

                etRegio.setText(regio);
                etDiagnosis.setText(diagnosis);
                etPerawatan.setText(perawatan);
                etObat.setText(obat);
                etKeluhan.setText(keluhanLain);

                if (foto != null) {

                    Glide.with(PerawatanLainActivity.this).load(foto)
                            .thumbnail(0.5f)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(ivPhotoUpload);

                    pbFoto.setVisibility(View.GONE);
                } else {
                    pbFoto.setVisibility(View.GONE);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        ivPhotoUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PerawatanLainActivity.this, ZoomActivity.class);
                intent.putExtra("photoUrl", foto);
                startActivity(intent);

            }
        });

        if (statusUser.equalsIgnoreCase("Pasien")||statusUser.equalsIgnoreCase("Administrator"))
        {
            etKeluhan.setEnabled(false);
            etRegio.setEnabled(false);
            etObat.setEnabled(false);
            etPerawatan.setEnabled(false);
            etDiagnosis.setEnabled(false);

        }


        CustomObject choice1 = new CustomObject(R.drawable.photocamera, "camera", "Camera", 0);
        choice.add(choice1);
        choice1 = new CustomObject(R.drawable.gallery, "gallery", "Gallery", 0);
        choice.add(choice1);





    }

    void saveState() {

        String regio = etRegio.getText().toString();
        String diagnosis = etDiagnosis.getText().toString();
        String perawatan = etPerawatan.getText().toString();
        String obat = etObat.getText().toString();
        String keluhanLain = etKeluhan.getText().toString();

        perawatanLainFoto(photoUrl, regio, diagnosis, perawatan, obat, keluhanLain);

    }

    public void perawatanLainFoto(String foto, String regio, String diagnosis, String perawatan, String obat, String keluhanLain) {
        PerawatanLain perawatanLain = new PerawatanLain(photoUrl, regio, diagnosis, perawatan, obat, keluhanLain);
        mRekamMedis.child(idPasien).child("perawatanLain").setValue(perawatanLain).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast.makeText(PerawatanLainActivity.this, "Perawatan Lain Berhasil Disimpan", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void perawatanLain(String regio, String diagnosis, String perawatan, String obat, String keluhanLain) {
        PerawatanLain perawatanLain = new PerawatanLain(null, regio, diagnosis, perawatan, obat, keluhanLain);
        mRekamMedis.child(idPasien).child("perawatanLain").setValue(perawatanLain).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast.makeText(PerawatanLainActivity.this, "Perawatan Lain Berhasil Disimpan", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void uploadFoto() {

        String path = "perawatanLain/" + UUID.randomUUID() + ".jpg";
        StorageReference eventref = storage.getReference(path);

        UploadTask uploadTask = eventref.putFile(imageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PerawatanLainActivity.this, "Upload Gagal", Toast.LENGTH_LONG).show();

            }
        }).addOnSuccessListener(PerawatanLainActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                Toast.makeText(PerawatanLainActivity.this, "Upload Success",Toast.LENGTH_LONG).show();

                @SuppressWarnings("VisibleForTests") Uri url = taskSnapshot.getDownloadUrl();
                photoUrl = url.toString();
                System.out.println("INI URL PHOTO!!!!" + photoUrl);
                saveState();
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


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_perawatan_lain, menu);
        MenuItem save = menu.findItem(R.id.save);
        MenuItem addphoto = menu.findItem(R.id.addphoto);
        MenuItem pdf = menu.findItem(R.id.pdf).setVisible(false);

        if (statusUser.equalsIgnoreCase("Pasien")||statusUser.equalsIgnoreCase("Administrator"))
        {
            save.setVisible(false);
            addphoto.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.save) {
            progressDialog = new ProgressDialog(PerawatanLainActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();

            String regio = etRegio.getText().toString();
            String diagnosis = etDiagnosis.getText().toString();
            String perawatan = etPerawatan.getText().toString();
            String obat = etObat.getText().toString();
            String keluhanLain = etKeluhan.getText().toString();
            System.out.println("imageUri = " + imageUri);
            if (imageUri != null) {
                uploadFoto();
                System.out.println("imageUri Tidak Null = " + imageUri);
            } else {
                perawatanLain(regio, diagnosis, perawatan, obat, keluhanLain);
            }

        }else if (id==R.id.addphoto)
        {
            if (statusUser.equalsIgnoreCase("Dokter"))
            {

                        final AlertDialog.Builder alBuilder = new AlertDialog.Builder(PerawatanLainActivity.this);
                        LayoutInflater inflater = LayoutInflater.from(PerawatanLainActivity.this);
                        final View dialog = (View) inflater.inflate(R.layout.dialog_foto, null);
                        final ListView lv = (ListView) dialog.findViewById(R.id.ListItem);

                        final CustomAdapter adapter = new CustomAdapter(PerawatanLainActivity.this, R.layout.row_list_pilihan, choice);

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
        }
        else if (id==android.R.id.home)
        {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                inputStream = getContentResolver().openInputStream(imageUri);

                //ambil bitmap dari stream
                Bitmap image = BitmapFactory.decodeStream(inputStream);
                System.out.println("INI URI IMAGE: " + imageUri);
                imageUri = getImageUri(getApplicationContext(), image);
                File finalFile = new File(getRealPathFromUri(imageUri));
                ivPhotoUpload.setImageBitmap(image);
                System.out.println("UPLOADDD");


            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(PerawatanLainActivity.this, "Gambar gagal di buka", Toast.LENGTH_LONG).show();
            }

        } else if (requestCode == CAMERA_PIC_REQUEST && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap photo = (Bitmap) extras.get("data");
            ivPhotoUpload.setImageBitmap(photo);
            imageUri = getImageUri(getApplicationContext(), photo);
            File finalFile = new File(getRealPathFromUri(imageUri));
            System.out.println("TEMP URI =" + imageUri);
            uploadFoto();
        }


    }

    public Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
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
}
