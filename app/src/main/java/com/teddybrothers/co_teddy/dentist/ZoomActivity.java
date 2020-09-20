package com.teddybrothers.co_teddy.dentist;


import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.util.FloatMath;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
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
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

public class ZoomActivity extends AppCompatActivity  {
    PhotoView ivZoom;
    public Bitmap bimage;
    public String photoUrl, photoUrl2, perawatanKey, photoKey, title;
    ProgressBar progressBar;
    DatabaseReference rootRef, perawatanRef, rekammedisRef, mPhoto;
    private static final int GALERY_INTENT = 2;
    static final int CAMERA_PIC_REQUEST = 3;
    ArrayList<CustomObject> choice = new ArrayList<CustomObject>();
    CustomObject pilihan;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    Uri imageUri;
    ScaleGestureDetector scaleGestureDetector;
    float scale = 1f;
    static final String TAG = "ZoomActivity.class";




    public String mCurrentPhotoPath, tanggal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom);





//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        ActionBar bar = getActionBar();
//        bar.setBackgroundDrawable(new ColorDrawable("#FFFFFF"));

        CustomObject choice1 = new CustomObject(R.drawable.photocamera, "camera", "Camera", 0);
        choice.add(choice1);
        choice1 = new CustomObject(R.drawable.gallery, "gallery", "Gallery", 0);
        choice.add(choice1);

        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#000000")));

        ivZoom = (PhotoView) findViewById(R.id.ivZoom);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            photoUrl = extras.getString("photoUrl");
            photoUrl2 = extras.getString("photoUrl2");
            photoKey = extras.getString("photoKey");
            title = extras.getString("title");
            perawatanKey = extras.getString("perawatanKey");
            System.out.println("PHOTO ZOOM = " + photoUrl);
        }
        if (photoUrl2 != null) {

            Glide.with(ZoomActivity.this).load(photoUrl2)
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivZoom);
            progressBar.setVisibility(View.GONE);
        }
        else
        {
            progressBar.setVisibility(View.GONE);
        }
        if (photoUrl != null) {

            Glide.with(ZoomActivity.this).load(photoUrl)
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivZoom);
            progressBar.setVisibility(View.GONE);

        }
        else
        {
            progressBar.setVisibility(View.GONE);
        }

        getSupportActionBar().setTitle(title);

        ivZoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportActionBar().show();
            }
        });

    }






    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_foto, menu);
        MenuItem delete = menu.findItem(R.id.delete);
        MenuItem edit = menu.findItem(R.id.edit);
        delete.setVisible(false);
        edit.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.delete) {

            AlertDialog.Builder alBuilder = new AlertDialog.Builder(this);
            alBuilder.setMessage("Apakah Anda yakin untuk menghapus foto?");
            alBuilder.setTitle("Konfirmasi");
            final TextView input = new TextView(this);
            alBuilder.setView(input);
            alBuilder.setCancelable(true).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    deletePhoto();
                    finish();

                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            AlertDialog alertDialog = alBuilder.create();
            alertDialog.show();

        } else if (id == R.id.download) {

            download();

        } else if (id == R.id.edit) {
            final AlertDialog.Builder alBuilder = new AlertDialog.Builder(this);
            LayoutInflater inflater = LayoutInflater.from(this);
            final View dialog = (View) inflater.inflate(R.layout.dialog_foto, null);
            final ListView lv = (ListView) dialog.findViewById(R.id.ListItem);

            final CustomAdapter adapter = new CustomAdapter(this, R.layout.row_list_pilihan, choice);


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
            return false;
        }
        else if (id==android.R.id.home)
        {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    public void deletePhoto() {

        FirebaseDatabase database;

        database = FirebaseDatabase.getInstance();
        rootRef = database.getReference();

        mPhoto = rootRef.child("photo");
        System.out.println("Post:" + mPhoto);
        mPhoto.child(perawatanKey).child(photoKey).removeValue();


    }

    private void download() {
        Toast.makeText(ZoomActivity.this, "Starting Download", Toast.LENGTH_SHORT).show();
        DownloadManager mManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request mRqRequest = null;
        if (photoUrl!=null)
        {
             mRqRequest = new DownloadManager.Request(Uri.parse(photoUrl));
        }
        else if (photoUrl2!=null)
        {
            mRqRequest = new DownloadManager.Request(Uri.parse(photoUrl2));
        }
      
        mRqRequest.setDescription("Download is starting . . .");
        mRqRequest.setDestinationInExternalPublicDir("/Dental", String.valueOf(UUID.randomUUID()) + ".jpg");

        long idDownLoad = mManager.enqueue(mRqRequest);
        Toast.makeText(ZoomActivity.this, "Download Complete ", Toast.LENGTH_LONG).show();

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
                ivZoom.setImageBitmap(image);
                ivZoom.setVisibility(View.VISIBLE);
                System.out.println("UPLOADDD");
                imageUri = getImageUri(getApplicationContext(), image);
                File finalFile = new File(getRealPathFromUri(imageUri));


            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Gambar gagal di buka", Toast.LENGTH_LONG).show();
            }


        } else if (requestCode == CAMERA_PIC_REQUEST && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();

            Bitmap photo = (Bitmap) extras.get("data");
            ivZoom.setImageBitmap(photo);
            ivZoom.setVisibility(View.VISIBLE);

            imageUri = getImageUri(getApplicationContext(), photo);
            File finalFile = new File(getRealPathFromUri(imageUri));
            System.out.println("TEMP URI =" + imageUri);
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






}
