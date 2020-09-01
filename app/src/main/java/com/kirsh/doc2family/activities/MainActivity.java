package com.kirsh.doc2family.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kirsh.doc2family.R;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        openActivityLogin();

        //testForTheFirebase todo to remove
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference document = firestore.collection("projectTest").document();
        Map<String, Object> user = new HashMap<>();
        user.put("second", "maayane");
        document.set(user);
        // check for user info
        //  if none: login activity
        //  if yes: list patients activity
    }

    private void openActivityLogin(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
