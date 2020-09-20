package com.teddybrothers.co_teddy.dentist;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailPasienActivity extends AppCompatActivity {

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    public static final String[] MONTHS = {"Januari", "Febuari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
    Uri imageUri;
    String photoUrl = "null";
    public String mCurrentPhotoPath;
    Button btnRekamMedis, btnProfilePasien;
    TextView tvJam, tvTanggal, tvNama, tvKeluhan, tvNamaDokter, tvIdPasien, tvStatus;
    CircleImageView ivProfil;
    public String idDokter, idPasien;
    FirebaseDatabase mDatabase;
    FirebaseAuth mAuth;
    DatabaseReference mRoot, mUserRef, mJadwalRef, mDokterRef, mPasienRef;
    ImageView ivLampiran, ivLampiran2;
    String jadwalKey;
    public String photo, fotoProfil, photo2;
    ProgressDialog progressDialog;
    ProgressBar progressBar, pbLampiran1, pbLampiran2;
    private static final int GALERY_INTENT = 2;
    static final int CAMERA_PIC_REQUEST = 3;
    ArrayList<CustomObject> choice = new ArrayList<CustomObject>();
    CustomObject pilihan;
    Utilities util = new Utilities();
    RelativeLayout rlLampiran1,rlLampiran2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pasien);
        System.out.println("ON CREATE OK");
        ivLampiran = (ImageView) findViewById(R.id.ivLampiran);
        ivLampiran2 = (ImageView) findViewById(R.id.ivLampiran2);
        btnRekamMedis = (Button) findViewById(R.id.btnRekamMedis);
        btnProfilePasien = (Button) findViewById(R.id.btnProfile);
        tvNamaDokter = (TextView) findViewById(R.id.tvNamaDokter);
//        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tvJam = (TextView) findViewById(R.id.tvJam);
        tvStatus = (TextView) findViewById(R.id.tvStatus);
        tvTanggal = (TextView) findViewById(R.id.tvTanggal);
        tvNama = (TextView) findViewById(R.id.tvNama);
        tvKeluhan = (TextView) findViewById(R.id.tvKeluhan);
        tvIdPasien = (TextView) findViewById(R.id.tvID);
        ivProfil = (CircleImageView) findViewById(R.id.ivProfile);
        pbLampiran1 = (ProgressBar) findViewById(R.id.pbLampiran1);
        pbLampiran2 = (ProgressBar) findViewById(R.id.pbLampiran2);
        rlLampiran1 = (RelativeLayout) findViewById(R.id.rlLampiran1);
        rlLampiran2 = (RelativeLayout) findViewById(R.id.rlLampiran2);

        CustomObject choice1 = new CustomObject(R.drawable.photocamera, "camera", "Camera", 0);
        choice.add(choice1);
        choice1 = new CustomObject(R.drawable.gallery, "gallery", "Gallery", 0);
        choice.add(choice1);


        idPasien = util.getIdPasien(DetailPasienActivity.this);
        idDokter = util.getIdDokter(DetailPasienActivity.this);
        jadwalKey = util.getIdJadwal(DetailPasienActivity.this);


        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
        }
        mRoot = mDatabase.getReference();
        mUserRef = mRoot.child("users");
        mPasienRef = mRoot.child("pasien");
        mJadwalRef = mRoot.child("jadwal");
        mDokterRef = mRoot.child("dokter");


        ivLampiran2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (photo2 != null) {
                    Intent intent = new Intent(DetailPasienActivity.this, ZoomActivity.class);
                    intent.putExtra("photoUrl2", photo2);
                    startActivity(intent);
                } else {
                    final AlertDialog.Builder alBuilder = new AlertDialog.Builder(DetailPasienActivity.this);
                    LayoutInflater inflater = LayoutInflater.from(DetailPasienActivity.this);
                    final View dialog = (View) inflater.inflate(R.layout.dialog_foto, null);
                    final ListView lv = (ListView) dialog.findViewById(R.id.ListItem);

                    final CustomAdapter adapter = new CustomAdapter(DetailPasienActivity.this, R.layout.row_list_pilihan, choice);


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
        });




        tvIdPasien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailPasienActivity.this, RekamMedisActivity.class);
                String status = "detail";
                intent.putExtra("status", status);
                startActivity(intent);
            }
        });


        btnRekamMedis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailPasienActivity.this, RekamMedisActivity.class);
                String status = "detail";
                intent.putExtra("status", status);
                startActivity(intent);
            }
        });

        btnProfilePasien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(DetailPasienActivity.this, ProfileActivity.class);


                mPasienRef.child(idPasien).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String userIDPasien = dataSnapshot.child("userID").getValue(String.class);
                        intent.putExtra("userID", userIDPasien);
                        intent.putExtra("statusIntent", "dariDetailJadwal");
                        intent.putExtra("idDataUser", idPasien);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });

        ivLampiran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailPasienActivity.this, ZoomActivity.class);
                intent.putExtra("photoUrl", photo);
                startActivity(intent);

            }
        });

        ivProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailPasienActivity.this, ZoomActivity.class);
                intent.putExtra("photoUrl", fotoProfil);
                startActivity(intent);

            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        System.out.println("ON START OK");
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
        }
        mRoot = mDatabase.getReference();
        mUserRef = mRoot.child("users");
        mPasienRef = mRoot.child("pasien");
        mJadwalRef = mRoot.child("jadwal");
        mDokterRef = mRoot.child("dokter");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            System.out.println(extras);
            jadwalKey = extras.getString("id_jadwal");
            idDokter = extras.getString("idDokter");
            idPasien = extras.getString("idPasien");
            System.out.println("jadwal_id = "+jadwalKey+" "+"idDokter = "+idDokter +" "+idPasien);
        }

        System.out.println("JadwalREf = " + mJadwalRef);
        tvIdPasien.setText(idPasien);
        mJadwalRef.child(jadwalKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                photo = dataSnapshot.child("foto").getValue(String.class);
                photo2 = dataSnapshot.child("foto2").getValue(String.class);
                if (photo != null) {

                    Glide.with(DetailPasienActivity.this).load(photo)
                            .thumbnail(0.5f)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(ivLampiran);
                    pbLampiran1.setVisibility(View.GONE);

                }
                else if (photo==null)
                {
                    pbLampiran1.setVisibility(View.GONE);
                    rlLampiran1.setVisibility(View.GONE);
                }

                if (photo2 != null) {

                    Glide.with(DetailPasienActivity.this).load(photo2)
                            .thumbnail(0.5f)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(ivLampiran2);
                    pbLampiran2.setVisibility(View.GONE);

                } else if (photo2 == null) {
                    ivLampiran2.setBackgroundResource(R.drawable.morephoto);
                    pbLampiran2.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mAuth = FirebaseAuth.getInstance();


        mDokterRef.child(idDokter).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String namaDokter = dataSnapshot.child("nama").getValue(String.class);
                tvNamaDokter.setText("drg. " + namaDokter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mPasienRef.child(idPasien).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userId = dataSnapshot.child("userID").getValue(String.class);
                String namaPasien = dataSnapshot.child("nama").getValue(String.class);
                tvNama.setText(namaPasien);
                mUserRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        fotoProfil = dataSnapshot.child("photoUrl").getValue(String.class);
                        if (fotoProfil != null) {

                            Glide.with(DetailPasienActivity.this).load(fotoProfil)
                                    .thumbnail(0.5f)
                                    .crossFade()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(ivProfil);
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


        mJadwalRef.child(jadwalKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Long timeStamp = dataSnapshot.child("timeStamp").getValue(Long.class);
                String jam = getTime(timeStamp);
                String tanggal = getDate(timeStamp);

                String keluhan = dataSnapshot.child("keluhan").getValue(String.class);
                String status = dataSnapshot.child("status").getValue(String.class);
                if (status.equalsIgnoreCase("Batal"))
                {
                    btnRekamMedis.setVisibility(View.GONE);

                }

                tvJam.setText(jam);
                tvTanggal.setText(tanggal);
                tvKeluhan.setText(keluhan);
                tvStatus.setText(status);
                if (status.equalsIgnoreCase("Terkonfirmasi")) {
                    tvStatus.setTextColor(Color.BLUE);
                }else if (status.equalsIgnoreCase("Selesai")) {
                    tvStatus.setTextColor(Color.GREEN);
                }else if (status.equalsIgnoreCase("Batal")) {
                    tvStatus.setTextColor(Color.RED);
                }else if (status.equalsIgnoreCase("Jadwal Ulang")) {
                    tvStatus.setTextColor(Color.MAGENTA);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        System.out.println("ID = " + idDokter + " " + idPasien);
        progressDialog.dismiss();
        Glide.get(this).clearMemory();
    }


    public void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, CAMERA_PIC_REQUEST);
        }
    }

    private String getDate(long timeStamp){

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
            String tanggal = dayWeek+", "+day+" "+nameMonth+" "+year;
            return tanggal;
        }
        catch(Exception ex){
            System.out.println("Log = "+ex);
            return "xx";
        }
    }

    private String getTime(long timeStamp){

        try{
            Date d=new Date(new Date().getTime()+28800000);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Date netDate = (new Date(timeStamp));
            return sdf.format(netDate);
        }
        catch(Exception ex){
            return "xx";
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


    void saveState() {

        mJadwalRef.child(jadwalKey).child("foto2").setValue(photoUrl);


    }

    private void uploadFoto() {


        String path = "perawatan/" + UUID.randomUUID() + ".jpg";
        StorageReference jadwalRef = storage.getReference(path);

        UploadTask uploadTask = jadwalRef.putFile(imageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DetailPasienActivity.this, "Upload Gagal", Toast.LENGTH_LONG).show();

            }
        }).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(DetailPasienActivity.this, "Foto Berhasil di Upload", Toast.LENGTH_LONG).show();
//                progressBar.setVisibility(View.GONE);
                @SuppressWarnings("VisibleForTests") Uri url = taskSnapshot.getDownloadUrl();
                photoUrl = url.toString();
                System.out.println("INI URL PHOTO!!!!" + photoUrl);
                saveState();
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
                ivLampiran2.setImageBitmap(image);
                ivLampiran2.setVisibility(View.VISIBLE);
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
            ivLampiran2.setImageBitmap(photo);
            ivLampiran2.setVisibility(View.VISIBLE);

            imageUri = getImageUri(getApplicationContext(), photo);
            File finalFile = new File(getRealPathFromUri(imageUri));
            System.out.println("TEMP URI =" + imageUri);
            uploadFoto();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //COMPRESS FOTO
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

      if (id==android.R.id.home)
        {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
