package com.teddybrothers.co_teddy.dentist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import static android.widget.Toast.LENGTH_SHORT;

public class ListGigi extends AppCompatActivity {
    ListView listItem;
    CustomObject[] data;
    EditText etSearch;
    CustomAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_gigi);

        listItem = (ListView) findViewById(R.id.ListItem);


        etSearch = (EditText) findViewById(R.id.etSearch);

//        adapter = new CustomAdapter(ListGigi.this,R.layout.row_list_gigi,data);
//        listItem.setAdapter(adapter);
//        listItem.setTextFilterEnabled(true);
//        dataItem = new String[5];
//
//        for(int i=0; i<5; i++)
//        {
//            dataItem[i]="Ini Item ke-"+i;
//        }

//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,dataItem);
//        listItem.setAdapter(adapter);


        listItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //cara 1 ambil array jadi toast
                //String item = parent.getAdapter().getItem(position).toString();

                //cara 2
                //String item=dataItem[position];
                //Toast.makeText(MainActivity.this,item,Toast.LENGTH_LONG).show();

//                if(position%2==0)
//                {
//                    Toast.makeText(MainActivity.this,"genap",Toast.LENGTH_SHORT).show();
//                }
//                else
//                {
//                    Toast.makeText(MainActivity.this,"ganjil",Toast.LENGTH_SHORT).show();
//                }
                CustomObject menu = data[position];
                String nama = menu.getNama();

                //explisitIntent
//                int gambar = menu.getGambar();
//                Intent myIntent = new Intent(ListGigi.this,DetailActivity.class);
//                myIntent.putExtra("xJudul",judul);
//                myIntent.putExtra("xGambar",gambar);
//                startActivity(myIntent);
                Toast.makeText(ListGigi.this,nama,LENGTH_SHORT).show();



            }
        });


//        etSearch.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                adapter.getFilter().filter(s.toString());
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });


    }







}
