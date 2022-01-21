package com.rkrelion.muzic;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import com.rkrelion.muzic.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class userlist extends AppCompatActivity {

    FirebaseFirestore fStore ;
    CollectionReference collectionReference ;

    ArrayList<String> userList = new ArrayList<>();
    ListView listView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userlist);

        getSupportActionBar().setTitle("List of Users");

        fStore = FirebaseFirestore.getInstance() ;
        collectionReference = fStore.collection("users") ;

        listView = findViewById(R.id.listView) ;

        collectionReference.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            userList.add(documentSnapshot.getId()) ;
                        }
                        userAdapter ad = new userAdapter(getApplicationContext() , R.layout.user_adapter , userList) ;
                        listView.setAdapter(ad);
                    }
                });



    }
}