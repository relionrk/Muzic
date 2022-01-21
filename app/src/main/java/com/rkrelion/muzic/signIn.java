package com.rkrelion.muzic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rkrelion.muzic.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class signIn extends AppCompatActivity {

    EditText email , pwd ;
    Button login ;
    ProgressBar progressBar ;

    TextView backTo ;

    FirebaseAuth fAuth ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        getSupportActionBar().setTitle("Sign In");

        email = findViewById(R.id.emailsi) ;
        pwd = findViewById(R.id.pwdsi) ;
        login = findViewById(R.id.signinsi) ;
        progressBar = findViewById(R.id.progressBar2) ;
        backTo = findViewById(R.id.backTo) ;



        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = email.getText().toString().trim() , Pwd = pwd.getText().toString().trim() ;


                if (TextUtils.isEmpty(Email)) {
                    email.setError("Email is required!");
                    return ;
                }
                if (TextUtils.isEmpty(Pwd)) {
                    pwd.setError("Password is required!");
                    return ;
                }

                if (Pwd.length() < 6) {
                    pwd.setError("Password must be of >= 6 characters!");
                    return ;
                }

                progressBar.setVisibility(View.VISIBLE);

                fAuth = FirebaseAuth.getInstance() ;
                fAuth.signInWithEmailAndPassword(Email , Pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(signIn.this, "Logged In Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext() , myProfile.class) ;
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK) ;
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) ;
                            startActivity(intent);
                        } else {
                            Toast.makeText(signIn.this, "Error :" + task.getException(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }

        });

        backTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext() , signUp.class));
                finish();
            }
        });
    }
}