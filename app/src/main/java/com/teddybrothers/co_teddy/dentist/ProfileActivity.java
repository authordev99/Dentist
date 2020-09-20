package com.teddybrothers.co_teddy.dentist;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.teddybrothers.co_teddy.dentist.entity.Dokter;

import com.teddybrothers.co_teddy.dentist.entity.Pasien;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseStorage storage = FirebaseStorage.getInstance();

    Utilities util = new Utilities();
    CircleImageView civProfile;
    private static final int GALERY_INTENT = 1;
    Uri imageUri;

    String photoUrl = "null";
    ProgressDialog progressDialog;
    FirebaseDatabase mDatabase;
    FirebaseAuth mAuth;
    DatabaseReference mRoot, mUserRef, mPasien, mDokter, mJadwal, mRekamMedis, mPerawatan, mAdminRef;
    ImageView ivProfile, ivEdit,ttdDokter;
    public String mCurrentPhotoPath, urlPhoto, idDokter, idPasien, idDataUser, statusUser;
    TextView tvTtl, tvNoIdentitas, tvJenKel, tvSuku, tvPekerjaan, tvAlamat, tvTelepon,
            tvGolDar, tvTekDar, tvJantung, tvDiabetes, tvHaemopilia, tvHepatitis, tvGastring, tvPenyakitLain, tvAlergiObat, tvAlergiMakanan;
    TextView tvKonsul, tvTanggalDaftar, tvNoStr, tvNoSip, tvEmail, tvNoTelepon, tvEmailAdmin, tvJenkelAdmin, tvAlamatAdmin;
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    String userId, status, statusCurrentUser, statusIntent, namaUser;
    static final int CAMERA_PIC_REQUEST = 3;
    ArrayList<CustomObject> choice = new ArrayList<CustomObject>();
    CustomObject pilihan;
    CardView llDataPasien, llDataMedik, llDataDokter, llDataAdmin, llTtdDokter, llDataLain;
    ProgressBar progressBarPhoto, pbRVHistoryPerawatan;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ttdDokter = findViewById(R.id.ttdDokter);
        civProfile = findViewById(R.id.civProfile);
        pbRVHistoryPerawatan = findViewById(R.id.pbRVHistoryPerawatan);
        progressBarPhoto = findViewById(R.id.progressBarPhoto);
        ivEdit = findViewById(R.id.ivEdit);
        llDataDokter = findViewById(R.id.llDataDokter);
        llDataAdmin = findViewById(R.id.llDataAdmin);
        llTtdDokter = findViewById(R.id.llTtdDokter);
        tvKonsul = findViewById(R.id.tvKonsul);
        tvTanggalDaftar = findViewById(R.id.tvTanggalDaftar);
        tvNoStr = findViewById(R.id.tvNoStr);
        tvNoSip = findViewById(R.id.tvNoSip);
        tvEmail = findViewById(R.id.tvEmail);
        tvNoTelepon = findViewById(R.id.tvNoTelepon);
        tvTtl = findViewById(R.id.tvTTL);
        tvNoIdentitas = findViewById(R.id.tvNoIdentitas);
        tvJenKel = findViewById(R.id.tvJenKel);
        tvSuku = findViewById(R.id.tvSuku);
        tvPekerjaan = findViewById(R.id.tvPekerjaan);
        tvAlamat = findViewById(R.id.tvAlamat);
        tvTelepon = findViewById(R.id.tvTelepon);
        llDataPasien = findViewById(R.id.llDataPasien);
        llDataMedik = findViewById(R.id.llDataMedik);
        tvGolDar = findViewById(R.id.tvGolDar);
        tvTekDar = findViewById(R.id.tvTekDar);
        tvJantung = findViewById(R.id.tvJantung);
        tvDiabetes = findViewById(R.id.tvDiabetes);
        tvHaemopilia = findViewById(R.id.tvHaemopilia);
        tvHepatitis = findViewById(R.id.tvHepatitis);
        tvGastring = findViewById(R.id.tvGastring);
        tvPenyakitLain = findViewById(R.id.tvPenyakitLain);
        tvAlergiObat = findViewById(R.id.tvAlergiObat);
        tvAlergiMakanan = findViewById(R.id.tvAlergiMakanan);
        tvAlamatAdmin = findViewById(R.id.tvAlamatAdmin);
        tvEmailAdmin = findViewById(R.id.tvEmailAdmin);
        tvJenkelAdmin = findViewById(R.id.tvJenKelAdmin);


        CustomObject choice1 = new CustomObject(R.drawable.photocamera, "camera", "Camera", 0);
        choice.add(choice1);
        choice1 = new CustomObject(R.drawable.gallery, "gallery", "Gallery", 0);
        choice.add(choice1);

        progressDialog = new ProgressDialog(ProfileActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        System.out.println("ON START OK");
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
        }
        mRoot = mDatabase.getReference();
        mUserRef = mRoot.child("users");
        mPasien = mRoot.child("pasien");
        mDokter = mRoot.child("dokter");
        mJadwal = mRoot.child("jadwal");
        mRekamMedis = mRoot.child("rekammedis");
        mPerawatan = mRoot.child("perawatan");
        mAdminRef = mRoot.child("admin");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




        ivProfile = (ImageView) findViewById(R.id.ivProfile);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mCollapsingToolbarLayout.setTitleEnabled(true);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }
        getSupportActionBar().setSubtitle("Administrator");



        statusCurrentUser = util.getStatus(ProfileActivity.this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString("userID");
            idDataUser = extras.getString("idDataUser");
            statusIntent = extras.getString("statusIntent");
        }
//        else {
//            mAuth = FirebaseAuth.getInstance();
//            userId = mAuth.getCurrentUser().getUid();
//        }

        System.out.println("iddatauser = " + idDataUser);

        mUserRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                urlPhoto = dataSnapshot.child("photoUrl").getValue(String.class);
                status = dataSnapshot.child("status").getValue(String.class);
                System.out.println("status snapshot= " + status);
                statusUser = dataSnapshot.child("statusUser").getValue(String.class);

                if (urlPhoto != null) {

                    Glide.with(getApplicationContext()).load(urlPhoto)
                            .thumbnail(0.5f)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(civProfile);


                }

                String nama = dataSnapshot.child("fullname").getValue(String.class);
                String email = dataSnapshot.child("email").getValue(String.class);
                mCollapsingToolbarLayout.setTitle(nama);
                tvEmail.setText(email);



                System.out.println("Status = " + status);
                if (status.equalsIgnoreCase("Pasien")) {
                    System.out.println("MASUKKKK");
                    llDataDokter.setVisibility(View.GONE);


                    mPasien.child(idDataUser).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Pasien pasien = dataSnapshot.getValue(Pasien.class);
                            idPasien = dataSnapshot.getKey();
                            setPasien(pasien);


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                } else if (status.equalsIgnoreCase("Dokter")) {

                    llDataPasien.setVisibility(View.GONE);
                    llDataMedik.setVisibility(View.GONE);
                    llDataDokter.setVisibility(View.VISIBLE);
                    llTtdDokter.setVisibility(View.VISIBLE);


                    mDokter.child(idDataUser).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Dokter dokter = dataSnapshot.getValue(Dokter.class);
                            idDokter = dataSnapshot.getKey();
                            tvKonsul.setText("TANGGAL PENDAFTARAN");
                            tvTanggalDaftar.setText(dokter.getTanggalDaftar());
                            tvNoSip.setText(dokter.getNoSIP());
                            tvNoStr.setText(dokter.getNoSTR());
                            tvNoTelepon.setText(dokter.getNoTelp());
                            String urlTtdDokter = dokter.getTtdUrl();

                            if (urlTtdDokter != null) {

                                Glide.with(getApplicationContext()).load(urlTtdDokter)
                                        .thumbnail(0.5f)
                                        .crossFade()
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .into(ttdDokter);


                            }



                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    System.out.println("idDokter = " + idDokter);


                } else if (status.equalsIgnoreCase("Administrator")) {
                    llDataPasien.setVisibility(View.GONE);
                    llDataMedik.setVisibility(View.GONE);
                    llDataDokter.setVisibility(View.GONE);
                    llDataAdmin.setVisibility(View.VISIBLE);
                    tvKonsul.setText("TANGGAL PENDAFTARAN");
                    tvEmailAdmin.setText(email);

                    mAdminRef.child(userId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String jenKel = dataSnapshot.child("jenisKelamin").getValue(String.class);
                            String alamat = dataSnapshot.child("alamat").getValue(String.class);
                            String dateCreated = dataSnapshot.child("dateCreated").getValue(String.class);
                            if (jenKel.equalsIgnoreCase("0")) {
                                jenKel = "Laki-laki";
                            } else if (jenKel.equalsIgnoreCase("1")) {
                                jenKel = "Perempuan";
                            }
                            tvJenkelAdmin.setText(jenKel);
                            tvAlamatAdmin.setText(alamat);
                            tvTanggalDaftar.setText(dateCreated);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                progressDialog.dismiss();

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        civProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ZoomActivity.class);
                intent.putExtra("photoUrl", urlPhoto);
                intent.putExtra("title", "Foto Profil");
                startActivity(intent);

            }
        });


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alBuilder = new AlertDialog.Builder(ProfileActivity.this);
                LayoutInflater inflater = LayoutInflater.from(ProfileActivity.this);
                final View dialog = (View) inflater.inflate(R.layout.dialog_foto, null);
                final ListView lv = (ListView) dialog.findViewById(R.id.ListItem);

                final CustomAdapter adapter = new CustomAdapter(ProfileActivity.this, R.layout.row_list_pilihan, choice);


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
        });
    }

    public void setPasien(Pasien pasien) {
        tvTtl.setText(pasien.getTempatLahir() + ", " + pasien.getTanggalLahir());
        mUserRef.child(pasien.getUserID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nama = dataSnapshot.child("fullname").getValue(String.class);
                mCollapsingToolbarLayout.setTitle(nama+" | "+idDataUser);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        tvKonsul.setText("TANGGAL PENDAFTARAN");
        tvTanggalDaftar.setText(pasien.getTanggalCatat());

        tvNoIdentitas.setText(pasien.getNoIdentitas());
        if (pasien.getJenisKelamin().equalsIgnoreCase("0")) {
            tvJenKel.setText("Laki-laki");
        } else if (pasien.getJenisKelamin().equalsIgnoreCase("1")) {
            tvJenKel.setText("Perempuan");
        }

        tvSuku.setText(pasien.getSuku());
        tvPekerjaan.setText(pasien.getPekerjaan());
        tvAlamat.setText(pasien.getAlamat());
        tvTelepon.setText(pasien.getTeleponRumah());
        if (pasien.getGolonganDarah().equalsIgnoreCase("0")) {
            tvGolDar.setText("A");
        } else if (pasien.getGolonganDarah().equalsIgnoreCase("1")) {
            tvGolDar.setText("AB");
        } else if (pasien.getGolonganDarah().equalsIgnoreCase("2")) {
            tvGolDar.setText("B");
        } else if (pasien.getGolonganDarah().equalsIgnoreCase("3")) {
            tvGolDar.setText("O");
        }

        tvTekDar.setText(pasien.getTekananDarah());
        tvJantung.setText(cekStatus(pasien.getPenyakitJantung()) + " (" + pasien.getKetPenyakitJantung() + ")");
        tvDiabetes.setText(cekStatus(pasien.getDiabetes()) + " (" + pasien.getKetDiabetes() + ")");
        tvHaemopilia.setText(cekStatus(pasien.getHaemopilia()) + " (" + pasien.getKetHaemopilia() + ")");
        tvHepatitis.setText(cekStatus(pasien.getHepatitis()) + " (" + pasien.getKetHepatitis() + ")");
        tvGastring.setText(cekStatus(pasien.getGastring()) + " (" + pasien.getKetGastring() + ")");
        tvPenyakitLain.setText(cekStatus(pasien.getPenyakitLainnya()) + " (" + pasien.getKetPenyakitLainnnya() + ")");
        tvAlergiObat.setText(cekStatus(pasien.getAlergiObat()) + " (" + pasien.getKetAlergiObat() + ")");
        tvAlergiMakanan.setText(cekStatus(pasien.getAlergiMakanan()) + " (" + pasien.getKetAlergiMakanan() + ")");
        progressDialog.dismiss();
    }

    private String cekStatus(String status) {


        return (status.equalsIgnoreCase("1") ? "Ada" : "Tidak Ada");
    }

    void saveState() {

        mUserRef.child(userId).child("photoUrl").setValue(photoUrl);
        if (statusIntent.equalsIgnoreCase("dariListPasien")) {
            mPasien.child(idDataUser).child("photoUrl").setValue(photoUrl);
        } else if (statusIntent.equalsIgnoreCase("dariListDokter")) {
            mDokter.child(idDataUser).child("photoUrl").setValue(photoUrl);
        } else if (statusIntent.equalsIgnoreCase("dariListAdmin")) {
            mAdminRef.child(idDataUser).child("photoUrl").setValue(photoUrl);
        } else if (statusIntent.equalsIgnoreCase("dariMain")) {
            if (statusCurrentUser.equalsIgnoreCase("Pasien")) {
                mPasien.child(idDataUser).child("photoUrl").setValue(photoUrl);
            } else if (statusCurrentUser.equalsIgnoreCase("Dokter")) {
                mDokter.child(idDataUser).child("photoUrl").setValue(photoUrl);
            } else if (statusCurrentUser.equalsIgnoreCase("Administrator")) {
                mAdminRef.child(idDataUser).child("photoUrl").setValue(photoUrl);
            }
        }
        progressBarPhoto.setVisibility(View.GONE);
        progressDialog.dismiss();
        Toast.makeText(ProfileActivity.this, "Foto Profil Berhasil Diperbaharui", Toast.LENGTH_SHORT).show();


    }


    private void uploadFoto() {


//        progressDialog.setMessage("Loading...");
//        progressDialog.show();

        String path = "profil/" + UUID.randomUUID() + ".jpg";
        StorageReference eventref = storage.getReference(path);

        UploadTask uploadTask = eventref.putFile(imageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivity.this, "Upload Gagal", Toast.LENGTH_LONG).show();

            }
        }).addOnSuccessListener(ProfileActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                Toast.makeText(ProfileActivity.this, "Upload Success", Toast.LENGTH_LONG).show();

                @SuppressWarnings("VisibleForTests") Uri url = taskSnapshot.getDownloadUrl();
                photoUrl = url.toString();
                System.out.println("INI URL PHOTO!!!!" + photoUrl);
                progressBarPhoto.setVisibility(View.VISIBLE);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == GALERY_INTENT && resultCode == RESULT_OK) {
            //kalau foto berhasil masuk
            //alamat gambar di memori

            imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
            System.out.println("INI URI IMAGE: " + imageUri);
            mCurrentPhotoPath = imageUri.getPath();
            System.out.println("Path:" + mCurrentPhotoPath);

            //setPic();
            //memanggil stream untuk membaca data dari memori


            //dapet input dari stream dari URI


        } else if (requestCode == CAMERA_PIC_REQUEST && resultCode == RESULT_OK) {

            Bundle extras = data.getExtras();
            Bitmap photo = (Bitmap) extras.get("data");

            imageUri = getImageUri(getApplicationContext(), photo);
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(ProfileActivity.this);
            File finalFile = new File(getRealPathFromUri(imageUri));
            System.out.println("TEMP URI =" + imageUri);
            uploadFoto();
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            progressDialog.show();
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                System.out.println("INI URI IMAGE : " + imageUri);
                InputStream inputStream;
                try {
                    inputStream = getContentResolver().openInputStream(imageUri);

                    //ambil bitmap dari stream
                    Bitmap image = BitmapFactory.decodeStream(inputStream);
                    System.out.println("INI URI IMAGE: " + imageUri);
                    imageUri = getImageUri(getApplicationContext(), image);
                    File finalFile = new File(getRealPathFromUri(imageUri));
                    civProfile.setImageBitmap(image);
                    System.out.println("UPLOADDD");
                    uploadFoto();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(ProfileActivity.this, "Gambar gagal di buka", Toast.LENGTH_LONG).show();
                }


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
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

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profil, menu);
        final MenuItem edit = menu.findItem(R.id.edit);
        final MenuItem aktif = menu.findItem(R.id.aktif);
        final MenuItem nonAktif = menu.findItem(R.id.nonAktif);
        final MenuItem rekamMedis = menu.findItem(R.id.rekammedis);

        mUserRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String statususer = dataSnapshot.child("statusUser").getValue(String.class);

                if (statususer.equalsIgnoreCase("Aktif")) {
                    aktif.setVisible(false);
                } else if (statususer.equalsIgnoreCase("Non Aktif")) {
                    nonAktif.setVisible(false);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        if (statusCurrentUser.equalsIgnoreCase("Pasien")) {
            edit.setVisible(false);
            aktif.setVisible(false);
            nonAktif.setVisible(false);
            llTtdDokter.setVisibility(View.GONE);
            if(statusIntent.equalsIgnoreCase("dariListDokter"))
            {
                fab.setVisibility(View.INVISIBLE);
            }


        } else if (statusCurrentUser.equalsIgnoreCase("Dokter") || statusCurrentUser.equalsIgnoreCase("Administrator")) {
            if (statusIntent.equalsIgnoreCase("dariListPasien")) {
                rekamMedis.setVisible(true);
            }else if (statusCurrentUser.equalsIgnoreCase("Administrator")&&statusIntent.equalsIgnoreCase("dariListAdmin"))
            {
                edit.setVisible(false);
                aktif.setVisible(false);
                nonAktif.setVisible(false);
            }

        }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.edit) {

            Intent intent = null;
            if (statusIntent.equalsIgnoreCase("dariListPasien")) {
                intent = new Intent(ProfileActivity.this, DaftarPasienActivity.class);
                intent.putExtra("idPasien", idDataUser);
                intent.putExtra("ubah", "ubah");
                intent.putExtra("userIDPasien", userId);
            } else if (statusIntent.equalsIgnoreCase("dariListDokter")) {
                intent = new Intent(ProfileActivity.this, InputDokterActivity.class);
                intent.putExtra("idDokter", idDataUser);
                intent.putExtra("userIDDokter", userId);
            } else if (statusIntent.equalsIgnoreCase("dariListAdmin")) {
                intent = new Intent(ProfileActivity.this, RegisterAdminActivity.class);
                intent.putExtra("idAdmin", idDataUser);
                intent.putExtra("statusIntent", "UPDATE");
            } else if (statusIntent.equalsIgnoreCase("dariMain")) {
                if (statusCurrentUser.equalsIgnoreCase("Pasien")) {
                    intent = new Intent(ProfileActivity.this, DaftarPasienActivity.class);
                    intent.putExtra("idPasien", idDataUser);
                    intent.putExtra("ubah", "ubah");
                    intent.putExtra("userIDPasien", userId);
                } else if (statusCurrentUser.equalsIgnoreCase("Dokter")) {
                    intent = new Intent(ProfileActivity.this, InputDokterActivity.class);
                    System.out.println("iddatauser = " + idDataUser);
                    intent.putExtra("idDokter", idDataUser);
                    intent.putExtra("userIDDokter", userId);
                } else if (statusCurrentUser.equalsIgnoreCase("Administrator")) {
                    intent = new Intent(ProfileActivity.this, RegisterAdminActivity.class);
                    intent.putExtra("idAdmin", idDataUser);
                    intent.putExtra("statusIntent", "UPDATE");
                }
            }else if (statusIntent.equalsIgnoreCase("dariDetailJadwal"))
            {
                intent = new Intent(ProfileActivity.this, DaftarPasienActivity.class);
                intent.putExtra("idPasien", idDataUser);
                intent.putExtra("ubah", "ubah");
                intent.putExtra("userIDPasien", userId);
            }


            startActivity(intent);

        } else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.nonAktif) {
            mUserRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String namaUser = dataSnapshot.child("fullname").getValue(String.class);
                    ubahStatusAktif("Non Aktif", idDataUser, namaUser, userId);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        } else if (id == R.id.aktif) {

            mUserRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String namaUser = dataSnapshot.child("fullname").getValue(String.class);
                    ubahStatusAktif("Aktif", idDataUser, namaUser, userId);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        else if (id==R.id.rekammedis)
        {
            final Intent intent = new Intent(ProfileActivity.this, RekamMedisActivity.class);
            intent.putExtra("idPasien", idDataUser);
            String statusProfil = "profil";
            intent.putExtra("status", statusProfil);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void ubahStatusAktif(final String statusUser, final String idDataUser, final String nama, final String userID) {
        AlertDialog.Builder alBuilder = new AlertDialog.Builder(this);
        alBuilder.setMessage("Apakah Anda yakin untuk men-" + statusUser + "kan Akun " + nama + "?");
        alBuilder.setTitle("Konfirmasi");
        alBuilder.setCancelable(true).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = alBuilder.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseDatabase database;
                DatabaseReference rootRef, pasienRef, userRef, dokterRef, adminRef;
                database = FirebaseDatabase.getInstance();
                rootRef = database.getReference();
                adminRef = rootRef.child("admin");
                dokterRef = rootRef.child("dokter");
                pasienRef = rootRef.child("pasien");
                userRef = rootRef.child("users");

                if (statusIntent.equalsIgnoreCase("dariListPasien")) {
                    pasienRef.child(idDataUser).child("statusUser").setValue(statusUser);
                } else if (statusIntent.equalsIgnoreCase("dariListDokter")) {
                    dokterRef.child(idDataUser).child("statusUser").setValue(statusUser);
                } else if (statusIntent.equalsIgnoreCase("dariListAdmin")) {
                    adminRef.child(idDataUser).child("statusUser").setValue(statusUser);
                }

                userRef.child(userID).child("statusUser").setValue(statusUser);
                Toast.makeText(ProfileActivity.this, "Akun " + nama + " telah di " + statusUser, Toast.LENGTH_SHORT).show();
                finish();


            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        Glide.get(this).clearMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Glide.get(this).clearMemory();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Glide.get(this).clearMemory();
    }
}
