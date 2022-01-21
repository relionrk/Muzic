package com.rkrelion.muzic;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rkrelion.muzic.R;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class user_activiy extends AppCompatActivity {

    ImageView imageView , g1 , g2 , g3 ;
    TextView name , email , g_one , g_two , g_three ;
    LinearLayout ll ;
    String userId ;
    HashMap<String , Integer> map = new HashMap<String, Integer>();

    FirebaseFirestore fStore ;
    FirebaseStorage fStorage ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_activiy);

        getSupportActionBar().setTitle("User Info");

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

        Intent intent = getIntent() ;

        imageView = findViewById(R.id.userprofileImage) ;
        name = findViewById(R.id.userprofileName) ;
        email = findViewById(R.id.userprofileEmail) ;
        g_one = findViewById(R.id.user_genre_One) ;
        g_two = findViewById(R.id.user_genre_Two) ;
        g_three = findViewById(R.id.user_genre_Three) ;
        ll = findViewById(R.id.linearLayoutfav) ;
        g1 = findViewById(R.id.g1_user) ;
        g2 = findViewById(R.id.g2_user) ;
        g3 = findViewById(R.id.g3_user) ;

        fStore = FirebaseFirestore.getInstance() ;
        fStorage = FirebaseStorage.getInstance() ;

        userId = intent.getStringExtra("userid") ;

        StorageReference imageRef = fStorage.getReference().child("uploads").child(userId) ;
        if (imageRef != null) {
            imageRef.getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(imageView);
                        }
                    });
        }

        DocumentReference documentReference = fStore.collection("users").document(userId) ;
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                assert value != null;
                name.setText(value.getString("Name"));
                email.setText(value.getString("Email"));

                List<String> genres = (List<String>) value.get("Genres") ;
                assert genres != null;
                g_one.setText(genres.get(0));
                g_two.setText(genres.get(1));
                g_three.setText(genres.get(2));

                g1.setImageResource(map.get(genres.get(0)));
                g2.setImageResource(map.get(genres.get(1)));
                g3.setImageResource(map.get(genres.get(2)));

            }
        }) ;

        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(getApplicationContext() , UserFav.class) ;
                newIntent.putExtra("id" , userId) ;
                startActivity(newIntent);
            }
        });
    }
}