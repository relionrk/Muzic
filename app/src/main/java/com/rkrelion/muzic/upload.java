package com.rkrelion.muzic;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.rkrelion.muzic.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class upload extends AppCompatActivity {

    Button selectPic , uploadPic , createAcc ;
    ImageView profilePic ;
    ProgressBar progressBar ;
    boolean uploaded = false ;

    private static final int PICK_IMAGE_REQUEST = 1 ;
    private Uri imageUri ;
    private String userId ;

    private StorageReference storageReference ;
    private FirebaseFirestore fStore ;
//    private DatabaseReference databaseReference ;
    FirebaseAuth fAuth ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        getSupportActionBar().setTitle("Upload Pic");

        selectPic = findViewById(R.id.choose) ;
        uploadPic = findViewById(R.id.uploadPic) ;
        profilePic = findViewById(R.id.userImg) ;
        progressBar = findViewById(R.id.progressUpload) ;
        createAcc = findViewById(R.id.createAcc) ;

        storageReference = FirebaseStorage.getInstance().getReference("uploads") ;
        fStore = FirebaseFirestore.getInstance();
//        databaseReference = FirebaseDatabase.getInstance().getReference("uploads") ;
        fAuth = FirebaseAuth.getInstance() ;

        userId = fAuth.getCurrentUser().getUid() ;

        selectPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser() ;
            }
        });

        uploadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile() ;
            }
        });

        createAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uploaded == false) {
                    Toast.makeText(upload.this, "You have to select and upload image before Proceeding", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(getApplicationContext(), myProfile.class));
                    finish();
                }
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent() ;
        intent.setType("image/*") ;
        intent.setAction(Intent.ACTION_GET_CONTENT) ;
        startActivityForResult(intent , PICK_IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData() ;
            DocumentReference documentReferenceUser = fStore.collection("users").document(userId);
            HashMap<String,Object> map = new HashMap<>() ;
            map.put("uri", imageUri.toString()) ;
            documentReferenceUser.update(map)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(upload.this, "Image Added", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Log.d("TAG" , "Failed") ;
                        }
                    }) ;
            Picasso.get().load(imageUri).into(profilePic);

        }
    }

//    private String getFileExtension(Uri uri) {
//        ContentResolver contentResolver = getContentResolver() ;
//        MimeTypeMap mime = MimeTypeMap.getSingleton() ;
//        return mime.getExtensionFromMimeType(contentResolver.getType(uri)) ;
//    }

    private void uploadFile() {
        if (imageUri != null) {
            StorageReference fileReference = storageReference.child(userId) ;

            fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler() ;
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setProgress(0);
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            } , 5000) ;

                            Toast.makeText(upload.this, "Image uploaded Successfully ", Toast.LENGTH_SHORT).show();
                            uploaded = true ;
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Toast.makeText(upload.this, "Error :" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull @NotNull UploadTask.TaskSnapshot snapshot) {
                            progressBar.setVisibility(View.VISIBLE);
                            double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount()) ;
                            progressBar.setProgress((int) progress);
                        }
                    }) ;
        } else {
            Toast.makeText(this, "No File Selected !", Toast.LENGTH_SHORT).show();
        }
    }
}