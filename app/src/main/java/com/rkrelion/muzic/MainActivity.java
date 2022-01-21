package com.rkrelion.muzic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.rkrelion.muzic.R;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private LinearLayout signIn , signUp ;

    FirebaseAuth fAuth ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fAuth = FirebaseAuth.getInstance() ;

        if (fAuth != null &&  fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext() , myProfile.class));
            finish();
        }

        signIn = findViewById(R.id.signIn) ;
        signUp = findViewById(R.id.signUp) ;

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext() , signUp.class));
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext() , signIn.class));
            }
        });
    }
}