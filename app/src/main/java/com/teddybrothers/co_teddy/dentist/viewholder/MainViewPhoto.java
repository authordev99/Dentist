package com.teddybrothers.co_teddy.dentist.viewholder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.teddybrothers.co_teddy.dentist.R;
import com.teddybrothers.co_teddy.dentist.ZoomActivity;
import com.teddybrothers.co_teddy.dentist.entity.Dokter;
import com.teddybrothers.co_teddy.dentist.entity.Perawatan;

import java.io.InputStream;


/**
 * Created by co_teddy on 4/17/2017.
 */

public class MainViewPhoto extends RecyclerView.ViewHolder {

    public ImageView ivPhotoPerawatan;
    Dokter displayedDokter = new Dokter();
    public String photoKey,photoUrl;


    public MainViewPhoto(View itemView) {
        super(itemView);
//        tvNoSTR = (TextView) itemView.findViewById(R.id.tvNoSTR);
        ivPhotoPerawatan = (ImageView) itemView.findViewById(R.id.photoPerawatan);

    }

    public void bindToPost(Perawatan perawatan, final String perawatanKey, final String photoKey, final Context context)
    {
        this.photoKey = photoKey;
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference mPhoto = database.getReference().child("photo").child(perawatanKey);
        mPhoto.child(photoKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                    photoUrl = dataSnapshot.child("url").getValue(String.class);
                    System.out.println("photo URL = "+photoUrl);
                    if (photoUrl!=null)
                    {

                        Glide.with(context).load(photoUrl)
                                .thumbnail(0.5f)
                                .crossFade()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(ivPhotoPerawatan);
                    }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(itemView.getContext(), ZoomActivity.class);
                intent.putExtra("photoUrl",photoUrl);
                intent.putExtra("perawatanKey",perawatanKey);
                intent.putExtra("photoKey",photoKey);
                intent.putExtra("title","Foto Perawatan");
                itemView.getContext().startActivity(intent);

            }
        });




    }

    private class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;



        public DownloadImageFromInternet(ImageView imageView) {
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String... urls) {
            String imageURL = urls[0];
            Bitmap bimage = null;
            try {
                InputStream in = new java.net.URL(imageURL).openStream();
                bimage = BitmapFactory.decodeStream(in);

            } catch (Exception e) {
                Log.e("Error Message", e.getMessage());
                e.printStackTrace();
            }

            return bimage;

        }


        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }

    }


}
