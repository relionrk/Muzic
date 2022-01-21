package com.rkrelion.muzic;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rkrelion.muzic.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class myProfile extends AppCompatActivity {

    TextView name , email , genre_One , genre_Two , genre_Three ;
    public TextView lp , mp ;
    Button logOut ;
    String userId ;
    ImageView Dp , g1 , g2 , g3 ;
    Uri uri ;

    FirebaseAuth fAuth ;
    FirebaseFirestore fStore ;
    FirebaseStorage fStorage ;

    MenuInflater inflater ;

   // {"Rock" , "Classical" , "Pop" , "Hip Hop" , "Jazz" , "Blues" , "Folk" , "Country" , "Heavy Metal" , "Electronic" , "Soulful" , "Disco" , "K-Pop" , "Rap"}
   HashMap<String , Integer> map = new HashMap<String, Integer>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        getSupportActionBar().setTitle("My Profile");

        map.put("Rock" , R.drawable.rockmusic) ;
        map.put("Classical" , R.drawable.classical) ;
        map.put("Pop" , R.drawable.pop) ;
        map.put("Hip Hop" , R.drawable.hip_hop) ;
        map.put("Jazz" , R.drawable.jazz) ;
        map.put("Blues" , R.drawable.blues) ;
        map.put("Folk" , R.drawable.folk) ;
        map.put("Country" , R.drawable.country) ;
        map.put("Heavy Metal" , R.drawable.heavy_metal) ;
        map.put("Electronic" , R.drawable.electronic) ;
        map.put("Soulful" , R.drawable.soul) ;
        map.put("Disco" , R.drawable.disco) ;
        map.put("K-Pop" , R.drawable.kpop) ;
        map.put("Rap" , R.drawable.rap) ;

        name = findViewById(R.id.profileName) ;
        email = findViewById(R.id.profileEmail) ;
        genre_One = findViewById(R.id.genre_One) ;
        genre_Two = findViewById(R.id.genre_Two) ;
        genre_Three = findViewById(R.id.genre_Three) ;
        logOut = findViewById(R.id.logOut) ;
        Dp = findViewById(R.id.userImage) ;
        lp = findViewById(R.id.lp) ;
        mp = findViewById(R.id.mp) ;
        g1 = findViewById(R.id.g1) ;
        g2 = findViewById(R.id.g2) ;
        g3 = findViewById(R.id.g3) ;

        fAuth = FirebaseAuth.getInstance() ;
        fStore = FirebaseFirestore.getInstance() ;
        fStorage = FirebaseStorage.getInstance() ;

        if (fAuth.getCurrentUser() != null)
            userId = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();

        SharedPreferences lP = getSharedPreferences("last"+userId , MODE_PRIVATE) ;
        SharedPreferences mP = getSharedPreferences("most"+userId , MODE_PRIVATE) ;


//        imageRef.getBytes(1024*1024)
//                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
//                    @Override
//                    public void onSuccess(byte[] bytes) {
//                        Bitmap bitmap  = BitmapFactory.decodeByteArray(bytes , 0 , bytes.length) ;
//                        Dp.setImageBitmap(bitmap);
//                    }
//                }) ;

        lp.setText(lP.getString("lastPlayed" , "NA"));
        mp.setText(mP.getString("mostPlayed" , "NA"));

        DocumentReference documentReference = fStore.collection("users").document(userId) ;
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                if (value != null) {
                    name.setText(value.getString("Name"));
                    email.setText(value.getString("Email"));

                    List<String> genres = (List<String>) value.get("Genres") ;

                    if (genres != null && genres.size() == 3) {
                        genre_One.setText(genres.get(0));
                        genre_Two.setText(genres.get(1));
                        genre_Three.setText(genres.get(2));

                        g1.setImageResource(map.get(genres.get(0)));
                        g2.setImageResource(map.get(genres.get(1)));
                        g3.setImageResource(map.get(genres.get(2)));

                        if (value.get("uri") != null) {
                            uri = Uri.parse(value.getString("uri")) ;
                            Picasso.get().load(uri).into(Dp);
                        }
                    }


                }

                Dp.setVisibility(View.VISIBLE);
                StorageReference imageRef = fStorage.getReference().child("uploads").child(userId) ;
                if (imageRef != null) {
                    imageRef.getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Picasso.get().load(uri).into(Dp);
                                }
                            });
                }



            }
        });

//        StorageReference imageRef = fStorage.getReference().child("uploads").child(userId) ;
//        if (imageRef != null) {
//            imageRef.getDownloadUrl()
//                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//                            Picasso.get().load(uri).into(Dp);
//                        }
//                    });
//
//            //            Uri uri = Uri.parse(dpUri) ;
//            //            Toast.makeText(this, dpUri, Toast.LENGTH_SHORT).show();
////                    if (dpUri.length() != 0) {
////                        Toast.makeText(myProfile.this, "Size is " + dpUri.length(), Toast.LENGTH_SHORT).show();
////                        Uri uri = Uri.parse(dpUri);
////                        Picasso.get().load(uri).into(Dp);
////                    }
//
//
//
//
//        }


        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext() , signIn.class) ;
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) ;
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK) ;
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        inflater  = getMenuInflater();
        inflater.inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_explore:
                startActivity(new Intent(getApplicationContext() , userlist.class));
                break ;

            case R.id.action_local:
                startActivity(new Intent(getApplicationContext() , local.class));
                break ;

            case R.id.action_delete:
                AlertDialog.Builder alert = new AlertDialog.Builder(myProfile.this) ;
                alert.setTitle("Delete Your Account")
                        .setMessage("Are You Sure ?")
                        .setNegativeButton("No" , null)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
                                firebaseUser.delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(myProfile.this, "Account Deleted", Toast.LENGTH_SHORT).show();

                                                    fStore.collection("users").document(userId).delete() ;
                                                    fStore.collection("fav").document(userId).delete() ;
                                                    StorageReference ref = fStorage.getReference().child("uploads").child(userId) ;
                                                    ref.delete() ;
                                                    Intent intent = new Intent(myProfile.this , MainActivity.class) ;
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) ;
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK) ;
                                                    startActivity(intent);
                                                } else {
                                                    Toast.makeText(myProfile.this, "Error :" + task.getException().getMessage() , Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });


                            }
                        }) ;

                alert.create().show();
        }
        return super.onOptionsItemSelected(item);
    }
}