package com.example.tezya.MovieBook;

import android.content.Intent;
import android.icu.text.IDNA;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class APPMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appmain);
    }

    public void ToInfo(View view) {
        Intent intent = new Intent(this, InfoPage.class);
        startActivity(intent);
    }
}
