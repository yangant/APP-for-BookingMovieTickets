package com.example.tezya.MovieBook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void Register(View view) {
        Intent intent = new Intent(this, RegisterPage.class);
        startActivity(intent);
    }

    public void LogIn(View view) {
        Intent intent = new Intent(this, APPMainActivity.class);
        startActivity(intent);
    }
}
