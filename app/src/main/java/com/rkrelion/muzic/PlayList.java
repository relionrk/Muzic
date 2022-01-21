package com.rkrelion.muzic;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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

public class PlayList extends AppCompatActivity {
    ArrayList<File> Songs = new ArrayList<>() ;
    ArrayList<Integer> playlistSongs = new ArrayList<>() ;
    ArrayList<File> playList = new ArrayList<>() ;
    HashMap<String,Integer> map = new HashMap<>() ;

    String userId ;

    myAdapter playAd ;
    RecyclerView recyclerView ;
    MenuInflater inflater ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);

        getSupportActionBar().setTitle("Playlist");

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid() ;

        databaseHelper dbh = new databaseHelper(this , "PlayList"+userId, null , 1) ;

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Songs = (ArrayList) bundle.getParcelableArrayList("songList");
        //playlistSongs = (ArrayList) bundle.getParcelableArrayList("playlistSongs");
        map = (HashMap<String, Integer>) intent.getSerializableExtra("map");

        playlistSongs = dbh.getItems() ;

        recyclerView = findViewById(R.id.recyclerView) ;

        for (int i=0 ; i<playlistSongs.size() ; i++) {
            playList.add(Songs.get(playlistSongs.get(i)));
        }

        if (playlistSongs.size() == 0)
            Toast.makeText(this, "The PlayList is Empty ! Add Some Songs ", Toast.LENGTH_SHORT).show();

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
                databaseHelper dbh = new databaseHelper(this , "PlayList"+userId , null , 1) ;
                ArrayList<Integer> newList = dbh.getItems() ;
                playAd.updatePlaylist(Songs , newList);
                break ;

        }
        return true;
    }

}