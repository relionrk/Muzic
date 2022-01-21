package com.rkrelion.muzic;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.rkrelion.muzic.R;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class myFavourites extends AppCompatActivity {

    ArrayList<File> Songs = new ArrayList<>() ;
    ArrayList<Integer> favouriteSongs = new ArrayList<>() ;
    ArrayList<File> playList = new ArrayList<>() ;
    HashMap<String,Integer> map = new HashMap<>() ;
    String userId ;

    myAdapter playAd ;
    RecyclerView recyclerView ;
    MenuInflater inflater ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_favourites);

        getSupportActionBar().setTitle("My Favourites");

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Songs = (ArrayList) bundle.getParcelableArrayList("songList");
        //playlistSongs = (ArrayList) bundle.getParcelableArrayList("playlistSongs");
        map = (HashMap<String, Integer>) intent.getSerializableExtra("map");

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid() ;
//
        SharedPreferences sP = getSharedPreferences("Favourites"+userId , MODE_PRIVATE) ;
        for (int i=0 ; i< Songs.size() ; i++) {
            String name = Songs.get(i).getName().replace(".mp3" , "") ;
            if (sP.getBoolean(name , false))
                favouriteSongs.add(i) ;
        }
//
        recyclerView = findViewById(R.id.recyclerViewmy) ;

        for (int i=0 ; i<favouriteSongs.size() ; i++) {
            playList.add(Songs.get(favouriteSongs.get(i))) ;
        }

        if (playList.size() == 0) {
            Toast.makeText(this, "You haven't added any song to your favourites yet !", Toast.LENGTH_SHORT).show();
        }
//
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        playAd = new myAdapter(this , playList , map) ;
        recyclerView.setAdapter(playAd);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        inflater  = getMenuInflater();
        inflater.inflate(R.menu.playlistmenu, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {

            case R.id.refresh:
                SharedPreferences sP = getSharedPreferences("Favourites"+userId , MODE_PRIVATE) ;
                ArrayList<Integer> newFav = new ArrayList<>() ;
                for (int i=0 ; i< Songs.size() ; i++) {
                    String name = Songs.get(i).getName().replace(".mp3" , "") ;
                    if (sP.getBoolean(name , false))
                        newFav.add(i) ;
                }
                playAd.updatePlaylist(Songs , newFav);
                break ;

        }
        return true;
    }
}