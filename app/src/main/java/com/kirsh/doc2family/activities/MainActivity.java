package com.kirsh.doc2family.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.kirsh.doc2family.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // check for user info
        //  if none: login activity
        //  if yes: list patients activity
    }
}
