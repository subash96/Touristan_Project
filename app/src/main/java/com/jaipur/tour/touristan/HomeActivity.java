package com.jaipur.tour.touristan;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }
    public  void track(View v)
    {

        Intent i=new Intent(HomeActivity.this,MapsActivity.class);
        startActivity(i);

    }
}
