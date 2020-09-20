package com.teddybrothers.co_teddy.dentist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

public class SettingActivity extends AppCompatActivity {

    RelativeLayout rlDate,rlTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);


        rlDate = findViewById(R.id.rlDate);
        rlTime = findViewById(R.id.rlTime);

        rlDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingActivity.this,ListBlockDateActivity.class));
            }
        });

        rlTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingActivity.this,TimeSettingActivity.class));
            }
        });
    }
}
