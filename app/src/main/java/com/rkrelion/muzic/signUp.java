package com.rkrelion.muzic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.rkrelion.muzic.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class signUp extends AppCompatActivity {

    EditText name , email , pwd ;
    ProgressBar progressBar ;

    FirebaseAuth fAuth ;
    FirebaseFirestore fStore ;
    String userId ;

    String[] genres = {"Rock" , "Classical" , "Pop" , "Hip Hop" , "Jazz" , "Blues" , "Folk" , "Country" , "Heavy Metal" , "Electronic" , "Soulful" , "Disco" , "K-Pop" , "Rap"} ;
    private boolean[] checked;
    ArrayList<String> selectedGenres = new ArrayList<>() ;
    private int count = 0 ;
    String TAG = "signUp" ;

    Button select , create ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        getSupportActionBar().setTitle("Sign Up");

        fAuth = FirebaseAuth.getInstance() ;
        fStore = FirebaseFirestore.getInstance() ;

        name = findViewById(R.id.userFavSong) ;
        email = findViewById(R.id.email) ;
        pwd = findViewById(R.id.password) ;
        progressBar = findViewById(R.id.progressBar) ;




        select = findViewById(R.id.select);
        create = findViewById(R.id.create) ;
//        button = findViewById(R.id.button) ;


        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count = 0 ;
                checked = new boolean[genres.length] ;
                selectedGenres.clear();
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(signUp.this) ;
                mBuilder.setTitle("Genres") ;
                mBuilder.setMultiChoiceItems(genres, checked, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        count += isChecked ? 1 : -1 ;
                        checked[which] = isChecked ;

                        if (count > 3) {
                            Toast.makeText(signUp.this, "You can only select upto 3 Genres !", Toast.LENGTH_SHORT).show();
                            checked[which] = false ;
                            count-- ;
                            ((AlertDialog) dialog).getListView().setItemChecked(which, false);
                        }
                    }
                }) ;

                mBuilder.setCancelable(false) ;
                mBuilder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i=0 ; i<checked.length ; i++) {
                            if (checked[i]) {
                                selectedGenres.add(genres[i]) ;
                            }
                        }
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });

//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(signUp.this, "List : " + selectedGenres.get(0) +"," + selectedGenres.get(1) + "," + selectedGenres.get(2), Toast.LENGTH_SHORT).show();
//            }
//        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Name = name.getText().toString().trim() , Email = email.getText().toString().trim() , Pwd = pwd.getText().toString().trim() ;

                if (TextUtils.isEmpty(Name)) {
                    name.setError("Name is required!");
                    return ;
                }
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
                
                if (count < 3) {
                    Toast.makeText(signUp.this, "Select 3 genres to proceed", Toast.LENGTH_SHORT).show();
                    return ;
                }

                progressBar.setVisibility(View.VISIBLE);

                fAuth.createUserWithEmailAndPassword(Email , Pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(signUp.this, "Congratulations ! You are a member of our App .", Toast.LENGTH_SHORT).show();

                            userId = fAuth.getCurrentUser().getUid() ;

                            DocumentReference documentReferenceUser = fStore.collection("users").document(userId);
                            HashMap<String , Object> userInfo = new HashMap<>() ;
                            userInfo.put("Name" , Name) ;
                            userInfo.put("Email" , Email) ;
                            userInfo.put("Genres" , selectedGenres);
                            documentReferenceUser.set(userInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG , "User Profile is created for " + userId ) ;
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @org.jetbrains.annotations.NotNull Exception e) {
                                    Log.d(TAG , "Failure : " + e.toString()) ;
                                }
                            }) ;

                            DocumentReference favRef = fStore.collection("fav").document(userId) ;
                            HashMap<String , Object> favDemo = new HashMap<>() ;
                            favDemo.put("Demo" , "Demo") ;
                            favRef.set(favDemo)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d(TAG , "User Profile is created for " + userId ) ;
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @org.jetbrains.annotations.NotNull Exception e) {
                                    Log.d(TAG , "Failure : " + e.toString()) ;
                                }
                            }) ;



                            startActivity(new Intent(getApplicationContext() , upload.class));
                            finish() ;
                        } else {
                            Toast.makeText(signUp.this, "Error :" + task.getException(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
    }
}