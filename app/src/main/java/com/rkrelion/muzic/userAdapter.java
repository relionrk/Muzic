package com.rkrelion.muzic;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

import java.util.ArrayList;

public class userAdapter extends ArrayAdapter<String> {

    ArrayList<String> users ;

    FirebaseFirestore fStore ;
    FirebaseStorage fStorage ;

    Context context ;
    Uri imgUri ;

    public userAdapter(@NonNull Context context, int resource, @NonNull ArrayList<String> users) {
        super(context, resource, users);
        this.users = users ;
        this.context = context ;
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return users.get(position) ;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.user_adapter , parent , false) ;
        TextView t1 = convertView.findViewById(R.id.userName) ;
        TextView t2 = convertView.findViewById(R.id.userEmail) ;
        ImageView imageView = convertView.findViewById(R.id.userImage) ;

        fStorage = FirebaseStorage.getInstance() ;
        fStore = FirebaseFirestore.getInstance() ;

        DocumentReference documentReference = fStore.collection("users").document(users.get(position)) ;
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                t1.setText(value.getString("Name"));
                t2.setText(value.getString("Email"));
            }
        }) ;


        StorageReference imageRef = fStorage.getReference().child("uploads").child(users.get(position)) ;
        if (imageRef != null) {
            imageRef.getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(imageView);
                            imgUri = uri ;
                        }
                    });
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context , user_activiy.class) ;
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("userid" , users.get(position)) ;
                context.startActivity(intent);
            }
        });

        return convertView ;
    }
}
