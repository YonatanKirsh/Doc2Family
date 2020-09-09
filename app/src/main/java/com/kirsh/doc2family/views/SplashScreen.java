package com.kirsh.doc2family.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;


public class SplashScreen extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int counter = 0;
        while(counter < 100) {
            try {
                Thread.sleep(1000);
            }catch(InterruptedException e) {
                e.printStackTrace();
            }
            counter+=100;
        }

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }


}
