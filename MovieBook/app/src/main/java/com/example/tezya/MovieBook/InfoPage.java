package com.example.tezya.MovieBook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class InfoPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_page);
    }

    public void ToBook(View view) {
        Intent intent = new Intent(this, BookingPage.class);
        startActivity(intent);
    }
}
