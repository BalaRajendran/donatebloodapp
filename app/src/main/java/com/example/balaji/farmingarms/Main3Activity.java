package com.example.balaji.farmingarms;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Main3Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main3);
        startActivity(new Intent(Main3Activity.this,MainActivity.class));
        // close splash activity
        finish();
    }
}
